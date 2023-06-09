(ns gxtdb.service-test
  (:require [clojure.test :refer [deftest is testing use-fixtures]]
            [com.xtdb.protos.GrpcApi.client :as client]
            [gxtdb.service :as service]
            [gxtdb.utils :as utils :refer [->inst]]
            [io.pedestal.http :as pedestal]
            [protojure.grpc.client.providers.http2 :refer [connect]]
            [protojure.pedestal.core :as protojure.pedestal]
            [xtdb.api :as xt]
            [clojure.string :as str]))

;; Setup.
(def ^:dynamic *opts* {})
(defonce node (xt/start-node *opts*))

(defonce xtdb-server (service/grpc-routes node))

(defonce test-env (atom {}))

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

(defn make-client
  []
  @(connect {:uri (str "http://localhost:" (:port @test-env))}))

;; Tests.
(deftest status-test
  (testing "Status return xtdb with MemKv"
    (is (=
         "xtdb.mem_kv.MemKv"
         (:kv-store @(client/Status
                      (make-client) {}))))))

(deftest submit-tx-test
  (testing "Submit a put tx to xtdb-node"
    (let [tx @(client/SubmitTx
               (make-client)
               {:tx-ops [{:transaction-type
                          {:put {:id-type :keyword, :xt-id "id1", :document {:fields {"key" {:kind {:string-value "value"}}}}}}}]})]
      (is (inst? (-> tx :tx-time utils/->inst)))
      (is (>=
           (:tx-id tx)
           0))))
  (testing "Submit a put tx to xtdb-node with tx-time"
    (let [tx @(client/SubmitTx
               (make-client)
               {:tx-time {:value {:some "2023-06-12T21:32:44.717-05:00"}}
                :tx-ops [{:transaction-type
                          {:put {:id-type :keyword, :xt-id "id1", :document {:fields {"key" {:kind {:string-value "value"}}}}}}}]})]
      (is (inst? (-> tx :tx-time utils/->inst)))
      (is (>=
           (:tx-id tx)
           0))))

  (testing "Submit a match tx to xtdb-node"
    (let [tx @(client/SubmitTx
               (make-client)
               {:tx-ops [{:transaction-type
                          {:match {:id-type :keyword :document-id "id1" :document {:fields {"key" {:kind {:string-value "value"}}}} :valid-time  {:value {:some "2023-06-12T21:32:44.717-05:00"}}}}}
                         {:transaction-type
                          {:put {:id-type :keyword, :xt-id "id1", :document {:fields {"key" {:kind {:string-value "value"}}}}}}}]})]
      (is (inst? (-> tx :tx-time utils/->inst)))
      (is (>=
           (:tx-id tx)
           0)))))

(deftest entity-tx-test
  (testing "query of entity tx status after a simple put - no open snapshot"
    (let [client (make-client)
          put_tx @(client/SubmitTx
                   client
                   {:tx-ops [{:transaction-type
                              {:put {:id-type :string, :xt-id "id1", :document {:fields {"key" {:kind {:string-value "value"}}}}}}}]})
          _await (xt/await-tx node {:xtdb.api/tx-id (:tx-id put_tx), :xtdb.api/tx-time (-> put_tx :tx-time ->inst)})
          e-tx        @(client/EntityTx
                        client
                        {:id-type :string :entity-id "id1" :open-snapshot false :valid-time {:value {:none {}}} :tx-time {:value {:none {}}} :tx-id {:value {:none {}}}})]
      (is (= '(:xt-id :content-hash :valid-time :tx-time :tx-id) (keys e-tx))))))

(deftest entity-test
  (testing "entity query after a simple put - no open snapshot"
    (let [client (make-client)
          put_tx @(client/SubmitTx
                   client
                   {:tx-ops [{:transaction-type
                              {:put {:id-type :string
                                     :xt-id "id1"
                                     :document {:fields {"key" {:kind {:string-value "value"}}}}}}}]})
          _await (xt/await-tx node {:xtdb.api/tx-id (:tx-id put_tx)
                                    :xtdb.api/tx-time (-> put_tx :tx-time ->inst)})
          e-tx        @(client/Entity
                        client
                        {:id-type :string
                         :entity-id "id1"
                         :open-snapshot false
                         :valid-time {:value {:none {}}}
                         :tx-time {:value {:none {}}}
                         :tx-id {:value {:none {}}}})]
      (is (= {:xt-id "id1"
              :content "{:key \"value\", :xt/id \"id1\"}"}
             (into {} e-tx))))))

(deftest speculative-tx-test
  (testing "speculative submit tx with"
    (let [client (make-client)
          put_tx @(client/SubmitTx
                   client
                   {:tx-ops [{:transaction-type
                              {:put {:id-type :string, :xt-id "id1", :document {:fields {"key" {:kind {:string-value "value"}}}}}}}]})
          _await (xt/await-tx node {:xtdb.api/tx-id (:tx-id put_tx), :xtdb.api/tx-time (-> put_tx :tx-time ->inst)})
          with-tx        @(client/SpeculativeTx
                           client
                           {:tx-ops [{:transaction-type
                                      {:put {:id-type :string, :xt-id "id1", :document {:fields {"key" {:kind {:string-value "Hellow"}}}}}}}]})]
      (is (>= (:tx-id with-tx) 0))
      (is (str/includes? (:edn-document with-tx) ":crux.db/id \"id1\"")))))
