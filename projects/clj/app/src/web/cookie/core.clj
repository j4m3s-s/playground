(ns web.cookie.core
  (:gen-class)
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]

            ;; Environment variable
            [environ.core :refer [env]]

            ;; For cookie storage
            [io.pedestal.http.ring-middlewares :as middlewares]
            [ring.util.response :as ring-resp]
            [ring.middleware.session.cookie :as cookie]))

(defn respond-hello [req]
  (let [name (if (nil? (get-in req [:session :name]))
               " World!"
               (get-in req [:session :name]))]
    {:status 200 :body (str "Hello, " name)}))

(defn login [req]
  {:status 200
   :body "Hello, World!"
   :session {:name "toto"}})

(def session-interceptor
  (middlewares/session {:store (cookie/cookie-store
                                {:key
                                 (or "AAAAAAAAAAAAAAAA"
                                     (env :key))})
                        ; Let's use the same as Django, this costs us nothing
                        ; but circumvents dumb analysis from spammers
                        :cookie-name "sessionid"
                        :cookie-attrs {; not accessible through JS
                                       :http-only true

                                       ; Not usable from other site (prevents
                                       ; csrf if implemented)
                                       :same-site :strict
                                       ; Only https (or localhost on firefox)
                                       :secure true
                                       :domain "localhost"}}))

(def routes
  (route/expand-routes [[["/" ^:interceptors [session-interceptor] {:get `respond-hello}]
                         ["/login" ^:interceptors [session-interceptor] {:get `login}]
                         ]]))

(def service-map {::http/routes routes
                  ::http/type   :jetty
                  ::http/host   "0.0.0.0"
                  ::http/join?  false
                  ::http/port (Integer. 5000)

                  ;; Jetty specific options
                  ;; see http://pedestal.io/reference/jetty
                  ::http/container-options {:h2c true}})

(defn -main [& _] (-> service-map http/create-server http/start))
