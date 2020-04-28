(ns starkbank.webhook
  "A Webhook is used to subscribe to notification events on a user-selected endpoint.
  Currently available services for subscription are transfer, boleto, boleto-payment,
  and utility-payment

  ## Parameters (required):
    - `:url` [string]: Url that will be notified when an event occurs.
    - `:subscriptions` [list of strings]: list of any non-empty combination of the available services. ex: [\"transfer\", \"boleto-payment\"]

  ## Attributes:
    - `:id` [string, default nil]: unique id returned when the log is created. ex: \"5656565656565656\""
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
  "Send a single Webhook subscription for creation in the Stark Bank API

  ## Parameters (required):
    - `:url` [string]: url to which notification events will be sent to. ex: \"https://webhook.site/60e9c18e-4b5c-4369-bda1-ab5fcd8e1b29\"
    - `:subscriptions` [list of strings]: list of any non-empty combination of the available services. ex: [\"transfer\", \"boleto-payment\"]

  ## Parameters (optional):
    - `:user` [Project]: Project struct returned from StarkBank.project(). Only necessary if default project has not been set in configs.

  ## Return:
    - Webhook struct with updated attributes"
  ([webhook-params]
    (def java-webhook-params (clojure-to-java webhook-params))
    (def created-java-webhook (Webhook/create java-webhook-params))
    (java-to-clojure created-java-webhook))

  ([webhook-params, user]
    (def java-webhook-params (clojure-to-java webhook-params))
    (def created-java-webhook (Webhook/create java-webhook-params (#'starkbank.user/get-java-project user)))
    (java-to-clojure created-java-webhook)))

(defn query
  "Receive a stream of Webhook subcription structs previously created in the Stark Bank API

  ## Options:
    - `:limit` [integer, default nil]: maximum number of structs to be retrieved. Unlimited if nil. ex: 35
    - `:user` [Project]: Project struct returned from StarkBank.project(). Only necessary if default project has not been set in configs.

  ## Return:
    - stream of Webhook structs with updated attributes"
  ([]
    (map java-to-clojure (Webhook/query)))

  ([params]
    (def java-params (clojure-query-to-java params))
    (map java-to-clojure (Webhook/query java-params)))

  ([params, user] 
    (def java-params (clojure-query-to-java params))
    (map java-to-clojure (Webhook/query java-params (#'starkbank.user/get-java-project user)))))

(defn get
  "Receive a single Webhook subscription struct previously created in the Stark Bank API by passing its id

  ## Parameters (required):
    - `id` [string]: struct unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project]: Project struct returned from StarkBank.project(). Only necessary if default project has not been set in configs.

  ## Return:
    - Webhook struct with updated attributes"
  ([id]
    (java-to-clojure
      (Webhook/get id)))

  ([id, user]
    (java-to-clojure
      (Webhook/get
        id
        (#'starkbank.user/get-java-project user)))))

(defn delete
  "Delete a Webhook subscription entity previously created in the Stark Bank API

  ## Parameters (required):
    - `id` [string]: Webhook unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project]: Project struct returned from StarkBank.project(). Only necessary if default project has not been set in configs.

  ## Return:
    - deleted Webhook with updated attributes"
  ([id]
    (java-to-clojure
      (Webhook/delete id)))

  ([id, user]
    (java-to-clojure
      (Webhook/delete
        id
        (#'starkbank.user/get-java-project user)))))
