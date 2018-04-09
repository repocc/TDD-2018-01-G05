(ns processor-test
  (:require [clojure.test :refer :all]
            [processor.data-processor :refer :all]))

(def rules '((define-counter "email-count" []
               true)
             (define-counter "important-count" []
                 true)
))
             ;(define-signal {"spam-fraction" (/ (counter-value "spam-count" [])
            ;                                    (counter-value "email-count" []))}
            ;   true)
            ; (define-counter "spam-important-table" [(current "spam")
            ;                                         (current "important")]
            ;   true)))

;State <-- { :counters {"email-count"  {:fcond fn :fargs fn values {"[]" 0} }}}

(defn process-data-dropping-signals [state new-data]
  (first (process-data state new-data)))

(deftest initial-state-test
  (testing "Query counter from initial state"
    (is (= 0
           (query-counter (initialize-processor rules) "important-count" [])))))

(deftest unconditional-counter-test
  (let [st0 (initialize-processor rules)
        st1 (process-data-dropping-signals st0 {"spam" true})
        st2 (process-data-dropping-signals st1 {"spam" true})]
       (is (= 2  (query-counter st2 "email-count" [])
               ))))
