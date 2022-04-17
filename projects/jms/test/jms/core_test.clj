(ns jms.core-test
  (:require [clojure.test :refer [deftest is]]
            [jms.core :refer [has-windows-line-ending?]]))

(def lines
  (->
   "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnntoto\ntata  \n"
   str/split-lines
   ))

(deftest test-has-windows-line-ending?
  (is (= (has-windows-line-ending? "toto\r\n") true)
      ))
