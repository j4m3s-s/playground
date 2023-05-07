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
#_ (xt/submit-tx @xtdb-node [[::xt/put
                              {:xt/id (java.util.UUID/randomUUID)
                               :type :tag
                               :tag/name "ha"}]
                             [::xt/put
                              {:xt/id (java.util.UUID/randomUUID)
                               :type :tag
                               :tag/name "ho"}
                              ]])

#_ (xt/q (xt/db @xtdb-node)
        '{:find [(pull ?e [*])]
          :where [[?e :type :tag]
                   ]})

#_(xt/submit-tx @xtdb-node [[::xt/put
                             {:xt/id :test
                              :post/content "Ha"
                              :post/title "ha"
                              :post/created 12
                              :tag (letfn [(search-tag [name] (xt/q (xt/db @xtdb-node) '{:find [id]
                                                                                         :in [name]
                                                                                         :where [[e :xt/id id]
                                                                                                 [e :type :tag]
                                                                                                 [e :tag/name name]]}
                                                                    name))]
                                     (->> #{"ha" "ho"}
                                          (map search-tag)
                                          (map ffirst)
                                          set))}
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
        '{:find [(pull ?post [* {:tag [:tag/name]}])]
          :where
          [[?post :post/title ?title]
           [(= :test ?post)]
           ]})
