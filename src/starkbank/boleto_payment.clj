(ns starkbank.boleto-payment
  "Used handle boleto payments."
  (:import [com.starkbank BoletoPayment])
  (:use [starkbank.user]
        [clojure.walk]))

(defn- clojure-to-java
  ([clojure-map]
    (let [{
      amount "amount"
      tax-id "tax-id"
      description "description"
      line "line"
      bar-code "bar-code"
      scheduled "scheduled"
      tags "tags"
    }
    (stringify-keys clojure-map)]

      (defn- apply-java-hashmap [x] (java.util.HashMap. x))
      
      (BoletoPayment. (java.util.HashMap.
        {
          "amount" (if (nil? amount) nil (Integer. amount))
          "taxId" tax-id
          "description" description
          "line" line
          "barCode" bar-code
          "scheduled" scheduled
          "tags" (if (nil? tags) nil (into-array String tags))
        }
      )))))

(defn- java-to-clojure
  ([java-object]
    (defn- java-hashmap-to-map [x] (into {} x))
    {
      :id (.id java-object)
      :amount (.amount java-object)
      :tax-id (.taxId java-object)
      :description (.description java-object)
      :line (.line java-object)
      :bar-code (.barCode java-object)
      :scheduled (.scheduled java-object)
      :tags (into [] (.tags java-object))
      :status (.status java-object)
      :fee (.fee java-object)
      :created (.created java-object)
    }))

(defn- clojure-query-to-java
  ([clojure-map]
    (let [{
        limit "limit"
        after "after"
        before "before"
        tags "tags"
        ids "ids"
        status "status"
      } (stringify-keys clojure-map)]
      (java.util.HashMap.
        {
          "limit" (if (nil? limit) nil (Integer. limit))
          "after" after
          "before" before
          "tags" (if (nil? tags) nil (into-array String tags))
          "ids" (if (nil? ids) nil (into-array String ids))
          "status" status
        }
      ))))

(defn create
  "creates boleto payments"
  ([payments]
    (def java-payments (map clojure-to-java payments))
    (def created-java-payments (BoletoPayment/create java-payments))
    (map java-to-clojure created-java-payments))

  ([payments, project] 
    (def java-payments (map clojure-to-java payments))
    (def created-java-payments (BoletoPayment/create java-payments (#'starkbank.user/get-java-project project)))
    (map java-to-clojure created-java-payments)))

(defn query
  "queries boleto payments"
  ([]
    (map java-to-clojure (BoletoPayment/query)))

  ([params]
    (def java-params (clojure-query-to-java params))
    (map java-to-clojure (BoletoPayment/query java-params)))

  ([params, project] 
    (def java-params (clojure-query-to-java params))
    (map java-to-clojure (BoletoPayment/query java-params (#'starkbank.user/get-java-project project)))))

(defn get
  "gets boleto payment"
  ([id]
    (java-to-clojure
      (BoletoPayment/get id)))

  ([id, project]
    (java-to-clojure
      (BoletoPayment/get
        id
        (#'starkbank.user/get-java-project project)))))

(defn delete
  "deletes boleto payment"
  ([id]
    (java-to-clojure
      (BoletoPayment/delete id)))

  ([id, project]
    (java-to-clojure
      (BoletoPayment/delete
        id
        (#'starkbank.user/get-java-project project)))))

(defn pdf
  "gets boleto payment PDF"
  ([id]
    (clojure.java.io/input-stream
      (BoletoPayment/pdf id)))

  ([id, project]
    (clojure.java.io/input-stream
      (BoletoPayment/pdf
        id
        (#'starkbank.user/get-java-project project)))))


(ns starkbank.boleto-payment.log
  "Used handle boleto payment logs."
  (:import [com.starkbank BoletoPayment$Log])
  (:require [starkbank.boleto-payment :as payment])
  (:use [starkbank.user]
        [clojure.walk]))

(defn- java-to-clojure
  ([java-object]
    {
      :id (.id java-object)
      :created (.created java-object)
      :errors (into [] (.errors java-object))
      :payment (#'payment/java-to-clojure (.payment java-object))
    }))

(defn- clojure-query-to-java
  ([clojure-map]
    (let [{
        limit "limit"
        after "after"
        before "before"
        types "types"
        payment-ids "payment-ids"
      } (stringify-keys clojure-map)]
      (java.util.HashMap.
        {
          "limit" (if (nil? limit) nil (Integer. limit))
          "after" after
          "before" before
          "types" (if (nil? types) nil (into-array String types))
          "paymentIds" (if (nil? payment-ids) nil (into-array String payment-ids))
        }
      ))))

(defn get
  "gets boleto payment log"
  ([id]
    (java-to-clojure
      (BoletoPayment$Log/get id)))

  ([id, project]
    (java-to-clojure
      (BoletoPayment$Log/get
        id
        (#'starkbank.user/get-java-project project)))))

(defn query
  "queries boleto payment logs"
  ([]
    (map java-to-clojure (BoletoPayment$Log/query)))

  ([params]
    (def java-params (clojure-query-to-java params))
    (map java-to-clojure (BoletoPayment$Log/query java-params)))

  ([params, project] 
    (def java-params (clojure-query-to-java params))
    (map java-to-clojure (BoletoPayment$Log/query java-params (#'starkbank.user/get-java-project project)))))
