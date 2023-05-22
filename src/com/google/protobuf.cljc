;;;----------------------------------------------------------------------------------
;;; Generated by protoc-gen-clojure.  DO NOT EDIT
;;;
;;; Message Implementation of package com.google.protobuf
;;;----------------------------------------------------------------------------------
(ns com.google.protobuf
  (:require [protojure.protobuf.protocol :as pb]
            [protojure.protobuf.serdes.core :as serdes.core]
            [protojure.protobuf.serdes.complex :as serdes.complex]
            [protojure.protobuf.serdes.utils :refer [tag-map]]
            [protojure.protobuf.serdes.stream :as serdes.stream]
            [com.google.protobuf :as com.google.protobuf]
            [clojure.set :as set]
            [clojure.spec.alpha :as s]))

;;----------------------------------------------------------------------------------
;;----------------------------------------------------------------------------------
;; Forward declarations
;;----------------------------------------------------------------------------------
;;----------------------------------------------------------------------------------

(declare cis->ListValue)
(declare ecis->ListValue)
(declare new-ListValue)
(declare cis->Empty)
(declare ecis->Empty)
(declare new-Empty)
(declare cis->Struct)
(declare ecis->Struct)
(declare new-Struct)
(declare cis->StatusResponse)
(declare ecis->StatusResponse)
(declare new-StatusResponse)
(declare cis->Evict)
(declare ecis->Evict)
(declare new-Evict)
(declare cis->Struct-FieldsEntry)
(declare ecis->Struct-FieldsEntry)
(declare new-Struct-FieldsEntry)
(declare cis->Put)
(declare ecis->Put)
(declare new-Put)
(declare cis->Delete)
(declare ecis->Delete)
(declare new-Delete)
(declare cis->SubmitRequest)
(declare ecis->SubmitRequest)
(declare new-SubmitRequest)
(declare cis->SubmitResponse)
(declare ecis->SubmitResponse)
(declare new-SubmitResponse)
(declare cis->Value)
(declare ecis->Value)
(declare new-Value)
(declare cis->Transaction)
(declare ecis->Transaction)
(declare new-Transaction)
(declare cis->OptionString)
(declare ecis->OptionString)
(declare new-OptionString)

;;----------------------------------------------------------------------------------
;;----------------------------------------------------------------------------------
;; Enumerations
;;----------------------------------------------------------------------------------
;;----------------------------------------------------------------------------------

;-----------------------------------------------------------------------------
; NullValue
;-----------------------------------------------------------------------------
(def NullValue-default :null-value)

(def NullValue-val2label {0 :null-value})

(def NullValue-label2val (set/map-invert NullValue-val2label))

(defn cis->NullValue [is]
  (let [val (serdes.core/cis->Enum is)]
    (get NullValue-val2label val val)))

(defn- get-NullValue [value]
  {:pre [(or (int? value) (contains? NullValue-label2val value))]}
  (get NullValue-label2val value value))

(defn write-NullValue
  ([tag value os] (write-NullValue tag {:optimize false} value os))
  ([tag options value os]
   (serdes.core/write-Enum tag options (get-NullValue value) os)))

;;----------------------------------------------------------------------------------
;;----------------------------------------------------------------------------------
;; Value-kind's oneof Implementations
;;----------------------------------------------------------------------------------
;;----------------------------------------------------------------------------------

(defn convert-Value-kind [origkeyval]
  (cond
    (get-in origkeyval [:kind :null-value]) origkeyval
    (get-in origkeyval [:kind :number-value]) origkeyval
    (get-in origkeyval [:kind :string-value]) origkeyval
    (get-in origkeyval [:kind :bool-value]) origkeyval
    (get-in origkeyval [:kind :struct-value]) (update-in origkeyval [:kind :struct-value] new-Struct)
    (get-in origkeyval [:kind :list-value]) (update-in origkeyval [:kind :list-value] new-ListValue)
    :default origkeyval))

