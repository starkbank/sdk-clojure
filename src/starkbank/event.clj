(ns starkbank.event
  "Used handle eventss."
  (:import [com.starkbank Event])
  (:require [starkbank.user]
            [starkbank.transfer]
            [starkbank.boleto]
            [starkbank.boleto-payment]
            [starkbank.utility-payment])
  (:use [clojure.walk]))

(defn- java-to-clojure
  ([java-object]
    (defn- java-hashmap-to-map [x] (into {} x))
    {
      :id (.id java-object)
      :created (.created java-object)
      :is-delivered (.isDelivered java-object)
      :subscription (.subscription java-object)
      :log (case (.subscription java-object)
            "transfer" (#'starkbank.transfer.log/java-to-clojure (.log java-object))
            "boleto" (#'starkbank.boleto.log/java-to-clojure (.log java-object))
            "boleto-payment" (#'starkbank.boleto-payment.log/java-to-clojure (.log java-object))
            "utility-payment" (#'starkbank.utility-payment.log/java-to-clojure (.log java-object)))
    }))

(defn- clojure-query-to-java
  ([clojure-map]
    (let [{
        limit "limit"
        after "after"
        before "before"
        is-delivered "is-delivered"
      } (stringify-keys clojure-map)]
      (java.util.HashMap.
        {
          "limit" (if (nil? limit) nil (Integer. limit))
          "after" after
          "before" before
          "isDelivered" is-delivered
        }
      ))))

(defn- clojure-update-to-java
  ([clojure-map]
    (let [{
        is-delivered "is-delivered"
      } (stringify-keys clojure-map)]
      (java.util.HashMap.
        {
          "isDelivered" is-delivered
        }
      ))))

(defn query
  "queries eventss"
  ([]
    (map java-to-clojure (Event/query)))

  ([params]
    (def java-params (clojure-query-to-java params))
    (map java-to-clojure (Event/query java-params)))

  ([params, project] 
    (def java-params (clojure-query-to-java params))
    (map java-to-clojure (Event/query java-params (#'starkbank.user/get-java-project project)))))

(defn get
  "gets events"
  ([id]
    (def event (Event/get id))
    (println event)
    (java-to-clojure event))
    ; (java-to-clojure
    ;   (Event/get id)))

  ([id, project]
    (java-to-clojure
      (Event/get
        id
        (#'starkbank.user/get-java-project project)))))

(defn delete
  "deletes events"
  ([id]
    (java-to-clojure
      (Event/delete id)))

  ([id, project]
    (java-to-clojure
      (Event/delete
        id
        (#'starkbank.user/get-java-project project)))))

(defn update
  "updates events"
  ([id, params]
    (java-to-clojure
      (Event/update id (clojure-update-to-java params))))

  ([id, params, project]
    (java-to-clojure
      (Event/update
        id
        (clojure-update-to-java params)
        (#'starkbank.user/get-java-project project)))))

(defn parse
  "parses events"
  ([content, signature]
    (java-to-clojure
      (Event/parse content signature)))

  ([content, signature, project]
    (java-to-clojure
      (Event/parse
        content
        signature
        (#'starkbank.user/get-java-project project)))))
