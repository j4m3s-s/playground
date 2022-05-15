(ns jms.webhooktest
  (:require [io.pedestal.http :as http]
            [environ.core :refer [env]]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]))

(defn respond-hello [req]
  {:status 200 :body (str (:json-params req))})

(def routes
  (route/expand-routes [[["/" ^:interceptors [(body-params/body-params)]
                          {:post `respond-hello }]]]))

(def service-map {::http/routes routes
             ::http/type   :jetty
             ::http/host   "0.0.0.0"
             ::http/join?  false
             ::http/port   (Integer. (or (env :port) 5000))}) ; Service map

(defn -main [& _] (-> service-map http/create-server http/start)) ; Server Instance
