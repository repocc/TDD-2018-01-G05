(ns interfaces.interfaces)

(defprotocol Executable
  (exec [this])
)

(defprotocol Evaluable
    (eval [this data])
)

(defprotocol Initializable
    (init [this])
)


;(defprotocol Updateable
;    (updateexp [this countName])
;)
