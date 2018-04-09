(ns expressions.expression)

(defn matchs [s] (into [] (map #(re-find % s) [#"true" #"false"])))

(defmulti calc_result (fn [exp data] (matchs exp)) )

(defmethod calc_result  ["true" :default] [exp data] true)

(defprotocol Evaluable
    (evaluate [this data])
)

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
