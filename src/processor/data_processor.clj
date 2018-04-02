(ns processor.data-processor
  (:require [processor.processor])
  (:import [processor.processor Processor])
)


(defn initialize-processor
      "Receives a set of rules.
      Returns a state s0."
      [rules]
      (processor.processor/init
        (new Processor rules)
      )
)

(defn process-data
  "Receives a current state 'state' and data 'new-data'.
  Returns a vector where the first element is the new state
  and the second element is the processed data."
  [state new-data]
  [nil []])

(defn query-counter [state counter-name counter-args]
  0)
