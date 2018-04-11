(ns protocols.protocols)

(defprotocol Evaluable
  (evaluate [this data])
)

(defprotocol Countable
  (count [this counter-name counter-args])
)

(defprotocol Initialize
  (initialize [this])
)
