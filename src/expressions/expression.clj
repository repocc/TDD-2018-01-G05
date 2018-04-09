(ns expressions.expression
  (:require [interfaces.interfaces])
  (:import [interfaces.interfaces Evaluable])
  (:import [interfaces.interfaces Updateable])
)

(defmulti calc_result (fn [exp data] exp) )

(defmethod calc_result  "true" [exp data] true)
(defmethod calc_result  "false" [exp data] false)
(defmethod calc_result  :default [exp data] exp)

(defrecord Expression [e]
    ;Represents a literal: Numbers, Booleans or strings.
    Evaluable
    (evaluate [this data]
        (calc_result e data)
    )
)

(defrecord Function [operator args];Represent functions such as: "\", "not", "+", "current", "past"...
  Evaluable
  (evaluate [this data]
    true;TODO: Evaluate expressions and apply operator
  )
)

(defrecord State [rules]
  Updateable
  (updateexp [this data-name data-value]
      (new State
          { :counters
            (into {} (for [ [counter-name counter] (:counters (:rules this))]
              [counter-name
                (interfaces.interfaces/updateexp counter data-name (new Expression data-value))
              ]
            ))
            :signals { }
          }
      )
  )
)
