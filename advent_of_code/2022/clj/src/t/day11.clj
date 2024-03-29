(ns t.day11
  (:require [clojure.core.match :refer [match]]
            [instaparse.core :as insta]
            [t.lib :as lib :refer [->long]]))

(def parser
  (insta/parser
    "<S> := <ws> index <ws> items <ws> op <ws> div
    index := <'Monkey '> num <':'>
    items := <'Starting items: '> num (<', '> num)*
    op := <'Operation: new = old '> ('+' | '*') <' '> ('old' | num)
    div := <'Test: divisible by '> num <if-true> num <if-false> num
    if-true := <ws> 'If true: throw to monkey' <ws>
    if-false := <ws> 'If false: throw to monkey ' <ws>
    <num> := #'-?\\d+'
    ws := (#'\\s+' | '\\n')*"))

(defn parse
  [lines]
  (->> lines
       (partition-by #(= % ""))
       (remove #{[""]})
       (mapv (fn [lines]
               (->> (parser (apply str lines))
                    (reduce (fn [acc v]
                              (match v
                                [:index n] (assoc acc :index (->long n))
                                [:items & s] (assoc acc :items (map ->long s))
                                [:op op p] (assoc acc :op (eval `(fn [~'old]
                                                                   (~({"+" +, "*" *} op)
                                                                           ~'old
                                                                           ~(if (= "old" p)
                                                                              'old
                                                                              (->long p))))))
                                [:div d t f] (-> acc
                                                 (assoc :div (->long d))
                                                 (assoc :throw-to (eval `(fn [~'w]
                                                                           (if (zero? (mod ~'w ~(->long d)))
                                                                             ~(->long t)
                                                                             ~(->long f))))))))
                            {:activity 0})
                    ((juxt :index :items :op :div :throw-to :activity)))))))

(defn solve
  [input restrict-fn max-iter]
  (loop [n 0
         i 0
         monkeys input]
    (cond (== n max-iter)
          (->> monkeys
               (map #(get % 5))
               sort
               reverse
               (take 2)
               (apply *))
          (== i (count monkeys))
          (recur (inc n) 0 monkeys)
          :else
          (let [[index items op _ throw-to] (get monkeys i)]
            (recur n
                   (inc i)
                   (reduce (fn [monkeys item]
                             (let [w (restrict-fn (op item))
                                   t (throw-to w)]
                               (update-in monkeys [t 1] conj w)))
                           (-> monkeys
                               (update-in [index 5] + (count items))
                               (assoc-in [index 1] []))
                           items))))))

(defn part1
  [input]
  (solve input #(quot % 3) 20))

(defn part2
  [input]
  (let [d (->> input (map #(get % 3)) (reduce *))]
    (solve input #(mod % d) 10000)))

(lib/check
  [part1 sample] 10605
  [part1 puzzle] 117624
  [part2 sample] 2713310158
  [part2 puzzle] 16792940265)
