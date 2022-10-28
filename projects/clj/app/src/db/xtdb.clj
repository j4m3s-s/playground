; Code inspired from the xtdb.com Doc
(ns db.xtdb
  (:require [clojure.java.io :as io]
            [xtdb.api :as xt]))

(defn start-xtdb! []
  (letfn [(kv-store [dir]
            {:kv-store {:xtdb/module 'xtdb.rocksdb/->kv-store
                        :db-dir (io/file dir)
                        :sync? true}})]
    (xt/start-node
     {:xtdb/tx-log (kv-store "/tmp/data/dev/tx-log")
      :xtdb/document-store (kv-store "/tmp/data/dev/doc-store")
      :xtdb/index-store (kv-store "/tmp/data/dev/index-store")})))

(def xtdb-node (start-xtdb!))
;; note that attempting to eval this expression more than once before first calling `stop-xtdb!` will throw a RocksDB locking error
;; this is because a node that depends on native libraries must be `.close`'d explicitly

(defn stop-xtdb! []
  (.close xtdb-node))

#_ (print xtdb-node)
#_ (stop-xtdb!)


; Submit a transaction
#_ (xt/submit-tx xtdb-node [[::xt/put
                             {:xt/id "hi2u"
                              :user/name "zig"}]])

; Query
#_ (xt/q (xt/db xtdb-node) '{:find [e]
                             :where [[e :user/name "zig"]]})
;; => #{["hi2u"]}


;; Let's design a simple HN frontpage


(defn uuid
  []
  (java.util.UUID/randomUUID))

(time (dotimes [_ 100000]
(let [id (uuid)]
     (xt/submit-tx xtdb-node [[::xt/put
                               {:xt/id id
                                :type :HNPost
                                :name "Hello!"
                                :url "World@"}]]))))

; Let's query all the posts
(time (xt/q (xt/db xtdb-node) '{:find [e v u]
                                :where [[e :type :HNPost]
                                        [e :name v]
                                        [e :url u]]
                                :timeout 10
                                :limit 10
                                :offset 10}))

; Query all type across the DB
(time (xt/q (xt/db xtdb-node) '{:find [e t]
                                :where [[e :type t]]}))

; Data representation :
; e a v valid-time? end-valid-time?
; - e is the entity (a node in graph terms)
; - a is an attribute. It can be either some KV data associated with the node
; - v is the value associated with the attribute
;
; To create links between nodes, we name the link with an attribute
; and put the `to` node id in the value.
