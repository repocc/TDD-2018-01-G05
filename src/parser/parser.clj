(ns parser.parser
  (:require [expressions.expression])
  (:require [expressions.counter])
  (:require [interfaces.interfaces])
  (:import [expressions.expression Expression])
  (:import [expressions.expression Function])
  (:import [expressions.expression State])
  (:import [expressions.counter Counter])
  ;(:import [interfaces.interfaces Evaluable])
  (:import [interfaces.interfaces Executable])
)
(require '[clojure.string :as str])



(defn parse-expression-list [exp-list]
  ;Returns a vector with the parameter expressions.
  (if (empty? (first exp-list))
    (into [] nil)

    (if (str/ends-with? (nth exp-list 0) ")")
        (into [] (new Expression (str/replace (nth exp-list 0) ")" "")))
        (if (str/starts-with? (nth exp-list 0) "(")
          (into [] (new Function (subs (nth exp-list 0) 1) (parse-expression-list (pop exp-list)))) ;build function receives operator + expression list
          (into [] (concat (into [] (new Expression (nth exp-list 0))) (parse-expression-list (pop exp-list)) ))
        )
    )
  )
)

(defn parsecounterargs [cexp]
    (def cname (str/replace (nth (re-find #"(\".*?\")" (:arg cexp)) 0) "\"" "")) ;gets the counter name.
    (def param_exp (str/split (nth (re-find #"(?<=\[)(.*?)(?=\])" (:arg cexp)) 0) #" ") ) ;gets the counter parameter list.
    ;(def eval_exp (str/replace (str/replace (str/replace (:arg cexp) cname "") (:type cexp) "") param_exp "") )

    (defn expression_evaluator [data-name data-value] true) ;TODO: parse eval_exp to generate evaluable expression.

    {:name cname :rule {:fcond expression_evaluator :fargs (parse-expression-list param_exp) :values {} } }

)

(defn parsesingalargs [sexp]
    ;TODO: define signal expressions:
    ;(def sname (nth (re-find #"(\".*?\")" (:arg sexp)) 0) )
    ;{:name sname :args (nth (re-find #"(?<=\{)(.*?)(?=\})" (clojure.string/replace (:arg sexp) sname "")) 0)}
)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defmulti ruleparser (fn [exp] (:type exp) ))

(defmethod ruleparser "(define-counter " [exp]
  {:counters { (:name (parsecounterargs exp)) (new Counter (:rule (parsecounterargs exp)) ) }  }
)

(defmethod ruleparser "(define-signal " [exp] {:signals { } } )

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn parserule [r]
    {:type (re-find #"\(define-.*?\ " (str r))  :arg (str r)}
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
