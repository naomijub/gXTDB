(ns gxtdb.controllers
  (:require [clojure.pprint :as pprint]
            [gxtdb.adapters.db :refer [->db-basis]]
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
    (println "TX:\n")
    (pprint/pprint (xt/entity-tx db id))
    (println "//")
    (pprint/pprint (->> id (xt/entity-tx db) entity-tx->proto))
    (println "\nEND\n")
    (->> id (xt/entity-tx db) entity-tx->proto) #_EntityTxResponse-defaults))

(comment
  (def actual {:xt/id #xtdb/id "4e89d81a2e6fb4be2578d245fd8511c1f4ad0b58",
               :xtdb.api/content-hash
               #xtdb/id "9863c9ea3bb26e49759e0381db0bea848955a0db",
               :xtdb.api/valid-time #inst "2023-06-16T21:23:12.448-00:00",
               :xtdb.api/tx-time #inst "2023-06-16T21:23:12.448-00:00",
               :xtdb.api/tx-id 0})

  (def expected {:xt-id "4e89d81a2e6fb4be2578d245fd8511c1f4ad0b58",
                 :content-hash "9863c9ea3bb26e49759e0381db0bea848955a0db",
                 :valid-time "Fri Jun 16 16:23:12 CDT 2023",
                 :tx-time "Fri Jun 16 16:23:12 CDT 2023",
                 :tx-id 0})
  (=  expected (entity-tx->proto actual))
  ())