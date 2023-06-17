(ns gxtdb.adapters.entity-test
  (:require [clojure.test :refer [deftest testing is]]
            [gxtdb.adapters.entity :refer [entity-tx->proto]]))

(def actual {:xt/id #xtdb/id "4e89d81a2e6fb4be2578d245fd8511c1f4ad0b58",
             :xtdb.api/content-hash
             #xtdb/id "9863c9ea3bb26e49759e0381db0bea848955a0db",
             :xtdb.api/valid-time #inst "2023-06-16T16:23:12.448-05:00",
             :xtdb.api/tx-time #inst "2023-06-16T16:23:12.448-05:00",
             :xtdb.api/tx-id 0})

(def expected {:xt-id "4e89d81a2e6fb4be2578d245fd8511c1f4ad0b58",
               :content-hash "9863c9ea3bb26e49759e0381db0bea848955a0db",
               :valid-time "2023-06-16T16:23:12.448-05:00",
               :tx-time "2023-06-16T16:23:12.448-05:00",
               :tx-id 0})

(deftest entity-tx->proto-test
  (testing "entity tx response map correctly parses to proto response"
    (is (=  expected (entity-tx->proto actual)))))