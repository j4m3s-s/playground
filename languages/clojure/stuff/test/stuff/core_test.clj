(ns stuff.core-test
  (:require [clojure.test :refer :all]
            [stuff.core :refer :all]))

(deftest hello-test
  (testing "Tests hello world."
    (is (= (hello) "Hello, World!"))))

; Time cooking
(deftest remaining-time-test
  (testing "remaining-time-test"
    (is (= (remaining-time 30) 10))))

(deftest prep-time-test
  (testing "prep-time-test"
    (is (= (prep-time 2) 4))))

(deftest total-time-test
  (testing "total-time-test"
    (is (= (total-time 3 20) 26))))

; tracks on tracks on tracks
(deftest new-list-test
  (testing "new-list-test"
    (is (= (new-list) '()))))

(deftest add-language-test
  (testing "add-language-test"
    (is (= (add-language "Javascript" '("Clojure")) '("Javascript", "Clojure")))))

(deftest first-language-test
  (testing "first-language-test"
    (is (= (first-language '("Javascript" "Clojure")) "Javascript"))))

(deftest remove-language-test
  (testing "remove-language-test"
    (is (= (remove-language '("Javascript" "Clojure")) '("Clojure")))))

(deftest count-languages-test
  (testing "count-languages-test"
    (is (= (count-languages-reduce '("Javascript" "Clojure")) 2))))

(deftest learning-list-test
  (testing "learning-list-test"
    (is (= (learning-list) 3))))
