(ns gxtdb.logic.time-test
  (:require [clojure.test :refer [deftest testing is]]
            [gxtdb.logic.time :refer [conj-some-valid-timerange]]))

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