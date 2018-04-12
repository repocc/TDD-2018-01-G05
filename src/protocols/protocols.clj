(ns protocols.protocols)

; Signature of thata that may be evaluable
(defprotocol Evaluable
  (evaluate [this data])
)

; Signature
(defprotocol Countable
  (count [this counter-name counter-args])
)


(defprotocol Initialize
  (initialize [this])
)
