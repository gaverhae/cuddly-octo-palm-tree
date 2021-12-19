(ns t.day19
  (:require [clojure.set :as set]))

(defn parse
  [lines]
  (let [scanner-line? (fn [l] (re-matches #"--- scanner \d+ ---" l))]
    (->> lines
         (partition-by scanner-line?)
         (remove (fn [g] (scanner-line? (first g))))
         (map #(remove #{""} %))
         (mapv #(mapv (fn [l]
                        (let [[_ x y z] (re-matches #"(-?\d+),(-?\d+),(-?\d+)" l)]
                          [(Long/parseLong x)
                           (Long/parseLong y)
                           (Long/parseLong z)]))
                      %)))))

(defn all-beacons-as-origin
  [probe]
  (->> probe
       (mapv (fn [[x0 y0 z0]]
               (mapv (fn [[x1 y1 z1]]
                       [(- x1 x0)
                        (- y1 y0)
                        (- z1 z0)])
                     probe)))))

(defn all-orientations
  [probe]
  (let [rotations [(fn [[x y z]] [(- x) (- y) z])
                   (fn [[x y z]] [(- x) (- z) (- y)])
                   (fn [[x y z]] [(- x) y (- z)])
                   (fn [[x y z]] [(- x) z y])
                   (fn [[x y z]] [(- y) (- x) (- z)])
                   (fn [[x y z]] [(- y) (- z) x])
                   (fn [[x y z]] [(- y) x z])
                   (fn [[x y z]] [(- y) z (- x)])
                   (fn [[x y z]] [(- z) (- x) y])
                   (fn [[x y z]] [(- z) (- y) (- x)])
                   (fn [[x y z]] [(- z) x (- y)])
                   (fn [[x y z]] [(- z) y x])
                   (fn [[x y z]] [x (- y) (- z)])
                   (fn [[x y z]] [x (- z) y])
                   (fn [[x y z]] [x y z])
                   (fn [[x y z]] [x z (- y)])
                   (fn [[x y z]] [y (- x) z])
                   (fn [[x y z]] [y (- z) (- x)])
                   (fn [[x y z]] [y x (- z)])
                   (fn [[x y z]] [y z x])
                   (fn [[x y z]] [z (- x) (- y)])
                   (fn [[x y z]] [z (- y) x])
                   (fn [[x y z]] [z x y])
                   (fn [[x y z]] [z y (- x)])]]
    (->> rotations
         (map (fn [rot]
                (->> probe
                     (map rot)))))))

(defn part1
  [input]
  (loop [beacons (first (all-beacons-as-origin (first input)))
         probes (rest input)]
    (prn [:count (count beacons) (count (first probes)) (count probes)])
    (if (empty? probes)
      (count beacons)
      (if-let [union (first (for [rotated-probe (all-orientations (first probes))
                                  probe (all-beacons-as-origin rotated-probe)
                                  beacons (all-beacons-as-origin beacons)
                                  :when (<= 12 (count (set/intersection (set probe) (set beacons))))]
                                  (set/union (set probe) (set beacons))))]
        (recur union (rest probes))
        (recur beacons (concat (rest probes) [(first probes)]))))))

(defn part2
  [input])
