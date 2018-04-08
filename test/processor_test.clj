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

;State <-- { :counters {"email-count"  {:fcond fn :fargs fn values {"[]" 0} }}}


(deftest initial-state-test
  (testing "Query counter from initial state"
    (is (= 0
           (query-counter (initialize-processor rules) "email-count" [])))))
