(ns api.db
  (:require [clojure.java.io :as io]
            [xtdb.api :as xt]))

(defn start-xtdb!
  []
  (letfn [(kv-store [dir]
            {:kv-store {:xtdb/module 'xtdb.rocksdb/->kv-store
                        :db-dir (io/file dir)
                        :sync true}})]
    (xt/start-node
     {:xtdb/tx-log (kv-store "data/dev/tx-log")
      :xtdb/document-store (kv-store "data/dev/doc-store")
      :xtdb/index-store (kv-store "data/dev/index-store")})))

(def db (atom start-xtdb!))

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
        :tags ["ha"]}})

(defn resolve-blog-post
  [_context args _value]
  (let [id (:id args)]
    (xt/q
     @db
     '{:find [title content created tags]
       :in [id]
       :where [[id :title title]
               [id :content content]
               [id :created created]
               [id :tag tags]]}
     id)))

(defn resolve-blog-posts
  [_context _args _value]
  (map second blog-posts))

(defn resolve-tags
  [])

(defn create-blog-post
  [])
