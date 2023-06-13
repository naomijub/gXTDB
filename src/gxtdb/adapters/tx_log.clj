(ns gxtdb.adapters.tx-log
  (:require [gxtdb.adapters.json :as json]
            [gxtdb.adapters.tx-time :as tx-time]
            [gxtdb.logic.time :refer [conj-some-valid-timerange]]
            [gxtdb.utils :as utils]
            [xtdb.api :as xt]))

(defn ->evict [transaction]
  (let [transaction (:evict transaction)
        id (:document-id transaction)
        id-type (:id-type transaction)]
    [::xt/evict (utils/->id id-type id)]))

(defn ->delete [transaction]
  (let [transaction (:delete transaction)
        id (:document-id transaction)
        id-type (:id-type transaction)]
    [::xt/delete (utils/->id id-type id)]))

(defn ->put [transaction]
  (let [transaction (:put transaction)
        id (:xt-id transaction)
        id-type (:id-type transaction)
        document (:document transaction)
        valid-time (-> transaction :valid-time tx-time/->clj-time)
        end-valid-time (-> transaction :end-valid-time tx-time/->clj-time)]
    (conj-some-valid-timerange
     [::xt/put (into {:xt/id (utils/->id id-type id)} (json/value-record->edn {:kind {:struct-value  document}}))]
     valid-time
     end-valid-time)))

(defn ->match [transaction]
  (let [transaction (:match transaction)
        id (:document-id transaction)
        id-type (:id-type transaction)
        document (:document transaction)]
    [::xt/match (into {:xt/id (utils/->id id-type id)} (json/value-record->edn {:kind {:struct-value document}}))]))

(defn ->tx-log [ops]
  (let [transaction (:transaction-type ops)
        transaction-type (-> transaction keys first)
        xt-converted-type (->> transaction-type name (keyword ":xt"))]
    (case transaction-type
      :put (->put transaction)
      :delete (->delete transaction)
      :evict (->evict transaction)
      :match (->match transaction)
      :else (throw (str "Transaction type " xt-converted-type " not implemented")))))

(defn proto->tx-log [tx-ops]
  (mapv ->tx-log tx-ops))

(defn xtdb->proto [edn]
  {:tx-time (-> edn :xtdb.api/tx-time utils/->inst-str) :tx-id (:xtdb.api/tx-id edn)})
