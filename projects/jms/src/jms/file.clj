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
            (apply 'and (map is-line-windows-line-ending? lines)))))

; recursive list of files from a point
(defn recursive-listing
  [folderpaths]
  (let [rec-listing #(file-seq (io/file %))]
    (flatten (map rec-listing folderpaths))))
