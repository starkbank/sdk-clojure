(ns starkbank.utility-payment
  "Used handle utility payments."
  (:import [com.starkbank UtilityPayment])
  (:use [starkbank.user]
        [clojure.walk]))

(defn- clojure-to-java
  ([clojure-map]
    (let [{
      amount "amount"
      description "description"
      line "line"
      bar-code "bar-code"
      scheduled "scheduled"
      tags "tags"
    }
    (stringify-keys clojure-map)]

      (defn- apply-java-hashmap [x] (java.util.HashMap. x))
      
      (UtilityPayment. (java.util.HashMap.
        {
          "amount" (if (nil? amount) nil (Integer. amount))
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
  "creates utility payments"
  ([payments]
    (def java-payments (map clojure-to-java payments))
    (def created-java-payments (UtilityPayment/create java-payments))
    (map java-to-clojure created-java-payments))

  ([payments, user]
    (def java-payments (map clojure-to-java payments))
    (def created-java-payments (UtilityPayment/create java-payments (#'starkbank.user/get-java-project user)))
    (map java-to-clojure created-java-payments)))

(defn query
  "queries utility payments"
  ([]
    (map java-to-clojure (UtilityPayment/query)))

  ([params]
    (def java-params (clojure-query-to-java params))
    (map java-to-clojure (UtilityPayment/query java-params)))

  ([params, user] 
    (def java-params (clojure-query-to-java params))
    (map java-to-clojure (UtilityPayment/query java-params (#'starkbank.user/get-java-project user)))))

(defn get
  "gets utility payment"
  ([id]
    (java-to-clojure
      (UtilityPayment/get id)))

  ([id, user]
    (java-to-clojure
      (UtilityPayment/get
        id
        (#'starkbank.user/get-java-project user)))))

(defn delete
  "deletes utility payment"
  ([id]
    (java-to-clojure
      (UtilityPayment/delete id)))

  ([id, user]
    (java-to-clojure
      (UtilityPayment/delete
        id
        (#'starkbank.user/get-java-project user)))))

(defn pdf
  "gets utility payment PDF"
  ([id]
    (clojure.java.io/input-stream
      (UtilityPayment/pdf id)))

  ([id, user]
    (clojure.java.io/input-stream
      (UtilityPayment/pdf
        id
        (#'starkbank.user/get-java-project user)))))


(ns starkbank.utility-payment.log
  "Used handle utility payment logs."
  (:import [com.starkbank UtilityPayment$Log])
  (:require [starkbank.utility-payment :as payment])
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
  "gets utility payment log"
  ([id]
    (java-to-clojure
      (UtilityPayment$Log/get id)))

  ([id, user]
    (java-to-clojure
      (UtilityPayment$Log/get
        id
        (#'starkbank.user/get-java-project user)))))

(defn query
  "queries utility payment logs"
  ([]
    (map java-to-clojure (UtilityPayment$Log/query)))

  ([params]
    (def java-params (clojure-query-to-java params))
    (map java-to-clojure (UtilityPayment$Log/query java-params)))

  ([params, user] 
    (def java-params (clojure-query-to-java params))
    (map java-to-clojure (UtilityPayment$Log/query java-params (#'starkbank.user/get-java-project user)))))
