(ns api.core
  (:gen-class)
  (:require [io.pedestal.http :as http]
            ; Local imports
            [api.api :refer [service-map]]))

(defn -main [& _] (-> service-map http/create-server http/start))

#_(def server (atom (->
                     service-map
                     http/create-server)))
#_@server

#_(http/start @server)
#_(http/stop @server)
