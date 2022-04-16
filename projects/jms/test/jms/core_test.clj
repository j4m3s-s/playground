(ns jms.core-test
  (:require [clojure.test :refer :all]
            [jms.core :refer :all]))

(deftest test-has-windows-line-ending?
  (is (= (has-windows-line-ending? "toto\r\n") true)
      ))
