(ns gxtdb.controllers
  (:require [gxtdb.adapters.tx-log :as tx-log-adapter]
            [gxtdb.adapters.tx-time :as tx-time-adapter]
            [xtdb.api :as xt]))

(defn submit-tx [xtdb-node tx-ops tx-time]
  (let [time   (tx-time-adapter/->clj-time tx-time)
        tx-log (tx-log-adapter/proto->tx-log tx-ops)
        xt-response (if (nil? time)
                      (xt/submit-tx xtdb-node tx-log)
                      (xt/submit-tx xtdb-node tx-log {::xt/tx-time time}))]
    (println (str "submit tx-time: " {::xt/tx-time time}))
    (tx-log-adapter/xtdb->proto xt-response)))