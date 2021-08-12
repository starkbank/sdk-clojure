(ns starkbank.brcode-payment
    "When you initialize a BrcodePayment, the entity will not be automatically
    created in the Stark Bank API. The 'create' function sends the objects
    to the Stark Bank API and returns the list of created objects.
  
    ## Parameters (required):
      - `:brcode` [string]: String loaded directly from the QRCode or copied from the invoice. ex: \"00020126580014br.gov.bcb.pix0136a629532e-7693-4846-852d-1bbff817b5a8520400005303986540510.005802BR5908T'Challa6009Sao Paulo62090505123456304B14A\"
      - `:tax-id` [string]: receiver tax ID (CPF or CNPJ) with or without formatting. ex: \"01234567890\" or \"20.018.183/0001-80\"
      - `:description` [string]: Text to be displayed in your statement (min. 10 characters). ex: \"payment ABC\"
  
    ## Parameters (optional):
      - `:amount` [integer, default nil]: amount automatically calculated from line or barCode. ex: 23456 (= R$ 234.56)
      - `:scheduled` [string, default now]: payment scheduled date or datetime. ex: \"2020-11-25T17:59:26.249976+00:00\"
      - `:tags` [list of strings, default nil]: list of strings for tagging
  
    ## Attributes (return-only):
      - `:id` [string, default nil]: unique id returned when payment is created. ex: \"5656565656565656\"
      - `:status` [string, default nil]: current payment status. ex: \"success\" or \"failed\"
      - `:type` [string, default nil]: brcode type. ex: \"static\" or \"dynamic\"
      - `:fee` [integer, default nil]: fee charged when the brcode payment is created. ex: 200 (= R$ 2.00)
      - `:transaction-ids` [list of strings, default nil]: ledger transaction ids linked to this boleto. ex: [\"19827356981273\"] 
      - `:updated` [string, default nil]: latest update datetime for the payment. ex: \"2020-11-25T17:59:26.249976+00:00\"
      - `:created` [string, default nil]: creation datetime for the payment. ex: \"2020-11-25T17:59:26.249976+00:00\""
    (:refer-clojure :exclude [get set update])
    (:import [com.starkbank BrcodePayment])
    (:use [starkbank.user]
          [clojure.walk]))
  
(defn- clojure-to-java
  ([clojure-map]
    (let [{
      brcode "brcode"
      tax-id "tax-id"
      description "description"
      amount "amount"
      scheduled "scheduled"
      tags "tags"
    }
    (stringify-keys clojure-map)]

      (defn- apply-java-hashmap [x] (java.util.HashMap. x))
      
      (BrcodePayment. (java.util.HashMap.
        {
          "brcode" brcode
          "taxId" tax-id
          "description" description
          "amount" (if (nil? amount) nil (Long. amount))
          "scheduled" scheduled
          "tags" (if (nil? tags) nil (into-array String tags))
        }
      )))))

(defn- java-to-clojure
  ([java-object]
    {
      :id (.id java-object)
      :brcode (.brcode java-object)
      :tax-id (.taxId java-object)
      :description (.description java-object)
      :amount (.amount java-object)
      :scheduled (.scheduled java-object)
      :tags (.tags java-object)
      :status (.status java-object)
      :type (.type java-object)
      :fee (.fee java-object)
      :transaction-ids (into [] (.transactionIds java-object))
      :updated (.updated java-object)
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

(defn- clojure-update-to-java
  ([clojure-map]
    (let [{
      status "status"
    } (stringify-keys clojure-map)]
      (java.util.HashMap.
      {
        "status" status
      }))))

(defn create
  "Send a list of BrcodePayment maps for creation in the Stark Bank API

  ## Parameters (required):
    - `payments` [list of BrcodePayment maps]: list of BrcodePayment maps to be created in the API

  ## Options:
    - `:user` [Project or Organization]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - list of BrcodePayment maps with updated attributes"
  ([payments]
    (def java-payments (map clojure-to-java payments))
    (def created-java-payments (BrcodePayment/create java-payments))
    (map java-to-clojure created-java-payments))

  ([payments, user]
    (def java-payments (map clojure-to-java payments))
    (def created-java-payments (BrcodePayment/create java-payments (#'starkbank.user/get-java-user user)))
    (map java-to-clojure created-java-payments)))

(defn query
  "Receive a stream of BrcodePayment maps previously created in the Stark Bank API

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-3-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-3-10\"
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved objects. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:status` [string, default nil]: filter for status of retrieved maps. ex: \"success\"
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - stream of BrcodePayment maps with updated attributes"
  ([]
    (map java-to-clojure (BrcodePayment/query)))

  ([params]
    (def java-params (clojure-query-to-java params))
    (map java-to-clojure (BrcodePayment/query java-params)))

  ([params, user] 
    (def java-params (clojure-query-to-java params))
    (map java-to-clojure (BrcodePayment/query java-params (#'starkbank.user/get-java-user user)))))

(defn get
  "Receive a single BrcodePayment map previously created by the Stark Bank API by passing its id

  ## Parameters (required):
    - `:id` [string]: map unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project or Organization]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - BrcodePayment map with updated attributes"
  ([id]
    (java-to-clojure
      (BrcodePayment/get id)))

  ([id, user]
    (java-to-clojure
      (BrcodePayment/get
        id
        (#'starkbank.user/get-java-user user)))))

(defn pdf
  "Receive a single BrcodePayment pdf file generated in the Stark Bank API by passing its id.
  Only valid for brcode payments with \"success\" status.

  ## Parameters (required):
    - `:id` [string]: map unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project or Organization]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - BrcodePayment pdf file content"
  ([id]
    (clojure.java.io/input-stream
      (BrcodePayment/pdf id)))

  ([id, user]
    (clojure.java.io/input-stream
      (BrcodePayment/pdf
        id
        (#'starkbank.user/get-java-user user)))))
  
(defn update
  "Update a BrcodePayment by passing id.

  ## Parameters (required):
    - `:id` [list of strings]: BrcodePayment unique ids. ex: \"5656565656565656\"

  ## Parameters (optional):
    - `:status` [string]: If the BrcodePayment hasn't been paid yet, you may cancel it by passing \"canceled\" in the status

  ## Return:
    - target BrcodePayment with updated attributes"
  ([id, params]
    (java-to-clojure
    (BrcodePayment/update id (clojure-update-to-java params))))

  ([id, params, user]
    (java-to-clojure
    (BrcodePayment/update
      id
      (clojure-update-to-java params)
      (#'starkbank.user/get-java-user user)))))

(ns starkbank.brcode-payment.log
  "Every time a BrcodePayment entity is modified, a corresponding BrcodePayment.Log
  is generated for the entity. This log is never generated by the
  user, but it can be retrieved to check additional information
  on the BrcodePayment.

  ## Attributes:
    - `:id` [string]: unique id returned when the log is created. ex: \"5656565656565656\"
    - `:payment` [BrcodePayment]: BrcodePayment entity to which the log refers to.
    - `:errors` [list of strings]: list of errors linked to this BrcodePayment event.
    - `:type` [string]: type of the BrcodePayment event which triggered the log creation. ex: \"processing\" or \"success\"
    - `:created` [string]: creation datetime for the log. ex: \"2020-03-26T19:32:35.418698+00:00\""
  (:refer-clojure :exclude [get set])
  (:import [com.starkbank BrcodePayment$Log])
  (:require [starkbank.brcode-payment :as payment])
  (:use [starkbank.user]
        [clojure.walk]))

(defn- java-to-clojure
  ([java-object]
    {
      :id (.id java-object)
      :created (.created java-object)
      :errors (into [] (.errors java-object))
      :type (.type java-object)
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
    - `:id` [string]: map unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project or Organization]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - Log map with updated attributes"
  ([id]
    (java-to-clojure
      (BrcodePayment$Log/get id)))

  ([id, user]
    (java-to-clojure
      (BrcodePayment$Log/get
        id
        (#'starkbank.user/get-java-user user)))))

(defn query
  "Receive a stream of Log maps previously created in the Stark Bank API

  ## Options:
    - `:limit` [integer, default nil]: maximum number of entities to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for entities created only after specified date. ex: \"2020-3-10\"
    - `:before` [string, default nil]: date filter for entities created only before specified date. ex: \"2020-3-10\"
    - `:types` [list of strings, default nil]: filter retrieved entities by event types. ex: \"processing\" or \"success\"
    - `:payment-ids` [list of strings, default nil]: list of BrcodePayment ids to filter retrieved entities. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - stream of Log maps with updated attributes"
  ([]
    (map java-to-clojure (BrcodePayment$Log/query)))

  ([params]
    (def java-params (clojure-query-to-java params))
    (map java-to-clojure (BrcodePayment$Log/query java-params)))

  ([params, user] 
    (def java-params (clojure-query-to-java params))
    (map java-to-clojure (BrcodePayment$Log/query java-params (#'starkbank.user/get-java-user user)))))
