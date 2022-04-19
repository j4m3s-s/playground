(ns jms.file
  (:require [clojure.string :as str])
  (:require [clojure.java.io :as io]))

; get lines with line-numbers
(defn get-lines-with-line-number
  [lines]
  (let [lines-numbers (iterate inc 1)]
    (zipmap lines-numbers lines)))

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

(defn change-dos2unix-line
  [line]
  (str/replace line #"\r\n" "\n"))

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
            (reduce and (map is-line-windows-line-ending? lines)))))

; recursive list of files from a point
(defn recursive-listing
  [folderpaths]
  (let [rec-listing #(file-seq (io/file %))]
    (flatten (map rec-listing folderpaths))))
