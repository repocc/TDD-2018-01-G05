(ns processor.processor
  (:require [parser.parser])
  (:import [parser.parser Parser])
)

(defprotocol Initializable
    (init [this])
)

(defrecord Processor [rules]
    Initializable
    (init [this]
         (parser.parser/exec (Parser. rules))
    )
)
