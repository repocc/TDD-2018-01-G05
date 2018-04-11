(ns processor.expressions)
(require '[clojure.string :as str])

(declare functions)
(declare function-evaluator)

(defn eval-lit [lit restofexp data f]
  (if (empty? restofexp)
      lit
      (f lit restofexp data)
  )
)

(defmulti function-evaluator (fn [exp data f]
                                 (type (first exp))))

(defmethod function-evaluator java.lang.String [exp data f]
    (eval-lit (first exp) (rest exp) data f)
)

(defmethod function-evaluator java.lang.Number [exp data f]
    (eval-lit (first exp) (rest exp) data f)
)

(defmethod function-evaluator java.lang.Boolean [exp data f]
    (eval-lit (first exp) (rest exp) data f)
)

(defmethod function-evaluator clojure.lang.Symbol [exp data f]
    ((functions (first exp)) exp data)
)

(defmethod function-evaluator :default [exp data f]
    (if (empty? (rest exp))
      (function-evaluator (first exp) data f)
      (f (function-evaluator (first exp) data nil)
         (rest exp) data)
    )
)

(defn sum [x y data] (+ x (function-evaluator y data sum)))
(defn sub [x y data] (- x (function-evaluator y data sum)))

(def functions
  {
   (symbol '+)
   (fn [exp data]
     (println "+")
     (function-evaluator (rest exp) data sum)
   )
   (symbol '-)
     (fn [exp data]
        (println "-")
        (function-evaluator (rest exp) data sub)
     )
   (symbol 'current)
   (fn [exp data]
      ;(println (str "current" " " data (rest exp)))
      (data (first (rest exp)))
   )
  }
)

;CASOS TESTEADOS:
;(function-evaluator '(- 10 1) {} nil)
;(function-evaluator '(+ 10 1 (- 4 3)) {} nil)
;(function-evaluator '(- 10 1 (+ 1 1) (+ 1 1)) {} nil)
