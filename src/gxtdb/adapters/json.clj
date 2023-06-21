(ns gxtdb.adapters.json
  (:require [gxtdb.utils :as utils]))

(defn value-record->edn [record]
  (let [kind (:kind record)
        key  (-> kind keys first)]
    (case key
      :number-value (:number-value kind)
      :bool-value (:bool-value kind)
      :string-value (-> kind :string-value str)
      :list-value (->> kind :list-value :values (mapv value-record->edn))
      :struct-value (->> kind :struct-value :fields (map (fn [[k v]] {(utils/str->keyword k) (value-record->edn v)})) (reduce into {}))
      "")))

(defn edn->proto-struct [edn]
  (cond
    (number? edn) {:kind {:number-value edn}}
    (string? edn) {:kind {:string-value edn}}
    (keyword? edn) {:kind {:string-value (name edn)}}
    (boolean? edn) {:kind {:bool-value edn}}
    (map? edn) {:kind {:struct-value {:fields (->> edn (map (fn [[k v]] {(keyword k) (edn->proto-struct v)})) (reduce into {}))}}}
    (vector? edn) {:kind {:list-value {:values (mapv edn->proto-struct edn)}}}
    :else {:hello "world"}))
