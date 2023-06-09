(ns gxtdb.adapters.db-test
  (:require [clojure.test :refer [deftest testing is]]
            [xtdb.api :as xt]
            [gxtdb.adapters.db :as db]))

(def all-none-input
  {:valid-time {:value {:none {}}}
   :tx-time {:value {:none {}}}
   :tx-id {:value {:none {}}}})

(def valid-time-input
  {:valid-time {:value {:some "2023-06-12T21:32:44.717-05:00"}}
   :tx-time {:value {:none {}}}
   :tx-id {:value {:none {}}}})

(def all-some-input
  {:valid-time {:value {:some "2023-06-12T21:32:44.717-05:00"}}
   :tx-time {:value {:some "2023-06-12T21:32:44.717-05:00"}}
   :tx-id {:value {:some 3}}})

(def tx-some-inputs
  {:valid-time {:value {:none {}}}
   :tx-time {:value {:some "2023-06-12T21:32:44.717-05:00"}}
   :tx-id {:value {:some 3}}})

(deftest ->db-basis-test
  (testing "When all fields are none"
    (is (= nil (db/->db-basis all-none-input))))
  (testing "When all tx inputs are some"
    (is (= nil (db/->db-basis tx-some-inputs))))
  (testing "When only tx-id input is some"
    (is (= {::xt/tx 3} (db/->db-basis {:tx-id {:value {:some 3}}}))))
  (testing "When only tx-time input is some"
    (is (= {::xt/tx-time #inst "2023-06-13T02:32:44.717-00:00"} (db/->db-basis {:tx-time {:value {:some "2023-06-12T21:32:44.717-05:00"}}}))))
  (testing "When only valid-time is some"
    (is (= {::xt/valid-time #inst "2023-06-13T02:32:44.717-00:00"} (db/->db-basis valid-time-input))))
  (testing "When all fields are some"
    (is (= {::xt/valid-time #inst "2023-06-13T02:32:44.717-00:00"} (db/->db-basis all-some-input)))))