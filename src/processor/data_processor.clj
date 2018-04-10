(ns processor.data-processor
  (:require [processor.state])
  (:require [protocols.protocols])
  (:import [processor.state State])
  (:import [protocols.protocols Evaluable])
  (:import [protocols.protocols Countable])
)

(defn initialize-processor [rules]
  (new State rules {:history {} :count {}})
)

(defn process-data [state new-data]
  (protocols.protocols/evaluate state new-data)
)
  ;[nil []])

(defn query-counter [state counter-name counter-args]
  (protocols.protocols/count state counter-name counter-args)
)
