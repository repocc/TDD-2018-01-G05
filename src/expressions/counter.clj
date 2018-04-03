(ns expressions.counter)

;TODO:unify interfaces.
(defprotocol Evaluable
    (eval [this])
)

(defprotocol Updateable
    (updateexp [this countName])
)

(defrecord Counter [count]
    Evaluable
    (eval [this countName]
        (get counter countName) ;TODO: if counter doesn't exist an error must be raised.
    )
    Updateable
    (updateexp [this countName]
        (if (contains? (:count this) countName)
          (update (:count this) countName inc)
          (assoc (:count this) countName 0)
        )

    )
)
