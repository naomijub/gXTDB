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
            [gxtdb.adapters.tx-log :as tx-log]))

(defn about-page
  [_request]
  (ring-resp/response (format "Clojure %s - served from %s"
                              (clojure-version)
                              (route/url-for ::about-page))))

(defn home-page
  [_request]
  (ring-resp/response "Hello from gxtdb, backed by Protojure Template!"))

;; -- PROTOC-GEN-CLOJURE --
;; Implement our "Greeter" service interface.  The compiler generates
;; a defprotocol (greeter/Service, in this case), and it is our job
;; to define an implementation of every function within it.  These will be
;; invoked whenever a request arrives, similarly to if we had defined
;; these functions as pedestal defhandlers.  The main difference is that
;; the :body returned in the response should correlate to the protobuf
;; return-type declared in the Service definition within the .proto
;;
;; Note that our GRPC parameters are associated with the request-map
;; as :grpc-params, similar to how the pedestal body-param module
;; injects other types, like :json-params, :edn-params, etc.
;;
;; see http://pedestal.io/reference/request-map

(defonce xtdb-in-memory-node (xt/start-node {}))

(deftype GrpcAPI [xtdb-node]
  api/Service
  (SubmitTx [_this {{:keys [tx-ops]} :grpc-params}]
    (let [tx-log (tx-log-adapter/proto->tx-log tx-ops)
          xt-response (xt/submit-tx xtdb-node tx-log)]
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
