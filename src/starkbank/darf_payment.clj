(ns starkbank.darf-payment
  "When you initialize a DarfPayment, the entity will not be automatically
  created in the Stark Bank API. The 'create' function sends the objects
  to the Stark Bank API and returns the list of created objects.

  ## Parameters (required):
    - `:description` [string]: Text to be displayed in your statement (min. 10 characters). ex: \"payment ABC\"
    - `:revenue-code` [string]: 4-digit tax code assigned by Federal Revenue. ex: \"5948\"
    - `:tax-id` [string]: tax id (formatted or unformatted) of the payer. ex: \"12.345.678/0001-95\"
    - `:competence` [string]: competence month of the service. ex: \"2021-4-30\"
    - `:nominal-amount` [long]: amount due in cents without fee or interest. ex: 23456 (= R$ 234.56)
    - `:fine-amount` [long]: fixed amount due in cents for fines. ex: 234 (= R$ 2.34)
    - `:interest-amount` [long]: amount due in cents for interest. ex: 456 (= R$ 4.56)
    - `:due` [string]: due date for payment. ex: \"2021-5-17\"

  ## Parameters (optional):
    - `:reference-number` [string]: number assigned to the region of the tax. ex: \"08.1.17.00-4\"
    - `:scheduled` [string, default today]: payment scheduled date. ex: \"2020-03-25\"
    - `:tags` [list of strings]: list of strings for tagging

  Attributes (return-only):
    - `:id` [string, default nil]: unique id returned when the payment is created. ex: \"5656565656565656\"
    - `:status` [string, default nil]: current payment status. ex: \"processing\" or \"success\"
    - `:amount` [integer, default nil]: amount automatically calculated from line or bar-code. ex: 23456 (= R$ 234.56)
    - `:fee` [integer, default nil]: fee charged when a utility payment is created. ex: 200 (= R$ 2.00)
    - `:updated` [string, default nil]: latest update datetime for the payment. ex: \"2020-03-26T19:32:35.418698+00:00\"
    - `:created` [string, default nil]: creation datetime for the payment. ex: \"2020-03-26T19:32:35.418698+00:00\""
  (:refer-clojure :exclude [get set])
  (:import [com.starkbank DarfPayment])
  (:use [starkbank.user]
        [clojure.walk]))

(defn- clojure-to-java
  ([clojure-map]
    (let [{
      description "description"
      revenue-code "revenue-code"
      tax-id "tax-id"
      competence "competence"
      nominal-amount "nominal-amount"
      fine-amount "fine-amount"
      interest-amount "interest-amount"
      due "due"
      reference-number "reference-number"
      scheduled "scheduled"
      tags "tags"
    }
    (stringify-keys clojure-map)]

      (defn- apply-java-hashmap [x] (java.util.HashMap. x))
      
      (DarfPayment. (java.util.HashMap.
        {
          "description" description
          "revenueCode" revenue-code
          "taxId" tax-id
          "competence" competence
          "nominalAmount" (if (nil? nominal-amount) nil (Long. nominal-amount))
          "fineAmount" (if (nil? fine-amount) nil (Long. fine-amount))
          "interestAmount" (if (nil? interest-amount) nil (Long. interest-amount))
          "due" due
          "referenceNumber" reference-number
          "scheduled" scheduled
          "tags" (if (nil? tags) nil (into-array String tags))
        }
      )))))