(defn write-Value-kind [kind os]
  (let [field (first kind)
        k (when-not (nil? field) (key field))
        v (when-not (nil? field) (val field))]
    (case k
      :null-value (write-NullValue 1  {:optimize false} v os)
      :number-value (serdes.core/write-Double 2  {:optimize false} v os)
      :string-value (serdes.core/write-String 3  {:optimize false} v os)
      :bool-value (serdes.core/write-Bool 4  {:optimize false} v os)
      :struct-value (serdes.core/write-embedded 5 v os)
      :list-value (serdes.core/write-embedded 6 v os)
      nil)))

;;----------------------------------------------------------------------------------
;;----------------------------------------------------------------------------------
;; Transaction-transaction-type's oneof Implementations
;;----------------------------------------------------------------------------------
;;----------------------------------------------------------------------------------

(defn convert-Transaction-transaction-type [origkeyval]
  (cond
    (get-in origkeyval [:transaction-type :put]) (update-in origkeyval [:transaction-type :put] new-Put)
    (get-in origkeyval [:transaction-type :delete]) (update-in origkeyval [:transaction-type :delete] new-Delete)
    (get-in origkeyval [:transaction-type :evict]) (update-in origkeyval [:transaction-type :evict] new-Evict)
    :default origkeyval))

(defn write-Transaction-transaction-type [transaction-type os]
  (let [field (first transaction-type)
        k (when-not (nil? field) (key field))
        v (when-not (nil? field) (val field))]
    (case k
      :put (serdes.core/write-embedded 1 v os)
      :delete (serdes.core/write-embedded 2 v os)
      :evict (serdes.core/write-embedded 3 v os)
      nil)))

;;----------------------------------------------------------------------------------
;;----------------------------------------------------------------------------------
;; OptionString-value's oneof Implementations
;;----------------------------------------------------------------------------------
;;----------------------------------------------------------------------------------

(defn convert-OptionString-value [origkeyval]
  (cond
    (get-in origkeyval [:value :none]) (update-in origkeyval [:value :none] new-Empty)
    (get-in origkeyval [:value :some]) origkeyval
    :default origkeyval))

(defn write-OptionString-value [value os]
  (let [field (first value)
        k (when-not (nil? field) (key field))
        v (when-not (nil? field) (val field))]
    (case k
      :none (serdes.core/write-embedded 1 v os)
      :some (serdes.core/write-String 2  {:optimize false} v os)
      nil)))

;;----------------------------------------------------------------------------------
;;----------------------------------------------------------------------------------
;; Message Implementations
;;----------------------------------------------------------------------------------
;;----------------------------------------------------------------------------------

;-----------------------------------------------------------------------------
; ListValue
;-----------------------------------------------------------------------------
(defrecord ListValue-record [values]
  pb/Writer
  (serialize [this os]
    (serdes.complex/write-repeated serdes.core/write-embedded 1 (:values this) os))
  pb/TypeReflection
  (gettype [this]
    "com.google.protobuf.ListValue"))

(s/def ::ListValue-spec (s/keys :opt-un []))
(def ListValue-defaults {:values []})

(defn cis->ListValue
  "CodedInputStream to ListValue"
  [is]
  (map->ListValue-record (tag-map ListValue-defaults (fn [tag index] (case index 1 [:values (serdes.complex/cis->repeated ecis->Value is)] [index (serdes.core/cis->undefined tag is)])) is)))

(defn ecis->ListValue
  "Embedded CodedInputStream to ListValue"
  [is]
  (serdes.core/cis->embedded cis->ListValue is))

