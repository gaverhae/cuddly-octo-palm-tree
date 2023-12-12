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
  (let [patterns (->> in
                      (mapcat (fn [[symbols pattern]]
                                (->> (s/split symbols #"\."))))
                      (into #{})
                      (map (fn [pat]
                             [pat
                              (loop [pats [pat]]
                                (if (s/index-of (first pats) \?)
                                  (recur (->> pats
                                              (mapcat (fn [s] [(s/replace-first s \? \.)
                                                               (s/replace-first s \? \#)]))))
                                  (->> pats
                                       (map (fn [s]
                                              (->> s (re-seq #"#+") (map count))))
                                       set)))]))
                      (into {}))]
    patterns))

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
  #_#_[part2 sample] 525152
  #_#_[part2 puzzle] 0)
