(ns starkbank.transaction
  "Used handle transactions."
  (:import [com.starkbank Transaction])
  (:use [starkbank.user]
        [clojure.walk]))

(defn- clojure-to-java
  ([clojure-map]
    (let [{
      id "id"
      amount "amount"
      description "description"
      external-id "external-id"
      sender-id "sender-id"
      receiver-id "receiver-id"
      tags "tags"
      fee "fee"
      created "created"
      source "source"}
      (stringify-keys clojure-map)]

      (defn- apply-java-hashmap [x] (java.util.HashMap. x))
      
      (Transaction. (java.util.HashMap.
        {
          "id" id
          "amount" (if (nil? amount) nil (Integer. amount))
          "description" description
          "externalId" external-id
          "senderId" sender-id
          "receiverId" receiver-id
          "tags" (if (nil? tags) nil (into-array String tags))
          "fee" fee
          "created" created
          "source" source
        }
      )))))

(defn- java-to-clojure
  ([java-object]
    (defn- java-hashmap-to-map [x] (into {} x))
    {
      :id (.id java-object)
      :amount (.amount java-object)
      :description (.description java-object)
      :external-id (.externalId java-object)
      :sender-id (.senderId java-object)
      :receiver-id (.receiverId java-object)
      :tags (into [] (.tags java-object))
      :fee (.fee java-object)
      :created (.created java-object)
      :source (.source java-object)
    }))

(defn- clojure-query-to-java
  ([clojure-map]
    (let [{
        limit "limit"
        after "after"
        before "before"
        external-ids "external-ids"
      } (stringify-keys clojure-map)]
      (java.util.HashMap.
        {
          "limit" (if (nil? limit) nil (Integer. limit))
          "after" after
          "before" before
          "externalIds" (if (nil? external-ids) nil (into-array String external-ids))
        }
      ))))

(defn create
  "creates transactions"
  ([transactions]
    (def java-transactions (map clojure-to-java transactions))
    (def created-java-transactions (Transaction/create java-transactions))
    (map java-to-clojure created-java-transactions))

  ([transactions, project] 
    (def java-transactions (map clojure-to-java transactions))
    (def created-java-transactions (Transaction/create java-transactions (#'starkbank.user/get-java-project project)))
    (map java-to-clojure created-java-transactions)))

(defn query
  "queries transactions"
  ([]
    (map java-to-clojure (Transaction/query)))

  ([params]
    (def java-params (clojure-query-to-java params))
    (map java-to-clojure (Transaction/query java-params)))

  ([params, project] 
    (def java-params (clojure-query-to-java params))
    (map java-to-clojure (Transaction/query java-params (#'starkbank.user/get-java-project project)))))

(defn get
  "gets transaction"
  ([id]
    (java-to-clojure
      (Transaction/get id)))

  ([id, project]
    (java-to-clojure
      (Transaction/get
        id
        (#'starkbank.user/get-java-project project)))))
