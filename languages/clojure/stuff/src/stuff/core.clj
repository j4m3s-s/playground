(ns stuff.core
  (:gen-class)
  (:require
   [clojure.string :refer [split]]))

(defn hello
  "I don't do a whole lot ... yet."
  [& args]
  "Hello, World!")

; time cooking
(def expected-time 40)

(defn remaining-time [input] (- expected-time input))

(defn prep-time [number_of_layer] (* 2 number_of_layer))

(defn total-time [number_of_layer number_of_minutes_cooking]
  (+ (prep-time number_of_layer) (remaining-time number_of_minutes_cooking)))

; tracks on tracks on tracks
(defn new-list [] '())

(defn add-language [lang lst]
  (conj lst lang))

(defn first-language [lst]
  (first lst))

(defn remove-language [lst]
  (rest lst))

(defn count-languages [lst]
  (if (empty? lst) 0 (+ 1 (count-languages (rest lst)))))

(defn count-languages-reduce [lst]
  (reduce (fn [x y] (+ x 1)) 0 lst))

(defn learning-list []
  (->>
   (new-list)
   (add-language "Clojure")
   (add-language "Lisp")
   (remove-language)
   (add-language "Java")
   (add-language "Javascript")
   (count-languages)))

; Bird watcher
(def last-week
  '(0 2 5 3 7 8 4))

(def birds-per-day [2 5 0 7 4 1])

(defn today [lst]
  (last lst))

(defn inc-bird [lst]
  (if (= (count lst) 1) (list (inc (first lst))) (cons (first lst) (inc-bird (rest lst)))))

(defn day-without-birds? [lst]
  (some (fn [x] (= x 0)) lst))

(defn n-days-count [lst cnt]
  (loop [i cnt
         res 0
         rst lst]
    (if (= 0 i)
      res
      (recur (dec i)
             (+ res (first rst))
             (rest rst)))))

(defn n-days-count-2 [lst cnt]
  (second (reduce (fn [x y]
                    (let
                     [counter (first x)
                      adder (second x)]
                      (if (= counter cnt)
                        (list counter adder)
                        (list (inc counter) (+ adder y)))))
                  (list 0 0) lst)))

(def book (slurp "war-and-peace"))

(defn count-chars [x]
  (count (seq x)))

(defn count-words [lst]
  (count (split lst #" ")))

; histogram of letters
; (reverse (sort-by val (frequencies (seq book))))
; histogram of words
; (reverse (sort-by val (frequencies (split book #" "))))
; lazy seq -> kinda impossible if we sort afaik? -> we freq then sort,
; so possible on that part

; main
(defn -main
  [& args]
  (println (take 10 (reverse (sort-by val (frequencies (split book #" ")))))))
