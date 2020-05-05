(ns starkbank.boleto-payment
  "When you initialize a BoletoPayment, the entity will not be automatically
  created in the Stark Bank API. The 'create' function sends the maps
  to the Stark Bank API and returns the list of created maps.

  ## Parameters (conditionally required):
    - `:line` [string, default nil]: Number sequence that describes the payment. Either 'line' or 'bar-code' parameters are required. If both are sent, they must match. ex: \"34191.09008 63571.277308 71444.640008 5 81960000000062\"
    - `:bar-code` [string, default nil]: Bar code number that describes the payment. Either 'line' or 'barCode' parameters are required. If both are sent, they must match. ex: \"34195819600000000621090063571277307144464000\"

  ## Parameters (required):
    - `:tax-id` [string]: receiver tax ID (CPF or CNPJ) with or without formatting. ex: \"01234567890\" or \"20.018.183/0001-80\"
    - `:description` [string]: Text to be displayed in your statement (min. 10 characters). ex: \"payment ABC\"

  ## Parameters (optional):
    - `:scheduled` [string, default today]: payment scheduled date. ex: ~D[2020-03-25]
    - `:tags` [list of strings]: list of strings for tagging

  ## Attributes (return-only):
    - `:id` [string, default nil]: unique id returned when the payment is created. ex: \"5656565656565656\"
    - `:status` [string, default nil]: current payment status. ex: \"processing\" or \"success\"
    - `:amount` [int, default nil]: amount automatically calculated from line or bar-code. ex: 23456 (= R$ 234.56)
    - `:fee` [integer, default nil]: fee charged when a boleto payment is created. ex: 200 (= R$ 2.00)
    - `:created` [string, default nil]: creation datetime for the payment. ex: \"2020-03-26T19:32:35.418698+00:00\""
  (:import [com.starkbank BoletoPayment])
  (:use [starkbank.user]
        [clojure.walk]))

