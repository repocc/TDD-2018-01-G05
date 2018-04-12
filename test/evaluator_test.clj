(ns evaluator-test
  (:require [clojure.test :refer :all]
            [processor.expressions :refer :all]))

(deftest evaluate-lit-bool
    (testing "Evaluator boolean literal returns literal value."
        (is  (function-evaluator '( true ) {"spam" true} {} {} nil)
              )))

(deftest evaluate-current
    (testing "Evaluator current returns data value"
        (is  (function-evaluator '(current "spam") {"spam" true} {} {} nil)
              )))

(deftest evaluate-counter-value
    (testing "Evaluator counter-value returns counter value"
        (is (= 1 (function-evaluator '(counter-value "email-count" []) {"spam" true} {"email-count" {[] 1}} {} nil)
              ))))

(deftest evaluate-simple-division
    (testing "Evaluator division operation with only one divisor test."
        (is (= 20 (function-evaluator '(/ 100 5) {"spam" true} {} {} nil)
            ))))

(deftest evaluate-mult-division
    (testing "Evaluator division operation with multiple divisors test."
        (is (= 10 (function-evaluator '(/ 100 5 2) {"spam" true} {} {} nil)
            ))))

(deftest evaluate-division-with-counters
    (testing "Evaluator division operation with counter values test."
        (is (= 4 (function-evaluator '(/ (counter-value "spam-count" []) (counter-value "email-count" []))
         {"spam" true} {"spam-count" {[] 20}  "email-count" {[] 5}} {} nil)
            ))))

(deftest zero-division-error-on-zero-division
   (testing "Processing zero division throws exception."
       (is (thrown? Exception (function-evaluator '(/ (counter-value "spam-count" []) (counter-value "email-count" []))
        {"spam" true} {"spam-count" {[] 20}  "email-count" {[] 0}} {} nil)
            ))))

;Equality operators tests:
(deftest equal-applied-to-literals
  (testing "Equal returns true if equal literals compared."
       (is (= true (function-evaluator '(= 1 1) {} {} {} nil) ))
           ))

(deftest equal-applied-to-seq-true-if-contained
   (testing "Equal returns true if value contained in sequence."
     (is (= true (function-evaluator '(= 1 (past "value")) {} {} {"value" [2 3 1 4]} nil) ))
         ))

(deftest not-equal-applied-to-different-vals-is-true
   (testing "Not equal returns true if non-equal values are compared."
     (is (= true (function-evaluator '(!= 1 2) {} {} {} nil) ))
        ))

;Boolean operators tests:
(deftest or-applied-to-multiple-vals-ors-all
   (testing "Or applied to sequence of booleans returns equivalent of applying or to all of them."
     (is (= true (function-evaluator '(or false false false true) {} {} {} nil) ))
        ))

(deftest and-applied-to-multiple-vals-ors-all
   (testing "And applied to sequence of booleans returns equivalent of applying and to all of them."
     (is (= false (function-evaluator '(and true true true false) {} {} {} nil) ))
        ))

(deftest not-applied-to-boolean-returns-opposite
   (testing "Not negates boolean evaluation."
     (is (= true (function-evaluator '(not (and true true true false)) {} {} {} nil) ))
        ))

(deftest not-applied-to-boolean-returns-opposite
   (testing "Not negates boolean evaluation."
     (is (= true (function-evaluator '(not (and true true true false)) {} {} {} nil) ))
        ))
