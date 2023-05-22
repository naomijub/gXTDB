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