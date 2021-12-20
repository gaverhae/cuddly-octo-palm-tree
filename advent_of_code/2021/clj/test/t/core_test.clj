(ns t.core-test
  (:require [clojure.test :refer [deftest are]]
            [clojure.string :as string]
            [t.util :refer [transpose]]
            [t.day1 :as day1]
            [t.day2 :as day2]
            [t.day3 :as day3]
            [t.day4 :as day4]
            [t.day5 :as day5]
            [t.day6 :as day6]
            [t.day7 :as day7]
            [t.day8 :as day8]
            [t.day9 :as day9]
            [t.day10 :as day10]
            [t.day11 :as day11]
            [t.day12 :as day12]
            [t.day13 :as day13]
            [t.day14 :as day14]
            [t.day15 :as day15]
            [t.day16 :as day16]
            [t.day17 :as day17]
            [t.day18 :as day18]
            [t.day19 :as day19]
            [t.day20 :as day20]))

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

  #_{:day 1
   :sample [199 200 208 210 200 207 240 269 260 263]
   :part1 [7 1292]
   :part2 [5 1262]}

  #_{:day 2
   :sample [[:forward 5] [:down 5] [:forward 8] [:up 3] [:down 8] [:forward 2]]
   :part1 [150 1660158]
   :part2 [900 1604592846]}

  #_{:day 3
   :sample ["00100" "11110" "10110" "10111" "10101" "01111"
            "00111" "11100" "10000" "11001" "00010" "01010"]
   :part1 [198 4103154]
   :part2 [230 4245351]}

  #_{:day 4
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

  #_{:day 5
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

  #_{:day 6
   :sample {3 2, 4 1, 1 1, 2 1}
   :part1 [5934 351092]
   :part2 [26984457539 1595330616005]}

  #_{:day 7
   :sample [16,1,2,0,4,2,7,1,2,14]
   :part1 [37 337833]
   :part2 [168 96678050]}

  #_{:day 8
   :sample [[#{"bcdef" "abcdefg" "abdefg" "be" "bcdefg" "acdefg" "bde" "bceg" "abcdf" "cdefg"}
             ["abcdefg" "bcdef" "bcdefg" "bceg"]]
            [#{"cg" "bcg" "abcdefg" "abdefg" "abcde" "bcdefg" "bcdeg" "cefg" "abcdfg" "bdefg"}
             ["bcdefg" "bcg" "abcdefg" "cg"]]
            [#{"cg" "acfg" "bcg" "abdfg" "abcdefg" "abcdg" "abdefg" "abcde" "bcdefg" "abcdfg"}
             ["cg" "cg" "abcdfg" "bcg"]]
            [#{"abdeg" "abcdef" "abcf" "abcdefg" "abcde" "bcdefg" "bcd" "acdefg" "bc" "acdef"}
             ["abcdef" "abcde" "acdefg" "bc"]]
            [#{"bfg" "abdef" "abcdefg" "abefg" "abcefg" "cefg" "fg" "abcdeg" "abcdfg" "abceg"}
             ["cefg" "abcdefg" "bfg" "abefg"]]
            [#{"acf" "ac" "bcdfg" "abcdefg" "abefg" "abce" "abcefg" "abcfg" "abdefg" "acdefg"}
             ["abcdefg" "abce" "ac" "abcdefg"]]
            [#{"dfg" "abcdef" "bcdfg" "bcdef" "abcdefg" "abcdg" "abdefg" "bcdefg" "cefg" "fg"}
             ["cefg" "bcdef" "cefg" "abcdefg"]]
            [#{"bdef" "abcdefg" "abcdg" "abcefg" "de" "bcefg" "bcdefg" "bcdeg" "acdefg" "cde"}
             ["de" "abcefg" "abcdg" "bcefg"]]
            [#{"cg" "cdeg" "bcg" "abcdefg" "abcef" "abdefg" "bcefg" "bcdefg" "abcdfg" "bdefg"}
             ["abcdefg" "bcg" "cg" "bcg"]]
            [#{"cfg" "abcdefg" "abcefg" "abcfg" "bcdefg" "fg" "abcdeg" "abceg" "aefg" "abcdf"}
             ["aefg" "abcfg" "fg" "abceg"]]]
   :part1 [26 381]
   :part2 [61229 1023686]}

  #_{:day 9
   :sample [[2 1 9 9 9 4 3 2 1 0]
            [3 9 8 7 8 9 4 9 2 1]
            [9 8 5 6 7 8 9 8 9 2]
            [8 7 6 7 8 9 6 7 8 9]
            [9 8 9 9 9 6 5 6 7 8]]
   :part1 [15 591]
   :part2 [1134 1113424]}

  #_{:day 10
   :sample [[:incomplete 288957]
            [:incomplete 5566]
            [:incorrect 1197]
            [:incomplete 1480781]
            [:incorrect 3]
            [:incorrect 57]
            [:incomplete 995444]
            [:incorrect 3]
            [:incorrect 25137]
            [:incomplete 294]]
   :part1 [26397 392043]
   :part2 [288957 1605968119]}

  #_{:day 11
   :sample (->> [[5 4 8 3 1 4 3 2 2 3]
                 [2 7 4 5 8 5 4 7 1 1]
                 [5 2 6 4 5 5 6 1 7 3]
                 [6 1 4 1 3 3 6 1 4 6]
                 [6 3 5 7 3 8 5 4 7 8]
                 [4 1 6 7 5 2 4 6 4 5]
                 [2 1 7 6 8 4 1 7 2 1]
                 [6 8 8 2 8 8 1 1 3 4]
                 [4 8 4 6 8 4 8 5 5 4]
                 [5 2 8 3 7 5 1 5 2 6]]
                (map-indexed
                  (fn [y line]
                    (map-indexed
                      (fn [x v]
                        [[y x] (Long/parseLong (str v))])
                      line)))
                (apply concat)
                (into {}))
   :part1 [1656 1673]
   :part2 [195 279]}

  #_{:day 12
   :sample {0 #{2 -3}
            1 #{2 -3}
            2 #{0 1 -3 -4}
            -3 #{0 1 2 -5}
            -4 #{2}
            -5 #{-3}}
   :part1 [10 4104]
   :part2 [36 119760]}

  #_{:day 13
   :sample {:dots #{[6 10] [0 14] [9 10] [0 3] [10 4] [4 11] [6 0] [6 12] [4 1]
                    [0 13] [10 12] [3 4] [3 0] [8 4] [1 10] [2 14] [8 10] [9 0]}
            :folds [[1 7] [0 5]]}
   :part1 [17 785]
   :part2 [["#####"
            "#...#"
            "#...#"
            "#...#"
            "#####"]
           ["####...##..##..#..#...##..##...##..#..#"
            "#.......#.#..#.#..#....#.#..#.#..#.#..#"
            "###.....#.#..#.####....#.#....#..#.####"
            "#.......#.####.#..#....#.#.##.####.#..#"
            "#....#..#.#..#.#..#.#..#.#..#.#..#.#..#"
            "#.....##..#..#.#..#..##...###.#..#.#..#"]]}

  #_{:day 14
   :sample {:start ["NN" "NC" "CB"]
            :ops {"CH" "B"
                  "HH" "N"
                  "CB" "H"
                  "NH" "C"
                  "HB" "C"
                  "HC" "B"
                  "HN" "C"
                  "NN" "C"
                  "BH" "H"
                  "NC" "B"
                  "NB" "B"
                  "BN" "B"
                  "BB" "N"
                  "BC" "B"
                  "CC" "N"
                  "CN" "C"}}
   :part1 [1588 3306]
   :part2 [2188189693529 3760312702877]}

  #_{:day 15
   :sample {:width 10,
            :height 10,
            :costs {[0 0] 1 [0 1] 1 [0 2] 2 [0 3] 3 [0 4] 7 [0 5] 1 [0 6] 1 [0 7] 3 [0 8] 1 [0 9] 2
                    [1 0] 1 [1 1] 3 [1 2] 1 [1 3] 6 [1 4] 4 [1 5] 3 [1 6] 3 [1 7] 1 [1 8] 2 [1 9] 3
                    [2 0] 6 [2 1] 8 [2 2] 3 [2 3] 9 [2 4] 6 [2 5] 1 [2 6] 5 [2 7] 2 [2 8] 9 [2 9] 1
                    [3 0] 3 [3 1] 1 [3 2] 6 [3 3] 4 [3 4] 3 [3 5] 9 [3 6] 9 [3 7] 5 [3 8] 3 [3 9] 1
                    [4 0] 7 [4 1] 3 [4 2] 5 [4 3] 9 [4 4] 4 [4 5] 1 [4 6] 9 [4 7] 4 [4 8] 1 [4 9] 9
                    [5 0] 5 [5 1] 7 [5 2] 1 [5 3] 3 [5 4] 1 [5 5] 2 [5 6] 1 [5 7] 2 [5 8] 3 [5 9] 4
                    [6 0] 1 [6 1] 3 [6 2] 1 [6 3] 1 [6 4] 7 [6 5] 8 [6 6] 2 [6 7] 1 [6 8] 8 [6 9] 4
                    [7 0] 7 [7 1] 6 [7 2] 3 [7 3] 5 [7 4] 1 [7 5] 1 [7 6] 4 [7 7] 6 [7 8] 5 [7 9] 5
                    [8 0] 4 [8 1] 7 [8 2] 2 [8 3] 6 [8 4] 1 [8 5] 3 [8 6] 2 [8 7] 3 [8 8] 2 [8 9] 8
                    [9 0] 2 [9 1] 2 [9 2] 8 [9 3] 9 [9 4] 1 [9 5] 7 [9 6] 1 [9 7] 9 [9 8] 1 [9 9] 1}}
   :part1 [40 748]
   :part2 [315 3045]}

  {:day 16
   :sample [:operator 5 0 [[:operator 1 0 [[:operator 3 0 [[:literal 7 6]
                                                           [:literal 6 6]
                                                           [:literal 5 12]
                                                           [:literal 2 15]
                                                           [:literal 2 15]]]]]]]
   :part1 [31 854]
   :part2 [54 186189840660]}

  #_{:day 17
   :sample {:x-min 20
            :x-max 30
            :y-min -10
            :y-max -5}
   :part1 [45 5671]
   :part2 [112 4556]}

  #_{:day 18
   :sample [[[[0 [5 8]] [[1 7] [9 6]]] [[4 [1 2]] [[1 4] 2]]]
            [[[5 [2 8]] 4] [5 [[9 9] 0]]]
            [6 [[[6 2] [5 6]] [[7 6] [4 7]]]]
            [[[6 [0 7]] [0 9]] [4 [9 [9 0]]]]
            [[[7 [6 4]] [3 [1 3]]] [[[5 5] 1] 9]]
            [[6 [[7 3] [3 2]]] [[[3 8] [5 7]] 4]]
            [[[[5 4] [7 7]] 8] [[8 3] 8]]
            [[9 3] [[9 9] [6 [4 9]]]]
            [[2 [[7 7] 7]] [[5 8] [[9 3] [0 2]]]]
            [[[[5 2] 5] [8 [3 7]]] [[5 [7 5]] [4 4]]]]
   :part1 [4140 3574]
   :part2 [3993 4763]}

  #_{:day 19
   :sample [[[404 -588 -901]
             [528 -643 409]
             [-838 591 734]
             [390 -675 -793]
             [-537 -823 -458]
             [-485 -357 347]
             [-345 -311 381]
             [-661 -816 -575]
             [-876 649 763]
             [-618 -824 -621]
             [553 345 -567]
             [474 580 667]
             [-447 -329 318]
             [-584 868 -557]
             [544 -627 -890]
             [564 392 -477]
             [455 729 728]
             [-892 524 684]
             [-689 845 -530]
             [423 -701 434]
             [7 -33 -71]
             [630 319 -379]
             [443 580 662]
             [-789 900 -551]
             [459 -707 401]]
            [[686 422 578]
             [605 423 415]
             [515 917 -361]
             [-336 658 858]
             [95 138 22]
             [-476 619 847]
             [-340 -569 -846]
             [567 -361 727]
             [-460 603 -452]
             [669 -402 600]
             [729 430 532]
             [-500 -761 534]
             [-322 571 750]
             [-466 -666 -811]
             [-429 -592 574]
             [-355 545 -477]
             [703 -491 -529]
             [-328 -685 520]
             [413 935 -424]
             [-391 539 -444]
             [586 -435 557]
             [-364 -763 -893]
             [807 -499 -711]
             [755 -354 -619]
             [553 889 -390]]
            [[649 640 665]
             [682 -795 504]
             [-784 533 -524]
             [-644 584 -595]
             [-588 -843 648]
             [-30 6 44]
             [-674 560 763]
             [500 723 -460]
             [609 671 -379]
             [-555 -800 653]
             [-675 -892 -343]
             [697 -426 -610]
             [578 704 681]
             [493 664 -388]
             [-671 -858 530]
             [-667 343 800]
             [571 -461 -707]
             [-138 -166 112]
             [-889 563 -600]
             [646 -828 498]
             [640 759 510]
             [-630 509 768]
             [-681 -892 -333]
             [673 -379 -804]
             [-742 -814 -386]
             [577 -820 562]]
            [[-589 542 597]
             [605 -692 669]
             [-500 565 -823]
             [-660 373 557]
             [-458 -679 -417]
             [-488 449 543]
             [-626 468 -788]
             [338 -750 -386]
             [528 -832 -391]
             [562 -778 733]
             [-938 -730 414]
             [543 643 -506]
             [-524 371 -870]
             [407 773 750]
             [-104 29 83]
             [378 -903 -323]
             [-778 -728 485]
             [426 699 580]
             [-438 -605 -362]
             [-469 -447 -387]
             [509 732 623]
             [647 635 -688]
             [-868 -804 481]
             [614 -800 639]
             [595 780 -596]]
            [[727 592 562]
             [-293 -554 779]
             [441 611 -461]
             [-714 465 -776]
             [-743 427 -804]
             [-660 -479 -426]
             [832 -632 460]
             [927 -485 -438]
             [408 393 -506]
             [466 436 -512]
             [110 16 151]
             [-258 -428 682]
             [-393 719 612]
             [-211 -452 876]
             [808 -476 -593]
             [-575 615 604]
             [-485 667 467]
             [-680 325 -822]
             [-627 -443 -432]
             [872 -547 -609]
             [833 512 582]
             [807 604 487]
             [839 -516 451]
             [891 -625 532]
             [-652 -548 -490]
             [30 -46 -14]]]
   #_#_:part1 [79 381]
   :part2 [3621 12201]}

  {:day 20
   :sample {:alg [0 0 1 0 1 0 0 1 1 1 1 1 0 1 0 1 0 1 0 1 1 1 0 1 1 0 0 0 0 0 1 1
                  1 0 1 1 0 1 0 0 1 1 1 0 1 1 1 1 0 0 1 1 1 1 1 0 0 1 0 0 0 0 1 0
                  0 1 0 0 1 1 0 0 1 1 1 0 0 1 1 1 1 1 1 0 1 1 1 0 0 0 1 1 1 1 0 0
                  1 0 0 1 1 1 1 1 0 0 1 1 0 0 1 0 1 1 1 1 1 0 0 0 1 1 0 1 0 1 0 0
                  1 0 1 1 0 0 1 0 1 0 0 0 0 0 0 1 0 1 1 1 0 1 1 1 1 1 1 0 1 1 1 0
                  1 1 1 1 0 0 0 1 0 1 1 0 1 1 0 0 1 0 0 1 0 0 1 1 1 1 1 0 0 0 0 0
                  1 0 1 0 0 0 0 1 1 1 0 0 1 0 1 1 0 0 0 0 0 0 1 0 0 0 0 0 1 0 0 1
                  0 0 1 0 0 1 1 0 0 1 0 0 0 1 1 0 1 1 1 1 1 1 0 1 1 1 1 0 1 1 1 1
                  0 1 0 1 0 0 0 1 0 0 0 0 0 0 0 1 0 0 1 0 1 0 1 0 0 0 1 1 1 1 0 1
                  1 0 1 0 0 0 0 0 0 1 0 0 1 0 0 0 1 1 0 1 0 1 1 0 0 1 0 0 0 1 1 0
                  1 0 1 1 0 0 1 1 1 0 1 0 0 0 0 0 0 1 0 1 0 0 0 0 0 0 0 1 0 1 0 1
                  0 1 1 1 1 0 1 1 1 0 1 1 0 0 0 1 0 0 0 0 0 1 1 1 1 0 1 0 0 1 0 0
                  1 0 1 1 0 1 0 0 0 0 1 1 0 0 1 0 1 1 1 1 0 0 0 0 1 1 0 0 0 1 1 0
                  0 1 0 0 0 1 0 0 0 0 0 0 1 0 1 0 0 0 0 0 0 0 1 0 0 0 0 0 0 0 1 1
                  0 0 1 1 1 1 0 0 1 0 0 0 1 0 1 0 1 0 0 0 1 1 0 0 1 0 1 0 0 1 1 1
                  0 0 1 1 1 1 1 0 0 0 0 0 0 0 0 1 0 0 1 1 1 1 0 0 0 0 0 0 1 0 0 1]
            :img [[1 0 0 1 0]
                  [1 0 0 0 0]
                  [1 1 0 0 1]
                  [0 0 1 0 0]
                  [0 0 1 1 1]]
            :default 0}
   :part1 [35 4964]
   :part2 [3351 13202]})
