(ns app.day-4
  (:require [clojure.string :as str]
            [clojure.math.combinatorics :as comb])
  (:gen-class))

(def test-input
  "2-4,6-8
2-3,4-5
5-7,7-9
2-8,3-7
6-6,4-6
2-6,4-8")

(defn parse-ranges
  [input]
  (map #(Integer/parseInt %) (str/split input #"-")))


(defn parse-ranges-tuple
  [in]
  (map parse-ranges in))

(defn is-range-contained?
  [rng]
  (let [left-lower (first (first rng))
        left-upper (second (first rng))
        right-lower (first (second rng))
        right-upper (second (second rng))]
    (if (or
         (and (<= left-lower right-lower)
              (<= right-upper left-upper))
         (and (<= right-lower left-lower)
              (<= left-upper right-upper)))
      1
      0)))

(def input (slurp "inputs/4/input-1.txt"))

(defn result-part-1
  [input]
  (->
   input
   str/split-lines
   ((fn [input] (map #(str/split % #",") input)))
   ((fn [input] (map parse-ranges-tuple input)))
   (#(map is-range-contained? %))
   (#(reduce + %))))

(defn range-overlap
  [rng]
  (let [ll (first (first rng))
        lu (second (first rng))
        rl (first (second rng))
        ru (second (second rng))]
    (if (and (<= ll rl)
             (<= rl lu))
      [rl (min lu ru)]
      (if (and (<= rl ll)
               (<= ll ru))
        [ll (min lu ru)]
        [0 0]))))

(defn is-null-range?
  [rng]
  (= [0 0] rng))

(defn result-part-2
  [input]
  (->
   input
   str/split-lines
   ((fn [input] (map #(str/split % #",") input)))
   ((fn [input] (map parse-ranges-tuple input)))
   (#(map range-overlap %))
   (#(filter (complement is-null-range?) %))
   count))
