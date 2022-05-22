(ns jms.webhooktest
  (:gen-class)
  (:require [io.pedestal.http :as http]
            [clojure.data.json :as json]
            [environ.core :refer [env]]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]))

(defn respond-hello [req]
  {:status 200 :body (str (:json-params req) (:headers req))})

(defn webhook-post [req]
  (cond
    (not (= (get-in req [:headers "x-gitea-event"]) "push"))
      {:status 400 :body (json/write-str {:error "Not a push event"})}
    :else {:status 200 :body (json/write-str {:event "submitted"})}))

(def routes
  (route/expand-routes [[["/" ^:interceptors [(body-params/body-params)]
                          {:post `respond-hello }]
                         ["/webhook" ^:interceptors [(body-params/body-params)]
                          {:post `webhook-post}]]]))

(def service-map {::http/routes routes
                  ::http/type   :jetty
                  ::http/host   "0.0.0.0"
                  ::http/join?  false
                  ::http/port   (Integer. (or (env :port) 5000))
                  ;; Container options contains jetty specific configuration
                  ;; http://pedestal.io/reference/jetty
                  ::http/container-options {:h2c true}})

(defn -main [& _] (-> service-map http/create-server http/start))
