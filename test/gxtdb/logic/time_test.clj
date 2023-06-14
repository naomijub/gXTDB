(ns gxtdb.logic.time-test
  (:require [clojure.test :refer [deftest testing is]]
            [xtdb.api :as xt]
            [gxtdb.logic.time :refer [conj-some-valid-timerange assoc-some-time]]))

(def base-vector [:a :b])

(deftest conj-time-ranges-test
  (testing "When init and end rages are nil, base vector is unchanged"
    (is (= base-vector (conj-some-valid-timerange base-vector nil nil))))
  (testing "When only end time is not nil, base vector is unchanged"
    (is (= base-vector (conj-some-valid-timerange base-vector nil :d))))
  (testing "When only init time is not nil, base vector is appended of init-time"
    (is (= [:a :b :c] (conj-some-valid-timerange base-vector :c nil))))
  (testing "When only init time and end itme are not nil, base vector is appended of init-time and end-time"
    (is (= [:a :b :c :d] (conj-some-valid-timerange base-vector :c :d)))))

(deftest assoc-time-test
  (testing "Assocs tx-time when tx-time is some"
    (is (= {::xt/tx-time #inst "2023-06-13T02:32:44.717-00:00"} (assoc-some-time {} ::xt/tx-time {:value {:some "2023-06-12T21:32:44.717-05:00"}}))))
  (testing "Assocs tx-time when tx-time is none"
    (is (= {} (assoc-some-time {} ::xt/tx-time {:value {:none {}}}))))
  (testing "Assocs tx-time when tx-time is nil"
    (is (= {} (assoc-some-time {} ::xt/tx-time nil)))))