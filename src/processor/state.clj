(ns processor.state
  (:require [processor.expressions :refer :all])
  (:require [protocols.protocols])
  (:import [protocols.protocols Countable])
  (:import [protocols.protocols Evaluable])

)

(defmulti rule-matcher (fn [rule_type rule data] (symbol rule_type)))

(defmethod rule-matcher 'define-counter [rule_type rule data]
  (def cname (first rule))
  (drop 1 rule) ;drop counter name
  (if (function-evaluator (rest rule))
    {(map #(function-evaluator % data) (first rule)) 1} ;evaluate parameter list
    {}
  )
)

(defmethod rule-matcher 'define-signal [rule_type rule data]
  {}
)

(defn eval-rule [rule data]
  (rule-matcher (first rule) (rest rule))
)

(defrecord State[rules counters]
  Evaluable
  (evaluate [this data]
    (map #(eval-rule % data) (:rules this)) ;TODO: armar nuevo mapa de contadores y estados con reduce y devolver new State.
  )
  Countable
  (count [this counter-name counter-args]
    (if (contains? (:count (:counters this)) counter-name)
      0
      (get ((:count (:counters this)) counter-name) counter-args 0)
    )
  )
)
