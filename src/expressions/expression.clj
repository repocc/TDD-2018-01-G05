(ns expressions.expression)

(defn matchs [s] (into [] (map #(re-find % s) [#"true" #"false"])))

(defmulti calc_result (fn [exp data] (matchs exp)) )

(defmethod calc_result  ["true" :default] [exp data] true)

(defprotocol Evaluable
    (evaluate [this data])
)

(defrecord Expression [e]
    Evaluable
    (evaluate [this data]
        (calc_result e data)
    )
)
