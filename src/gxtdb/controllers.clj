(ns gxtdb.controllers
  (:require [gxtdb.adapters.tx-log :as tx-log-adapter]
            [gxtdb.logic.time :refer [assoc-some-time]]
            [xtdb.api :as xt]))

(defn submit-tx [xtdb-node tx-ops tx-time]
  (let [tx-log (tx-log-adapter/proto->tx-log tx-ops)
        xt-response (xt/submit-tx xtdb-node tx-log (assoc-some-time {} ::xt/tx-time tx-time))]
    (tx-log-adapter/xtdb->proto xt-response)))