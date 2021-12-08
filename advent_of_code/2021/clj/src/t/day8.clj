(ns t.day8
  (:require (clojure [string :as string]
                     [set :as set])))

(defn parse
  [lines]
  (->> lines
       (map (fn [s]
              (-> s
                  (string/split #" ")
                  (->> (split-with (comp not #{"|"}))
                       (map (fn [x] (remove #{"|"} x)))))))))

(defn decode
  [[inputs outputs]]
  (let [e (->> "abcdefg"
               (filter (fn [c]
                         (->> inputs
                              (filter #(contains? (set %) c))
                              count
                              (== 4))))
               set)
        b (->> "abcdefg"
               (filter (fn [c]
                         (->> inputs
                              (filter #(contains? (set %) c))
                              count
                              (== 6))))
               set)
        d (set/intersection
            (->> "abcdefg"
                 (filter (fn [c]
                           (->> inputs
                                (filter #(contains? (set %) c))
                                count
                                (== 7))))
                 set)
            (->> inputs
                 (filter #(= (count %) 4))
                 first
                 set))
        g (set/difference
            (->> "abcdefg"
                 (filter (fn [c]
                           (->> inputs
                                (filter #(contains? (set %) c))
                                count
                                (== 7))))
                 set)
            d)
        a (set/difference
            (set "abcdefg")
            e b d g
            (->> inputs
                 (filter #(= (count %) 2))
                 first
                 set))
        f (set/difference
            (->> inputs
                 (filter #(= (count %) 6))
                 (filter #(set/subset? d (set %)))
                 (filter #(set/subset? e (set %)))
                 first
                 set)
            a b d e g)
        c (set/difference
            (set "abcdefg")
            a b d e f g)
        decoder (->> [a b c d e f g]
                     (map first)
                     (apply str)
                     (map vector "abcdefg")
                     (map (comp vec reverse))
                     (into {}))]
    (->> outputs
         (map (fn [w] (->> w (map decoder) sort (apply str))))
         (map {"abcefg" "0"
               "cf" "1"
               "acdeg" "2"
               "acdfg" "3"
               "bcdf" "4"
               "abdfg" "5"
               "abdefg" "6"
               "acf" "7"
               "abcdefg" "8"
               "abcdfg" "9"})
         (apply str)
         (Long/parseLong))))

(defn part1
  [input]
  (->> input
       (map rest)
       (apply concat)
       (apply concat)
       (map count)
       (filter #{2 3 4 7})
       count))

(defn part2
  [input]
  (->> input
       (map decode)
       (reduce + 0)))
