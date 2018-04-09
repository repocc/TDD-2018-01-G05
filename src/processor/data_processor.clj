(ns processor.data-processor
  (:require [processor.processor])
  (:import [processor.processor Processor])
  (:require [interfaces.interfaces])
  (:import [interfaces.interfaces Evaluable])
  (:import [interfaces.interfaces Initializable])
)


(defn initialize-processor
      "Receives a set of rules.
      Returns a state s0."
      [rules]
      (interfaces.interfaces/init
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
  (if (contains? (:counters (:rules state)) counter-name)
    (interfaces.interfaces/eval (get (:counters (:rules state)) counter-name) counter-args) ;TODO: move this.
    0
  )
)
