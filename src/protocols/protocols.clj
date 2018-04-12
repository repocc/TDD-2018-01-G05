(ns protocols.protocols)

; Signature of data that may be evaluable
(defprotocol Evaluable
  (evaluate [this data])
)

; Signature of a counter
(defprotocol Countable
  (count [this counter-name counter-args])
)

; Signature of initialization
(defprotocol Initialize
  (initialize [this])
)
