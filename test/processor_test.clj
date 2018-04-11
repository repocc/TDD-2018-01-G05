(ns processor-test
  (:require [clojure.test :refer :all]
            [processor.data-processor :refer :all]))

(def rules '((define-counter "email-count" []
               true)
             (define-counter "spam-count" []
               (current "spam"))
;             (define-signal {"spam-fraction" (/ (counter-value "spam-count" [])
;                                                (counter-value "email-count" []))}
;               true)
             (define-counter "spam-important-table" [(current "spam")
                                                     (current "important")]
               true)))

(defn process-data-dropping-signals [state new-data]
  (first (process-data state new-data)))

(deftest process-data-test
  (let [st0 (initialize-processor rules)
        st1 (process-data-dropping-signals st0 {"spam" true})]
    (is (= 1 (query-counter st1 "email-count" [])))))
