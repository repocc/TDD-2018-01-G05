(ns processor.state
  (:require [processor.expressions :refer :all])
  (:require [protocols.protocols])
  (:import [protocols.protocols Countable])
  (:import [protocols.protocols Evaluable])
)
(require '[clojure.string :as str])

(defn evaluate-counter [rule data counters history]
  (def rule-exp (:counter-rule rule))
  (def counter-name (first rule-exp))
  (def params-cond (rest  rule-exp))
  (if (function-evaluator (rest params-cond) data counters history nil)
       { counter-name
         (merge
              (if (contains? counters counter-name) (counters counter-name) {})
              {
                (into [] (map #(function-evaluator % data counters history nil) (first params-cond)))  ;evaluate parameter list
                (inc
                  (if (nil? (counters counter-name))
                    0                                                                                             ;initialize parameter counter.
                    (get (counters counter-name) (into [] (map #(function-evaluator % data counters history nil) (first params-cond))) 0) ;get old counter value
                  )
                )
              }
          )
        }

      { counter-name (if (contains? counters counter-name) (counters counter-name) {})}
  )
)

(defn evaluate-signal [rule data counters history]
  (def rule-exp (:signal-rule rule))
  (def rule-data (first rule-exp))
  (def condition (rest rule-exp))
  (if (function-evaluator condition data counters history nil)
      (try
        (reduce-kv (fn [map sname srule] (assoc map sname (function-evaluator srule data counters history nil))) {} rule-data)
        (catch Exception e '())
      )
      '()
  )
)

(defn update-history [history data]
  ;(def res
    (reduce-kv (fn [map key value]
        (if (contains? history key)
          (assoc map key (conj (history key) value))
          map
        )
      )
      history
      data
    )
  ;)
  ;(println (str "History " history " updated " res " " data))
  ;res
)

(defrecord State[rules counters history]
  Evaluable
  (evaluate [this data]
    [
    (new State
     (:rules this)
     (into {} (map (fn [rule] (evaluate-counter rule data (:counters this) (:history this))) (:counter-rules (:rules this) ) ))
     (update-history (:history this) data)
    )
    (filter #(not (empty? %)) (map (fn [rule] (evaluate-signal rule data (:counters this) (:history this))) (:signal-rules (:rules this) ) ))
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

(defn init-history [rules]
  (def past-keys (distinct (flatten (map #(re-seq #"(?<=\(past)(.*?)(?=\))" (str %) ) rules) ))) ;get all (past "*") matching values.
  (def past-vals (reduce #(assoc %1  %2  []) {} past-keys))
  (into {} (for [[k v] past-vals :when (not (nil? k))] [(str/trim (str/replace k "\"" "")) v]))
)

(defn get-init-state [rules]
  (def rule-list (map #(rule-matcher (first %)  %) rules))
  (def processed-rules {:counter-rules (filter #(contains? % :counter-rule) rule-list) :signal-rules (filter #(contains? % :signal-rule) rule-list)})
  (new State processed-rules {} (init-history rules))
)
