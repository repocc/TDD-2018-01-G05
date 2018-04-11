(ns processor.expressions)
(require '[clojure.string :as str])

(declare functions)
(declare function-evaluator)

(defn eval-lit [lit restofexp data counters f]
  (if (empty? restofexp)
      lit
      (f lit restofexp data counters)
  )
)

(defmulti function-evaluator (fn [exp data counters f]
                                 (type (first exp))))

(defmethod function-evaluator java.lang.String [exp data counters f]
    (eval-lit (first exp) (rest exp) data counters f)
)

(defmethod function-evaluator java.lang.Number [exp data counters f]
    (eval-lit (first exp) (rest exp) data counters f)
)

(defmethod function-evaluator java.lang.Boolean [exp data counters f]
    (eval-lit (first exp) (rest exp) data counters f)
)

(defmethod function-evaluator clojure.lang.Symbol [exp data counters f]
    ((functions (first exp)) exp data counters)
)

(defmethod function-evaluator :default [exp data counters f]
    ;(println (str "hellooooou " exp))
    (if (empty? (rest exp))
      (function-evaluator (first exp) data counters f)
      (f (function-evaluator (first exp) data counters nil)
         (rest exp) data counters)
    )
)

(defn sum [x y data counters] (+ x (function-evaluator y data counters sum)))
(defn sub [x y data counters] (- x (function-evaluator y data counters sum)))
(defn prod [x y data counters] (* x (function-evaluator y data counters prod)))
(defn div [x y data counters]
  (/ x (function-evaluator y data counters prod))
)

(def functions
  {
   (symbol '+)
     (fn [exp data counters]
       (function-evaluator (rest exp) data counters sum)
     )
   (symbol '-)
     (fn [exp data counters]
        (function-evaluator (rest exp) data counters sub)
     )
   (symbol 'current)
     (fn [exp data counters]
        (data (first (rest exp)))
     )
   (symbol 'counter-value)
     (fn [exp data counters]
        ;(println (str "Looking for counter-value " (first (rest exp)) " " (last (rest exp)) " in " counters ))
        (def cname (first (rest exp)))
        (def params (last (rest exp)))
        (if  (not (contains? counters cname) )
          (throw (Exception. "Counter not found!"))
          ((counters cname) params)
        )
     )
   (symbol '/)
    (fn [exp data counters]
      (function-evaluator (rest exp) data counters div)
    )
  }
)
