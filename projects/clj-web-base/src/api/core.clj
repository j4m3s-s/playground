(ns api.core
  (:gen-class)
  (:require [io.pedestal.http :as http]
            ;[io.pedestal.http.route :as route]
            ;[io.pedestal.http.body-params :as body-params]
            ;[environ.core :refer [env]]
            [com.walmartlabs.lacinia.pedestal2 :as p2]
            ;[clojure.java.io :as io]

            ; Local imports
            [api.schema :as schem]
  ))

#_(def routes
  [[["/" {:get `homepage}]
    ["/graphql" {:post `schem/blog-schema}]
    ;["/add-link" ^:interceptors [(body-params/body-params)] {:post `add-link}]
    ]])

; for production usage, use "export environment=prod" otherwise it expands
; routes on each request which destroys performance
#_(def service-map {::http/routes (if (= (env :environment) "prod")
                                  #(route/expand-routes routes)
                                  (route/expand-routes routes))
                  ::http/type   :jetty
                  ::http/host   (or (env :host) "0.0.0.0")
                  ::http/join?  false
                  ::http/port (Integer. (or (env :port) 5000))

                  ;; CSP
                  ;; FIXME: use something more secure?
                  ::http/secure-headers {:content-security-policy-settings "script-src 'unsafe-inline';"}

                  ;; Jetty specific options
                  ;; see http://pedestal.io/reference/jetty
                  ::http/container-options {:h2c true}})

(def service-map
  (p2/default-service schem/blog-schema nil))

(defn -main [& _] (-> service-map http/create-server http/start))

#_ (def server (atom (->
                          service-map
                          http/create-server)))
#_ @server

#_ (http/start @server)
#_ (http/stop @server)
