(ns jms.core
  (:require [jms.file-cmd :refer [subcommand-file]])
  (:gen-class))


; Entrypoint
(defn -main [& args]
  (let [subcommand (first args)
        args-rest (rest args)]
    ; TODO: use clojure.tools.cli to parse cli options correctly
    (cond
      (= subcommand "file") (subcommand-file args-rest)
      :else (println "tata"))))
