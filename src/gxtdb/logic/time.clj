(ns gxtdb.logic.time
  (:require [gxtdb.adapters.tx-time :refer [->clj-time]]))

(defn conj-some-valid-timerange [vec init-time end-time]
  (case [(nil? init-time) (nil? end-time)]
    [false true] (conj vec init-time)
    [false false] (conj vec init-time end-time)
    vec))

(defn assoc-some-time
  "Associates a key with a value in a map, if and only if the value is not nil."
  ([m k tx-time]
   (let [time (->clj-time tx-time)]
     (if (nil? time) m (assoc m k time)))))
