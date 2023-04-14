(ns gxtdb.service
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]
            [ring.util.response :as ring-resp]
            
            [gxtdb.adapters.status :as status-adapter]

            ;; -- XTDB -- 
            [xtdb.api :as xt]

            ;; -- PROTOC-GEN-CLOJURE --
            [protojure.pedestal.core :as protojure.pedestal]
            [protojure.pedestal.routes :as proutes]
            [com.xtdb.protos.GrpcApi.server :as api]))

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

;; (deftype Greeter []
;;   greeter/Service
;;   (Hello
;;     [this {{:keys [name]} :grpc-params :as request}]
;;     {:status 200
;;      :body {:message (str "Hello, " name)}}))

(defonce xtdb-node (xt/start-node {}))

(deftype GrpcAPI []
  api/Service
  (Status
    [_this _request]
    (let [status (xt/status xtdb-node)]
      (println (str "\n" status "\n"))
      {:status 200
       :body  (status-adapter/edn->grpc status)})))

;; Defines "/" and "/about" routes with their associated :get handlers.
;; The interceptors defined after the verb map (e.g., {:get home-page}
;; apply to / and its children (/about).
(def common-interceptors [(body-params/body-params) http/html-body])

;; Tabular routes
(def routes #{["/" :get (conj common-interceptors `home-page)]
              ["/about" :get (conj common-interceptors `about-page)]})

;; -- PROTOC-GEN-CLOJURE --
;; Add the routes produced by Greeter->routes
(def grpc-routes (reduce conj routes (proutes/->tablesyntax {:rpc-metadata api/rpc-metadata :interceptors common-interceptors :callback-context (GrpcAPI.) })))

(def service {:env :prod
              ::http/routes grpc-routes

              ;; -- PROTOC-GEN-CLOJURE --
              ;; We override the chain-provider with one provided by protojure.protobuf
              ;; and based on the Undertow webserver.  This provides the proper support
              ;; for HTTP/2 trailers, which GRPCs rely on.  A future version of pedestal
              ;; may provide this support, in which case we can go back to using
              ;; chain-providers from pedestal.
              ::http/type protojure.pedestal/config
              ::http/chain-provider protojure.pedestal/provider

              ;;::http/host "localhost"
              ::http/port 8080})
