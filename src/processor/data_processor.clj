(ns processor.data-processor
  (:require [processor.processor])
  (:import [processor.processor Processor])
  (:require [interfaces.interfaces])
  (:import [interfaces.interfaces Evaluable])
  (:import [interfaces.interfaces Initializable])
  (:import [interfaces.interfaces Updateable])
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
  [
    ;(reduce-kv #(interfaces.interfaces/updateexp state  %2 %3) state new-data)
    (interfaces.interfaces/updateexp state "spam" "true")
    []
  ]
)
  ;[nil []])

(defn query-counter [state counter-name counter-args]
  (if (contains? (:counters (:rules state)) counter-name)
    (interfaces.interfaces/evaluate ((:counters (:rules state)) counter-name) counter-args) ;TODO: move this.
    0
  )
)
