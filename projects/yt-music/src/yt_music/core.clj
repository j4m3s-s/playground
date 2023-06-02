(ns yt-music.core
  (:gen-class)
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]
            [hiccup.core :refer [html]]
            [clojure.java.shell :refer [sh]]
            [crouton.html :as html]))

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
    input))

(defn dl-handler
  [req]
  (let [url (get-in req [:json-params :url] nil)]
    (future (do
              (let [name (get-music-name url)]
                (add-item name)
                (-> (sh "yt-dlp" "-x" url "--embed-thumbnail")
                    log-stdout)
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
