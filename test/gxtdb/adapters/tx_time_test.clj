(ns gxtdb.adapters.tx-time-test
  (:require [clojure.test :refer [deftest testing is]]
            [gxtdb.adapters.tx-time :as tx-time]))

(deftest proto-option-time->option-clj-time
  (testing "when proto is nil"
    (is (= nil (tx-time/->clj-time nil))))
  (testing "when proto is None"
    (is (= nil (tx-time/->clj-time {:value {:none {}}}))))
  (testing "when proto is some datetime"
    (is (= #inst "2023-06-12T21:32:44.717-05:00" (tx-time/->clj-time {:value {:some "2023-06-12T21:32:44.717-05:00"}})))))