(ns yt-music.core
  (:gen-class)
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]
            [hiccup.core :refer [html]]
            [clojure.java.shell :refer [sh]]
            [crouton.html :as html]
            [cheshire.core :as json]
            [clj-http.client :as client]))

(def URL "http://localhost:5000/api/dl")

(defn- music-to-html-li
  [item]
  [:li (let [url (first item)
             state (second item)]
         (str url " - " state))])

(defonce submitted-music
  (atom {}))

(defn set-music-state
  [url state]
  (swap! submitted-music #(merge % {url state})))

(defn add-item
  [url]
  (set-music-state url :state/downloading))

(defn finish-download-item
  [url]
  (set-music-state url :state/downloaded))

(defn list-of-musics
  []
  [:ul (map music-to-html-li @submitted-music)])

(defn homepage
  [req]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (html
          [:body
                [:input {:type "text" :id "dl-link"}]
                [:button {:onclick "LaunchDl()"} "DL!"]
                [:script (str "function LaunchDl(){
  let url = document.getElementById(\"dl-link\").value;
  let xhr = new XMLHttpRequest();
  xhr.open(\"POST\", \""
                              URL
                              "\");
  xhr.setRequestHeader(\"Content-Type\", \"application/json\");
  xhr.send(JSON.stringify({ url: url }));
}")]
           (list-of-musics)])})

(defn is-title-tag?
  [tag]
  (= (:tag tag) :title))

(defn get-music-name
  [url]
  (->
   ; Here we use slurp to convert from utf-8 to java's encoding utf-16
   (html/parse-string (slurp url :encoding "UTF-8"))
   (get-in [:content])
   first ; head is always first
   :content
   (#(filter is-title-tag? %))
   first
   :content
   first))

; TODO: get config from environ
; TODO: reload page on successful POST
; TODO: persist all dls list. Do we really care ?
; TODO: some basic CSS

(defn log-stdout
  [input]
  (let [stdout (:out input)]
    (println stdout)
    stdout))

(defn filename-from-stdout
  [input]
  (let [stdout (:out input)]
    (-> (re-find #"\[ExtractAudio\] Destination: (.*)" stdout)
        second)))

(defn fingerprint-duration
  [filename]
  (let [output (:out (sh "fpcalc" filename))]
    [(-> (re-find #"FINGERPRINT=(.*)$" output)
         second)
     (-> (re-find #"DURATION=(.*)" output)
         second)]))

; Just so GH search engine doesn't index it by default
(def api-key (apply str (map #(char (bit-xor 2r10100101 (int %))) "ÓÕôÊÜç")))

(defn get-title
  [input]
  (-> input
      (get-in [:results 0 :recordings 3 :title])))

(defn get-artist
  [input]
  (get-in input [:results 0 :recordings 3 :artists 0 :name]))

(defn get-album
  [input]
  (get-in input [:results 0 :recordings 3 :releases 1 :title]))

(defn add-metadata
  [filename]
  (let [[music-signature music-duration] (fingerprint-duration filename)

        json-output (-> (client/get (str "https://api.acoustid.org/v2/lookup?client="
                                         api-key
                                         "&meta=releases+recordings+recordingsidstracks+compress+usermeta+sources&duration="
                                         music-duration
                                         "&fingerprint="
                                         music-signature))
                        :body
                        (json/parse-string true))

        title (get-title json-output)

        ; FIXME: support multiple artists
        artist (get-artist json-output)
        album (get-album json-output)]
    (println (json/generate-string {:title title :artist artist :album album}))
    (sh "id3v2" "--song" title "--album" album "--artist" artist filename)))

(defn dl-handler
  [req]
  (let [url (get-in req [:json-params :url] nil)]
    (future (do
              (let [name (get-music-name url)]
                (add-item name)
                (let [filename (-> (sh "yt-dlp" "-x" url "--embed-thumbnail")
                                   log-stdout
                                   filename-from-stdout)]
                  (add-metadata filename))
                (finish-download-item name))))
  {:status 200 :headers {"Content-Type" "text/plain"}}))

(def routes
  [[["/" {:get `homepage}]
    ["/api/dl" ^:interceptors [(body-params/body-params)] {:post `dl-handler}]]])

; FIXME: for production usage, don't expand routes on each request
; otherwise we'll destroy performance
(def service-map {::http/routes #(route/expand-routes routes)
                  ::http/type   :jetty
                  ::http/host   "0.0.0.0"
                  ::http/join?  false
                  ::http/port (Integer. 5000)

                  ;; CSP
                  ;; FIXME: use something more secure?
                  ::http/secure-headers {:content-security-policy-settings "script-src 'unsafe-inline';"}

                  ;; Jetty specific options
                  ;; see http://pedestal.io/reference/jetty
                  ::http/container-options {:h2c true}})

(defn start
  []
  (->
   service-map
   http/create-server
   http/start))

(defn -main [& _] (-> service-map http/create-server http/start))

(defonce server (atom nil))

; to start/stop the server
#_ (reset! server (start))
#_ (http/stop @server)
