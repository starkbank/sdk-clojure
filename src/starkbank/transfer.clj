(ns starkbank.transfer
  "Used handle transfers."
  (:import [com.starkbank Transfer])
  (:use [starkbank.user]
        [clojure.walk]))

(defn- clojure-to-java
  ([clojure-map]
    (let [{
      amount "amount"
      name "name"
      tax-id "tax-id"
      bank-code "bank-code"
      branch-code "branch-code"
      account-number "account-number"
      tags "tags"
    }
    (stringify-keys clojure-map)]
      
      (Transfer. (java.util.HashMap.
        {
          "amount" (if (nil? amount) nil (Integer. amount))
          "name" name
          "taxId" tax-id
          "bankCode" bank-code
          "branchCode" branch-code
          "accountNumber" account-number
          "tags" (if (nil? tags) nil (into-array String tags))
        }
      )))))

(defn- java-to-clojure
  ([java-object]
    {
      :id (.id java-object)
      :amount (.amount java-object)
      :name (.name java-object)
      :tax-id (.taxId java-object)
      :bank-code (.bankCode java-object)
      :branch-code (.branchCode java-object)
      :account-number (.accountNumber java-object)
      :tags (into [] (.tags java-object))
      :fee (.fee java-object)
      :status (.status java-object)
      :created (.created java-object)
      :updated (.updated java-object)
      :transaction-ids (into [] (.transactionIds java-object))
    }))

(defn- clojure-query-to-java
  ([clojure-map]
    (let [{
        limit "limit"
        after "after"
        before "before"
        transaction-ids "transaction-ids"
        status "status"
        sort "sort"
        tags "tags"
      } (stringify-keys clojure-map)]
      (java.util.HashMap.
        {
          "limit" (if (nil? limit) nil (Integer. limit))
          "after" after
          "before" before
          "transactionIds" (if (nil? transaction-ids) nil (into-array String transaction-ids))
          "status" status
          "sort" sort
          "tags" (if (nil? tags) nil (into-array String tags))
        }
      ))))

(defn create
  "creates transfers"
  ([transfers]
    (def java-transfers (map clojure-to-java transfers))
    (def created-java-transfers (Transfer/create java-transfers))
    (map java-to-clojure created-java-transfers))

  ([transfers, project] 
    (def java-transfers (map clojure-to-java transfers))
    (def created-java-transfers (Transfer/create java-transfers (#'starkbank.user/get-java-project project)))
    (map java-to-clojure created-java-transfers)))

(defn query
  "queries transfers"
  ([]
    (map java-to-clojure (Transfer/query)))

  ([params]
    (def java-params (clojure-query-to-java params))
    (map java-to-clojure (Transfer/query java-params)))

  ([params, project] 
    (def java-params (clojure-query-to-java params))
    (map java-to-clojure (Transfer/query java-params (#'starkbank.user/get-java-project project)))))

(defn get
  "gets transfer"
  ([id]
    (java-to-clojure
      (Transfer/get id)))

  ([id, project]
    (java-to-clojure
      (Transfer/get
        id
        (#'starkbank.user/get-java-project project)))))

(defn pdf
  "gets transfer PDF"
  ([id]
    (clojure.java.io/input-stream
      (Transfer/pdf id)))

  ([id, project]
    (clojure.java.io/input-stream
      (Transfer/pdf
        id
        (#'starkbank.user/get-java-project project)))))


(ns starkbank.transfer.log
  "Used handle transfer logs."
  (:import [com.starkbank Transfer$Log])
  (:require [starkbank.transfer :as transfer])
  (:use [starkbank.user]
        [clojure.walk]))

(defn- java-to-clojure
  ([java-object]
    {
      :id (.id java-object)
      :created (.created java-object)
      :errors (into [] (.errors java-object))
      :transfer (#'transfer/java-to-clojure (.transfer java-object))
    }))

(defn- clojure-query-to-java
  ([clojure-map]
    (let [{
        limit "limit"
        after "after"
        before "before"
        types "types"
        transfer-ids "transfer-ids"
      } (stringify-keys clojure-map)]
      (java.util.HashMap.
        {
          "limit" (if (nil? limit) nil (Integer. limit))
          "after" after
          "before" before
          "types" (if (nil? types) nil (into-array String types))
          "transferIds" (if (nil? transfer-ids) nil (into-array String transfer-ids))
        }
      ))))

(defn get
  "gets transfer log"
  ([id]
    (java-to-clojure
      (Transfer$Log/get id)))

  ([id, project]
    (java-to-clojure
      (Transfer$Log/get
        id
        (#'starkbank.user/get-java-project project)))))

(defn query
  "queries transfer logs"
  ([]
    (map java-to-clojure (Transfer$Log/query)))

  ([params]
    (def java-params (clojure-query-to-java params))
    (map java-to-clojure (Transfer$Log/query java-params)))

  ([params, project] 
    (def java-params (clojure-query-to-java params))
    (map java-to-clojure (Transfer$Log/query java-params (#'starkbank.user/get-java-project project)))))
