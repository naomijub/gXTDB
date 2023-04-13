(ns gxtdb.utils
    (:require [clojure.instant :refer [read-instant-date]]
            [clojure.string :as str])
  (:import java.text.SimpleDateFormat))

(defmacro tokenize [x] `(let [x# ~x] x#))

(defn assoc-some-str
  "Associates a key with a value in a map, if and only if the value is not nil."
  ([m k v]
   (if (nil? v) [:value {:none {}}] (assoc m k [:value {:some v}]))))

(defn assoc-with-fn
  "Associates a key in a map if funtion applied to value is a non nil return"
  ([m k v f]
   (let [fn-v (f v)]
     (if (or (nil? v) (nil? fn-v)) m (assoc m k fn-v)))))

(defn nil->default
  ([m k v default]
   (if (nil? v) (assoc m k default) (assoc m k v))))

(defn not-nil? [value]
  (not (nil? value)))

(defn ->inst [value]
  (try
    (read-instant-date value)
    (catch Exception _e nil)))

(defn ->inst-str [inst]
  (.format (SimpleDateFormat. "yyyy-MM-dd'T'HH:mm:ss.SSSXXX") inst))

(defn edn-or-str [value]
  (if (str/starts-with? value ":")
    (keyword (subs value 1))
    value))