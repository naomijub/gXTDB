(ns gxtdb.service
  (:require [com.xtdb.protos.GrpcApi.server :as api]
            [gxtdb.adapters.status :as status-adapter]
            [gxtdb.controllers :as controllers]
            [io.pedestal.http :as http]
            [io.pedestal.http.body-params :as body-params]
            [io.pedestal.http.route :as route]
            [protojure.pedestal.core :as protojure.pedestal]
            [protojure.pedestal.routes :as proutes]
            [ring.util.response :as ring-resp]
            [xtdb.api :as xt]))

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
    {:status 200
     :body (controllers/submit-tx xtdb-node tx-ops tx-time)})
  (SpeculativeTx [_this {{:keys [tx-ops]} :grpc-params}]
    {:status 200
     :body (controllers/with-tx xtdb-node tx-ops)})
  (EntityTx [_this {params :grpc-params}]
    {:status 200
     :body (controllers/entity-tx xtdb-node params)})
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
