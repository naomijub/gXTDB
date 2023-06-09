(ns gxtdb.controllers
  (:require [clojure.pprint :as pprint]
            [gxtdb.adapters.db :refer [->db-basis]]
            [gxtdb.adapters.entity :as adapters.entity]
            [gxtdb.adapters.tx-log :as tx-log-adapter]
            [gxtdb.logic.time :refer [assoc-some-time]]
            [gxtdb.utils :refer [->id open-db record->map]]
            [xtdb.api :as xt]))

(defn submit-tx [xtdb-node tx-ops tx-time]
  (let [tx-log (tx-log-adapter/proto->tx-log tx-ops)
        xt-response (xt/submit-tx xtdb-node tx-log (assoc-some-time {} ::xt/tx-time tx-time))]
    (tx-log-adapter/xtdb->proto xt-response)))

(defn with-tx [xtdb-node tx-ops]
  (let [tx-log (tx-log-adapter/proto->tx-log tx-ops)
        xt-response (xt/with-tx (xt/db xtdb-node) tx-log)]
    (tx-log-adapter/speculative-tx->proto (record->map xt-response))))

(defn entity-tx [xtdb-node params]
  (let [id (->id (:id-type params) (:entity-id params))
        db-basis (->db-basis params)
        db (open-db xtdb-node (:open-snapshot params) db-basis)]
    (->> id (xt/entity-tx db) adapters.entity/entity-tx->proto)))

(defn entity [xtdb-node params]
  (let [id (->id (:id-type params) (:entity-id params))
        db-basis (->db-basis params)
        db (open-db xtdb-node (:open-snapshot params) db-basis)]
    (->> id (xt/entity db) adapters.entity/entity->proto)))