(defn- clojure-to-java
  ([clojure-map]
    (let [{
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
  "Send a list of BoletoPayment maps for creation in the Stark Bank API

  ## Parameters (required):
    - `payments` [list of BoletoPayment maps]: list of BoletoPayment maps to be created in the API

  ## Options:
    - `:user` [Project]: Project map returned from starkbank.user/project. Only necessary if starkbank.user/set-default-user has not been set.

  ## Return:
    - list of BoletoPayment maps with updated attributes"
  ([payments]
    (def java-payments (map clojure-to-java payments))
    (def created-java-payments (BoletoPayment/create java-payments))
    (map java-to-clojure created-java-payments))

  ([payments, user]
    (def java-payments (map clojure-to-java payments))
    (def created-java-payments (BoletoPayment/create java-payments (#'starkbank.user/get-java-project user)))
    (map java-to-clojure created-java-payments)))

(defn query
  "Receive a stream of BoletoPayment maps previously created in the Stark Bank API

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-3-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-3-10\"
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:ids` [list of strings, default null]: list of ids to filter retrieved objects. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:status` [string, default nil]: filter for status of retrieved maps. ex: \"success\"
    - `:user` [Project]: Project map returned from starkbank.user/project. Only necessary if starkbank.user/set-default-user has not been set.

  ## Return:
    - stream of BoletoPayment maps with updated attributes"
  ([]
    (map java-to-clojure (BoletoPayment/query)))

  ([params]
    (def java-params (clojure-query-to-java params))
    (map java-to-clojure (BoletoPayment/query java-params)))

  ([params, user] 
    (def java-params (clojure-query-to-java params))
    (map java-to-clojure (BoletoPayment/query java-params (#'starkbank.user/get-java-project user)))))

(defn get
  "Receive a single BoletoPayment map previously created by the Stark Bank API by passing its id

  ## Parameters (required):
    - `id` [string]: map unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project]: Project map returned from starkbank.user/project. Only necessary if starkbank.user/set-default-user has not been set.

  ## Return:
    - BoletoPayment map with updated attributes"
  ([id]
    (java-to-clojure
      (BoletoPayment/get id)))

  ([id, user]
    (java-to-clojure
      (BoletoPayment/get
        id
        (#'starkbank.user/get-java-project user)))))

(defn delete
  "Delete a BoletoPayment entity previously created in the Stark Bank API

  ## Parameters (required):
    - `id` [string]: BoletoPayment unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project]: Project map returned from starkbank.user/project. Only necessary if starkbank.user/set-default-user has not been set.

  ## Return:
    - deleted BoletoPayment map with updated attributes"
  ([id]
    (java-to-clojure
      (BoletoPayment/delete id)))

  ([id, user]
    (java-to-clojure
      (BoletoPayment/delete
        id
        (#'starkbank.user/get-java-project user)))))

(defn pdf
  "Receive a single BoletoPayment pdf file generated in the Stark Bank API by passing its id.
  Only valid for boleto payments with \"success\" status.

  ## Parameters (required):
    - `id` [string]: map unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project]: Project map returned from starkbank.user/project. Only necessary if starkbank.user/set-default-user has not been set.

  ## Return:
    - BoletoPayment pdf file content"
  ([id]
    (clojure.java.io/input-stream
      (BoletoPayment/pdf id)))

  ([id, user]
    (clojure.java.io/input-stream
      (BoletoPayment/pdf
        id
        (#'starkbank.user/get-java-project user)))))


(ns starkbank.boleto-payment.log
  "Every time a BoletoPayment entity is modified, a corresponding BoletoPayment.Log
  is generated for the entity. This log is never generated by the
  user, but it can be retrieved to check additional information
  on the BoletoPayment.

  ## Attributes:
    - `:id` [string]: unique id returned when the log is created. ex: \"5656565656565656\"
    - `:payment` [BoletoPayment]: BoletoPayment entity to which the log refers to.
    - `:errors` [list of strings]: list of errors linked to this BoletoPayment event.
    - `:type` [string]: type of the BoletoPayment event which triggered the log creation. ex: \"processing\" or \"success\"
    - `:created` [string]: creation datetime for the log. ex: \"2020-03-26T19:32:35.418698+00:00\""
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
  "Receive a single Log map previously created by the Stark Bank API by passing its id

  ## Parameters (required):
    - `id` [string]: map unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project]: Project map returned from starkbank.user/project. Only necessary if starkbank.user/set-default-user has not been set.

  ## Return:
    - Log map with updated attributes"
  ([id]
    (java-to-clojure
      (BoletoPayment$Log/get id)))

  ([id, user]
    (java-to-clojure
      (BoletoPayment$Log/get
        id
        (#'starkbank.user/get-java-project user)))))

(defn query
  "Receive a stream of Log maps previously created in the Stark Bank API

  ## Options:
    - `:limit` [integer, default nil]: maximum number of entities to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for entities created only after specified date. ex: \"2020-3-10\"
    - `:before` [string, default nil]: date filter for entities created only before specified date. ex: \"2020-3-10\"
    - `:types` [list of strings, default nil]: filter retrieved entities by event types. ex: \"processing\" or \"success\"
    - `:payment-ids` [list of strings, default nil]: list of BoletoPayment ids to filter retrieved entities. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:user` [Project]: Project map returned from starkbank.user/project. Only necessary if starkbank.user/set-default-user has not been set.

  ## Return:
    - stream of Log maps with updated attributes"
  ([]
    (map java-to-clojure (BoletoPayment$Log/query)))

  ([params]
    (def java-params (clojure-query-to-java params))
    (map java-to-clojure (BoletoPayment$Log/query java-params)))

  ([params, user] 
    (def java-params (clojure-query-to-java params))
    (map java-to-clojure (BoletoPayment$Log/query java-params (#'starkbank.user/get-java-project user)))))
