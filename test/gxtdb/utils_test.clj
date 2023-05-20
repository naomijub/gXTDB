#_{:clj-kondo/ignore [:refer-all]}
(ns gxtdb.utils-test
  (:require [clojure.test :refer :all]
            [gxtdb.utils :refer :all]))

;; assoc-some-str tests
(deftest assoc-some-str-test
  (testing "Should associate a key with a value when the value is not nil"
    (let [input {:foo 42}]
      (is (= (assoc-some-str input :bar "hello")
             {:foo 42 :bar {:value {:some "hello"}}}))))

  (testing "Should associate a key with {:value {:none {}}} when the value is nil"
    (let [input {:foo 42}]
      (is (= (assoc-some-str input :bar nil)
             {:foo 42 :bar {:value {:none {}}}})))))

;; assoc-with-fn tests
(deftest assoc-with-fn-test
  (testing "Should associate a key in a map if the function applied to value is non-nil"
    (let [input {:foo 42}]
      (is (= (assoc-with-fn input :bar "hello" str)
             {:foo 42 :bar "hello"}))))

  (testing "Should not associate a key in a map if the value is nil"
    (let [input {:foo 42}]
      (is (= (assoc-with-fn input :bar nil str)
             {:foo 42})))))

;; nil->default tests
(deftest nil->default-test
  (testing "Should associate a key with the default value when the value is nil"
    (let [input {:foo nil}]
      (is (= (nil->default input :foo nil "default")
             {:foo "default"}))))

  (testing "Should not associate a key with the default value when the value is not nil"
    (let [input {:foo "value"}]
      (is (= (nil->default input :foo "default" :default)
             {:foo "default"})))))

;; not-nil? tests
(deftest not-nil?-test
  (testing "Should return true if the value is not nil"
    (is (not-nil? 42)))

  (testing "Should return false if the value is nil"
    (is (not (not-nil? nil)))))

;; ->inst tests
(deftest ->inst-test
  (testing "Should convert a valid string representation of an instant to an instance of java.time.Instant"
    (let [input "2023-05-19T10:30:00Z"]
      (is (inst? (->inst input)))))

  (testing "Should return nil if the input is an invalid string representation of an instant"
    (let [input "invalid-instant"]
      (is (nil? (->inst input))))))

;; ->inst-str tests
(deftest ->inst-str-test
  (testing "Should convert an instance of java.time.Instant to a formatted string representation"
    (let [input (->inst  "2023-05-19T10:30:00Z")]
      (is (string? (->inst-str input))))))

;; edn-or-str tests
(deftest edn-or-str-test
  (testing "Should return a keyword when the value starts with a colon"
    (is (= (edn-or-str ":keyword") :keyword)))

  (testing "Should return the original value when it doesn't start with a colon"
    (is (= (edn-or-str "value") "value")))

  (testing "Should handle empty string as input"
    (is (= (edn-or-str "") ""))))

;; str->keyword tests
(deftest str->keyword-test
  (testing "Should convert a string to a keyword"
    (is (= (str->keyword "my-keyword") :my-keyword)))

  (testing "Should handle whitespace in the string and replace it with hyphen"
    (is (= (str->keyword "my keyword") :my-keyword)))

  (testing "Should handle empty string as input"
    (is (= (str->keyword "") nil))))

;; ->id tests
(deftest ->id-test
  (testing "Should convert a string ID to a keyword when id-type is :keyword"
    (is (= (->id :keyword "my-id") :my-id)))

  (testing "Should parse a valid integer string and return the parsed integer when id-type is :int"
    (is (= (->id :int "42") 42)))

  (testing "Should return the original string ID when id-type is :string"
    (is (= (->id :string "my-id") "my-id")))

  (testing "Should parse a valid UUID string and return the parsed UUID when id-type is :uuid"
    (is (uuid? (->id :uuid "08d1fc6e-85f9-4a55-b0b9-3fd08963c375"))))

  (testing "Should return the original value when id-type is not recognized"
    (is (= (->id :unknown "my-id") :my-id)))

  (testing "Should handle empty string as input"
    (is (= (->id :keyword "") nil))))