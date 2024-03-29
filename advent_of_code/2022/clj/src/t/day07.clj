(ns t.day07
  (:require [t.lib :as lib :refer [->long]]
            [clojure.core.match :refer [match]]))

(defn parse
  [lines]
  (->> lines
       (keep (fn [line]
               (or (when-let [[_ dir] (re-matches #"\$ cd (\S+)" line)]
                     [:cd dir])
                   (when-let [[_ size] (re-matches #"(\d+) \S+" line)]
                     [:file (->long size)]))))
       (reduce (fn [acc el]
                 (match el
                   [:cd "/"] (assoc acc :cwd [[]])
                   [:cd ".."] (update acc :cwd pop)
                   [:cd child] (update acc :cwd conj (conj (peek (:cwd acc)) child))
                   [:file size] (assoc acc :sizes
                                       (reduce
                                         (fn [acc el]
                                           (update acc el (fnil + 0) size))
                                         (:sizes acc)
                                         (:cwd acc)))))
               {})
       :sizes))

(defn part1
  [input]
  (->> input
       (keep (fn [[_ size]] (when (< size 100000) size)))
       (reduce + 0)))

(defn part2
  [input]
  (let [required-space (- 30000000 (- 70000000 (get input [])))]
    (->> input
         (keep (fn [[_ size]] (when (>= size required-space) size)))
         sort
         first)))

(lib/check
  [part1 sample] 95437
  [part1 puzzle] 1491614
  [part2 sample] 24933642
  [part2 puzzle] 6400111)
