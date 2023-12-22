(ns t.day21
  (:require [clojure.core.async :as async]
            [clojure.core.match :refer [match]]
            [clojure.math :as math]
            [clojure.set :as set]
            [clojure.string :as s]
            [instaparse.core :as insta]
            [t.lib :as lib])
  (:import [java.util Arrays]))

(defn parse
  [lines]
  {:grid (->> lines
              (map (fn [line]
                     (s/replace line \S \.)))
              vec)
   :start (->> lines
               (map-indexed (fn [y line]
                              (->> line
                                   (keep-indexed (fn [x c]
                                                   (when (= c \S)
                                                     [y x]))))))
               (apply concat)
               first)})

(defn part1
  [input max-steps]
  (loop [step 0
         ps [(:start input)]]
    (if (= max-steps step)
      (count ps)
      (recur (inc step)
             (->> ps
                  (mapcat (fn [[y x]]
                            (for [[dy dx] [[-1 0] [1 0] [0 1] [0 -1]]
                                  :let [y (+ y dy)
                                        x (+ x dx)]
                                  :when (= \. (get-in input [:grid y x]))]
                              [y x])))
                  set)))))

(defn walk-one-map
  [grid]
  (fn [start-pos]
    (loop [step 0
           filled? {}
           exits {}
           cur [start-pos]
           prev #{}]
      (if (empty? cur)
        [filled? exits]
        (let [t (->> cur
                     (mapcat (fn [[y x]]
                               (for [[dy dx] [[-1 0] [1 0] [0 1] [0 -1]]
                                     :let [y (+ y dy)
                                           x (+ x dx)
                                           dst (get-in grid [y x] :out)]
                                     :when (and (#{\. :out} dst)
                                                (not (prev [y x])))]
                                 [dst [y x] [dy dx]]))))
              nxt (->> t
                       (filter (comp #{\.} first))
                       (map second))
              exits (->> t
                         (filter (comp #{:out} first))
                         (reduce (fn [acc [_ x d]]
                                   (if (acc d)
                                     acc
                                     (assoc acc d [(inc step) x])))
                                 exits))
              filled? (reduce #(assoc %1 %2 step) filled? cur)]
          (recur (inc step) filled?  exits nxt (set cur)))))))

(defn part2
  [input max-steps]
  (let [[filled? exits] ((walk-one-map (:grid input)) (:start input))]
    (doseq [y (range 0 11)]
      (doseq [x (range 0 11)]
        (print (format " %s" (if-let [n (filled? [y x])]
                                (format "%2d" n)
                                "##"))))
      (println))
    (prn exits))

  (let [h (-> input :grid count)
        w (-> input :grid first count)
        ->neighs (->> (range h)
                      (mapcat (fn [y]
                                (->> (range w)
                                     (map (fn [x]
                                            [[y x] (for [[dy dx] [[-1 0] [1 0] [0 1] [0 -1]]
                                                         :let [y (+ y dy)
                                                               x (+ x dx)]
                                                         :when (= \. (get-in input [:grid (mod y h) (mod x w)]))]
                                                     [dy dx])])))))
                      (into {}))]
    (loop [step 0
           cur #{(:start input)}
           prev #{}
           internal [0 0]]
      (if (= max-steps step)
        (+ (count cur) (get internal (rem step 2)))
        (let [nxt (->> cur
                       (mapcat (fn [[y x]]
                                 (->> (->neighs [(mod y h) (mod x w)])
                                      (map (fn [[dy dx]] [(+ y dy) (+ x dx)])))))
                       (remove prev)
                       set)]
          (recur (inc step)
                 nxt
                 cur
                 (update internal (rem step 2) + (count cur))))))))

(lib/check
  #_#_[part1 sample 6] 16
  #_#_[part1 puzzle 64] 3639
  #_#_[part2 sample 6] 16
  #_#_[part2 sample 10] 50
  #_#_[part2 sample 50] 1594
  #_#_[part2 sample 1] 2
  #_#_[part2 sample 10] 50
  #_#_[part2 sample 100] 6536
  #_#_[part2 sample 200] 26538
  #_#_[part2 sample 300] 59895
  #_#_[part2 sample 400] 106776
  [part2 sample 500] 167004
  #_#_[part2 sample 1000] 668697
  #_#_[part2 sample 2000] 2677337
  #_#_[part2 sample 5000] 16733044
  #_#_[part2 puzzle 100] 8829
  #_#_[part2 puzzle 200] 34889
  #_#_[part2 puzzle 400] 138314
  #_#_[part2 puzzle 1000] 862969
  #_#_[part2 puzzle 2000] 862969
  #_#_[part2 puzzle 26501365] 0)

(defn benchmark
  []
  (doseq [data [#'sample #'puzzle]
          n [100 300 1000 3000 10000]]
    (let [start (System/currentTimeMillis)
          _ (part2 @@data n)
          end (System/currentTimeMillis)]
      (println (format "%10s %4s: %6.2fs"
                       (-> data meta :name)
                       n
                       (/ (- end start) 1000.0))))))
