(ns app.day-3
  (:require [clojure.string :as str]
            [clojure.set :refer [intersection]])
  (:gen-class))

(defn parse-input-3
  [input]
  (str/split input #"\n"))

(defn split-items
  [items]
  (split-at (/ (count items) 2) items))

(defn split-rucksacks-items
  [input]
  (map split-items input))

(defn unique-item
  [items]
  (let [left (first items)
        right (second items)]
    (intersection (set left) (set right))))

(defn unique-item-per-backpack
  [input]
  (map unique-item input))

(defn d3-score
  [score]
  (if (<= score 90)
    (- score 38)
    (- score 96)))

(defn score-item-priorities
  [item]
  (let [score (first (map int item))]
    (d3-score score)))

(defn total-d3-score
  [arr]
  (reduce + (map d3-score arr)))

(defn score-3
  [input]
  (reduce +
          (map score-item-priorities input)))

(defn array-set-intersection
  [arr]
  (apply intersection arr))

(intersection #{\A} #{\A} #{\B})

(defn array-to-3-tuple-rec
  [arr acc]
  (if (empty? arr)
    acc
    (let [item-1 (first arr)
          item-2 (first (rest arr))
          item-3 (first (rest (rest arr)))
          remain (rest (rest (rest arr)))
          tuple [item-1 item-2 item-3]]
      (recur remain (conj acc tuple)))))

(defn array-to-3-tuple
  [arr]
  (array-to-3-tuple-rec arr []))

(defn unique-element
  [elt]
  (apply intersection elt))

(defn result-3-part-2
  [input]
  (->
   input
   parse-input-3
   (#(map set %))
   array-to-3-tuple
   (#(map unique-element %))
   (#(map first %))
   (#(map int %))
   total-d3-score))
