(ns starkbank.payment-request
  "A PaymentRequest is an indirect request to access a specific cash-out service
  (such as Transfer, BrcodePayments, etc.) which goes through the cost center
  approval flow on our website. To emit a PaymentRequest, you must direct it to
  a specific cost center by its ID, which can be retrieved on our website at the
  cost center page.

  ## Parameters (required):
  - `:center-id` [string]: unique id returned when PaymentRequest is created. ex: \"5656565656565656\"
  - `:payment` [Transfer, BrcodePayment, BoletoPayment, UtilityPayment, Transaction or map]: payment entity that should be approved and executed.
  - `:type` [string]: payment type, inferred from the payment parameter if it is not a map. ex: \"transfer\", \"brcode-payment\"

  ## Parameters (optional):
  - `:due` [string]: Payment target date in ISO format.
  - `:tags` [list of strings]: list of strings for tagging

  Attributes (return-only):
  - `:id` [string]: id of the object
  - `:amount` [integer]: PaymentRequest amount. ex: 100000 = R$1.000,00
  - `:status` [string]: current PaymentRequest status.ex: \"pending\" or \"approved\"
  - `:actions` [list of PaymentRequest.Action, default nil]: list of actions that are affecting this PaymentRequest.ex: [{\"type\": \"member\", \"id\": \"56565656565656, \"action\": \"requested\"}]
  - `:updated` [string]: latest update datetime for the PaymentRequest. ex: \"2020-03-26T19:32:35.418698+00:00\"
  - `:created` [string]: creation datetime for the PaymentRequest. ex: \"2020-03-26T19:32:35.418698+00:00\""
  (:refer-clojure :exclude [get type])
  (:import [com.starkbank PaymentRequest])
  (:require [starkbank.user]
            [starkbank.transfer]
            [starkbank.transaction]
            [starkbank.brcode-payment]
            [starkbank.boleto-payment]
            [starkbank.utility-payment])
  (:use [clojure.walk]))

(defn- java-to-clojure
  ([java-object]
    (defn- java-action-to-map [action] {
      :name (.name action)
      :action (.action action)
      :type (.type action)
      :id (.id action)})
    {
      :id (.id java-object)
      :center-id (.centerId java-object)
      :type (.type java-object)
      :due (.due java-object)
      :amount (.amount java-object)
      :status (.status java-object)
      :actions (into [] (keywordize-keys (map java-action-to-map (.actions java-object))))
      :tags (into [] (.tags java-object))
      :updated (.updated java-object)
      :created (.created java-object)
      :payment (case (.type java-object)
        "transfer" (#'starkbank.transfer/java-to-clojure (.payment java-object))
        "transaction" (#'starkbank.transaction/java-to-clojure (.payment java-object))
        "brcode-payment" (#'starkbank.brcode-payment/java-to-clojure (.payment java-object))
        "boleto-payment" (#'starkbank.boleto-payment/java-to-clojure (.payment java-object))
        "utility-payment" (#'starkbank.utility-payment/java-to-clojure (.payment java-object)))
    }))

(defn- clojure-to-java
  ([clojure-map]
   (let [{center-id "center-id"
          payment "payment"
          type "type"
          due "due"
          tags "tags"}
         (stringify-keys clojure-map)]
     (PaymentRequest. (java.util.HashMap.
        {"centerId" center-id
        "payment" (case type
          "transfer" (#'starkbank.transfer/clojure-to-java payment)
          "transaction" (#'starkbank.transaction/clojure-to-java payment)
          "brcode-payment" (#'starkbank.brcode-payment/clojure-to-java payment)
          "boleto-payment" (#'starkbank.boleto-payment/clojure-to-java payment)
          "utility-payment" (#'starkbank.utility-payment/clojure-to-java payment))
        "type" type
        "due" due
        "tags" (if (nil? tags) nil (into-array String tags))})))))

(defn- clojure-query-to-java
  ([clojure-map]
    (let [{
        center-id "center-id"
        limit "limit"
        after "after"
        before "before"
        sort "sort"
        status "status"
        type "type"
        tags "tags"
        ids "ids"
      } (stringify-keys clojure-map)]
      (java.util.HashMap.
        {
          "centerId" center-id
          "limit" (if (nil? limit) nil (Integer. limit))
          "after" after
          "before" before
          "sort" sort
          "status" (if (nil? status) nil (into-array String status))
          "type" type
          "tags" (if (nil? tags) nil (into-array String tags))
          "ids" (if (nil? ids) nil (into-array String ids))
        }
      ))))

(defn create
  "Send a list of PaymentRequest maps for creation in the Stark Bank API

  ## Parameters (required):
    - `requests` [list of PaymentRequest maps]: list of PaymentRequest maps to be created in the API

  ## Options:
    - `:user` [Project or Organization]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - list of PaymentRequest maps with updated attributes"
  ([requests]
   (def java-requests (map clojure-to-java requests))
   (def created-java-requests (PaymentRequest/create java-requests))
   (map java-to-clojure created-java-requests))

  ([requests, user]
   (def java-requests (map clojure-to-java requests))
   (def created-java-requests (PaymentRequest/create java-requests (#'starkbank.user/get-java-user user)))
   (map java-to-clojure created-java-requests)))

(defn query
  "Receive a stream of PaymentRequest maps previously created by this user in the Stark Bank API

  ## Options:
    - `:limit` [integer, default nil]: maximum number of objects to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil] date filter for objects created only after specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil] date filter for objects created only before specified date. ex: \"2020-03-10\"
    - `:sort` [string, default \"-created\"]: sort order considered in response. Valid options are \"-created\" or \"-due\".
    - `:status` [string, default nil]: filter for status of retrieved objects. ex: \"success\" or \"failed\"
    - `:type` [string, default nil]: payment type, inferred from the payment parameter if it is not a map. ex: \"transfer\", \"brcode-payment\"
    - `:tags` [list of strings, default nil]: tags to filter retrieved objects. ex: [\"tony\", \"stark\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved objects. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - stream of PaymentRequest maps with updated attributes"
  ([]
    (map java-to-clojure (PaymentRequest/query)))

  ([params]
    (def java-params (clojure-query-to-java params))
    (map java-to-clojure (PaymentRequest/query java-params)))

  ([params, user] 
    (def java-params (clojure-query-to-java params))
    (map java-to-clojure (PaymentRequest/query java-params (#'starkbank.user/get-java-user user)))))
