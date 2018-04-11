(ns processor.state
  (:require [processor.expressions :refer :all])
  (:require [protocols.protocols])
  (:import [protocols.protocols Countable])
  (:import [protocols.protocols Evaluable])
)

(defn evaluate-counter [rule data counters]
  (def rule-exp (:counter-rule rule))
  (def cname (first rule-exp))
  (def params-cond (rest rule-exp))
  (if (function-evaluator (rest params-cond) data nil)
       { cname
              {
                (into [] (map #(function-evaluator % data nil) (first params-cond)))  ;evaluate parameter list
                (inc
                  (if (nil? (counters (str cname)))
                    0                                                                                             ;initialize parameter counter.
                    (get (counters cname) (into [] (map #(function-evaluator % data nil) (first params-cond))) 0) ;get old counter value
                  )
                )
              }
        }

      counters
  )
)

(defn evaluate-signal [rule data]
  ;TODO: evaluate signals
)

(defrecord State[rules counters history]
  Evaluable
  (evaluate [this data]
    (new State
     (:rules this)
     (into {} (map (fn [rule] (evaluate-counter rule data (:counters this))) (filter #(contains? % :counter-rule) (:rules this) ) ))
     (:history this)
    )
  )
  Countable
  (count [this counter-name counter-args]
    (if (nil? (get (:counters this) counter-name))
      0
      (get ((:counters this) counter-name) counter-args 0)
    )
  )
)


(defmulti rule-matcher (fn [rule_type rule] (symbol rule_type)))

(defmethod rule-matcher 'define-counter [rule_type rule]
  {:counter-rule (rest rule)}
)

(defmethod rule-matcher 'define-signal [rule_type rule]
  {:signal-rule (rest rule)}
)

(defn get-init-state [rules]
  (new State (map #(rule-matcher (first %)  %) rules) {} {})
)
