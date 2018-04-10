(ns evaluator-test
  (:require [clojure.test :refer :all]
            [processor.expressions :refer :all]))


(deftest initial-state-test
    (testing "Evaluator current returns data value"
        (is  (function-evaluator '(current "spam") {"spam" true} nil)
              )))
