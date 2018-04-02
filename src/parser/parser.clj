(ns parser.parser)
(require '[clojure.string :as str])

(defprotocol Executable
  (exec [this])
)

(defn parserule [r]
    (first (str/split (str r) #" "))
)


(defmulti ruleparser (fn [exp] exp))

(defmethod ruleparser "(define-counter" [exp] "Is counter" )

(defmethod ruleparser "(define-signal" [exp] "Is signal" )


(defrecord Parser [rules]
    Executable
    (exec [this]
      (for [r (:rules this) :let [y (parserule r)] ] (ruleparser y))
    )
)
