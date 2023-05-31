#_{:clj-kondo/ignore [:refer-all]}
(ns gxtdb.service-test
  (:require [clojure.test :refer :all]
            [com.xtdb.protos.GrpcApi.client :refer :all]
            [io.pedestal.http :as pedestal]
            [io.pedestal.test :refer :all]
            [protojure.grpc.client.providers.http2 :refer [connect]]
            [protojure.pedestal.core :as protojure.pedestal]
            [xtdb.api :as xt]
            [gxtdb.service :as service]
            [gxtdb.utils :as utils]))

(def ^:dynamic *opts* {})

(def xtdb-server (service/grpc-routes  (xt/start-node *opts*)))

(def test-env (atom {}))

(defn create-service []
  (let [port (let [socket (java.net.ServerSocket. 0)]
               (.close socket)
               (.getLocalPort socket))

        server-params {::pedestal/routes         xtdb-server
                       ::pedestal/port           port

                       ::pedestal/type           protojure.pedestal/config
                       ::pedestal/chain-provider protojure.pedestal/provider}]

    (swap! test-env assoc :port port :server (-> server-params pedestal/create-server pedestal/start))))

(defn destroy-service []
  (swap! test-env update :server pedestal/stop))

(defn wrap-service [test-fn]
  (create-service)
  (test-fn)
  (destroy-service))

(use-fixtures :each wrap-service)
(deftest status-test
  (testing "Status return xtdb with MemKv"
    (is (=
         (:kv-store @(Status @(connect {:uri (str "http://localhost:" (:port @test-env))}) {}))
         "xtdb.mem_kv.MemKv"))))

(deftest submit-tx-test
  #_(testing "Submit a put tx to xtdb-node"
      (let [tx @(SubmitTx @(connect {:uri (str "http://localhost:" (:port @test-env))})
                          {:tx-ops [{:transaction-type
                                     {:put {:id-type :keyword, :xt-id "id1", :document {:fields {"key" {:kind {:string-value "value"}}}}}}}]})]
        (is (inst? (-> tx :tx-time utils/->inst)))
        (is (>=
             (:tx-id tx)
             0))))

  (testing "Submit a match tx to xtdb-node"
    (let [tx @(SubmitTx @(connect {:uri (str "http://localhost:" (:port @test-env))})
                        {:tx-ops [{:transaction-type
                                   {:match {:id-type :keyword, :valid-time "123" , :document-id "id1", :document {:fields {"key" {:kind {:string-value "value"}}}}}}}]})]
      (is (inst? (-> tx :tx-time utils/->inst)))
      (is (>=
           (:tx-id tx)
           0)))))


