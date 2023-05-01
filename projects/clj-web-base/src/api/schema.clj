(ns api.schema
  (:require [com.walmartlabs.lacinia.schema :as schema]
            [com.walmartlabs.lacinia.util :as util]
            [clojure.edn :as edn]
            [api.db :as db]))

(defn blog-schema
  []
  ; FIXME: for prod use io/resources
  (-> (slurp "resources/blog-schema.edn")
      edn/read-string
      (util/attach-resolvers {:resolve-blog-post db/resolve-blog-post
                              :resolve-blog-posts db/resolve-blog-posts
                              :resolve-tags db/resolve-tags
                              :create-blog-post db/create-blog-post})
      schema/compile))
