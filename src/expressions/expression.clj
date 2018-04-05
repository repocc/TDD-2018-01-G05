(ns expressions.expression)

(defmulti calc_result (fn [exp data] (type exp)))

(defmethod calc_result java.lang.Boolean [exp data] exp)

(defprotocol Evaluable
    (evaluateexp [this data])
)

(defrecord Expression [e]
    Evaluable
    (evaluateexp [this data]
        (calc_result e data)
    )

)
