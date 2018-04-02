(ns processor-test
  (:require [clojure.test :refer :all]
            [processor.data-processor :refer :all]))

(def rules '((define-counter "email-count" []
               true)
             (define-counter "spam-count" []
               (current "spam"))
             (define-signal {"spam-fraction" (/ (counter-value "spam-count" [])
                                                (counter-value "email-count" []))}
               true)
             (define-counter "spam-important-table" [(current "spam")
                                                     (current "important")]
               true)))

(deftest initial-state-test
    (testing
      (is (= '("Is counter", "Is counter", "Is signal", "Is counter")  (initialize-processor rules) ))
    )
)
