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
     ; FIXME: make configuration generic
     {:xtdb/tx-log (kv-store "data/dev/tx-log")
      :xtdb/document-store (kv-store "data/dev/doc-store")
      :xtdb/index-store (kv-store "data/dev/index-store")})))

(defonce db (atom (start-xtdb!)))

(def tags
  [{:xt/id #uuid "0ae3c154-5b28-4a67-ae3d-f630a9ab1289"
    :type :tag
    :tag/name "ho!"}
   {:xt/id #uuid "bf6bef5f-5582-4233-a2f8-1426eaa48265"
    :type :tag
    :tag/name "ha!"}])

#_(xt/submit-tx @db (map #(vector ::xt/put %) tags))

(def blog-posts
  [{:xt/id #uuid "603ae9a6-1163-4005-afec-5167cb97d4e9"
    :type :post
    :post/content "Ho"
    :post/title "ho"
    :post/created 0
    :post/tags (set (map :xt/id tags))
    }
   {:xt/id #uuid "23f590aa-b33f-410d-be0a-bb3e6f94af17"
    :type :post
    :post/content "Ha"
    :post/title "ha"
    :post/created 1
    :post/tags (set (map :xt/id tags))
    }])

#_(xt/submit-tx @db [[::xt/put
                      (first blog-posts)
                      ]
                     [::xt/put
                      (second blog-posts)
                      ]
                     ])

(defn resolve-blog-post!
  [_context args _value]
  (let [id (:id args)]
    (xt/q
     (xt/db  @db)
     '{:find [(pull ?post [* {:post/tags [:tag/name :xt/id]}])]
       :in [?post]
       :where [[?post :xt/id _]
               ]}
     id)))

#_(resolve-blog-post! nil {:id #uuid "23f590aa-b33f-410d-be0a-bb3e6f94af17"} nil)


(defn resolve-blog-posts!
  [_context _args _value]
  (xt/q
   (xt/db @db)
   '{:find [(pull ?post [* {:post/tags [:tag/name :xt/id]}])]
     :where [[?post :type :post]]}))

#_(resolve-blog-posts! nil nil nil)

(defn resolve-tags!
  [_context _args _value]
  (xt/q
   (xt/db @db)
   '{:find [(pull ?tag [*])]
     :where [[?tag :type :tag]]}))

(defn create-blog-post
  [])
