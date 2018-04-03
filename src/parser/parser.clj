(ns parser.parser)
(require '[clojure.string :as str])

(defrecord State [rules])

(defn inner-str [s]
    (subs s 1 (- (count s) 1 ))
)

(defn parsecounterargs [c]
    (clojure.string/replace (clojure.string/join #" " c) (nth c 0) "") ;TODO:parse arguments + condition and return expression.
)

(defn parsesingalargs [s]
    (filter #(not= % (nth s 0) s)) ;TODO:parse data + condition and return expression.
)

(defn parserule [r]
    (def exp (str/split (inner-str (str r) ) #" "))
    {:type (nth exp 0) :args  (filter #(not= % (nth exp 0)) exp)}
)

(defmulti ruleparser (fn [exp] (:type exp) ))

(defmethod ruleparser "define-counter"  [exp] {:counters, {(nth (:args exp) 0) (parsecounterargs (:args exp)) } } )

(defmethod ruleparser "define-signal"  [exp] {:signals,{(nth (:args exp) 0) (parsesingalargs (:args exp))}})

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
