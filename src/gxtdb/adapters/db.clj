(ns gxtdb.adapters.db
  (:require [gxtdb.utils :as utils]))

(defn db-basis->proto [db-basis-response]
  {:xt-id (-> db-basis-response :xt/id str)
   :valid-time (-> db-basis-response :xtdb.api/valid-time utils/->inst-str)
   :tx-time (-> db-basis-response :xtdb.api/tx-time utils/->inst-str)})
