(ns app.core
  (:require [clojure.string :as str]
            [clojure.core.match :refer [match]]
            [clojure.set :refer [intersection]])
  (:gen-class))

(def input-1
  (slurp "inputs/1/input-1.txt"))

(def test-input
  (slurp "inputs/1/test.txt"))

(defn parse-input
  [input]
  (->>
   (str/split input #"\n\n")
   (map #(str/split % #"\n"))))

(defn input-to-array-int
  [input]
  (map (fn [x] (map #(Integer/parseInt %) x)) input))

(defn add-elf-calories
  [arr]
  (map #(reduce + %) arr))

(defn most-calories-from-array
  [arr]
  (apply max (add-elf-calories arr)))

(defn result-part-1
  [input]
  (->
   input
   parse-input
   input-to-array-int
   most-calories-from-array))

(defn result-part-2
  [input]
  (reduce +
          (take-last 3
             (->
              input
              parse-input
              input-to-array-int
              add-elf-calories
              sort))))


;; Day 2

(def test-input-2
"A Y
B X
C Z
")

(def input-2
  (slurp "inputs/2/input-1.txt"))

(defn parse-input-2
  [input]
  (->>
   (str/split input #"\n")
   (map #(str/split % #" "))))



; A X Rock
; B Y Paper
; C Z Scissors

(defn input-to-score
  [tuple]
  (let [left (first tuple)
        right (second tuple)]
    (+
     (match [right]
            ["X"] 1
            ["Y"] 2
            ["Z"] 3)
     (match [left right]
            ["A" "X"] 3
            ["A" "Y"] 6
            ["A" "Z"] 0

            ["B" "X"] 0
            ["B" "Y"] 3
            ["B" "Z"] 6

            ["C" "X"] 6
            ["C" "Y"] 0
            ["C" "Z"] 3))))

(defn compute-score
  [input]
  (reduce + (map input-to-score input)))

; A X Rock
; B Y Paper
; C Z Scissors
;;
;; Action to take
; X Lose
; Y Draw
; Z Win

(defn compute-rps-choice
  [tuple]
  (let [left (first tuple)
        right (second tuple)]
    (match [left right]
           ["A" "X"] ["A" "Z"]
           ["A" "Y"] ["A" "X"]
           ["A" "Z"] ["A" "Y"]

           ["B" "X"] ["B" "X"]
           ["B" "Y"] ["B" "Y"]
           ["B" "Z"] ["B" "Z"]

           ["C" "X"] ["C" "Y"]
           ["C" "Y"] ["C" "Z"]
           ["C" "Z"] ["C" "X"])))

(defn compute-choices
  [input]
  (map compute-rps-choice input))

(defn result-2-part-1
  [input]
  (->
   input
   parse-input-2
   compute-score))

(defn result-2-part-2
  [input]
  (->
   input
   parse-input-2
   compute-choices
   compute-score))


;; Day 3
(def test-input-3
  "vJrwpWtwJgWrhcsFMMfFFhFp
jqHRNqRjqzjGDLGLrsFMfFZSrLrFZsSL
PmmdzqPrVvPwwTWBwg
wMqvLMZHhHMvwLHjbvcjnnSBnvTQFn
ttgJtRGJQctTZtZT
CrZsJsPPZsGzwwsLwLmpwMDw")

(def input-3
  (slurp "inputs/3/input-1.txt"))

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

(defn -main
  []
  ; Day 1
  (println (result-part-1 input-1))
  (println (result-part-2 input-1))
  ; Day 2
  (println (result-2-part-1 input-2))
  (println (result-2-part-2 input-2))
  ; Day 3
  (println (result-3-part-2 input-3)))
