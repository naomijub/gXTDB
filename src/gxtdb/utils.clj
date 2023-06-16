(ns gxtdb.utils
  (:require [clojure.instant :refer [read-instant-date]]
            [clojure.string :as str]
            [xtdb.api :as xt])
  (:import java.text.SimpleDateFormat))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defmacro tokenize [x] `(let [x# ~x] x#))

(defn assoc-some-str
  "Associates a key with a value in a map, if and only if the value is not nil."
  ([m k v]
   (if (nil? v) (assoc m k {:value {:none {}}}) (assoc m k {:value {:some v}}))))

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

(defn str->keyword [s]
  (when-not (or (empty? s) (= s ":")) (-> s (str/replace #" " "-") keyword)))

(defn ->id [id-type id]
  (case id-type
    :keyword (str->keyword id)
    :string id
    :int (parse-long id)
    :uuid (let [uuid (parse-uuid id)]
            (if (uuid? uuid) uuid  id))
    (str->keyword id)))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn record->map
  [record]
  (let [f #(if (record? %) (record->map %) %)
        ks (keys record)
        vs (map f (vals record))]
    (zipmap ks vs)))

(defn open-db [xtdb-node should-open-snapshot db-basis]
  (case [should-open-snapshot (nil? db-basis)]
    [true true] (xt/open-db xtdb-node)
    [false true] (xt/db xtdb-node)
    [true false] (xt/open-db xtdb-node db-basis)
    [false false] (xt/db xtdb-node db-basis)))