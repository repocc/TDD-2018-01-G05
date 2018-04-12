(ns processor.data-processor
  (:require [processor.state :refer :all])
  (:require [protocols.protocols])
  (:import [protocols.protocols Evaluable])
  (:import [protocols.protocols Countable])
)

; Initialice the processor with rules
;
; @params : Rules list of rules
(defn initialize-processor [rules]
  (get-init-state rules)
)

; Processes the data
;
; @params : state
;           new-data
(defn process-data [state new-data]
  (protocols.protocols/evaluate state new-data)
)

; 
;
;
(defn query-counter [state counter-name counter-args]
  (protocols.protocols/count state counter-name counter-args)
)
