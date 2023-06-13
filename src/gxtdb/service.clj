(ns gxtdb.service
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]
            [ring.util.response :as ring-resp]

            [gxtdb.adapters.status :as status-adapter]
            [gxtdb.adapters.tx-log :as tx-log-adapter]

            ;; -- XTDB -- 
            [xtdb.api :as xt]

            ;; -- PROTOC-GEN-CLOJURE --
            [protojure.pedestal.core :as protojure.pedestal]
            [protojure.pedestal.routes :as proutes]
            [com.xtdb.protos.GrpcApi.server :as api]
            [gxtdb.adapters.tx-log :as tx-log]
            [gxtdb.adapters.tx-time :as tx-time]))

(defn about-page
  [_request]
  (ring-resp/response (format "Clojure %s - served from %s"
                              (clojure-version)
                              (route/url-for ::about-page))))

(defn home-page
  [_request]
  (ring-resp/response "Hello from gxtdb, backed by Protojure Template!"))

;; -- XTDB-DOCS --
;; Available functionalities:
;; - submittx: writes transactions in a log and returns details about the transactor.
;; - status: return status of a xtdb-node.
(defonce xtdb-in-memory-node (xt/start-node {}))

(deftype GrpcAPI [xtdb-node]
  api/Service
  (SubmitTx [_this {{:keys [tx-ops tx-time]} :grpc-params}]
    (let [time   (tx-time/->clj-time tx-time)
          tx-log (tx-log-adapter/proto->tx-log tx-ops)
          xt-response (if (nil? time) (xt/submit-tx xtdb-node tx-log) (xt/submit-tx xtdb-node tx-log {::xt/tx-time time}))]
      {:status 200
       :body (tx-log-adapter/xtdb->proto xt-response)}))
  (Status
    [_this _request]
    (let [status (xt/status xtdb-node)]
      {:status 200
       :body  (status-adapter/edn->grpc status)})))

(def common-interceptors [(body-params/body-params) http/html-body])

;; Tabular routes
(def routes #{["/" :get (conj common-interceptors `home-page)]
              ["/about" :get (conj common-interceptors `about-page)]})

;; -- PROTOC-GEN-CLOJURE --
(defn grpc-routes [xtdb-node] (reduce conj routes (proutes/->tablesyntax {:rpc-metadata api/rpc-metadata :interceptors common-interceptors :callback-context (GrpcAPI. xtdb-node)})))

(defn service [xtdb-node]
  {:env :prod
   ::http/routes (grpc-routes xtdb-node)
   ::http/type protojure.pedestal/config
   ::http/chain-provider protojure.pedestal/provider

              ;;::http/host "localhost"
   ::http/port 8080})
