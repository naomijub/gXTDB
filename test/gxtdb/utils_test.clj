(ns gxtdb.utils-test
  (:require [clojure.test :refer [deftest testing is]]
            [gxtdb.utils :as utils]))

;; assoc-some-str tests
(deftest assoc-some-str-test
  (testing "Should associate a key with a value when the value is not nil"
    (let [input {:foo 42}]
      (is (= {:foo 42 :bar {:value {:some "hello"}}}
             (utils/assoc-some-str input :bar "hello")))))

  (testing "Should associate a key with {:value {:none {}}} when the value is nil"
    (let [input {:foo 42}]
      (is (= {:foo 42 :bar {:value {:none {}}}}
             (utils/assoc-some-str input :bar nil))))))

;; assoc-with-fn tests
(deftest assoc-with-fn-test
  (testing "Should associate a key in a map if the function applied to value is non-nil"
    (let [input {:foo 42}]
      (is (= {:foo 42 :bar "hello"}
             (utils/assoc-with-fn input :bar "hello" str)))))

  (testing "Should not associate a key in a map if the value is nil"
    (let [input {:foo 42}]
      (is (= {:foo 42}
             (utils/assoc-with-fn input :bar nil str))))))

;; nil->default tests
(deftest nil->default-test
  (testing "Should associate a key with the default value when the value is nil"
    (let [input {:foo nil}]
      (is (= {:foo "default"}
             (utils/nil->default input :foo nil "default")))))

  (testing "Should not associate a key with the default value when the value is not nil"
    (let [input {:foo "value"}]
      (is (= {:foo "default"}
             (utils/nil->default input :foo "default" :default))))))

;; not-nil? tests
(deftest not-nil?-test
  (testing "Should return true if the value is not nil"
    (is (utils/not-nil? 42)))

  (testing "Should return false if the value is nil"
    (is (not (utils/not-nil? nil)))))

;; ->inst tests
(deftest ->inst-test
  (testing "Should convert a valid string representation of an instant to an instance of java.time.Instant"
    (let [input "2023-05-19T10:30:00Z"]
      (is (inst? (utils/->inst input)))))

  (testing "Should return nil if the input is an invalid string representation of an instant"
    (let [input "invalid-instant"]
      (is (nil? (utils/->inst input))))))

;; ->inst-str tests
(deftest ->inst-str-test
  (testing "Should convert an instance of java.time.Instant to a formatted string representation"
    (let [input (utils/->inst  "2023-05-19T10:30:00Z")]
      (is (string? (utils/->inst-str input))))))

;; edn-or-str tests
(deftest edn-or-str-test
  (testing "Should return a keyword when the value starts with a colon"
    (is (= :keyword (utils/edn-or-str ":keyword"))))

  (testing "Should return the original value when it doesn't start with a colon"
    (is (= "value" (utils/edn-or-str "value"))))

  (testing "Should handle empty string as input"
    (is (= "" (utils/edn-or-str "")))))

;; str->keyword tests
(deftest str->keyword-test
  (testing "Should convert a string to a keyword"
    (is (= :my-keyword (utils/str->keyword "my-keyword"))))

  (testing "Should handle whitespace in the string and replace it with hyphen"
    (is (= :my-keyword (utils/str->keyword "my keyword"))))

  (testing "Should handle empty string as input"
    (is (nil? (utils/str->keyword "")))))

;; ->id tests
(deftest ->id-test
  (testing "Should convert a string ID to a keyword when id-type is :keyword"
    (is (= :my-id (utils/->id :keyword "my-id"))))

  (testing "Should parse a valid integer string and return the parsed integer when id-type is :int"
    (is (= 42 (utils/->id :int "42"))))

  (testing "Should return the original string ID when id-type is :string"
    (is (= "my-id" (utils/->id :string "my-id"))))

  (testing "Should parse a valid UUID string and return the parsed UUID when id-type is :uuid"
    (is (uuid? (utils/->id :uuid "08d1fc6e-85f9-4a55-b0b9-3fd08963c375"))))

  (testing "Should return the original value when id-type is not recognized"
    (is (= :my-id (utils/->id :unknown "my-id"))))

  (testing "Should handle empty string as input"
    (is (nil? (utils/->id :keyword "")))))
