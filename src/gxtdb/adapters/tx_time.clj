(ns gxtdb.adapters.tx-time
  (:require [gxtdb.utils :as utils]))

(defn ->clj-time [str-time]
  (some-> str-time :value :some utils/->inst))