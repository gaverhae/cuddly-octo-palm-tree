(ns t.day05
  (:require [t.lib :as lib :refer [->long]]))

(defn parse
  [lines]
  (let [[crates _ moves] (partition-by #{""} lines)]
    {:crates (->> (butlast crates)
                  (map (fn [line]
                         (map (fn [i] (nth line i)) (range 1 (count line) 4))))
                  lib/transpose
                  (map #(remove #{\space} %))
                  vec)
     :moves (->> moves
                 (map (fn [c]
                        (let [[_ n from to] (re-matches #"move (\d+) from (\d+) to (\d+)" c)]
                          {:n (->long n) :from (dec (->long from)), :to (dec (->long to))}))))}))

(defn solve
  [crates moves]
  (->> (reduce (fn [crates {:keys [n from to]}]
                 (-> crates
                     (update from #(drop n %))
                     (update to #(concat (take n (crates from)) %))))
               crates
               moves)
       (map first)
       (apply str)))

(defn part1
  [{:keys [crates moves]}]
  (solve crates (->> moves
                     (mapcat (fn [m] (repeat (:n m) (assoc m :n 1)))))))

(defn part2
  [{:keys [crates moves]}]
  (solve crates moves))

(lib/check
  [part1 sample] "CMZ"
  [part1 puzzle] "SHMSDGZVC"
  [part2 sample] "MCD"
  [part2 puzzle] "VRZGHDFBQ")
