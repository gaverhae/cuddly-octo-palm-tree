(ns t.day12
  (:require [clojure.math :as math]
            [clojure.set :as set]
            [clojure.string :as s]
            [instaparse.core :as insta]
            [t.lib :as lib]))

(defn parse
  [lines]
  (->> lines
       (map (fn [line]
              (let [[symbols bounds] (s/split line #" ")]
                [symbols
                 (->> (re-seq #"\d+" bounds)
                      (map parse-long))])))))

(defn solve
  [in]
  (let [matches? (fn [pattern]
                   (fn [line]
                     (->> line
                          (re-seq #"#+")
                          (map count)
                          (= pattern))))
        pre-matches? (fn [pattern]
                       (fn [line]
                         (let [line-p (->> line
                                           (re-seq #"#+")
                                           (map count))]
                           (and (<= (count line-p) (count pattern))
                                (= (butlast (take (count line-p) pattern))
                                   (butlast line-p))))))]
    (->> in
         (map (fn [[symbols pattern]]
                (->> (loop [to-process symbols
                            processed []]
                       (if (empty? to-process)
                         processed
                         (let [s (first to-process)
                               to-process (rest to-process)]
                           (recur to-process
                                  (->> (if (= \? s) [\. \#] [s])
                                       (mapcat (fn [new-s]
                                                 (if (empty? processed)
                                                   [(str new-s)]
                                                   (->> processed
                                                        (map (fn [prev] (str prev new-s)))))))
                                       (filter (pre-matches? pattern)))))))
                     (filter (matches? pattern))
                     count)))
         (map-indexed (fn [idx x]
                        (prn [idx x])
                        x))
         (reduce + 0))))

(defn part1
  [input]
  (->> input
       solve))

(defn part2
  [input]
  (->> input
       (map (fn [[symbols pattern]]
              [(apply str (interpose \? (repeat 5 symbols)))
               (apply concat (repeat 5 pattern))]))
       solve))

(lib/check
  [part1 sample] 21
  [part1 puzzle] 7090
  [part2 sample] 525152
  #_#_[part2 puzzle] 0)
