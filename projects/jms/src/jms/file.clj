(ns jms.file
  (:require [clojure.string :as str])
  (:require [clojure.java.io :as io]))

(defn is-line-windows-line-ending?
  [line]
  (< 0
     (count (re-seq #"\r\n$" line))))

; returns true if file has a "\r\n"
(defn has-windows-line-ending?
  [filename]
  (cond
    (.isDirectory filename) false
    :else (let [content (slurp filename)
                lines (str/split content #"\r\n")]
            (first (and (map is-line-windows-line-ending? lines))))))

(defn recursive-listing
  [folderpath]
  (let [fd (io/file folderpath)]
    (file-seq fd)))

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
      (= subcommand "has-crlf") (let [files (recursive-listing (first args-rest))
                                      crlf-files (filter has-windows-line-ending? files)]
                                  (if (= 0 (count crlf-files))
                                    (println "There's no windows ending files")
                                    (mapv #(println (str %) " has a CRLF ending") crlf-files)))
      (= subcommand "help") (subcommand-file-help)
      (= subcommand "") (subcommand-file-help)
      (= subcommand "-h") (subcommand-file-help)
      :else  (subcommand-file-help))))
