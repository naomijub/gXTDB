(ns gxtdb.adapters.db
  (:require [gxtdb.adapters.tx-time :refer [->clj-time]]
            [gxtdb.utils :refer [not-nil?]]
            [xtdb.api :as xt]))

(defn ->db-basis [params]
  (let [valid-time (some-> params :valid-time ->clj-time)
        tx-time (some-> params :tx-time ->clj-time)
        tx-id (some-> params :tx-id :value :some)]
    (case [(not-nil? valid-time) (not-nil? tx-time) (not-nil? tx-id)]
      [true true true] {::xt/valid-time valid-time}
      [true false false] {::xt/valid-time valid-time}
      [true true false] {::xt/valid-time valid-time ::xt/tx-time tx-time}
      [true false true] {::xt/valid-time valid-time ::xt/tx tx-id}
      [false true false] {::xt/tx-time tx-time}
      [false false true] {::xt/tx tx-id}
      nil)))

