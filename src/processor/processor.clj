(ns processor.processor
  (:require [parser.parser])
  (:require [interfaces.interfaces])
  (:import [parser.parser Parser])
  (:import [interfaces.interfaces Executable])
  (:import [interfaces.interfaces Initializable])
)

(defrecord Processor [rules]
    Initializable
    (init [this]
         (interfaces.interfaces/exec (Parser. rules))
    )
)
