(ns parser.parser)
(require '[clojure.string :as str])

(defrecord State [rules])

(defn parsecounterargs [c]
    {:name (nth (re-find #"(\".*?\")" c) 0) :args (nth (re-find #"(?<=\[)(.*?)(?=\])" c) 0)}
)

(defn parsesingalargs [s]
    ;(filter #(not= % (nth s 0) s)) ;TODO:parse data + condition and return expression.
    s
)

(defn parserule [r]
    {:type (nth (re-find #"(\(define-.*?\ )" (str r)) 0)  :arg (str r)}
)

(defmulti ruleparser (fn [exp] (:type exp) ))

(defmethod ruleparser "(define-counter "  [exp] {:counters, {(:name (parsecounterargs (:arg exp))) (:args (parsecounterargs (:arg exp)))  } } )

(defmethod ruleparser "(define-signal "  [exp] {:signals (parsesingalargs (:arg exp))} )

(defprotocol Executable
  (exec [this])
)

;TODO:must validate rule string format.
(defrecord Parser [rules]
    Executable
    (exec [this]
      (new State
        (apply merge-with (comp merge) (map ruleparser (map parserule (:rules this))) )
      )
    )
)
