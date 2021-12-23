(ns t.day23
  (:require [taoensso.tufte :as tufte :refer (defnp p profiled profile)]))

(def mapping
  {[0 0] 0 [1 0] 1 [2 0] 2 [3 0] 3 [4 0] 4 [5 0] 5 [6 0] 6
   [7 0] 7 [8 0] 8 [9 0] 9 [10 0] 10
   [2 1] 11 [4 1] 12 [6 1] 13 [8 1] 14
   [2 2] 15 [4 2] 16 [6 2] 17 [8 2] 18
   [2 3] 19 [4 3] 20 [6 3] 21 [8 3] 22
   [2 4] 23 [4 4] 24 [6 4] 25 [8 4] 26})

(defnp sign
  [^longs arr]
  (let [l (alength arr)
        ^long s (areduce arr idx ret (long 0) (+ (case (aget arr idx)
                                             0 0, 1 1, 10 2, 100 3, 1000 4, 0)
                                           (* 5 ret)))]
    (aset arr (dec l) s)
    arr))

(defn parse
  [lines]
  (->> lines
       (drop 2)
       (take 2)
       (map-indexed (fn [y line]
                      (map-indexed (fn [x c]
                                     (when-let [c ({\A 1 \B 10 \C 100 \D 1000} c)]
                                       [[(- x 1) (inc y)] c]))
                                   line)))
       (apply concat)
       (remove nil?)
       (reduce (fn [^longs arr [k ^long v]]
                 (aset arr (mapping k) v)
                 arr)
               (make-array Long/TYPE 20))
       (sign)))

(def ^"[[J" adj-arr-1
  (->> {[0 0] [[1 0]]
        [1 0] [[0 0] [2 0]]
        [2 0] [[1 0] [3 0] [2 1]]
        [3 0] [[2 0] [4 0]]
        [4 0] [[3 0] [5 0] [4 1]]
        [5 0] [[4 0] [6 0]]
        [6 0] [[5 0] [7 0] [6 1]]
        [7 0] [[6 0] [8 0]]
        [8 0] [[7 0] [9 0] [8 1]]
        [9 0] [[10 0] [8 0]]
        [10 0] [[9 0]]
        [2 1] [[2 0] [2 2]]
        [2 2] [[2 1]]
        [4 1] [[4 0] [4 2]]
        [4 2] [[4 1]]
        [6 1] [[6 0] [6 2]]
        [6 2] [[6 1]]
        [8 1] [[8 0] [8 2]]
        [8 2] [[8 1]]}
       (reduce (fn [^"[[J" arr [k kvs]]
                 (aset arr ^long (mapping k) ^longs (into-array Long/TYPE (map mapping kvs)))
                 arr)
               (make-array Long/TYPE 27 0))))

(def ^"[[J" adj-arr-2
  (let [arr ^"[[J" (make-array Long/TYPE 27 0)]
    (loop [i 0]
      (when (< i (alength adj-arr-1))
        (aset arr i (aget adj-arr-1 i))
        (recur (inc i))))
    (reduce (fn [^"[[J" arr [k kvs]]
              (aset arr ^long (mapping k) ^longs (into-array Long/TYPE (map mapping kvs)))
              arr)
            arr
            {[2 2] [[2 1] [2 3]]
             [2 3] [[2 2] [2 4]]
             [2 4] [[2 3]]
             [4 2] [[4 1] [4 3]]
             [4 3] [[4 2] [4 4]]
             [4 4] [[4 3]]
             [6 2] [[6 1] [6 3]]
             [6 3] [[6 2] [6 4]]
             [6 4] [[6 3]]
             [8 2] [[8 1] [8 3]]
             [8 3] [[8 2] [8 4]]
             [8 4] [[8 3]]})))

#_(def ^longs end-state
  (into-array Long/TYPE
              [0 0 0 0 0 0 0 0 0 0 0
               1 10 100 1000
               1 10 100 1000
               1 10 100 1000
               1 10 100 1000]))

