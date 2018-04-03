(ns parser.parser)
(require '[clojure.string :as str])

(defrecord State [rules])

(defn inner-str [s begin end]
    (clojure.string/replace (clojure.string/replace s begin "") end "")
)

(defn parsecounterargs [c]
    (filter #(not= % (nth c 0) c)) ;TODO:parse arguments + condition and return expression.
)

(defn parsesingalargs [s]
    (filter #(not= % (nth s 0) s)) ;TODO:parse data + condition and return expression.
)

(defn parserule [r]
    (def exp (str/split (inner-str r "(" ")") #" "))
    {:type (nth exp 0) :args  (filter #(not= % (nth exp 0)) exp)}
)

(defmulti ruleparser (fn [exp] (:type exp) ))

(defmethod ruleparser "define-counter"  [exp] {:counters, {(nth (:args exp) 0) (parsecounterargs (:args exp)) } } )

(defmethod ruleparser "define-signal"  [exp] {:signals,{(nth (:args exp) 0) (parsesingalargs (:args exp))}})

(defprotocol Executable
  (exec [this])
)

(defrecord Parser [rules]
    Executable
    (exec [this]
      (new State
        (apply merge-with (comp merge) (map ruleparser (map parserule (:rules this))) )
      )
    )
)
