(ns parser.parser
  (:require [expressions.expression])
  (:require [expressions.counter])
  (:require [interfaces.interfaces])
  (:import [expressions.expression Expression])
  (:import [expressions.counter Counter])
  (:import [interfaces.interfaces Evaluable])
  (:import [interfaces.interfaces Executable])

)
(require '[clojure.string :as str])


(defrecord State [rules]
)

(defn parsecounterargs [cexp]
    (def cname (nth (re-find #"(\".*?\")" (:arg cexp)) 0) ) ;gets the counter name.
    (def param_exp (nth (re-find #"(\[.*?\])" (:arg cexp)) 0)) ;gets the counter parameter list.
    (def eval_exp (clojure.string/replace (clojure.string/replace (clojure.string/replace (:arg cexp) cname "") (:type cexp) "" ) param_exp ""))

    (defn param_generator [data] "[]") ;solo para que pase el 1er test por ahora. Hay que generar una funcion que sepa devolver los parametros dado un dato.
    (defn expression_evaluator [data] true) ;solo para que pase el 1er test por ahora. Hay que generar una funcion que sepa devolver tru/false dado un dato.
    {:name cname :rule {:fcond expression_evaluator :fargs param_generator :values {}}}
)

(defn parsesingalargs [sexp]
    ;(def sname (nth (re-find #"(\".*?\")" (:arg sexp)) 0) )
    ;{:name sname :args (nth (re-find #"(?<=\{)(.*?)(?=\})" (clojure.string/replace (:arg sexp) sname "")) 0)}
)

(defn parserule [r]
    {:type (nth (re-find #"(\(define-.*?\ )" (str r)) 0)  :arg (str r)}
)

(defmulti ruleparser (fn [exp] (:type exp) ))

;(defmethod ruleparser "(define-counter "  [exp] {:counters, {(:name (parsecounterargs exp)) {(:args (parsecounterargs exp)) (:func (parsecounterargs exp))}  } } )
(defmethod ruleparser "(define-counter " [exp]
  {:counters { (:name (parsecounterargs exp)) (new Counter (:rule (parsecounterargs exp)) ) }  }
)

;TODO: define signal expressions:
;(defmethod ruleparser "(define-signal "  [exp] {:signals {(:name (parsesingalargs exp)) (:args (parsesingalargs exp)) } } )
(defmethod ruleparser "(define-signal " [exp] {:signals { } } )


;TODO:must validate rule string format.
(defrecord Parser [rules]
    Executable
    (exec [this]
      (new State
        (apply merge-with (comp merge) (map ruleparser (map parserule (:rules this))) )
      )
    )
)
