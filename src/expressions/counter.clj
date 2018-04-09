(ns expressions.counter
  (:require [expressions.expression])
  (:require [interfaces.interfaces])
  (:import [expressions.expression Expression])
  (:import [interfaces.interfaces Evaluable])
  (:import [interfaces.interfaces Updateable])

)

(defn param_map [param_exp_list data]
    (str (into [] (map #(interfaces.interfaces/evaluate % data) param_exp_list)  ))
)

(defn new_count_map [current_values param_exp_list new-data]
  (assoc current_values (param_map param_exp_list new-data) (inc (get current_values (param_map param_exp_list new-data) 0))
  )
)

(defrecord Counter [count_rule]
  Evaluable
  (evaluate [this args]
    (if (contains? (:values (:count_rule this)) (str args))
      ((:values (:count_rule this))  (str args))
      0
    )
  )
  Updateable
  (updateexp [this data-name data-value]
    (if ((:fcond (:count_rule this)) data-name data-value)
      (new Counter {:fcond (:fcond (:count_rule this)) :fargs (:fargs (:count_rule this))
        :values (new_count_map (:values (:count_rule this)) (:fargs (:count_rule this)) {data-name data-value})}
      )
    )
  )
)
