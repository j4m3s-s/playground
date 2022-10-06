(ns web.webhook.core
  (:gen-class)
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]))

(defn respond-hello [req]
  {:status 200 :body "Hello, World!"})

(def routes
  (route/expand-routes [[["/" {:get `respond-hello}]]]))

(def service-map {::http/routes routes
                  ::http/type   :jetty
                  ::http/host   "0.0.0.0"
                  ::http/join?  false
                  ::http/port (Integer. 5000)

                  ;; Jetty specific options
                  ;; see http://pedestal.io/reference/jetty
                  ::http/container-options {:h2c true}})

(defn -main [& _] (-> service-map http/create-server http/start))
