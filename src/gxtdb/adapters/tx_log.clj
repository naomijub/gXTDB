(ns gxtdb.adapters.tx-log
  (:require [gxtdb.utils :as utils]))

(defn ->tx-log [ops]
  (let [transaction (-> ops :transaction-type)
        transaction-type (-> transaction keys first)]
    (println (str "---" "\n" transaction-type "\n" transaction "\n\n\n"))))

(defn proto->tx-log [tx-ops]
  (println (str "ARROZ" "\n\n\n" tx-ops "\n\n"))
  (mapv ->tx-log tx-ops))
