(ns t.day24
  (:require [clojure.core.match :refer [match]]
            [clojure.set :as set]
            [clojure.walk :as walk]))

(defn parse
  [lines]
  (->> lines
       (map (fn [l] (let [[_ op arg1 _ arg2] (re-matches #"(...) (.)( (-?.+))?" l)]
                      [(keyword op) (keyword arg1) arg2])))
       (map (fn [[op arg1 arg2]]
              (if (= op :inp)
                [:inp arg1]
                [op arg1 (if (Character/isLetter ^Character (first arg2))
                           [:reg (keyword arg2)]
                           [:lit (Long/parseLong arg2)])])))))

(defn to-exprs
  [instrs]
  (->> instrs
       (partition-by (fn [[op arg]] (= op :inp)))
       (partition 2)
       (map (fn [[input ops]] (concat input ops)))
       (map
         (fn [ops]
           (reduce (fn [acc instr]
                     (match instr
                       [:inp r] (assoc acc r [:inp])
                       [:add _ [:lit 0]] acc
                       [:add r1 [:lit n]] (update acc r1 (fn [prev] [:add prev [:lit n]]))
                       [:add r1 [:reg r2]] (update acc r1 (fn [prev] [:add prev (acc r2)]))
                       [:mul r1 [:lit 0]] (assoc acc r1 [:lit 0])
                       [:mul r1 [:lit 1]] acc
                       [:mul r1 [:lit n]] (update acc r1 (fn [prev] [:mul prev [:lit n]]))
                       [:mul r1 [:reg r2]] (update acc r1 (fn [prev] [:mul prev (acc r2)]))
                       [:div r1 [:lit 1]] acc
                       [:div r1 [:lit n]] (update acc r1 (fn [prev] [:div prev [:lit n]]))
                       [:div r1 [:reg r2]] (update acc r1 (fn [prev] [:div prev (acc r2)]))
                       [:mod r1 [:lit n]] (update acc r1 (fn [prev] [:mod prev [:lit n]]))
                       [:mod r1 [:reg r2]] (update acc r1 (fn [prev] [:mod prev (acc r2)]))
                       [:eql r1 [:lit n]] (update acc r1 (fn [prev] [:eql prev [:lit n]]))
                       [:eql r1 [:reg r2]] (update acc r1 (fn [prev] [:eql prev (acc r2)]))))
                   {:w [:reg :w], :x [:reg :x], :y [:reg :y], :z [:reg :z]}
                   ops)))))

(defn remove-unneeded-registers
  [exprs]
  (->> exprs
       (map (fn [exprs]
              (assoc exprs :read (->> exprs
                                      (map val)
                                      (map (fn rec [v]
                                             (match v
                                               (:or [:inp]
                                                    [:lit _])
                                               #{}
                                               (:or [:add arg1 arg2]
                                                    [:mul arg1 arg2]
                                                    [:div arg1 arg2]
                                                    [:mod arg1 arg2]
                                                    [:eql arg1 arg2])
                                               (set/union (rec arg1) (rec arg2))
                                               [:reg r] #{r})))
                                      (reduce set/union)))))
       reverse
       (reduce (fn [[read-from-prev exprs] step]
                 [(:read step)
                  (cons (select-keys step read-from-prev) exprs)])
               [#{:z} ()])
       second))

(defn simplify-expr
  [expr]
  (walk/postwalk
    (fn [op] (match op
               [:add [:lit 0] exp] exp
               [:add exp [:lit 0]] exp
               [:eql [:eql e1 e2] [:lit 0]] [:eqn e1 e2]
               [:eql [:lit 0] [:eql e1 e2]] [:eqn e1 e2]
               :else op))
    expr))

(defmacro mdo
  [bindings body]
  (if (== 1 (count bindings))
    (throw
      (RuntimeException. "invalid number of elements in mdo bindings"))
    (if (empty? bindings)
      [:return body]
      (let [[n mv & bindings] bindings]
        [:bind mv `(fn [~n] (mdo ~bindings ~body))]))))

(defn to-bindings
  ([m] (to-bindings m {:bindings (), :rbindings {}}))
  ([m state]
   (match m
     [:return v] [v state]
     [:bind ma f] (let [[v state] (to-bindings ma state)]
                    (to-bindings (f v) state))
     [:expr expr & t] (if-let [sym (get (:rbindings state) expr)]
                        [sym state]
                        (let [sym (with-meta (symbol (str "r-" (count (:rbindings state))))
                                             (when (seq t)
                                               {:tag (first t)}))]
                          [sym (-> state
                                   (update :bindings concat [sym expr])
                                   (update :rbindings assoc expr sym))])))))

(defn compile-expr
  [expr]
  (let [h (fn rec [expr]
            (match expr
              [:reg r] (mdo [s1 [:expr `(get (get ~'state ~r) 0) "long"]
                             s2 [:expr `(get (get ~'state ~r) 1) "long"]]
                         [s1 s2])
              [:inp] (mdo [m [:expr `(get ~'input 0) "long"]
                           M [:expr `(get ~'input 1) "long"]]
                       [m M])
              [:lit n] (mdo []
                         [n n])
              [:add e1 e2] (mdo [[m1 M1] (rec e1)
                                 [m2 M2] (rec e2)
                                 s1 [:expr `(+ ~m1 ~m2)]
                                 s2 [:expr `(+ ~M1 ~M2)]]
                             [s1 s2])
              [:mul e1 e2] (mdo [[m1 M1] (rec e1)
                                 [m2 M2] (rec e2)
                                 r1 [:expr `(* ~m1 ~m2)]
                                 r2 [:expr `(* ~M1 ~M2)]]
                             [r1 r2])
              [:div e [:lit n]] (mdo [[m M] (rec e)
                                      s1 [:expr `(quot ~m ~n)]
                                      s2 [:expr `(quot ~M ~n)]]
                                  [s1 s2])
              [:mod e [:lit n]] (mdo [[m M] (rec e)
                                      s1 [:expr `(> (- ~M ~m) ~n)]
                                      s2 [:expr `(if ~s1 0 (rem ~m ~n))]
                                      s3 [:expr `(if ~s1 0 (rem ~M ~n))]
                                      s4 [:expr `(or ~s1 (> ~s2 ~s3))]
                                      s5 [:expr `(if ~s4 0 ~s2)]
                                      s6 [:expr `(if ~s4 (dec ~n) ~s3)]]
                                  [s5 s6])
              [:eqn e1 e2] (mdo [[m1 M1] (rec e1)
                                 [m2 M2] (rec e2)
                                 s1 [:expr `(== ~m1 ~M1 ~m2 ~M2)]
                                 s2 [:expr `(or (< ~M2 ~m1)
                                                (< ~M1 ~m2))]
                                 s3 [:expr `(if ~s2 1 0)]
                                 s4 [:expr `(if ~s1 0 1)]]
                             [s3 s4])))
        [result {:keys [bindings]}] (to-bindings (h expr))]
    (binding [*unchecked-math* :warn-on-boxed]
      (eval `(fn [~'state ~'input]
               (let [~@bindings]
                 ~result))))))

(comment

  (compile-expr
    [:div [:reg :z] [:lit 26]])

  (set! *unchecked-math* :warn-on-boxed)
  (set! *unchecked-math* false)

(def input (parse (clojure.string/split-lines (slurp "data/day24"))))

(->> input
     to-exprs
     remove-unneeded-registers
     simplify-expr
     (map :z)
     (map compile-expr)
     (map (fn [f]
            (f init-state [1 9]))))
([3 11] [17 25] [10 18] [1 9] [2 10] [13 21] [7 15] [7 15] [4 12] [6 14] [10 18] [4 12] [3 11] [4 12])

(def step
  (->> input
       to-exprs
       remove-unneeded-registers
       simplify-expr
       (map :z)
       last))

(compute-range-expr step [1 9] init-state)
[4 12]

((compile-expr step) init-state [1 9])

((eval (compile-expr step)) init-state [1 9])
[4 12]


(compile-expr step)
(fn [state input]
  (let [r-0 (get (get state :z) 0)
        r-1 (get (get state :z) 1)
        r-2 (quot r-0 26)
        r-3 (quot r-1 26)
        r-4 (> (unchecked-subtract r-1 r-0) 26)
        r-5 (if r-4 0 (rem r-0 26))
        r-6 (if r-4 0 (rem r-1 26))
        r-7 (or r-4 (> r-5 r-6))
        r-8 (if r-7 0 r-5)
        r-9 (if r-7 (unchecked-dec 26) r-6)
        r-10 (unchecked-add r-8 -7)
        r-11 (unchecked-add r-9 -7)
        r-12 (get input 0)
        r-13 (get input 1)
        r-14 (== r-10 r-11 r-12 r-13)
        r-15 (or (< r-13 r-10) (< r-11 r-12))
        r-16 (if r-15 1 0)
        r-17 (if r-14 0 1)
        r-18 (unchecked-multiply 25 r-16)
        r-19 (unchecked-multiply 25 r-17)
        r-20 (unchecked-add r-18 1)
        r-21 (unchecked-add r-19 1)
        r-22 (unchecked-multiply r-2 r-20)
        r-23 (unchecked-multiply r-3 r-21)
        r-24 (unchecked-add r-12 3)
        r-25 (unchecked-add r-13 3)
        r-26 (unchecked-multiply r-24 r-16)
        r-27 (unchecked-multiply r-25 r-17)
        r-28 (unchecked-add r-22 r-26)
        r-29 (unchecked-add r-23 r-27)]
    [r-28 r-29]))




[4 12]

  )

(def init-state
  {:w [0 0], :x [0 0], :y [0 0], :z [0 0]})

(defn solve-expr
  [instr target reverse?]
  (let [split-exprs (->> instr
                         to-exprs
                         remove-unneeded-registers
                         (map (fn [step]
                                (->> step
                                     (map (fn [[reg expr]]
                                            [reg (-> expr
                                                     simplify-expr
                                                     compile-expr)]))
                                     (into {})))))
        h (fn rec [state exprs input-so-far]
            (let [[m M] (:z (reduce (fn [state expr]
                                      (->> expr
                                           (map (fn [[reg f]]
                                                  [reg (f state [1 9])]))
                                           (into {})))
                                    state
                                    exprs))]
              (cond (and (empty? exprs) (== target m M))
                    input-so-far
                    (or (empty? exprs) (not (<= m target M)))
                    nil
                    :else
                    (->> (range 9)
                         (map inc)
                         ((fn [s] (if reverse? (reverse s) s)))
                         (some (fn [next-input]
                                 (rec (->> (first exprs)
                                           (map (fn [[reg f]]
                                                  [reg (f state [next-input next-input])]))
                                           (into {}))
                                      (rest exprs)
                                      (+ (* 10 input-so-far) next-input))))))))]
    (h init-state split-exprs 0)))

(defn part1
  [input]
  (solve-expr input 0 true))

(defn part2
  [input]
  (solve-expr input 0 false))
