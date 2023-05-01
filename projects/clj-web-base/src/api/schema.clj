(ns api.schema
  (:require [com.walmartlabs.lacinia.schema :as schema]
            [com.walmartlabs.lacinia.util :as util]
            [clojure.edn :as edn]
            ))

(def blog-posts
  {"0" {:id "0"
      :content "Ho"
      :title "ho"
      :created 0
      :tags ["ho"]}
   "1" {:id "1"
      :content "Ha"
      :title "ha"
      :created 1
      :tags ["ha"]}
   })

(defn resolve-blog-post
  [_context args _value]
  (blog-posts (:id args))
  )

(defn resolve-blog-posts
  [_context _args _value]
  (map second blog-posts))

(defn resolve-tags
  [])

(defn create-blog-post
  [])

(defn blog-schema
  []
  ; FIXME: for prod use io/resources
  (-> (slurp "resources/blog-schema.edn")
      edn/read-string
      (util/attach-resolvers {:resolve-blog-post resolve-blog-post
                              :resolve-blog-posts resolve-blog-posts
                              :resolve-tags resolve-tags
                              :create-blog-post create-blog-post})
      schema/compile))
