(ns evaluator-test
  (:require [clojure.test :refer :all]
            [processor.expressions :refer :all]))

(deftest evaluate-lit-bool
    (testing "Evaluator boolean literal returns literal value."
        (is  (function-evaluator '( true ) {"spam" true} {} nil)
              )))

(deftest evaluate-current
    (testing "Evaluator current returns data value"
        (is  (function-evaluator '(current "spam") {"spam" true} {} nil)
              )))

(deftest evaluate-counter-value
    (testing "Evaluator counter-value returns counter value"
        (is (= 1 (function-evaluator '(counter-value "email-count" []) {"spam" true} {"email-count" {[] 1}} nil)
              ))))

(deftest evaluate-simple-division
    (testing "Evaluator division operation with only one divisor test."
        (is (= 20 (function-evaluator '(/ 100 5) {"spam" true} {} nil)
            ))))

(deftest evaluate-mult-division
    (testing "Evaluator division operation with multiple divisors test."
        (is (= 10 (function-evaluator '(/ 100 5 2) {"spam" true} {} nil)
            ))))

(deftest evaluate-division-with-counters
    (testing "Evaluator division operation with counter values test."
        (is (= 4 (function-evaluator '(/ (counter-value "spam-count" []) (counter-value "email-count" []))
         {"spam" true} {"spam-count" {[] 20}  "email-count" {[] 5}} nil)
            ))))
