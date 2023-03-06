(ns golinks.core
  (:gen-class)
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]
            [hiccup.core :as hiccup]
            [alandipert.enduro :as e]
            [clojure.string :as str]))

; Utils
(defn render-html
  [_rest]
  (hiccup/html _rest))

(def content-type-html "text/html")
(def content-type "Content-Type")

(defn html
  [body]
  {:status 200
   :headers {content-type content-type-html}
   :body (render-html body)})

; Components utils
(defn text-input
  [meta]
  [:input (merge {:type "text"} meta)])

(defn submit-input
  ([]
   [:input {:type "submit" :value "Submit"}])
  ([value]
   [:input {:type "submit" :value value}]))

; Components
; FIXME: add "SUCCESS" arg to show
(defn homepage
  [_req]
  (html
    [:form {:action "/add-link" :method "POST"}
     [:label {:for "lname"} "Link name :"][:br]
     (text-input {:name "lname"})[:br]
     [:label {:for "url"} "URL: "][:br]
     (text-input {:name "url"})[:br]
     (submit-input)
     ]))

(defonce links (e/file-atom {} "/tmp/links.edn" :pending-dir "/tmp"))

(defn add-link
  [req]
  (let [_form-params (:form-params req)
        url (:url _form-params)
        lname (:lname _form-params)
        ]
      (println lname url)
      (e/swap! links assoc lname url)
      {:status 303 :headers {"Content-Type" "text/plain"
                             "Location" "/"}
       :body "Success"}))


(defn list-links
  [_req]
  (html
   [:ul (map #(vector :li (first %) (second %)) @links)]))

(defn redirect-response
  [location body]
  {:status 302
   :headers {"Location" location}
   :body body})

(defn redirect-link
  [req]
  ; Technically we throw away everything after second '/'
  (let [link (-> (:uri req)
                (str/split #"/")
                (nth 2))]
    (redirect-response (@links link) "Sucess")
    ))

; Routes

(def routes
  [[["/" {:get `homepage}]
    ["/add-link" ^:interceptors [(body-params/body-params)] {:post `add-link}]
    ["/list-links" {:get `list-links}]
    ; FIXME: actually make redirect for links
    ["/t/*" {:get `redirect-link}]
    ]])

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
