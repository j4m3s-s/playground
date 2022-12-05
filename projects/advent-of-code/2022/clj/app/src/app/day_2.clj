(ns app.day-2
  (:require [clojure.string :as str]
            [clojure.core.match :refer [match]])
  (:gen-class))

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
