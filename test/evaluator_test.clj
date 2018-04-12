(ns evaluator-test
  (:require [clojure.test :refer :all]
            [processor.expressions :refer :all]))

(deftest evaluate-lit-bool
    (testing "Evaluator boolean literal returns literal value."
        (is  (function-evaluator '( true ) {"spam" true} {} {} nil)
              )))

(deftest evaluate-current
    (testing "Evaluator current returns data value"
        (is  (function-evaluator '(current "spam") {"spam" true} {} {} nil)
              )))

(deftest evaluate-counter-value
    (testing "Evaluator counter-value returns counter value"
        (is (= 1 (function-evaluator '(counter-value "email-count" []) {"spam" true} {"email-count" {[] 1}} {} nil)
              ))))

;Number operators tests:
(deftest evaluate-simple-division
    (testing "Evaluator division operation with only one divisor test."
        (is (= 20 (function-evaluator '(/ 100 5) {"spam" true} {} {} nil)
            ))))

(deftest evaluate-mult-division
    (testing "Evaluator division operation with multiple divisors test."
        (is (= 10 (function-evaluator '(/ 100 5 2) {"spam" true} {} {} nil)
            ))))

(deftest evaluate-division-with-counters
    (testing "Evaluator division operation with counter values test."
        (is (= 4 (function-evaluator '(/ (counter-value "spam-count" []) (counter-value "email-count" []))
         {"spam" true} {"spam-count" {[] 20}  "email-count" {[] 5}} {} nil)
            ))))

(deftest zero-division-error-on-zero-division
   (testing "Processing zero division throws exception."
       (is (thrown? Exception (function-evaluator '(/ (counter-value "spam-count" []) (counter-value "email-count" []))
        {"spam" true} {"spam-count" {[] 20}  "email-count" {[] 0}} {} nil)
            ))))

(deftest mod-returns-division-modulus
   (testing "Mod returns division modulus."
       (is (= 0.5 (function-evaluator '(mod 1.5 1) {} {} {} nil)
            ))))

(deftest less-than-returns-true-with-ordered-literals
   (testing "Less than returns true when first literal is smaller than second."
       (is (= true (function-evaluator '(< 1 2) {} {} {} nil)
            ))))

(deftest less-than-returns-true-with-monotonical-sequence
   (testing "Less than returns true with monotonically increasing sequence."
       (is (= true (function-evaluator '(< 1 2 3 4 5) {} {} {} nil)
            ))))

(deftest less-than-returns-false-with-non-monotonical-sequence
   (testing "Less than returns false with non increasing sequence."
       (is (= false (function-evaluator '(< 1 2 7 4 5) {} {} {} nil)
            ))))

(deftest less-than-equal-returns-true-with-ordered-literals
   (testing "Less than equal returns true when first literal is smaller than or equal to second."
       (is (= true (function-evaluator '(<= 1 2) {} {} {} nil)
            ))))

(deftest less-than-equal-returns-true-with-monotonical-sequence
   (testing "Less than equal returns true with monotonically increasing sequence."
       (is (= true (function-evaluator '(<= 1 2 2 4 5 5) {} {} {} nil)
            ))))

(deftest less-than-equal-returns-false-with-non-monotonical-sequence
   (testing "Less than equal returns false with non increasing sequence."
       (is (= false (function-evaluator '(<= 1 2 7 4 5) {} {} {} nil)
            ))))

(deftest greater-than-returns-true-with-monotonical-sequence
   (testing "Greater than returns true with monotonically decreasing sequence."
       (is (= true (function-evaluator '(> 6 4 2 1) {} {} {} nil)
            ))))

(deftest greater-than-returns-false-with-non-monotonical-sequence
   (testing "Greater than returns false with non decreasing sequence."
       (is (= false (function-evaluator '(> 8 4 9 2) {} {} {} nil)
            ))))

(deftest greater-than-equal-returns-true-with-monotonical-sequence
   (testing "Greater than equal returns true with monotonically decreasing sequence."
       (is (= true (function-evaluator '(>= 6 4 1 1 0) {} {} {} nil)
            ))))

(deftest greater-than-equal-returns-false-with-non-monotonical-sequence
   (testing "Greater than equal returns false with non decreasing sequence."
       (is (= false (function-evaluator '(>= 8 4 9 2) {} {} {} nil)
            ))))

;Equality operators tests:
(deftest equal-applied-to-literals
  (testing "Equal returns true if equal literals compared."
       (is (= true (function-evaluator '(= 1 1) {} {} {} nil) ))
           ))

(deftest equal-applied-to-seq-true-if-contained
   (testing "Equal returns true if value contained in sequence."
     (is (= true (function-evaluator '(= 1 (past "value")) {} {} {"value" [2 3 1 4]} nil) ))
         ))

(deftest not-equal-applied-to-different-vals-is-true
   (testing "Not equal returns true if non-equal values are compared."
     (is (= true (function-evaluator '(!= 1 2) {} {} {} nil) ))
        ))

;Boolean operators tests:
(deftest or-applied-to-multiple-vals-ors-all
   (testing "Or applied to sequence of booleans returns equivalent of applying or to all of them."
     (is (= true (function-evaluator '(or false false false true) {} {} {} nil) ))
        ))

(deftest and-applied-to-multiple-vals-ors-all
   (testing "And applied to sequence of booleans returns equivalent of applying and to all of them."
     (is (= false (function-evaluator '(and true true true false) {} {} {} nil) ))
        ))

(deftest not-applied-to-boolean-returns-opposite
   (testing "Not negates boolean evaluation."
     (is (= true (function-evaluator '(not (and true true true false)) {} {} {} nil) ))
        ))

(deftest not-applied-to-boolean-returns-opposite
   (testing "Not negates boolean evaluation."
     (is (= true (function-evaluator '(not (and true true true false)) {} {} {} nil) ))
        ))

;String operator tests
(deftest concat-returns-expression-str
   (testing "Concat returns the string associated to an expression."
     (is (= "false" (function-evaluator '(concat (and true true true false)) {} {} {} nil) ))
        ))

(deftest include-returns-true-if-substr-present
   (testing "include? returns true if string contains substring"
     (is (= true (function-evaluator '(includes? "hello" "el") {} {} {} nil) ))
        ))

(deftest include-returns-false-if-substr-present
   (testing "include? returns true if string contains substring"
     (is (= false (function-evaluator '(includes? "hello" "pe") {} {} {} nil) ))
          ))

(deftest starts-with-true-if-begins-with-substr
   (testing "starts-with? returns true if string begins with substring"
       (is (= true (function-evaluator '(starts-with? "hello" "he") {} {} {} nil) ))
          ))

(deftest starts-with-false-if-not-begins-with-substr
   (testing "starts-with? returns false if string does not begin with substring"
       (is (= false (function-evaluator '(starts-with? "hello" "wo") {} {} {} nil) ))
          ))

(deftest ends-with-true-if-ends-with-substr
   (testing "ends-with? returns true if string ends with substring"
       (is (= true (function-evaluator '(ends-with? "hello" "llo") {} {} {} nil) ))
            ))
