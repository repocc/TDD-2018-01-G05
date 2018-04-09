(ns expressions.expression
  (:require [interfaces.interfaces])
  (:import [interfaces.interfaces Evaluable])
  (:import [interfaces.interfaces Updateable])
)

(defn matchs [s] (into [] (map #(re-find % s) [#"true" #"false"])))

(defmulti calc_result (fn [exp data] (matchs exp)) )

(defmethod calc_result  ["true" :default] [exp data] true)

(defrecord Expression [e];Represent literals: Numbers, Booleans or strings.
    Evaluable
    (evaluate [this data]
        (calc_result e data);TODO: Evaluate ALL literals.
    )
)

(defrecord Function [operator args];Represent functions such as: "\", "not", "+", "current", "past"...
  Evaluable
  (evaluate [this data]
    false;TODO: Evaluate expressions and apply operator
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
