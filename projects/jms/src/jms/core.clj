(ns jms.core
  (:require [clojure.string :as str])
  (:require [clojure.java.io :as io])
  (:gen-class))


(defn get-lines-with-line-number
  [lines]
  (let [lines-numbers (iterate inc 1)]
    (zipmap lines-numbers lines)))

; get lines with line-numbers
(defn get-lines-with-line-number
  [lines]
  (let [lines-numbers (iterate inc 1)]
    (mapv #(let [line-number (first %)
                 line-content (second %)]
             {:line-number line-number
              :line-content line-content})
          (zipmap lines-numbers lines))))

; trailing whitespace lines
(defn get-trailing-whitespace-lines
  [lines]
  (filter
    #(let
      [_line-number (first %)
       line-content (second %)]
      (< 0 (count (re-seq #"\s+$" line-content)))
      lines)))

; default to 80
(def max-line-character-count 80)

(defn get-lines-too-long
  [lines-with-line-number]
  (filter #(> (count (second %)) max-line-character-count) lines-with-line-number))

(defn print-lines-too-long
  [lines]
  (map
   #(let [line-number (:line-number %)
          line-length (count (:line-content %))]
      (println "line " line-number "is too long (" line-length " characters long)."))
   lines))

(defn remove-trailing-whitespace
  [line]
  (str/replace line #"\s+$" ""))

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

(defn dos2unix-line
  [line]
  (str/replace line #"\r\n" "\n"))

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
      (= subcommand "has-crlf") (let [files (recursive-listing args-rest)
                                      crlf-files (filter has-windows-line-ending? files)]
                                  (if (= 0 (count crlf-files))
                                    (println "There's no windows ending files")
                                    (mapv #(println (str %) " has a CRLF ending") crlf-files)))
      (= subcommand "help") (subcommand-file-help)
      (= subcommand "") (subcommand-file-help)
      (= subcommand "-h") (subcommand-file-help)
      :else  (subcommand-file-help))))

; Entrypoint
(defn -main [& args]
  (let [subcommand (first args)
        args-rest (rest args)]
    ; TODO: use clojure.tools.cli to parse cli options correctly
    (cond
      (= subcommand "file") (subcommand-file args-rest)
      :else (println "tata"))))
