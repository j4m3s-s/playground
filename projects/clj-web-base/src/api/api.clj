(ns api.api
  (:gen-class)
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [environ.core :refer [env]]
            [com.walmartlabs.lacinia.pedestal2 :as p2]

            ; Local imports
            [api.schema :as schem]
            [api.utils :refer [is-prod?]]))

; Inspired from private pedestal2 (used in p2/default-service)
; Can't really change them since it seems to be hardcoded somewhere
(def ^:private default-asset-path "/assets/graphiql")
(def ^:private default-api-path "/api")
(def ^:private default-subscriptions-path "/ws")
(def ^:private default-ide-path "/ide")

; FIXME: graphql subscriptions are not enabled -- need more configuration
(def ^:private default-jetty-options
  {::http/type   :jetty
   ::http/host   (or (env :host) "0.0.0.0")
   ::http/join?  false
   ::http/port (Integer. (or (env :port) 5000))

   ;; CSP
   ;; FIXME: use something more secure?
   ;; NB: the nil for secure headers is needed for graphiql usage
   ;; See lacinia-pedestal enable-graphiql.
   ::http/secure-headers (if (is-prod?)
                           {:content-security-policy-settings "script-src 'unsafe-inline';"}
                           nil)

   ;; Jetty specific options
   ;; see http://pedestal.io/reference/jetty
   ::http/container-options {:h2c true}})
(def ^:private interceptors (p2/default-interceptors schem/blog-schema {} default-jetty-options))

(def routes
  (into #{[default-api-path :post interceptors :route-name ::graphql-api]
          ; FIXME not a good idea to keep this in prod -- removes security
          [default-ide-path :get (p2/graphiql-ide-handler {}) :route-name ::graphql-ide]}
        (p2/graphiql-asset-routes default-asset-path)))

; for production usage, use "export environment=prod" otherwise it expands
; routes on each request which destroys performance
(def service-map (merge default-jetty-options {::http/routes (if (is-prod?)
                                                               #(route/expand-routes routes)
                                                               (route/expand-routes routes))}))
