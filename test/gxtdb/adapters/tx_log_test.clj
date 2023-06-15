(ns gxtdb.adapters.tx-log-test
  (:require [clojure.test :refer [deftest testing is]]
            [gxtdb.utils :as utils]
            [gxtdb.adapters.tx-log :as tx-log]))

(def var->put-tx
  {:put
   {:valid-time {:value {:some "2023-06-12T21:32:44.717-05:00"}}
    :end-valid-time {:value {:some "2023-06-12T21:32:44.717-05:00"}}
    :xt-id "id 2",
    :document
    {:fields
     {"painting ids"
      {:kind
       {:list-value
        {:values
         [{:kind {:number-value 2.0}}
          {:kind {:number-value 3.0}}
          {:kind {:number-value 76.0}}
          {:kind {:number-value 3.0}}]}}},
      "age" {:kind {:number-value 200.0}},
      "is-artist?" {:kind {:bool-value true}},
      "full name"
      {:kind
       {:struct-value
        {:fields
         {"last" {:kind {:string-value "picasso"}},
          "first" {:kind {:string-value "pablo"}}}}}},
      "name" {:kind {:string-value "pablo picasso"}}}}}})

(def expected-put-tx [:xtdb.api/put
                      {:xt/id :id-2,
                       :painting-ids [2.0 3.0 76.0 3.0],
                       :age 200.0,
                       :is-artist? true,
                       :full-name {:last "picasso", :first "pablo"},
                       :name "pablo picasso"}
                      #inst "2023-06-13T02:32:44.717-00:00" ;;valid-time
                      #inst "2023-06-13T02:32:44.717-00:00" ;;end-valid-time
                      ])

(def var->delete-tx {:delete {:document-id "f2eed61a-1928-4d75-8620-debfc23eae8d", :id-type :uuid}})
(def expected-delete-tx [:xtdb.api/delete #uuid "f2eed61a-1928-4d75-8620-debfc23eae8d"])

(def var->delete-tx-with-time {:delete
                               {:valid-time {:value {:some "2023-06-12T21:32:44.717-05:00"}}
                                :end-valid-time {:value {:some "2023-06-12T21:32:44.717-05:00"}}
                                :document-id "f2eed61a-1928-4d75-8620-debfc23eae8d", :id-type :uuid}})
(def expected-delete-tx-with-time [:xtdb.api/delete #uuid "f2eed61a-1928-4d75-8620-debfc23eae8d" #inst "2023-06-13T02:32:44.717-00:00" #inst "2023-06-13T02:32:44.717-00:00"])

(def var->evict-tx {:evict {:document-id "f2eed61a-1928-4d75-8620-debfc23eae8d", :id-type :uuid}})
(def expected-evict-tx [:xtdb.api/evict #uuid "f2eed61a-1928-4d75-8620-debfc23eae8d"])

(def var->match-tx {:match {:document-id "id 3", :document {:fields {"id" {:kind {:number-value 3.0}}}}}})
(def expected-match-tx [:xtdb.api/match
                        :id-3
                        {:xt/id :id-3,
                         :id 3.0}])

(def var->tx-ops
  [{:transaction-type
    {:put {:id-type :keyword, :xt-id "id1", :document {:fields {"key" {:kind {:string-value "value"}}}}}}}
   {:transaction-type
    {:evict {:document-id "45", :id-type :int}}}
   {:transaction-type
    {:delete {:document-id "f2eed61a-1928-4d75-8620-debfc23eae8d", :id-type :uuid}}}
   {:transaction-type
    {:match {:id-type :keyword, :document-id "id3", :document {:fields {"key" {:kind {:string-value "value"}}}} :valid-time {:value {:some "2023-06-12T21:32:44.717-05:00"}}}}}])

(deftest tx-log-adapters-testing
  (testing "Testing if put transactions parses correctly"
    (is (= expected-put-tx (tx-log/->put var->put-tx))))
  (testing "Testing if delete transactions parses correctly"
    (is (= expected-delete-tx (tx-log/->delete var->delete-tx))))
  (testing "Testing if delete transactions parses correctly with valid-times"
    (is (= expected-delete-tx-with-time (tx-log/->delete var->delete-tx-with-time))))
  (testing "Testing if evict transactions parses correctly"
    (is (= expected-evict-tx (tx-log/->evict var->evict-tx))))
  (testing "Testing if match transactions parses correctly"
    (is (= expected-match-tx (tx-log/->match var->match-tx))))
  (testing "Testing if proto tx-ops becomes xtdb datalog transaction"
    (let [tx-ops  (tx-log/proto->tx-log var->tx-ops)]
      (is (= [[:xtdb.api/put {:xt/id :id1, :key "value"}]
              [:xtdb.api/evict 45]
              [:xtdb.api/delete #uuid "f2eed61a-1928-4d75-8620-debfc23eae8d"]
              [:xtdb.api/match :id3 {:key "value" :xt/id :id3}  #inst "2023-06-13T02:32:44.717-00:00"]]
             tx-ops)))))

(deftest xtdb-edn->proto-test
  (testing "Testing if submit tx response can be parsed to proto"
    (let [proto (tx-log/xtdb->proto {:xtdb.api/tx-id 0, :xtdb.api/tx-time #inst "2023-05-20T18:12:24.836Z"})]
      (is (inst? (-> proto :tx-time utils/->inst)))
      (is (= 0 (:tx-id proto))))))
