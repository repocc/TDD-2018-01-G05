(ns parser.parser
  (:require [expressions.expression])
  (:import [expressions.expression Expression])
)
(require '[clojure.string :as str])

(defrecord State [rules])

(defn parsecounterargs [cexp]
    (def cname (nth (re-find #"(\".*?\")" (:arg cexp)) 0) ) ;gets the counter name.
    ;(def arg (nth (re-find #"(?<=\[)(.*?)(?=\])" (:arg cexp)) 0) ) ;gets the counter parameter list (This one removes the [])
    (def arg (nth (re-find #"(\[.*?\])" (:arg cexp)) 0)) ;gets the counter parameter list.
    (def eval_exp (clojure.string/replace (clojure.string/replace (clojure.string/replace (:arg cexp) cname "") (:type cexp) "" ) arg ""))
    {:name cname :args arg :func (new Expression eval_exp)}
)

(defn parsesingalargs [sexp]
    (def sname (nth (re-find #"(\".*?\")" (:arg sexp)) 0) )
    {:name sname :args (nth (re-find #"(?<=\{)(.*?)(?=\})" (clojure.string/replace (:arg sexp) sname "")) 0)}
)

(defn parserule [r]
    {:type (nth (re-find #"(\(define-.*?\ )" (str r)) 0)  :arg (str r)}
)

(defmulti ruleparser (fn [exp] (:type exp) ))

(defmethod ruleparser "(define-counter "  [exp] {:counters, {(:name (parsecounterargs exp)) {(:args (parsecounterargs exp)) (:func (parsecounterargs exp))}  } } )

;TODO: define signal expressions:
(defmethod ruleparser "(define-signal "  [exp] {:signals {(:name (parsesingalargs exp)) (:args (parsesingalargs exp)) } } )

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
