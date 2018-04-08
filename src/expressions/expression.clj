(ns expressions.expression)

(defmulti calc_result (fn [exp data] (type exp)))

(defmethod calc_result java.lang.Boolean [exp data] exp)

(defprotocol Evaluable
    (evaluate [this data])
)

(defrecord Expression [e]
    Evaluable
    (evaluate [this data]
        (calc_result e data)
    )

)
