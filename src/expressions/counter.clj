(ns expressions.counter
  (:require [interfaces.interfaces])
  (:import [interfaces.interfaces Evaluable])
)


(defrecord Counter [count_rule]
  Evaluable
  (eval [this data]
    ;(if (:fcond ))
  )
)