(defn- java-to-clojure
  ([java-object]
    {
      :id (.id java-object)
      :description (.description java-object)
      :revenue-code (.revenueCode java-object)
      :tax-id (.taxId java-object)
      :competence (.competence java-object)
      :nominal-amount (.nominalAmount java-object)
      :fine-amount (.fineAmount java-object)
      :interest-amount (.interestAmount java-object)
      :due (.due java-object)
      :reference-number (.referenceNumber java-object)
      :scheduled (.scheduled java-object)
      :tags (into [] (.tags java-object))
      :status (.status java-object)
      :amount (.amount java-object)
      :fee (.fee java-object)
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

(defn- clojure-page-to-java
  ([clojure-map]
    (let [{
        cursor "cursor"
        limit "limit"
        after "after"
        before "before"
        tags "tags"
        ids "ids"
        status "status"
      } (stringify-keys clojure-map)]
      (java.util.HashMap.
        {
          "cursor" cursor
          "limit" (if (nil? limit) nil (Integer. limit))
          "after" after
          "before" before
          "tags" (if (nil? tags) nil (into-array String tags))
          "ids" (if (nil? ids) nil (into-array String ids))
          "status" status
        }
      ))))

(defn create
  "Send a list of DarfPayment objects for creation in the Stark Bank API

  ## Parameters (required):
    - `payments` [list of DarfPayment maps]: list of DarfPayment maps to be created in the API

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - list of DarfPayment maps with updated attributes"
  ([payments]
    (def java-payments (map clojure-to-java payments))
    (def created-java-payments (DarfPayment/create java-payments))
    (map java-to-clojure created-java-payments))

  ([payments, user]
    (def java-payments (map clojure-to-java payments))
    (def created-java-payments (DarfPayment/create java-payments (#'starkbank.user/get-java-user user)))
    (map java-to-clojure created-java-payments)))

(defn query
  "Receive a stream of DarfPayment objects previously created in the Stark Bank API.
  Use this function instead of page if you want to stream the objects without worrying about cursors and pagination.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-3-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-3-10\"
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:status` [string, default nil]: filter for status of retrieved maps. ex: \"success\"
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - stream of UtilityPayment maps with updated attributes"
  ([]
    (map java-to-clojure (DarfPayment/query)))

  ([params]
    (def java-params (clojure-query-to-java params))
    (map java-to-clojure (DarfPayment/query java-params)))

  ([params, user] 
    (def java-params (clojure-query-to-java params))
    (map java-to-clojure (DarfPayment/query java-params (#'starkbank.user/get-java-user user)))))

(defn page
  "Receive a list of up to 100 DarfPayment objects previously created in the Stark Bank API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-3-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-3-10\"
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:status` [string, default nil]: filter for status of retrieved maps. ex: \"success\"
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - map with :payments and :cursor:
      - `:payments`: list of payment maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page of payments"
  ([]
    (def payment-page (DarfPayment/page))
    (def cursor (.cursor payment-page))
    (def payments (map java-to-clojure (.payments payment-page)))
    {:payments payments, :cursor cursor})

  ([params]
    (def java-params (clojure-page-to-java params))
    (def payment-page (DarfPayment/page java-params))
    {:payments (map java-to-clojure (.payments payment-page)), :cursor (.cursor payment-page)})

  ([params, user] 
    (def java-params (clojure-page-to-java params))
    (def payment-page (DarfPayment/page java-params (#'starkbank.user/get-java-user user)))
    {:payments (map java-to-clojure (.payments payment-page)), :cursor (.cursor payment-page)}))

(defn get
  "Receive a single DarfPayment object previously created by the Stark Bank API by passing its id

  ## Parameters (required):
    - `:id` [string]: map unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - DarfPayment map with updated attributes"
  ([id]
    (java-to-clojure
      (DarfPayment/get id)))

  ([id, user]
    (java-to-clojure
      (DarfPayment/get
        id
        (#'starkbank.user/get-java-user user)))))

(defn delete
  "Delete a DarfPayment entity previously created in the Stark Bank API

  ## Parameters (required):
    - `:id` [string]: UtilityPayment unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - deleted DarfPayment map"
  ([id]
    (java-to-clojure
      (DarfPayment/delete id)))

  ([id, user]
    (java-to-clojure
      (DarfPayment/delete
        id
        (#'starkbank.user/get-java-user user)))))

(defn pdf
  "Receive a single DarfPayment pdf file generated in the Stark Bank API by passing its id.
  Only valid for darf payments with \"success\" status.

  ## Parameters (required):
    - `:id` [string]: map unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - DarfPayment pdf file content"
  ([id]
    (clojure.java.io/input-stream
      (DarfPayment/pdf id)))

  ([id, user]
    (clojure.java.io/input-stream
      (DarfPayment/pdf
        id
        (#'starkbank.user/get-java-user user)))))


(ns starkbank.darf-payment.log
  "Every time a DarfPayment entity is modified, a corresponding darfpayment.Log
  is generated for the entity. This log is never generated by the user, but it can
  be retrieved to check additional information on the DarfPayment.

  ## Attributes:
    - `:id` [string]: unique id returned when the log is created. ex: \"5656565656565656\"
    - `:payment` [UtilityPayment]: UtilityPayment entity to which the log refers to.
    - `:errors` [list of strings]: list of errors linked to this UtilityPayment event.
    - `:type` [string]: type of the UtilityPayment event which triggered the log creation. ex: \"processing\" or \"success\"
    - `:created` [string]: creation datetime for the log. ex: \"2020-03-26T19:32:35.418698+00:00\""
  (:refer-clojure :exclude [get set])
  (:import [com.starkbank DarfPayment$Log])
  (:require [starkbank.darf-payment :as payment])
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

(defn- clojure-page-to-java
  ([clojure-map]
    (let [{
        cursor "cursor"
        limit "limit"
        after "after"
        before "before"
        types "types"
        payment-ids "payment-ids"
      } (stringify-keys clojure-map)]
      (java.util.HashMap.
        {
          "cursor" cursor
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
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - Log map with updated attributes"
  ([id]
    (java-to-clojure
      (DarfPayment$Log/get id)))

  ([id, user]
    (java-to-clojure
      (DarfPayment$Log/get
        id
        (#'starkbank.user/get-java-user user)))))

(defn query
  "Receive a stream of Log maps previously created in the Stark Bank API.
  Use this function instead of page if you want to stream the objects without worrying about cursors and pagination.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of entities to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for entities created only after specified date. ex: \"2020-3-10\"
    - `:before` [string, default nil]: date filter for entities created only before specified date. ex: \"2020-3-10\"
    - `:types` [list of strings, default nil]: filter retrieved entities by event types. ex: \"processing\" or \"success\"
    - `:payment-ids` [list of strings, default nil]: list of UtilityPayment ids to filter retrieved entities. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - stream of Log maps with updated attributes"
  ([]
    (map java-to-clojure (DarfPayment$Log/query)))

  ([params]
    (def java-params (clojure-query-to-java params))
    (map java-to-clojure (DarfPayment$Log/query java-params)))

  ([params, user] 
    (def java-params (clojure-query-to-java params))
    (map java-to-clojure (DarfPayment$Log/query java-params (#'starkbank.user/get-java-user user)))))

(defn page
  "Receive a list of up to 100 DarfPayment.Log maps previously created in the Stark Bank API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default nil]: maximum number of entities to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for entities created only after specified date. ex: \"2020-3-10\"
    - `:before` [string, default nil]: date filter for entities created only before specified date. ex: \"2020-3-10\"
    - `:types` [list of strings, default nil]: filter retrieved entities by event types. ex: \"processing\" or \"success\"
    - `:payment-ids` [list of strings, default nil]: list of DarfPayment ids to filter retrieved entities. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - map with :logs and :cursor:
      - `:logs`: list of log maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page of logs"
  ([]
    (def log-page (DarfPayment$Log/page))
    (def cursor (.cursor log-page))
    (def logs (map java-to-clojure (.logs log-page)))
    {:logs logs, :cursor cursor})

  ([params]
    (def java-params (clojure-page-to-java params))
    (def log-page (DarfPayment$Log/page java-params))
    {:logs (map java-to-clojure (.logs log-page)), :cursor (.cursor log-page)})

  ([params, user] 
    (def java-params (clojure-page-to-java params))
    (def log-page (DarfPayment$Log/page java-params (#'starkbank.user/get-java-user user)))
    {:logs (map java-to-clojure (.logs log-page)), :cursor (.cursor log-page)}))
