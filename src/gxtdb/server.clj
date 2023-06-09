(ns gxtdb.server
  (:gen-class) ; for -main method in uberjar
  (:require [gxtdb.service :as service :refer [xtdb-in-memory-node]]
            [io.pedestal.http :as server]
            [io.pedestal.http.route :as route]))

;; This is an adapted service map, that can be started and stopped
;; From the REPL you can call server/start and server/stop on this service
(defn runnable-service [xtdb-node] (-> xtdb-node service/service server/create-server))

(defn -run-test
  "The entry-point for 'lein run-dev'"
  [& _args]
  (println "\nCreating your [DEV] server...")
  (-> (service/service xtdb-in-memory-node) ;; start with production configuration
      (merge {:env :dev
              ;; do not block thread that starts web server
              ::server/join? false
              ;; Routes can be a function that resolve routes,
              ;;  we can use this to set the routes to be reloadable
              ::server/routes #(route/expand-routes (deref #'service/grpc-routes)) ;; -- PROTOC-GEN-CLOJURE -- update route
              ;; all origins are allowed in dev mode
              ::server/allowed-origins {:creds true :allowed-origins (constantly true)}
              ;; Content Security Policy (CSP) is mostly turned off in dev mode
              ::server/secure-headers {:content-security-policy-settings {:object-src "none"}}})
      ;; Wire up interceptor chains
      server/default-interceptors
      server/dev-interceptors
      server/create-server
      server/start))

(defn -main
  "The entry-point for 'lein run'"
  [& _args]
  (println "\nCreating your server...")
  (server/start (runnable-service xtdb-in-memory-node)))

(comment
  ;; For the REPL.

  (def *server (atom nil))

  (do (some-> @*server server/stop)
      (reset! *server (-main)))

  ())

;; If you package the service up as a WAR,
;; some form of the following function sections is required (for io.pedestal.servlet.ClojureVarServlet).

;;(defonce servlet  (atom nil))
;;
;;(defn servlet-init
;;  [_ config]
;;  ;; Initialize your app here.
;;  (reset! servlet  (server/servlet-init service/service nil)))
;;
;;(defn servlet-service
;;  [_ request response]
;;  (server/servlet-service @servlet request response))
;;
;;(defn servlet-destroy
;;  [_]
;;  (server/servlet-destroy @servlet)
;;  (reset! servlet nil))
