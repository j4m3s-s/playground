(ns jms.file-cmd
  (:require [jms.file :refer [recursive-listing has-windows-line-ending?]])
  (:require [clojure.string :as str]))

; print help
(defn subcommand-file-help
  []
  (->>
   ["Available commands: "
    "=================== "

    "has-crlf: print filenames that have crlf ending."
    "help:     this command"]
   (str/join \newline)
   println))

(defn subcommand-file
  [args]
  (let [subcommand (first args)
        args-rest (rest args)]
    (cond
      ; TODO: accept multiple directories here
      (= subcommand "has-crlf") (let [files (recursive-listing args-rest)
                                      crlf-files (filter has-windows-line-ending? files)]
                                  (if (= 0 (count crlf-files))
                                    (println "There's no windows ending files")
                                    (mapv #(println (str %) " has a CRLF ending") crlf-files)))
      (= subcommand "help") (subcommand-file-help)
      (= subcommand "") (subcommand-file-help)
      (= subcommand "-h") (subcommand-file-help)
      :else  (subcommand-file-help))))
