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

; Recursively evaluates an expression given a String
(defmethod function-evaluator java.lang.String [exp data counters history f]
    (eval-lit (first exp) (rest exp) data counters history f)
)

; Recursively evaluates an expression given a Number
(defmethod function-evaluator java.lang.Number [exp data counters history f]
    (eval-lit (first exp) (rest exp) data counters history f)
)

; Recursively evaluates an expression given a Boolean
(defmethod function-evaluator java.lang.Boolean [exp data counters history f]
    (eval-lit (first exp) (rest exp) data counters history f)
)


; Recursively evaluates an expression given a Symbol,
; Possible symbols
; +
; -
; /
; <
; >
; =
; !=
; or
; not
; and
; current
; counter-value
; past
; concat
(defmethod function-evaluator clojure.lang.Symbol [exp data counters history f]
    ((functions (first exp)) exp data counters history)
)

; Recursively evaluates an expression given default
(defmethod function-evaluator :default [exp data counters history f]
    (if (empty? (rest exp))
      (function-evaluator (first exp) data counters history f)
      (f (function-evaluator (first exp) data counters history nil)
         (rest exp) data counters history)
    )
)

; Define native operations and wrapps theme to clojure operations
(defn sum [x y data counters history] (+ x (function-evaluator y data counters history sum)))
(defn sub [x y data counters history] (- x (function-evaluator y data counters history sum)))
(defn prod [x y data counters history] (* x (function-evaluator y data counters history prod)))
(defn div [x y data counters history] (/ x (function-evaluator y data counters history prod)))
(defn my-or [x y data counters history]  (or x (function-evaluator y data counters history my-or)))
(defn my-and [x y data counters history]  (and x (function-evaluator y data counters history my-and)))
(defn my-mod [x y data counters history]  (mod x (function-evaluator y data counters history nil)))
(defn includes [x y data counters history]  (str/includes? x (function-evaluator y data counters history nil)))
(defn starts-with [x y data counters history]  (str/starts-with? x (function-evaluator y data counters history nil)))
(defn ends-with [x y data counters history]  (str/ends-with? x (function-evaluator y data counters history nil)))
(defn cmp-lt [x y data counters history]
    (if (= (count y) 1)
      (< x (first y))
      (and (function-evaluator y data counters history cmp-lt) (< x (first y)))
    )
  )
(defn cmp-lte [x y data counters history]
    (if (= (count y) 1)
      (<= x (first y))
      (and (function-evaluator y data counters history cmp-lte) (<= x (first y)))
    )
)
(defn cmp-gt [x y data counters history]
    (if (= (count y) 1)
      (> x (first y))
      (and (function-evaluator y data counters history cmp-gt) (> x (first y)))
    )
  )
(defn cmp-gte [x y data counters history]
    (if (= (count y) 1)
      (>= x (first y))
      (and (function-evaluator y data counters history cmp-gte) (>= x (first y)))
    )
)

(defn equal [x y data counters history]
   (if (vector? (function-evaluator y data counters history nil))
    (some #(= x %) (function-evaluator y data counters history nil))
    (= x (function-evaluator y data counters history nil))
   )
)
(defn not-equal [x y data counters history]
   (if (vector? (function-evaluator y data counters history nil))
    (some #(not= x %) (function-evaluator y data counters history nil))
    (not= x (function-evaluator y data counters history nil))
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
   (symbol '/)
    (fn [exp data counters history]
        (function-evaluator (rest exp) data counters history div)
    )
   (symbol 'mod)
    (fn [exp data counters history]
        (function-evaluator (rest exp) data counters history my-mod)
    )
   (symbol '<)
     (fn [exp data counters history]
         (function-evaluator (rest exp) data counters history cmp-lt)
     )
   (symbol '<=)
     (fn [exp data counters history]
         (function-evaluator (rest exp) data counters history cmp-lte)
     )
   (symbol '>)
     (fn [exp data counters history]
         (function-evaluator (rest exp) data counters history cmp-gt)
     )
   (symbol '>=)
     (fn [exp data counters history]
         (function-evaluator (rest exp) data counters history cmp-gte)
     )
   (symbol '=)
    (fn [exp data counters history]
        (function-evaluator (rest exp) data counters history equal)
    )
   (symbol '!=)
     (fn [exp data counters history]
         (function-evaluator (rest exp) data counters history not-equal)
     )
   (symbol 'or)
     (fn [exp data counters history]
         (function-evaluator (rest exp) data counters history my-or)
     )
   (symbol 'not)
     (fn [exp data counters history]
        (not (function-evaluator (rest exp) data counters history nil))
     )
   (symbol 'and)
     (fn [exp data counters history]
         (function-evaluator (rest exp) data counters history my-and)
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
   (symbol 'past)
    (fn [exp data counters history]
      (def data-name (first (rest exp)))
      (if (or (not (contains? history data-name)) (empty? (history data-name)))
        (throw (Exception. "Referenced past value not found!"))
        (history data-name)
      )
    )
  (symbol 'concat)
      (fn [exp data counters history]
          (str (function-evaluator (rest exp) data counters history nil))
      )
  (symbol 'includes?)
     (fn [exp data counters history]
          (function-evaluator (rest exp) data counters history includes)
     )
 (symbol 'starts-with?)
     (fn [exp data counters history]
         (function-evaluator (rest exp) data counters history starts-with)
      )
 (symbol 'ends-with?)
    (fn [exp data counters history]
         (function-evaluator (rest exp) data counters history ends-with)
     )
  }
)
