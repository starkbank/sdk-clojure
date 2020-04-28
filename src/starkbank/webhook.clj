(ns starkbank.webhook
  "Used handle webhooks."
  (:import [com.starkbank Webhook])
  (:use [starkbank.user]
        [clojure.walk]))

(defn- clojure-to-java
  ([clojure-map]
    (let [{
      url "url"
      subscriptions "subscriptions"
    }
    (stringify-keys clojure-map)]
      (java.util.HashMap.
        {
          "url" url
          "subscriptions" (if (nil? subscriptions) nil (into-array String subscriptions))
        }
      ))))

(defn- java-to-clojure
  ([java-object]
    (defn- java-hashmap-to-map [x] (into {} x))
    {
      :id (.id java-object)
      :url (.url java-object)
      :subscriptions (into [] (.subscriptions java-object))
    }))

(defn- clojure-query-to-java
  ([clojure-map]
    (let [{
        limit "limit"
      } (stringify-keys clojure-map)]
      (java.util.HashMap.
        {
          "limit" (if (nil? limit) nil (Integer. limit))
        }
      ))))

(defn create
  "creates webhooks"
  ([webhook-params]
    (def java-webhook-params (clojure-to-java webhook-params))
    (def created-java-webhook (Webhook/create java-webhook-params))
    (java-to-clojure created-java-webhook))

  ([webhook-params, user]
    (def java-webhook-params (clojure-to-java webhook-params))
    (def created-java-webhook (Webhook/create java-webhook-params (#'starkbank.user/get-java-project user)))
    (java-to-clojure created-java-webhook)))

(defn query
  "queries webhooks"
  ([]
    (map java-to-clojure (Webhook/query)))

  ([params]
    (def java-params (clojure-query-to-java params))
    (map java-to-clojure (Webhook/query java-params)))

  ([params, user] 
    (def java-params (clojure-query-to-java params))
    (map java-to-clojure (Webhook/query java-params (#'starkbank.user/get-java-project user)))))

(defn get
  "gets webhook"
  ([id]
    (java-to-clojure
      (Webhook/get id)))

  ([id, user]
    (java-to-clojure
      (Webhook/get
        id
        (#'starkbank.user/get-java-project user)))))

(defn delete
  "deletes webhook"
  ([id]
    (java-to-clojure
      (Webhook/delete id)))

  ([id, user]
    (java-to-clojure
      (Webhook/delete
        id
        (#'starkbank.user/get-java-project user)))))
