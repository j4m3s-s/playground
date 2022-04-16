(ns jms.core
  (:require [clojure.string :as str]))


(def lines
  (->
   "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnntoto\ntata  \n"
   str/split-lines
   ))

(def lines-with-line-number
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

(def lines-too-long
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

(defn has-windows-line-ending?
  [line]
  (< 0
     (count (re-seq #"\r\n$" line))))

(defn dos2unix-line
  [line]
  (str/replace line #"\r\n" "\n"))

(dos2unix-line "toto\r\n")
