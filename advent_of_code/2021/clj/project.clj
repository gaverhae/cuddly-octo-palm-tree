(defproject t "app"
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/core.match "1.0.0"]
                 [instaparse "1.4.10"]
                 [criterium "0.4.6"]
                 [com.taoensso/tufte "2.2.0"]]
  :global-vars {*warn-on-reflection* true}
  :jvm-opts ["-Xverify:none"]
  :test-selectors {:default (constantly true)}
  :main ^:skip-aot t.core
  :plugins [[com.jakemccrary/lein-test-refresh "0.24.1"]]
  :target-path "target/%s"
  :test-refresh {:quiet true
                 :changes-only true}
  :profiles {:uberjar {:aot :all}})
