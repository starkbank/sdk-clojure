(ns starkbank.event
  "An Event is the notification received from the subscription to the Webhook.
  Events cannot be created, but may be retrieved from the Stark Bank API to
  list all generated updates on entities.

  ## Attributes:
    - `:id` [string]: unique id returned when the event is created. ex: \"5656565656565656\"
    - `:log` [Log]: a Log map from one the subscription services (Transfer.Log, Boleto.Log, BoletoPayment.log or UtilityPayment.Log)
    - `:created` [string]: creation datetime for the notification event. ex: \"2020-03-26T19:32:35.418698+00:00\"
    - `:is-delivered` [bool]: true if the event has been successfully delivered to the user url. ex: false
    - `:workspace-id` [string]: ID of the Workspace that generated this event. Mostly used when multiple Workspaces have Webhooks registered to the same endpoint. ex: \"4545454545454545\"
    - `:subscription` [string]: service that triggered this event. ex: \"transfer\", \"utility-payment\""
  (:refer-clojure :exclude [get update])
  (:import [com.starkbank Event])
  (:require [starkbank.user]
            [starkbank.transfer]
            [starkbank.boleto]
            [starkbank.boleto-payment]
            [starkbank.invoice]
            [starkbank.deposit]
            [starkbank.boleto-holmes]
            [starkbank.brcode-payment]
            [starkbank.utility-payment])
  (:use [clojure.walk]))

(defn- java-to-clojure
  ([java-object]
    {
      :id (.id java-object)
      :created (.created java-object)
      :is-delivered (.isDelivered java-object)
      :subscription (.subscription java-object)
      :workspace-id (.workspaceId java-object)
      :log (case (.subscription java-object)
            "transfer" (#'starkbank.transfer.log/java-to-clojure (.log java-object))
            "invoice" (#'starkbank.invoice.log/java-to-clojure (.log java-object))
            "deposit" (#'starkbank.deposit.log/java-to-clojure (.log java-object))
            "boleto" (#'starkbank.boleto.log/java-to-clojure (.log java-object))
            "boleto-holmes" (#'starkbank.boleto-holmes.log/java-to-clojure (.log java-object))
            "brcode-payment" (#'starkbank.brcode-payment.log/java-to-clojure (.log java-object))
            "boleto-payment" (#'starkbank.boleto-payment.log/java-to-clojure (.log java-object))
            "utility-payment" (#'starkbank.utility-payment.log/java-to-clojure (.log java-object))
            (.log java-object))
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
  "Receive a stream of notification Event maps previously created in the Stark Bank API

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: ~D[2020-03-25]
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: ~D[2020-03-25]
    - `:is-delivered` [bool, default nil]: filter successfully delivered events. ex: true or false
    - `:user` [Project or Organization]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - stream of Event maps with updated attributes"
  ([]
    (map java-to-clojure (Event/query)))

  ([params]
    (def java-params (clojure-query-to-java params))
    (map java-to-clojure (Event/query java-params)))

  ([params, user] 
    (def java-params (clojure-query-to-java params))
    (map java-to-clojure (Event/query java-params (#'starkbank.user/get-java-user user)))))

(defn get
  "Receive a single notification Event map previously created in the Stark Bank API by passing its id

  ## Parameters (required):
    - `id` [string]: map unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project or Organization]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - Event map with updated attributes"
  ([id]
    (java-to-clojure
      (Event/get id)))

  ([id, user]
    (java-to-clojure
      (Event/get
        id
        (#'starkbank.user/get-java-user user)))))

(defn delete
  "Delete a list of notification Event entities previously created in the Stark Bank API

  ## Parameters (required):
    - `id` [string]: Event unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project or Organization]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - deleted Event map with updated attributes"
  ([id]
    (java-to-clojure
      (Event/delete id)))

  ([id, user]
    (java-to-clojure
      (Event/delete
        id
        (#'starkbank.user/get-java-user user)))))

(defn update
  "Update notification Event by passing id.
    If is-delivered is true, the event will no longer be returned on queries with is-delivered=false.

  ## Parameters (required):
    - `id` [list of strings]: Event unique ids. ex: \"5656565656565656\"
    - `:is-delivered` [bool]: If true and event hasn't been delivered already, event will be set as delivered. ex: true

  ## Parameters (optional):
    - `:user` [Project or Organization]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - target Event with updated attributes"
  ([id, params]
    (java-to-clojure
      (Event/update id (clojure-update-to-java params))))

  ([id, params, user]
    (java-to-clojure
      (Event/update
        id
        (clojure-update-to-java params)
        (#'starkbank.user/get-java-user user)))))

(defn parse
  "Create a single Event map received from event listening at subscribed user endpoint.
  If the provided digital signature does not check out with the StarkBank public key, an \"invalidSignature\"
  error will be returned.

  ## Parameters (required):
    - `content` [string]: response content from request received at user endpoint (not parsed)
    - `signature` [string]: base-64 digital signature received at response header \"Digital-Signature\"

  ## Parameters (optional):
    - `cache-pid` [PID, default nil]: PID of the process that holds the public key cache, returned on previous parses. If not provided, a new cache process will be generated.
    - `user` [Project]: Project map returned from StarkBank.project(). Only necessary if default project has not been set in configs.

  ## Return:
    - Event map with updated attributes
    - Cache PID that holds the Stark Bank public key in order to avoid unnecessary requests to the API on future parses"
  ([content, signature]
    (java-to-clojure
      (Event/parse content signature)))

  ([content, signature, user]
    (java-to-clojure
      (Event/parse
        content
        signature
        (#'starkbank.user/get-java-user user)))))
