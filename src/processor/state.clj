(ns processor.state
  (:require [processor.expressions :refer :all])
  (:require [protocols.protocols])
  (:import [protocols.protocols Countable])
  (:import [protocols.protocols Evaluable])
)

(defn evaluate-counter [rule data counters]
  (def rule-exp (:counter-rule rule))
  (def counter-name (first rule-exp))
  (def params-cond (rest  rule-exp))
  (if (function-evaluator (rest params-cond) data counters nil)
       { counter-name
         (merge
              (if (nil? (counters counter-name)) {} (counters counter-name))
              {
                (into [] (map #(function-evaluator % data counters nil) (first params-cond)))  ;evaluate parameter list
                (inc
                  (if (nil? (counters counter-name))
                    0                                                                                             ;initialize parameter counter.
                    (get (counters counter-name) (into [] (map #(function-evaluator % data counters nil) (first params-cond))) 0) ;get old counter value
                  )
                )
              }
          )
        }

      counters
  )
)

(defn evaluate-signal [rule data counters]
  (def rule-exp (:signal-rule rule))
  (def rule-data (first rule-exp))
  (def condition (rest rule-exp))
  ;(println (str "processing singal... " data " with counters " counters))
  (if (function-evaluator condition data counters nil)
      (try
        (reduce-kv (fn [map sname srule] (assoc map sname (function-evaluator srule data counters nil))) {} rule-data)
        (catch Exception e '())
      )
      '()
  )

)

(defrecord State[rules counters history]
  Evaluable
  (evaluate [this data]
    [
    (new State
     (:rules this)
     (into {} (map (fn [rule] (evaluate-counter rule data (:counters this))) (:counter-rules (:rules this) ) ))
     (:history this)
    )
    (filter #(not (empty? %)) (map (fn [rule] (evaluate-signal rule data (:counters this))) (:signal-rules (:rules this) ) ))
    ]
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
  (def rule-list (map #(rule-matcher (first %)  %) rules))
  (def processed-rules {:counter-rules (filter #(contains? % :counter-rule) rule-list) :signal-rules (filter #(contains? % :signal-rule) rule-list)})
  (new State processed-rules {} {})
)
