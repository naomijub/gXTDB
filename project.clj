(defproject gxtdb "0.0.1-SNAPSHOT"
  :description "XTDB gRPC server plugin"
  :url "https://github.com/naomijub/gXTDB"
  :license {:name "MIT"
            :url "https://www.mit.edu/~amini/LICENSE.md"
            :year 2023
            :key "mit"}
  :plugins [[lein-cljfmt "0.9.2"]]
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [com.xtdb/xtdb-core "1.22.1"]
                 [io.pedestal/pedestal.service "0.5.9"]
                 [com.cognitect/anomalies "0.1.12"]

                 ;; -- PROTOC-GEN-CLOJURE --
                 [io.github.protojure/grpc-server "2.0.1"]
                 [io.github.protojure/google.protobuf "2.0.0"]

                 [ch.qos.logback/logback-classic "1.2.9"]
                 [org.slf4j/jul-to-slf4j "1.7.32"]
                 [org.slf4j/jcl-over-slf4j "1.7.32"]
                 [org.slf4j/log4j-over-slf4j "1.7.32"]]
  :min-lein-version "2.0.0"
  :resource-paths ["config", "resources"]
  :profiles {:dev {:aliases {"run-dev" ["trampoline" "run" "-m" "gxtdb.server/run-dev"]}
                   :dependencies [[io.pedestal/pedestal.service-tools "0.5.9"]]}
             :uberjar {:aot [gxtdb.server]}}
  :main ^{:skip-aot true} gxtdb.server)
