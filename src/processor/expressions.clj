(ns processor.expressions)
(require '[clojure.string :as str])

(declare functions)
(declare function-evaluator)

(defn eval-lit [lit restofexp data counters history f]
  (if (empty? restofexp)
      lit
      (f lit restofexp data counters history)
  )
)

(defmulti function-evaluator (fn [exp data counters history f]
                                 (type (first exp))))

(defmethod function-evaluator java.lang.String [exp data counters history f]
    (eval-lit (first exp) (rest exp) data counters history f)
)

(defmethod function-evaluator java.lang.Number [exp data counters history f]
    (eval-lit (first exp) (rest exp) data counters history f)
)

(defmethod function-evaluator java.lang.Boolean [exp data counters history f]
    (eval-lit (first exp) (rest exp) data counters history f)
)

(defmethod function-evaluator clojure.lang.Symbol [exp data counters history f]
    ;(println (str "Evaluate " (first exp)))
    ((functions (first exp)) exp data counters history)
)

(defmethod function-evaluator :default [exp data counters history f]
    (if (empty? (rest exp))
      (function-evaluator (first exp) data counters history f)
      (f (function-evaluator (first exp) data counters history nil)
         (rest exp) data counters history)
    )
)

(defn sum [x y data counters history] (+ x (function-evaluator y data counters history sum)))
(defn sub [x y data counters history] (- x (function-evaluator y data counters history sum)))
(defn prod [x y data counters history] (* x (function-evaluator y data counters history prod)))
(defn div [x y data counters history] (/ x (function-evaluator y data counters history prod)))
(defn equal [x y data counters history]
   (if (vector? (function-evaluator y data counters history nil))
    (some #(= x %) (function-evaluator y data counters history nil))
    (= x (function-evaluator y data counters history nil))
   )
)

(def functions
  {
   (symbol '+)
     (fn [exp data counters history]
       (function-evaluator (rest exp) data counters history sum)
     )
   (symbol '-)
     (fn [exp data counters history]
        (function-evaluator (rest exp) data counters history sub)
     )
   (symbol 'current)
     (fn [exp data counters history]
        (data (first (rest exp)))
     )
   (symbol 'counter-value)
     (fn [exp data counters history]
        (def cname (first (rest exp)))
        (def params (last (rest exp)))
        (if  (not (contains? counters cname) )
          (throw (Exception. "Counter not found!"))
          ((counters cname) params)
        )
     )
   (symbol '/)
    (fn [exp data counters history]
      (function-evaluator (rest exp) data counters history div)
    )
   (symbol '=)
    (fn [exp data counters history]
      (function-evaluator (rest exp) data counters history equal)
    )
   (symbol 'past)
    (fn [exp data counters history]
      (def data-name (first (rest exp)))
      (if (or (not (contains? history data-name)) (empty? (history data-name)))
        (throw (Exception. "Referenced past value not found!"))
        (history data-name)
      )
      ;(println (str "Evaluated past " (history data-name)))
    )
  }
)