(def end-state {2 1, 4 10, 6 100, 8 1000})

(def decode
  (->> mapping (map (fn [[k v]] [v k])) (into {})))

(defnp possible-moves
  [^longs amphipods ^"[[J" adjacent]
  (loop [i 0
         ret ()]
    (if (== i (dec (alength amphipods)))
      ret
      (recur (inc i)
             (let [cost (aget amphipods i)
                   [start-x start-y :as start-pos] (decode i)]
               (if (zero? cost)
                 ret
                 (conj ret
                       [start-pos
                        cost
                        (if (p :already-good
                               (and (end-state start-x)
                                    (== (end-state start-x) cost)
                                    (every? (fn [c] (= c cost))
                                            (->> (range start-y 5)
                                                 (map (fn [y] [start-x y]))
                                                 (filter (fn [pos]
                                                           (let [idx ^long (mapping pos)]
                                                             (seq (aget adjacent idx)))))
                                                 (map (fn [pos] (aget amphipods ^long (mapping pos))))))))
                          []
                          (loop [poss [[start-pos 0]]
                                 visited #{start-pos}
                                 reachable []]
                            (if (empty? poss)
                              reachable
                              (let [[[end-x end-y :as end-pos] cost-to-reach] (first poss)]
                                (recur (concat (rest poss)
                                               (->> (aget adjacent ^long (mapping end-pos))
                                                    (mapv decode)
                                                    (remove visited)
                                                    (remove (fn [[x y]]
                                                              (let [k (mapping [x y])]
                                                                (pos? (aget amphipods ^long k)))))
                                                    (remove (fn entered-wrong-room
                                                              [[adj-x adj-y]]
                                                              (p :entered-wrong-room
                                                                 (and (not= adj-x start-x)
                                                                      (>= adj-y 1)
                                                                      (not= cost (end-state adj-x))))))
                                                    (remove (fn wrong-amphi-in-room
                                                              [[adj-x adj-y]]
                                                              (p :wrong-amphi-in-room
                                                                 (let [k2 (mapping [adj-x 2])
                                                                       k3 (mapping [adj-x 3])
                                                                       k4 (mapping [adj-x 4])]
                                                                   (and (not= adj-x start-x)
                                                                        (== 1 adj-y)
                                                                        (or (and (pos? (aget amphipods k2))
                                                                                 (not= (aget amphipods k2) cost))
                                                                            (and (< k3 (dec (alength amphipods)))
                                                                                 (pos? (aget amphipods k3))
                                                                                 (not= (aget amphipods k3) cost))
                                                                            (and (< k4 (dec (alength amphipods)))
                                                                                 (pos? (aget amphipods k4))
                                                                                 (not= (aget amphipods k4) cost))))))))
                                                    (mapv (fn [adj] [adj (+ cost cost-to-reach)]))))
                                       (conj visited end-pos)
                                       (if (p :check-end-pos
                                              (or
                                                ;; we've already reached this one
                                                (visited end-pos)
                                                ;; can't start in hallway, end in hallway
                                                (and (zero? start-y)
                                                     (zero? end-y))
                                                ;; can't stop in room with space beneath
                                                (and (>= 1 end-y)
                                                     (mapping [end-x (inc end-y)])
                                                     (seq (aget adjacent ^long (mapping [end-x (inc end-y)])))
                                                     (or (= start-pos [end-x (inc end-y)])
                                                         (zero? (aget amphipods (mapping [end-x (inc end-y)])))))
                                                ;; can't stop in front of room
                                                (#{[2 0] [4 0] [6 0] [8 0]} end-pos)))
                                         reachable
                                         (conj reachable [end-pos cost-to-reach])))))))])))))))

(defnp abs
  [n]
  (if (neg? n) (- n) n))

(defnp heuristic
  [state]
  (->> state
       butlast
       (map-indexed (fn [idx t] [(decode idx) t]))
       (remove (fn [[_ t]] (zero? t)))
       (map (fn [[[x y] c]]
              (let [dx (abs (- x ({1 2, 10 4, 100 6, 1000 8} c)))]
                (if (zero? dx)
                  0
                  (* c (+ dx y 1))))))
       (reduce +)))

(defnp move-amphi
  [^longs state ^long from-idx ^long to-idx]
  (let [ret ^longs (aclone state)]
    (sign (doto ^longs (aclone state)
            (aset from-idx 0)
            (aset to-idx (aget state from-idx))))))

(defn print-state
  [^longs state]
  (let [p (fn [pos]
            (get {1 "A" 10 "B" 100 "C" 1000 "D" 0 "."} (aget state ^long (mapping pos))))]
    (println "#############")
    (print "#")
    (doseq [n (range 11)]
      (print (p [n 0])))
    (println "#")
    (println (format "###%s#%s#%s#%s###" (p [2 1]) (p [4 1]) (p [6 1]) (p [8 1])))
    (doseq [y (range 2 3 #_(->> state (map (fn [[[x y] t]] y)) (reduce max) inc))]
      (println (format "  #%s#%s#%s#%s#  " (p [2 y]) (p [4 y]) (p [6 y]) (p [8 y]))))
    (println         "  #########  ")))

(defnp solve
  [^longs input ^"[[J" adjacency]
  (loop [to-process {(heuristic input) (list [input 0])}
         visited {}
         i 0]
    (if (empty? to-process)
      :error
      (let [min-h (->> to-process keys (reduce min))
            [[^longs state cost-to-reach] & r] (to-process min-h)
            to-process (if (empty? r)
                         (dissoc to-process min-h)
                         (assoc to-process min-h r))]
        (when (zero? (mod i 10000))
          #_(print-state state)
          (prn [:cost cost-to-reach :h min-h :i i]))
        (cond (p :filter-visited
                 (when-let [v (visited (last state))]
                   (<= v cost-to-reach)))
              (recur to-process visited (inc i))
              (p :end-state?
                 (->> state
                      butlast
                      (map-indexed (fn [idx t] [(decode idx) t]))
                      (remove (fn [[pos t]] (zero? t)))
                      (every? (fn [[[x y] c]]
                                (and (pos? y)
                                     (== c (end-state x)))))))
              cost-to-reach
              :else
              (recur (p :next-states
                        (->> (possible-moves state adjacency)
                             (mapcat (fn [[start-pos atype end-poss]]
                                       (p :compute-h
                                          (->> end-poss
                                               (mapv (fn [arg]
                                                       (let [[end-pos c] arg]
                                                       (let [cost (+ cost-to-reach c)
                                                             state (move-amphi state (mapping start-pos) (mapping end-pos))]
                                                         [(+ cost (heuristic state))
                                                          state
                                                          cost]))))))))
                             (reduce (fn [tp [h state cost]]
                                       (update tp h (fnil conj ()) [state cost]))
                                     to-process)))
                     (assoc visited (last state) cost-to-reach)
                     (inc i)))))))

(defn part1
  [input]
  (p :part1 (solve input adj-arr-1)))

(defn part2
  [^longs input]
  (solve (let [arr ^"[J" (make-array Long/TYPE 27)]
           (loop [i 0]
             (when (< i 15)
               (aset arr i (aget input i))
               (recur (inc i))))
           (reduce (fn [^"[J" arr [k ^long t]]
                     (aset arr ^long (mapping k) t)
                     arr)
                   arr
                   [[[2 2] 1000] [[2 3] 1000] [[4 2] 100] [[4 3] 10]
                    [[6 2] 10] [[6 3] 1] [[8 2] 1] [[8 3] 100]])
           (loop [i 23]
             (when (<  i 27)
               (aset arr i (aget input (- i 8)))
               (recur (inc i))))
           arr)
         adj-arr-2))
