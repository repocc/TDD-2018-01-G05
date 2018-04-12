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

(defn process-data-dropping-signals [state new-data]
  (first (process-data state new-data)))

(deftest process-data-test
  (let [st0 (initialize-processor rules)
        st1 (process-data-dropping-signals st0 {"spam" true})]
    (is (= 1 (query-counter st1 "email-count" [])))))

(deftest data-modifies-different-counters-test
  (let [st0 (initialize-processor rules)
        st1 (process-data-dropping-signals st0 {"spam" true})
        st2 (process-data-dropping-signals st1 {"spam" false})]
    (is (= 1 (query-counter st1 "email-count" [])))
    (is (= 1 (query-counter st1 "spam-count" [])))
    (is (= 2 (query-counter st2 "email-count" [])))
    (is (= 1 (query-counter st2 "spam-count" [])))))

(deftest past-value-updated
  (let [st0 (initialize-processor '((define-signal {"repeated" (past "value")}
                                      true)))
        [st1 sg1] (process-data st0 {"value" 1})
        [st2 sg2] (process-data st1 {"value" 2})
        [st3 sg3] (process-data st2 {"value" 1})
        [st4 sg4] (process-data st3 {"value" 1})
        [st5 sg5] (process-data st4 {"value" 2})]
    (is (= '() sg1))
    (is (= '({"repeated" [1]}) sg2))
    (is (= '({"repeated" [1 2]}) sg3))
    (is (= '({"repeated" [1 2 1]}) sg4))
    (is (= '({"repeated" [1 2 1 1]}) sg5))))
