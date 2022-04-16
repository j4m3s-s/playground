(ns jms.core
  (:require [clojure.string :as str]))

(def lines
  (str/split-lines "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnntoto\ntata  \n"))

(def lines-with-line-number
  (let [lines-numbers (map inc (range (count lines)))]
    (apply assoc {} (interleave lines-numbers lines))))

; trailing whitespace lines
(defn get-trailing-whitespace-lines [lines]
  (filter
   #(let [_line-number (first %) line-content (second %)]
      (< 0 (count (re-seq #"\s+$" line-content))))
   lines))

(get-trailing-whitespace-lines lines-with-line-number)

; default to 80
(def max-line-character-count 80)

(def lines-too-long
  (filter #(> (count (second %)) max-line-character-count) lines-with-line-number))


(map #(println "line " (first %) "is too long (" (count (second %)) " characters long).") lines-too-long)
