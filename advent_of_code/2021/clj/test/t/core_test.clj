(ns t.core-test
  (:require [clojure.test :refer [deftest]]
            [clojure.string :as string]
            [t.util :refer [transpose]]
            [t.day1 :as day1]
            [t.day2 :as day2]
            [t.day3 :as day3]
            [t.day4 :as day4]
            [t.day5 :as day5]
            [t.day6 :as day6]
            [t.day7 :as day7]))

(let [read (fn [s i] (string/split-lines (slurp (str "data/" s i))))]
  (defn sample [i] (read "sample" i))
  (defn data [i] (read "day" i)))

(defmacro is
  [form]
  `(let [form# (quote ~form)
         start# (System/currentTimeMillis)
         _# (clojure.test/is ~form)
         t# (- (System/currentTimeMillis) start#)]
     (when (> t# 100)
       (println (format "%d %s" t# (nth form# 2))))))

(defmacro make-tests
  [& expected]
  `(do
     ~@(->> expected
            (map (fn [spec]
                   (let [d (str "day" (:day spec))
                         parse (symbol d "parse")
                         part1 (symbol d "part1")
                         part2 (symbol d "part2")
                         sample `(sample ~(:day spec))
                         data `(data ~(:day spec))
                         check (fn [expected actual]
                                 (when expected
                                   `(is (= ~expected ~actual))))
                         samp-sym (gensym "sample")
                         data-sym (gensym "data")]
                     `(deftest ~(symbol d)
                        (let [~samp-sym (delay (~parse ~sample))
                              ~data-sym (delay (~parse ~data))]
                          ~(check (:sample spec)
                                  `@~samp-sym)
                          ~(check (get-in spec [:part1 0])
                                  `(~part1 @~samp-sym))
                          ~(check (get-in spec [:part1 1])
                                  `(~part1 @~data-sym))
                          ~(check (get-in spec [:part2 0])
                                  `(~part2 @~samp-sym))
                          ~(check (get-in spec [:part2 1])
                                  `(~part2 @~data-sym))))))))))

(make-tests

  {:day 1
   :sample [199 200 208 210 200 207 240 269 260 263]
   :part1 [7 1292]
   :part2 [5 1262]}

  {:day 2
   :sample [[:forward 5] [:down 5] [:forward 8] [:up 3] [:down 8] [:forward 2]]
   :part1 [150 1660158]
   :part2 [900 1604592846]}

  {:day 3
   :sample ["00100" "11110" "10110" "10111" "10101" "01111"
            "00111" "11100" "10000" "11001" "00010" "01010"]
   :part1 [198 4103154]
   :part2 [230 4245351]}

  {:day 4
   :sample {:numbers [7 4 9 5 11 17 23 2 0 14 21 24 10 16 13 6 15 25 12 22 18 20 8 19 3 26 1]
            :boards (->> [[[22 13 17 11 0]
                           [8 2 23 4 24]
                           [21 9 14 16 7]
                           [6 10  3 18  5]
                           [1 12 20 15 19]]

                          [[3 15  0  2 22]
                           [9 18 13 17  5]
                           [19  8  7 25 23]
                           [20 11 10 24  4]
                           [14 21 16 12  6]]

                          [[14 21 17 24  4]
                           [10 16 15  9 19]
                           [18  8 23 26 20]
                           [22 11 13  6  5]
                           [2  0 12  3  7]]]
                         (map (fn [b] (->> (concat b (transpose b))
                                           (map set)))))}
   :part1 [4512 82440]
   :part2 [1924 20774]}

  {:day 5
   :sample [[0 9 5 9]
            [8 0 0 8]
            [9 4 3 4]
            [2 2 2 1]
            [7 0 7 4]
            [6 4 2 0]
            [0 9 2 9]
            [3 4 1 4]
            [0 0 8 8]
            [5 5 8 2]]
   :part1 [5 4993]
   :part2 [12 21101]}

  {:day 6
   :sample {3 2, 4 1, 1 1, 2 1}
   :part1 [5934 351092]
   :part2 [26984457539 1595330616005]}

  {:day 7
   :sample [16,1,2,0,4,2,7,1,2,14]
   :part1 [37 337833]
   :part2 [168 0]})
