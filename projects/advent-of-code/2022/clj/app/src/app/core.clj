(ns app.core
  (:require [app.day-1 :as d1]
            [app.day-2 :as d2]
            [app.day-3 :as d3]
            [app.day-4 :as d4]
            [clojure.string :as str])
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

;; Day 5
(def example-input-5
"    [D]
[N] [C]
[Z] [M] [P]
 1   2   3

move 1 from 2 to 1
move 3 from 1 to 3
move 2 from 2 to 1
move 1 from 1 to 2")

(defn parse-puzzle
  [input]
  (str/split input #"\n\n"))

(defn parse-int
  [int]
  (Integer/parseInt int))

(defn parse-instruction
  [input]
  (let [ins (str/split input #" ")]
    (->
     ; nb - from - to
     [(nth ins 1) (nth ins 3) (nth ins 5)]
     (#(map parse-int %))
     )
    ))

(defn parse-instructions
  [input]
  (as-> input input
    (str/split input #"\n")
    (map parse-instruction input))
  )

(defn my-subs
  ;This is to avoid exception and continue execution. Basically it's subs but
  ;returns nil on exception.
  ;If only one argument is given, we assume it's the size for curried version.
  ([input n]
  (try (subs input n)
        (catch Exception _ nil)))
  ([n]
   (partial #(my-subs % n))))

(defn- top-parse-crate-line
  [acc input size]
  (if (nil? input)
    (if (zero? size)
      acc
      (recur (conj acc \space) nil (dec size)))
    (let [c (nth input 0)]
      (recur (conj acc c) (my-subs input 4) (dec size)))))

(defn parse-crate-line
  [input size]
  (top-parse-crate-line [] input size))

#_(-> "[D] [E]"
    (subs 1)
    (as-> input (parse-crate-line input 3)))

(defn- top-parse-crate-lines
  [acc input size]
  (if (empty? input)
    acc
    (recur (conj acc (parse-crate-line (first input) size)) (rest input) size)))

(defn parse-crate-lines
  [input size]
  (as-> input input
    (map (my-subs 1) input)
    (top-parse-crate-lines [] input size)))

#_(as-> ["[D] [E]" "[B] [O] [V]"] input
      (parse-crate-lines input 3))

(defn- get-column
  [input position]
  (map #(nth % position) input))

(defn- top-matrix-rotate-90r
  [acc input position]
  (println acc)
  (let [new-acc (conj acc (get-column input position))]
    (if (zero? position)
      new-acc
      (recur new-acc input (dec position)))))

(defn matrix-rotate-90r
  [input]
  (let [n (dec (count (last input)))]
    (reverse (top-matrix-rotate-90r [] input n))
    ))

; What a beautiful name
(defn- pad-array
  [input size]
  (if (= size (count input))
    input
    (recur (cons \space input) size)))

(defn- top-pad-arrays
  [acc input size]
  (let [entry (first input)
        n (count entry)]
    (if (= size n)
      (recur (conj acc entry) (rest input) size)
      (recur (conj acc (pad-array entry size)) (rest input) size)
      )))

(defn pad-arrays
  [input size]
  (top-pad-arrays [] input size))

(defn replace-at
  [position array new-element]
  (let [[a b] (split-at position array)]
    (concat a [new-element] (rest b))))

#_(replace-at 0 [1 2 3] 0)

(defn- top-execute-step
  [table instruction]
  (let [[_crate-nb from to] instruction
        [from to] [(dec from) (dec to)]

        [piece-arr old-crate] (split-at 1 (nth table from))
        piece (first piece-arr)

        new-crate (conj (nth table to) piece)
        ]
    (as-> table table
      (replace-at from table old-crate)
      (replace-at to table new-crate)
    )))

(defn execute-step
  [table instruction]
  (let [[nb from to] instruction]
    (if (= 0 nb)
      table
      (recur (top-execute-step table instruction) [(dec nb) from to]))))

(def input-5
  (slurp "inputs/5/input-1.txt"))

#_(time (-> example-input-5
          parse-puzzle
          second
          parse-instructions

          ))

(defn get-crate-matrix
  [input]
  (let [crates-cols (-> input
                   parse-puzzle
                   first
                   (str/split #"\n"))
        crates (drop-last crates-cols)
        crates-count (as-> crates-cols v
                      (take-last 1 v)
                      (first v)
                      (str/split v #" ")
                      (filter (complement empty?) v)
                      (count v))]
    (-> crates
        (#(parse-crate-lines % crates-count))
        matrix-rotate-90r
     )))

(defn map-on-vals
  [array fn]
  (for [v array]
    (map fn v)))

(defn filter-on-vals
  [array fn]
  (for [v array]
    (filter fn v)))

(def example-instructions
  (-> (parse-puzzle example-input-5)
      second
      parse-instructions))

#_(-> (filter-on-vals (get-crate-matrix input-5) (partial not= \space))
      ;(#(execute-step % [1 2 1]))
      (#(reduce execute-step % instructions))
      (#(map first %))
      )

; TODO: need to change moving into array and not element to retain (the inverse)
;       order.

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
