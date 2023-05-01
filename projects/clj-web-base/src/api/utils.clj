(ns api.utils
  (:require
   [environ.core :refer [env]]))

(defn is-prod?
  []
  (= (env :environment) "prod"))
