(ns interfaces.interfaces)

(defprotocol Executable
  (exec [this])
)

(defprotocol Evaluable
    (evaluate [this data])
)

(defprotocol Initializable
    (init [this])
)

(defprotocol Updateable
    (updateexp [this data-name data-value])
)
