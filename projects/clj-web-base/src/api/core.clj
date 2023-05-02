(ns api.core
  (:gen-class)
  (:require [io.pedestal.http :as http]
            ; Local imports
            [api.api :refer [service-map]]
            [api.db :as db]
            [xtdb.api :as xt]))

(defn -main [& _] (-> service-map http/create-server http/start))

; NOTABENE: xtdb is currently launched when the xtdb ns is imported.

#_(def server (atom (->
                     service-map
                     http/create-server)))
#_@server

#_(http/start @server)
#_(http/stop @server)

#_(def xtdb-node (atom (db/start-xtdb!)))
#_(do (.close @xtdb-node)
      (reset! xtdb-node nil))

; XTDB
#_(xt/submit-tx @xtdb-node [[::xt/put
                             {:xt/id "hi2u"
                              :user/name "zig"}]])

#_(xt/q (xt/db @xtdb-node) '{:find [e]
                            :where [[e :user/name "zig"]]})

#_ (xt/submit-tx @xtdb-node [[::xt/put
                              {:xt/id (java.util.UUID/randomUUID)
                               :type :tag
                               :name "ha"}]])

#_ (xt/q (xt/db @xtdb-node)
        '{:find [(pull ?e [*])]
          :where [[?e :type :tag]
                   ]})

#_(xt/submit-tx @xtdb-node [[::xt/put
                              {:xt/id :test
                               :content "Ha"
                               :title "ha"
                               :created 12
                               :tags (letfn [(search-tag [name] (xt/q (xt/db @xtdb-node) '{:find [id]
                                                                                           :in [name]
                                                                                           :where [[e :xt/id id]
                                                                                                   [e :type :tag]
                                                                                                   [e :name name]]}
                                                                      name))]
                                       (map search-tag #{"ha" "ho"}))}
                              ]])

#_(xt/sync @xtdb-node)


#_(xt/q (xt/db @xtdb-node)
        '{:find [(pull ?bp [:tags])]
          :where [[?bp :xt/id :test]
                   ]})

#_(xt/q (xt/db @xtdb-node)
        '{:find [title content tags-name]
          :where [[?bp :xt/id :test]
                  [?bp :title title]
                  [?bp :content content]
                  [?bp :tags tags]
                  [(q
                    {:find [name]
                     :where [[?e :name name]
                             [?e :type :tag]
                             [?e :xt/id ?id]
                             [?bp :tags ?id]
                             ]
                     :in [?bp]
                     }
                    ?bp) tags-name]
                   ]})

#_(xt/q (xt/db @xtdb-node)
        '{:find [?tag]
          :where [[?tag :name "ho"]
                  [?bp :tags ?tag]
                   ]})



#_(xt/entity (xt/db @xtdb-node) "1")

