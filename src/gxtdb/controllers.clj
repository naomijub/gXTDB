(ns gxtdb.controllers
  (:require [gxtdb.adapters.db :refer [->db-basis]]
            [gxtdb.adapters.entity :refer [entity-tx->proto]]
            [gxtdb.adapters.tx-log :as tx-log-adapter]
            [gxtdb.logic.time :refer [assoc-some-time]]
            [gxtdb.utils :refer [->id open-db]]
            [xtdb.api :as xt]))

(defn submit-tx [xtdb-node tx-ops tx-time]
  (let [tx-log (tx-log-adapter/proto->tx-log tx-ops)
        xt-response (xt/submit-tx xtdb-node tx-log (assoc-some-time {} ::xt/tx-time tx-time))]
    (tx-log-adapter/xtdb->proto xt-response)))

(defn entity-tx [xtdb-node params]
  (let [id (->id (:id-type params) (:entity-id params))
        db-basis (->db-basis params)
        db (open-db xtdb-node (:open-snapshot params) db-basis)]
    (->> id (xt/entity-tx db) entity-tx->proto)))
