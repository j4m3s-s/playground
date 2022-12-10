(ns app.core
  (:require [app.day-1 :as d1]
            [app.day-2 :as d2]
            [app.day-3 :as d3]
            [app.day-4 :as d4])
  (:gen-class))

;; Day 1
(def input-1
  (slurp "inputs/1/input-1.txt"))

(def test-input
  (slurp "inputs/1/test.txt"))

;; Day 2

(def test-input-2
"A Y
B X
C Z
")

(def input-2
  (slurp "inputs/2/input-1.txt"))

;; Day 3
(def test-input-3
  "vJrwpWtwJgWrhcsFMMfFFhFp
jqHRNqRjqzjGDLGLrsFMfFZSrLrFZsSL
PmmdzqPrVvPwwTWBwg
wMqvLMZHhHMvwLHjbvcjnnSBnvTQFn
ttgJtRGJQctTZtZT
CrZsJsPPZsGzwwsLwLmpwMDw")

(def input-4
  (slurp "inputs/4/input-1.txt"))

(def input-3
  (slurp "inputs/3/input-1.txt"))

(defn -main
  []
  ; Day 1
  (println (d1/result-part-1 input-1))
  (println (d1/result-part-2 input-1))
  ; Day 2
  (println (d2/result-2-part-1 input-2))
  (println (d2/result-2-part-2 input-2))
  ; Day 3
  (println (d3/result-3-part-2 input-3))
  ;day 4
  (println (d4/result-part-1 input-4))
  (println (d4/result-part-2 input-4)))

#_ (-main)
