(ns api.core
  (:gen-class)
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [environ.core :refer [env]]
            [com.walmartlabs.lacinia.pedestal2 :as p2]

            ; Local imports
            [api.schema :as schem]
            [api.utils :refer [is-prod?]]
            [api.api :refer [service-map]]
  ))

(defn -main [& _] (-> service-map http/create-server http/start))

#_ (def server (atom (->
                          service-map
                          http/create-server)))
#_ @server

#_ (http/start @server)
#_ (http/stop @server)
