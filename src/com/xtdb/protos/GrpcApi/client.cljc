;;;----------------------------------------------------------------------------------
;;; Generated by protoc-gen-clojure.  DO NOT EDIT
;;;
;;; GRPC com.xtdb.protos.GrpcApi Client Implementation
;;;----------------------------------------------------------------------------------
(ns com.xtdb.protos.GrpcApi.client
  (:require [com.xtdb.protos :refer :all]
            [com.xtdb.protos :as com.xtdb.protos]
            [com.google.protobuf :as com.google.protobuf]
            [clojure.core.async :as async]
            [protojure.grpc.client.utils :refer [send-unary-params invoke-unary]]
            [promesa.core :as p]
            [protojure.grpc.client.api :as grpc]))

;-----------------------------------------------------------------------------
; GRPC Client Implementation
;-----------------------------------------------------------------------------

(def GrpcApi-service-name "com.xtdb.protos.GrpcApi")

(defn Status
  ([client params] (Status client {} params))
  ([client metadata params]
   (let [input (async/chan 1)
         output (async/chan 1)
         desc {:service "com.xtdb.protos.GrpcApi"
               :method  "Status"
               :input   {:f com.xtdb.protos/new-Empty :ch input}
               :output  {:f com.xtdb.protos/pb->StatusResponse :ch output}
               :metadata metadata}]
     (p/then (send-unary-params input params) (fn [_] (invoke-unary client desc output))))))

(defn SubmitTx
  ([client params] (SubmitTx client {} params))
  ([client metadata params]
   (let [input (async/chan 1)
         output (async/chan 1)
         desc {:service "com.xtdb.protos.GrpcApi"
               :method  "SubmitTx"
               :input   {:f com.xtdb.protos/new-SubmitRequest :ch input}
               :output  {:f com.xtdb.protos/pb->SubmitResponse :ch output}
               :metadata metadata}]
     (p/then (send-unary-params input params) (fn [_] (invoke-unary client desc output))))))

(defn SpeculativeTx
  ([client params] (SpeculativeTx client {} params))
  ([client metadata params]
   (let [input (async/chan 1)
         output (async/chan 1)
         desc {:service "com.xtdb.protos.GrpcApi"
               :method  "SpeculativeTx"
               :input   {:f com.xtdb.protos/new-SpeculativeTxRequest :ch input}
               :output  {:f com.xtdb.protos/pb->SpeculativeTxResponse :ch output}
               :metadata metadata}]
     (p/then (send-unary-params input params) (fn [_] (invoke-unary client desc output))))))

(defn EntityTx
  ([client params] (EntityTx client {} params))
  ([client metadata params]
   (let [input (async/chan 1)
         output (async/chan 1)
         desc {:service "com.xtdb.protos.GrpcApi"
               :method  "EntityTx"
               :input   {:f com.xtdb.protos/new-EntityTxRequest :ch input}
               :output  {:f com.xtdb.protos/pb->EntityTxResponse :ch output}
               :metadata metadata}]
     (p/then (send-unary-params input params) (fn [_] (invoke-unary client desc output))))))

