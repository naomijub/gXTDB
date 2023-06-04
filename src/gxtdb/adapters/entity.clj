(ns gxtdb.adapters.entity
  (:require [gxtdb.utils :as utils]))

(defn entity-tx->proto [entity-tx-response]
  {:xt-id (-> entity-tx-response :xt/id str),
   :content-hash (-> entity-tx-response :xtdb.api/content-hash str),
   :valid-time (-> entity-tx-response :xtdb.api/valid-time utils/->inst-str),
   :tx-time (-> entity-tx-response :xtdb.api/tx-time utils/->inst-str),
   :tx-id (:xtdb.api/tx-id entity-tx-response)})
