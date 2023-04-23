(ns gxtdb.adapters.status
  (:require [gxtdb.utils :as utils]))

(defn edn->grpc [edn]
  (->
   {:version (:xtdb.version/version  edn)
    :kv-store (:xtdb.kv/kv-store edn)
    :estimate-num-keys (:xtdb.kv/estimate-num-keys edn)
    :index-version (:xtdb.index/index-version edn)}
   (utils/nil->default :size (:xtdb.kv/size edn) 0)
   (utils/assoc-some-str :revision (:xtdb.version/revision edn))
   (utils/assoc-some-str :consumer-state (:xtdb.tx-log/consumer-state edn))))
