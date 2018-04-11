(ns evaluator-test
  (:require [clojure.test :refer :all]
            [processor.expressions :refer :all]))


(deftest evaluate-lit-bool
    (testing "Evaluator boolean literal returns literal value."
        (is  (function-evaluator '( true ) {"spam" true} nil)
              )))


(deftest evaluate-current
    (testing "Evaluator current returns data value"
        (is  (function-evaluator '(current "spam") {"spam" true} nil)
              )))
