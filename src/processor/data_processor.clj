(ns processor.data-processor
  (:require [processor.state :refer :all])
  (:require [protocols.protocols])
  (:import [protocols.protocols Evaluable])
  (:import [protocols.protocols Countable])
)

(defn initialize-processor [rules]
  (get-init-state rules)
)

(defn process-data [state new-data]
  [(protocols.protocols/evaluate state new-data) []]
)

(defn query-counter [state counter-name counter-args]
  ;(println (str "state at count " state))
  (protocols.protocols/count state counter-name counter-args)
)
