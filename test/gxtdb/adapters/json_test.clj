(ns gxtdb.adapters.json-test
  (:require [clojure.test :refer [deftest testing is]]
            [gxtdb.adapters.json :as json]))

(def xtdb-struct-with-whitespaced-keys
  {:kind {:struct-value {:fields
                         {"painting ids"
                          {:kind {:list-value {:values [{:kind {:number-value 2.0}}
                                                        {:kind {:number-value 3.0}}
                                                        {:kind {:number-value 76.0}}
                                                        {:kind {:number-value 3.0}}]}}}
                          "age" {:kind {:number-value 200.0}}
                          "is-artist?" {:kind {:bool-value true}}
                          "full name"
                          {:kind {:struct-value {:fields {"last" {:kind {:string-value "picasso"}}
                                                          "first" {:kind {:string-value "pablo"}}}}}}
                          "name" {:kind {:string-value "pablo picasso"}}}}}})

(def xtdb-struct
  {:kind {:struct-value {:fields
                         {"painting-ids"
                          {:kind {:list-value {:values [{:kind {:number-value 2.0}}
                                                        {:kind {:number-value 3.0}}
                                                        {:kind {:number-value 76.0}}
                                                        {:kind {:number-value 3.0}}]}}}
                          "age" {:kind {:number-value 200.0}}
                          "is-artist?" {:kind {:bool-value true}}
                          "full-name"
                          {:kind {:struct-value {:fields {"last" {:kind {:string-value "picasso"}}
                                                          "first" {:kind {:string-value "pablo"}}}}}}
                          "name" {:kind {:string-value "pablo picasso"}}}}}})

(def json {:painting-ids [2.0 3.0 76.0 3.0]
           :age 200.0
           :is-artist? true
           :full-name {:last "picasso" :first "pablo"}
           :name "pablo picasso"})

(deftest json-value-test
  (testing "convert proto to value"
    (is (= json (json/value-record->edn xtdb-struct))))

  (testing "whitespaced keys are equivalent to kebab cased keys"
    (is (= (json/value-record->edn xtdb-struct-with-whitespaced-keys)
           (json/value-record->edn xtdb-struct))))

  (testing "roundtrip"
    (is (= xtdb-struct
           (-> (json/value-record->edn xtdb-struct)
               json/edn->value-record)))))
