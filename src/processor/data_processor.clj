(ns data-processor
  (:require [processor.state])
  (:import [processor.state Evaluable])
  (:import [processor.state State])
)

(defn initialize-processor [rules]
  (new State rules)
)

(defn process-data [state new-data]
  (processor.state/evaluate state new-data)
)
  ;[nil []])

(defn query-counter [state counter-name counter-args]
  0)
