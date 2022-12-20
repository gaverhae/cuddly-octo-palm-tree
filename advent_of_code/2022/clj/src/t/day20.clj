(ns t.day20
  (:require [clojure.edn :as edn]
            [clojure.string :as string]
            [clojure.set :as set]
            [clojure.core.match :refer [match]]
            [instaparse.core :as insta]
            [t.lib :as lib :refer [->long]]))

(defn parse
  [lines]
  (->> lines
       (mapv ->long)))

(defn rotate
  [acc [^long idx ^long el]]
  (let [current-index (.indexOf ^java.util.List acc [idx el])
        centered (concat (rest (drop current-index acc))
                         (take current-index acc))
        rev (if (neg? el) reverse identity)
        c (cycle (rev centered))
        r (take (count acc) (cons [idx el] (drop (Math/abs el) c)))]
    (rev r)))

(defn part1
  [input]
  (let [marked (->> input (map-indexed vector))
        v (mapv second (reduce rotate marked marked))
        zero-idx (.indexOf ^java.util.List v 0)]
    (+ (v (mod (+ zero-idx 1000) (count v)))
       (v (mod (+ zero-idx 2000) (count v)))
       (v (mod (+ zero-idx 3000) (count v))))))

(defn part2
  [input]
  input)

(lib/check
  [part1 sample] 3
  [part1 puzzle] 0
  #_#_[part2 sample] 0
  #_#_[part2 puzzle] 0)
