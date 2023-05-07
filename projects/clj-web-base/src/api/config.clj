(ns api.config
  (:require [clojure.edn :as edn]))

; java -Dcwb-configfile=toto.edn
; NB: since this is an atom we can update part of it
(def config (atom (-> (slurp ((into {} (System/getProperties)) "cwb-configfile"))
                      (edn/read-string))))

(defn get-config
  [part]
  (get-in config part))
