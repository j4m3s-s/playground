(ns app.day-1
  (:require [clojure.string :as str])
  (:gen-class))

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
