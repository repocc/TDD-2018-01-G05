(ns interfaces.interfaces)

(defprotocol Executable
  (exec [this])
)

(defprotocol Evaluable
    (eval [this data])
)

;(defprotocol Updateable
;    (updateexp [this countName])
;)