(defn new-ListValue
  "Creates a new instance from a map, similar to map->ListValue except that
  it properly accounts for nested messages, when applicable.
  "
  [init]
  {:pre [(if (s/valid? ::ListValue-spec init) true (throw (ex-info "Invalid input" (s/explain-data ::ListValue-spec init))))]}
  (-> (merge ListValue-defaults init)
      (cond-> (some? (get init :values)) (update :values #(map new-Value %)))
      (map->ListValue-record)))

(defn pb->ListValue
  "Protobuf to ListValue"
  [input]
  (cis->ListValue (serdes.stream/new-cis input)))

(def ^:protojure.protobuf.any/record ListValue-meta {:type "com.google.protobuf.ListValue" :decoder pb->ListValue})

;-----------------------------------------------------------------------------
; Empty
;-----------------------------------------------------------------------------
(defrecord Empty-record []
  pb/Writer
  (serialize [this os])
  pb/TypeReflection
  (gettype [this]
    "com.google.protobuf.Empty"))

(s/def ::Empty-spec (s/keys :opt-un []))
(def Empty-defaults {})

(defn cis->Empty
  "CodedInputStream to Empty"
  [is]
  (map->Empty-record (tag-map Empty-defaults (fn [tag index] (case index [index (serdes.core/cis->undefined tag is)])) is)))

(defn ecis->Empty
  "Embedded CodedInputStream to Empty"
  [is]
  (serdes.core/cis->embedded cis->Empty is))

(defn new-Empty
  "Creates a new instance from a map, similar to map->Empty except that
  it properly accounts for nested messages, when applicable.
  "
  [init]
  {:pre [(if (s/valid? ::Empty-spec init) true (throw (ex-info "Invalid input" (s/explain-data ::Empty-spec init))))]}
  (map->Empty-record (merge Empty-defaults init)))

(defn pb->Empty
  "Protobuf to Empty"
  [input]
  (cis->Empty (serdes.stream/new-cis input)))

(def ^:protojure.protobuf.any/record Empty-meta {:type "com.google.protobuf.Empty" :decoder pb->Empty})

;-----------------------------------------------------------------------------
; Struct
;-----------------------------------------------------------------------------
(defrecord Struct-record [fields]
  pb/Writer
  (serialize [this os]
    (serdes.complex/write-map new-Struct-FieldsEntry 1 (:fields this) os))
  pb/TypeReflection
  (gettype [this]
    "com.google.protobuf.Struct"))

(s/def ::Struct-spec (s/keys :opt-un []))
(def Struct-defaults {:fields []})

(defn cis->Struct
  "CodedInputStream to Struct"
  [is]
  (map->Struct-record (tag-map Struct-defaults (fn [tag index] (case index 1 [:fields (serdes.complex/cis->map ecis->Struct-FieldsEntry is)] [index (serdes.core/cis->undefined tag is)])) is)))

(defn ecis->Struct
  "Embedded CodedInputStream to Struct"
  [is]
  (serdes.core/cis->embedded cis->Struct is))

(defn new-Struct
  "Creates a new instance from a map, similar to map->Struct except that
  it properly accounts for nested messages, when applicable.
  "
  [init]
  {:pre [(if (s/valid? ::Struct-spec init) true (throw (ex-info "Invalid input" (s/explain-data ::Struct-spec init))))]}
  (map->Struct-record (merge Struct-defaults init)))

(defn pb->Struct
  "Protobuf to Struct"
  [input]
  (cis->Struct (serdes.stream/new-cis input)))

(def ^:protojure.protobuf.any/record Struct-meta {:type "com.google.protobuf.Struct" :decoder pb->Struct})

;-----------------------------------------------------------------------------
; StatusResponse
;-----------------------------------------------------------------------------
(defrecord StatusResponse-record [version index-version kv-store estimate-num-keys size revision consumer-state]
  pb/Writer
  (serialize [this os]
    (serdes.core/write-String 1  {:optimize true} (:version this) os)
    (serdes.core/write-Int32 2  {:optimize true} (:index-version this) os)
    (serdes.core/write-String 3  {:optimize true} (:kv-store this) os)
    (serdes.core/write-Int32 4  {:optimize true} (:estimate-num-keys this) os)
    (serdes.core/write-Int64 5  {:optimize true} (:size this) os)
    (serdes.core/write-embedded 6 (:revision this) os)
    (serdes.core/write-embedded 7 (:consumer-state this) os))
  pb/TypeReflection
  (gettype [this]
    "com.google.protobuf.StatusResponse"))

(s/def :com.google.protobuf.StatusResponse/version string?)
(s/def :com.google.protobuf.StatusResponse/index-version int?)
(s/def :com.google.protobuf.StatusResponse/kv-store string?)
(s/def :com.google.protobuf.StatusResponse/estimate-num-keys int?)
(s/def :com.google.protobuf.StatusResponse/size int?)

(s/def ::StatusResponse-spec (s/keys :opt-un [:com.google.protobuf.StatusResponse/version :com.google.protobuf.StatusResponse/index-version :com.google.protobuf.StatusResponse/kv-store :com.google.protobuf.StatusResponse/estimate-num-keys :com.google.protobuf.StatusResponse/size]))
(def StatusResponse-defaults {:version "" :index-version 0 :kv-store "" :estimate-num-keys 0 :size 0})

(defn cis->StatusResponse
  "CodedInputStream to StatusResponse"
  [is]
  (map->StatusResponse-record (tag-map StatusResponse-defaults (fn [tag index] (case index 1 [:version (serdes.core/cis->String is)] 2 [:index-version (serdes.core/cis->Int32 is)] 3 [:kv-store (serdes.core/cis->String is)] 4 [:estimate-num-keys (serdes.core/cis->Int32 is)] 5 [:size (serdes.core/cis->Int64 is)] 6 [:revision (ecis->OptionString is)] 7 [:consumer-state (ecis->OptionString is)] [index (serdes.core/cis->undefined tag is)])) is)))

(defn ecis->StatusResponse
  "Embedded CodedInputStream to StatusResponse"
  [is]
  (serdes.core/cis->embedded cis->StatusResponse is))

(defn new-StatusResponse
  "Creates a new instance from a map, similar to map->StatusResponse except that
  it properly accounts for nested messages, when applicable.
  "
  [init]
  {:pre [(if (s/valid? ::StatusResponse-spec init) true (throw (ex-info "Invalid input" (s/explain-data ::StatusResponse-spec init))))]}
  (-> (merge StatusResponse-defaults init)
      (cond-> (some? (get init :revision)) (update :revision new-OptionString))
      (cond-> (some? (get init :consumer-state)) (update :consumer-state new-OptionString))
      (map->StatusResponse-record)))

(defn pb->StatusResponse
  "Protobuf to StatusResponse"
  [input]
  (cis->StatusResponse (serdes.stream/new-cis input)))

(def ^:protojure.protobuf.any/record StatusResponse-meta {:type "com.google.protobuf.StatusResponse" :decoder pb->StatusResponse})

;-----------------------------------------------------------------------------
; Evict
;-----------------------------------------------------------------------------
(defrecord Evict-record [document-id]
  pb/Writer
  (serialize [this os]
    (serdes.core/write-String 1  {:optimize true} (:document-id this) os))
  pb/TypeReflection
  (gettype [this]
    "com.google.protobuf.Evict"))

(s/def :com.google.protobuf.Evict/document-id string?)
(s/def ::Evict-spec (s/keys :opt-un [:com.google.protobuf.Evict/document-id]))
(def Evict-defaults {:document-id ""})

(defn cis->Evict
  "CodedInputStream to Evict"
  [is]
  (map->Evict-record (tag-map Evict-defaults (fn [tag index] (case index 1 [:document-id (serdes.core/cis->String is)] [index (serdes.core/cis->undefined tag is)])) is)))

(defn ecis->Evict
  "Embedded CodedInputStream to Evict"
  [is]
  (serdes.core/cis->embedded cis->Evict is))

(defn new-Evict
  "Creates a new instance from a map, similar to map->Evict except that
  it properly accounts for nested messages, when applicable.
  "
  [init]
  {:pre [(if (s/valid? ::Evict-spec init) true (throw (ex-info "Invalid input" (s/explain-data ::Evict-spec init))))]}
  (map->Evict-record (merge Evict-defaults init)))

(defn pb->Evict
  "Protobuf to Evict"
  [input]
  (cis->Evict (serdes.stream/new-cis input)))

(def ^:protojure.protobuf.any/record Evict-meta {:type "com.google.protobuf.Evict" :decoder pb->Evict})

;-----------------------------------------------------------------------------
; Struct-FieldsEntry
;-----------------------------------------------------------------------------
(defrecord Struct-FieldsEntry-record [key value]
  pb/Writer
  (serialize [this os]
    (serdes.core/write-String 1  {:optimize true} (:key this) os)
    (serdes.core/write-embedded 2 (:value this) os))
  pb/TypeReflection
  (gettype [this]
    "com.google.protobuf.Struct-FieldsEntry"))

(s/def :com.google.protobuf.Struct-FieldsEntry/key string?)

(s/def ::Struct-FieldsEntry-spec (s/keys :opt-un [:com.google.protobuf.Struct-FieldsEntry/key]))
(def Struct-FieldsEntry-defaults {:key ""})

(defn cis->Struct-FieldsEntry
  "CodedInputStream to Struct-FieldsEntry"
  [is]
  (map->Struct-FieldsEntry-record (tag-map Struct-FieldsEntry-defaults (fn [tag index] (case index 1 [:key (serdes.core/cis->String is)] 2 [:value (ecis->Value is)] [index (serdes.core/cis->undefined tag is)])) is)))

(defn ecis->Struct-FieldsEntry
  "Embedded CodedInputStream to Struct-FieldsEntry"
  [is]
  (serdes.core/cis->embedded cis->Struct-FieldsEntry is))

(defn new-Struct-FieldsEntry
  "Creates a new instance from a map, similar to map->Struct-FieldsEntry except that
  it properly accounts for nested messages, when applicable.
  "
  [init]
  {:pre [(if (s/valid? ::Struct-FieldsEntry-spec init) true (throw (ex-info "Invalid input" (s/explain-data ::Struct-FieldsEntry-spec init))))]}
  (-> (merge Struct-FieldsEntry-defaults init)
      (cond-> (some? (get init :value)) (update :value new-Value))
      (map->Struct-FieldsEntry-record)))

(defn pb->Struct-FieldsEntry
  "Protobuf to Struct-FieldsEntry"
  [input]
  (cis->Struct-FieldsEntry (serdes.stream/new-cis input)))

(def ^:protojure.protobuf.any/record Struct-FieldsEntry-meta {:type "com.google.protobuf.Struct-FieldsEntry" :decoder pb->Struct-FieldsEntry})

;-----------------------------------------------------------------------------
; Put
;-----------------------------------------------------------------------------
(defrecord Put-record [xt-id document]
  pb/Writer
  (serialize [this os]
    (serdes.core/write-String 1  {:optimize true} (:xt-id this) os)
    (serdes.core/write-embedded 2 (:document this) os))
  pb/TypeReflection
  (gettype [this]
    "com.google.protobuf.Put"))

(s/def :com.google.protobuf.Put/xt-id string?)

(s/def ::Put-spec (s/keys :opt-un [:com.google.protobuf.Put/xt-id]))
(def Put-defaults {:xt-id ""})

(defn cis->Put
  "CodedInputStream to Put"
  [is]
  (map->Put-record (tag-map Put-defaults (fn [tag index] (case index 1 [:xt-id (serdes.core/cis->String is)] 2 [:document (ecis->Value is)] [index (serdes.core/cis->undefined tag is)])) is)))

(defn ecis->Put
  "Embedded CodedInputStream to Put"
  [is]
  (serdes.core/cis->embedded cis->Put is))

(defn new-Put
  "Creates a new instance from a map, similar to map->Put except that
  it properly accounts for nested messages, when applicable.
  "
  [init]
  {:pre [(if (s/valid? ::Put-spec init) true (throw (ex-info "Invalid input" (s/explain-data ::Put-spec init))))]}
  (-> (merge Put-defaults init)
      (cond-> (some? (get init :document)) (update :document new-Value))
      (map->Put-record)))

(defn pb->Put
  "Protobuf to Put"
  [input]
  (cis->Put (serdes.stream/new-cis input)))

(def ^:protojure.protobuf.any/record Put-meta {:type "com.google.protobuf.Put" :decoder pb->Put})

;-----------------------------------------------------------------------------
; Delete
;-----------------------------------------------------------------------------
(defrecord Delete-record [document-id]
  pb/Writer
  (serialize [this os]
    (serdes.core/write-String 1  {:optimize true} (:document-id this) os))
  pb/TypeReflection
  (gettype [this]
    "com.google.protobuf.Delete"))

(s/def :com.google.protobuf.Delete/document-id string?)
(s/def ::Delete-spec (s/keys :opt-un [:com.google.protobuf.Delete/document-id]))
(def Delete-defaults {:document-id ""})

(defn cis->Delete
  "CodedInputStream to Delete"
  [is]
  (map->Delete-record (tag-map Delete-defaults (fn [tag index] (case index 1 [:document-id (serdes.core/cis->String is)] [index (serdes.core/cis->undefined tag is)])) is)))

(defn ecis->Delete
  "Embedded CodedInputStream to Delete"
  [is]
  (serdes.core/cis->embedded cis->Delete is))

(defn new-Delete
  "Creates a new instance from a map, similar to map->Delete except that
  it properly accounts for nested messages, when applicable.
  "
  [init]
  {:pre [(if (s/valid? ::Delete-spec init) true (throw (ex-info "Invalid input" (s/explain-data ::Delete-spec init))))]}
  (map->Delete-record (merge Delete-defaults init)))

(defn pb->Delete
  "Protobuf to Delete"
  [input]
  (cis->Delete (serdes.stream/new-cis input)))

(def ^:protojure.protobuf.any/record Delete-meta {:type "com.google.protobuf.Delete" :decoder pb->Delete})

;-----------------------------------------------------------------------------
; SubmitRequest
;-----------------------------------------------------------------------------
(defrecord SubmitRequest-record [tx-ops]
  pb/Writer
  (serialize [this os]
    (serdes.complex/write-repeated serdes.core/write-embedded 1 (:tx-ops this) os))
  pb/TypeReflection
  (gettype [this]
    "com.google.protobuf.SubmitRequest"))

(s/def ::SubmitRequest-spec (s/keys :opt-un []))
(def SubmitRequest-defaults {:tx-ops []})

(defn cis->SubmitRequest
  "CodedInputStream to SubmitRequest"
  [is]
  (map->SubmitRequest-record (tag-map SubmitRequest-defaults (fn [tag index] (case index 1 [:tx-ops (serdes.complex/cis->repeated ecis->Transaction is)] [index (serdes.core/cis->undefined tag is)])) is)))

(defn ecis->SubmitRequest
  "Embedded CodedInputStream to SubmitRequest"
  [is]
  (serdes.core/cis->embedded cis->SubmitRequest is))

(defn new-SubmitRequest
  "Creates a new instance from a map, similar to map->SubmitRequest except that
  it properly accounts for nested messages, when applicable.
  "
  [init]
  {:pre [(if (s/valid? ::SubmitRequest-spec init) true (throw (ex-info "Invalid input" (s/explain-data ::SubmitRequest-spec init))))]}
  (-> (merge SubmitRequest-defaults init)
      (cond-> (some? (get init :tx-ops)) (update :tx-ops #(map new-Transaction %)))
      (map->SubmitRequest-record)))

(defn pb->SubmitRequest
  "Protobuf to SubmitRequest"
  [input]
  (cis->SubmitRequest (serdes.stream/new-cis input)))

(def ^:protojure.protobuf.any/record SubmitRequest-meta {:type "com.google.protobuf.SubmitRequest" :decoder pb->SubmitRequest})

;-----------------------------------------------------------------------------
; SubmitResponse
;-----------------------------------------------------------------------------
(defrecord SubmitResponse-record [tx-time tx-id]
  pb/Writer
  (serialize [this os]
    (serdes.core/write-String 1  {:optimize true} (:tx-time this) os)
    (serdes.core/write-Int64 2  {:optimize true} (:tx-id this) os))
  pb/TypeReflection
  (gettype [this]
    "com.google.protobuf.SubmitResponse"))

(s/def :com.google.protobuf.SubmitResponse/tx-time string?)
(s/def :com.google.protobuf.SubmitResponse/tx-id int?)
(s/def ::SubmitResponse-spec (s/keys :opt-un [:com.google.protobuf.SubmitResponse/tx-time :com.google.protobuf.SubmitResponse/tx-id]))
(def SubmitResponse-defaults {:tx-time "" :tx-id 0})

(defn cis->SubmitResponse
  "CodedInputStream to SubmitResponse"
  [is]
  (map->SubmitResponse-record (tag-map SubmitResponse-defaults (fn [tag index] (case index 1 [:tx-time (serdes.core/cis->String is)] 2 [:tx-id (serdes.core/cis->Int64 is)] [index (serdes.core/cis->undefined tag is)])) is)))

(defn ecis->SubmitResponse
  "Embedded CodedInputStream to SubmitResponse"
  [is]
  (serdes.core/cis->embedded cis->SubmitResponse is))

(defn new-SubmitResponse
  "Creates a new instance from a map, similar to map->SubmitResponse except that
  it properly accounts for nested messages, when applicable.
  "
  [init]
  {:pre [(if (s/valid? ::SubmitResponse-spec init) true (throw (ex-info "Invalid input" (s/explain-data ::SubmitResponse-spec init))))]}
  (map->SubmitResponse-record (merge SubmitResponse-defaults init)))

(defn pb->SubmitResponse
  "Protobuf to SubmitResponse"
  [input]
  (cis->SubmitResponse (serdes.stream/new-cis input)))

(def ^:protojure.protobuf.any/record SubmitResponse-meta {:type "com.google.protobuf.SubmitResponse" :decoder pb->SubmitResponse})

;-----------------------------------------------------------------------------
; Value
;-----------------------------------------------------------------------------
(defrecord Value-record [kind]
  pb/Writer
  (serialize [this os]
    (write-Value-kind  (:kind this) os))
  pb/TypeReflection
  (gettype [this]
    "com.google.protobuf.Value"))

(s/def ::Value-spec (s/keys :opt-un []))
(def Value-defaults {})

(defn cis->Value
  "CodedInputStream to Value"
  [is]
  (map->Value-record (tag-map Value-defaults (fn [tag index] (case index 1 [:kind {:null-value (cis->NullValue is)}] 2 [:kind {:number-value (serdes.core/cis->Double is)}] 3 [:kind {:string-value (serdes.core/cis->String is)}] 4 [:kind {:bool-value (serdes.core/cis->Bool is)}] 5 [:kind {:struct-value (ecis->Struct is)}] 6 [:kind {:list-value (ecis->ListValue is)}] [index (serdes.core/cis->undefined tag is)])) is)))

(defn ecis->Value
  "Embedded CodedInputStream to Value"
  [is]
  (serdes.core/cis->embedded cis->Value is))

(defn new-Value
  "Creates a new instance from a map, similar to map->Value except that
  it properly accounts for nested messages, when applicable.
  "
  [init]
  {:pre [(if (s/valid? ::Value-spec init) true (throw (ex-info "Invalid input" (s/explain-data ::Value-spec init))))]}
  (-> (merge Value-defaults init)
      (convert-Value-kind)
      (map->Value-record)))

(defn pb->Value
  "Protobuf to Value"
  [input]
  (cis->Value (serdes.stream/new-cis input)))

(def ^:protojure.protobuf.any/record Value-meta {:type "com.google.protobuf.Value" :decoder pb->Value})

;-----------------------------------------------------------------------------
; Transaction
;-----------------------------------------------------------------------------
(defrecord Transaction-record [transaction-type]
  pb/Writer
  (serialize [this os]
    (write-Transaction-transaction-type  (:transaction-type this) os))
  pb/TypeReflection
  (gettype [this]
    "com.google.protobuf.Transaction"))

(s/def ::Transaction-spec (s/keys :opt-un []))
(def Transaction-defaults {})

(defn cis->Transaction
  "CodedInputStream to Transaction"
  [is]
  (map->Transaction-record (tag-map Transaction-defaults (fn [tag index] (case index 1 [:transaction-type {:put (ecis->Put is)}] 2 [:transaction-type {:delete (ecis->Delete is)}] 3 [:transaction-type {:evict (ecis->Evict is)}] [index (serdes.core/cis->undefined tag is)])) is)))

(defn ecis->Transaction
  "Embedded CodedInputStream to Transaction"
  [is]
  (serdes.core/cis->embedded cis->Transaction is))

(defn new-Transaction
  "Creates a new instance from a map, similar to map->Transaction except that
  it properly accounts for nested messages, when applicable.
  "
  [init]
  {:pre [(if (s/valid? ::Transaction-spec init) true (throw (ex-info "Invalid input" (s/explain-data ::Transaction-spec init))))]}
  (-> (merge Transaction-defaults init)
      (convert-Transaction-transaction-type)
      (map->Transaction-record)))

(defn pb->Transaction
  "Protobuf to Transaction"
  [input]
  (cis->Transaction (serdes.stream/new-cis input)))

(def ^:protojure.protobuf.any/record Transaction-meta {:type "com.google.protobuf.Transaction" :decoder pb->Transaction})

;-----------------------------------------------------------------------------
; OptionString
;-----------------------------------------------------------------------------
(defrecord OptionString-record [value]
  pb/Writer
  (serialize [this os]
    (write-OptionString-value  (:value this) os))
  pb/TypeReflection
  (gettype [this]
    "com.google.protobuf.OptionString"))

(s/def ::OptionString-spec (s/keys :opt-un []))
(def OptionString-defaults {})

(defn cis->OptionString
  "CodedInputStream to OptionString"
  [is]
  (map->OptionString-record (tag-map OptionString-defaults (fn [tag index] (case index 1 [:value {:none (ecis->Empty is)}] 2 [:value {:some (serdes.core/cis->String is)}] [index (serdes.core/cis->undefined tag is)])) is)))

(defn ecis->OptionString
  "Embedded CodedInputStream to OptionString"
  [is]
  (serdes.core/cis->embedded cis->OptionString is))

(defn new-OptionString
  "Creates a new instance from a map, similar to map->OptionString except that
  it properly accounts for nested messages, when applicable.
  "
  [init]
  {:pre [(if (s/valid? ::OptionString-spec init) true (throw (ex-info "Invalid input" (s/explain-data ::OptionString-spec init))))]}
  (-> (merge OptionString-defaults init)
      (convert-OptionString-value)
      (map->OptionString-record)))

(defn pb->OptionString
  "Protobuf to OptionString"
  [input]
  (cis->OptionString (serdes.stream/new-cis input)))

(def ^:protojure.protobuf.any/record OptionString-meta {:type "com.google.protobuf.OptionString" :decoder pb->OptionString})

