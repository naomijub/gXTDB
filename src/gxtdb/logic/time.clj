(ns gxtdb.logic.time)

(defn conj-some-valid-timerange [vec init-time end-time]
  (case [(nil? init-time) (nil? end-time)]
    [false true] (conj vec init-time)
    [false false] (conj vec init-time end-time)
    vec))