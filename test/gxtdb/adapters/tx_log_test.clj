(ns gxtdb.adapters.tx-log-test
  #_{:clj-kondo/ignore [:refer-all]}
  (:require [clojure.test :refer :all]
            [gxtdb.adapters.tx-log :as tx-log]))

(def var->put-tx {:put {:xt-id "id 2", :document {:fields {"painting ids" {:kind {:list-value {:values [{:kind {:number-value 2.0}}, {:kind {:number-value 3.0}} {:kind {:number-value 76.0}} {:kind {:number-value 3.0}}]}}}, "age" {:kind {:number-value 200.0}}, "is-artist?" {:kind {:bool-value true}}, "full name" {:kind {:struct-value {:fields {"last" {:kind {:string-value "picasso"}}, "first" {:kind {:string-value "pablo"}}}}}}, "name" {:kind {:string-value "pablo picasso"}}}}}})
(def expected-put-tx [:xtdb.api/put
                      {:xt/id :id-2,
                       :painting-ids [2.0 3.0 76.0 3.0],
                       :age 200.0,
                       :is-artist? true,
                       :full-name {:last "picasso", :first "pablo"},
                       :name "pablo picasso"}])

(def var->delete-tx {:delete {:document-id "f2eed61a-1928-4d75-8620-debfc23eae8d", :id-type :uuid}})
(def expected-delete-tx [:xtdb.api/delete #uuid "f2eed61a-1928-4d75-8620-debfc23eae8d"])

(def var->evict-tx {:evict {:document-id "f2eed61a-1928-4d75-8620-debfc23eae8d", :id-type :uuid}})
(def expected-evict-tx [:xtdb.api/evict #uuid "f2eed61a-1928-4d75-8620-debfc23eae8d"])

(def var->tx-ops
  [{:transaction-type
    {:put {:id-type :keyword, :xt-id "id1", :document {:fields {"key" {:kind {:string-value "value"}}}}}}}
   {:transaction-type
    {:evict {:document-id "45", :id-type :int}}}
   {:transaction-type
    {:delete {:document-id "f2eed61a-1928-4d75-8620-debfc23eae8d", :id-type :uuid}}}])

(deftest tx-log-adapters-testing
  (testing "Testing if put transactions parses correctly"
    (is (= (tx-log/->put var->put-tx) expected-put-tx)))
  (testing "Testing if delete transactions parses correctly"
    (is (= (tx-log/->delete var->delete-tx) expected-delete-tx)))
  (testing "Testing if evict transactions parses correctly"
    (is (= (tx-log/->evict var->evict-tx) expected-evict-tx)))
  (testing "Testing if proto tx-ops becomes xtdb datalog transaction"
    (let [tx-ops  (tx-log/proto->tx-log var->tx-ops)]
      (is (= tx-ops
             [[:xtdb.api/put {:xt/id :id1, :key "value"}] [:xtdb.api/evict 45] [:xtdb.api/delete #uuid "f2eed61a-1928-4d75-8620-debfc23eae8d"]])))))

(deftest xtdb-edn->proto-test
  (testing "Testing if submit tx response can be parsed to proto"
    (is (=
         (tx-log/xtdb->proto {:xtdb.api/tx-id 0, :xtdb.api/tx-time #inst "2023-05-20T18:12:24.836-00:00"})
         {:tx-time "2023-05-20T13:12:24.836-05:00", :tx-id 0}))))