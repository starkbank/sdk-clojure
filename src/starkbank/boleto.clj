(ns starkbank.boleto
  "When you initialize a Boleto map, the entity will not be automatically
  sent to the Stark Bank API. The 'create' function sends the maps
  to the Stark Bank API and returns the list of created maps.

  ## Parameters (required):
    - `:amount` [integer]: Boleto value in cents. Minimum amount = 200 (R$2,00). ex: 1234 (= R$ 12.34)
    - `:name` [string]: payer full name. ex: \"Anthony Edward Stark\"
    - `:tax-id` [string]: payer tax ID (CPF or CNPJ) with or without formatting. ex: \"01234567890\" or \"20.018.183/0001-80\"
    - `:street-line-1` [string]: payer main address. ex: Av. Paulista, 200
    - `:street-line-2` [string]: payer address complement. ex: Apto. 123
    - `:district` [string]: payer address district / neighbourhood. ex: Bela Vista
    - `:city` [string]: payer address city. ex: Rio de Janeiro
    - `:state-code` [string]: payer address state. ex: GO
    - `:zip-code` [string]: payer address zip code. ex: 01311-200

  ## Parameters (optional):
    - `:due` [string, default today + 2 days]: Boleto due date in ISO format. ex: 2020-04-30
    - `:fine` [float, default 0.0]: Boleto fine for overdue payment in %. ex: 2.5
    - `:interest` [float, default 0.0]: Boleto monthly interest for overdue payment in %. ex: 5.2
    - `:overdue-limit` [integer, default 59]: limit in days for payment after due date. ex: 7 (max: 59)
    - `:receiver-name` [string]: receiver (Sacador Avalista) full name. ex: \"Anthony Edward Stark\"
    - `:receiver-tax-id` [string]: receiver (Sacador Avalista) tax ID (CPF or CNPJ) with or without formatting. ex: \"01234567890\" or \"20.018.183/0001-80\"
    - `:descriptions` [list of maps, default nil]: list of maps with :text (string) and :amount (int, optional) pairs
    - `:discounts` [list of maps, default nil]: list of maps with :percentage (float) and :date (string) pairs
    - `:tags` [list of strings]: list of strings for tagging

  ## Attributes (return-only):
    - `:id` [string, default nil]: unique id returned when the Boleto is created. ex: \"5656565656565656\"
    - `:our-number` [string, default nil]: Reference number registered at the settlement bank. ex: \"10131474\"
    - `:fee` [integer, default nil]: fee charged when the Boleto is paid. ex: 200 (= R$ 2.00)
    - `:line` [string, default nil]: generated Boleto line for payment. ex: \"34191.09008 63571.277308 71444.640008 5 81960000000062\"
    - `:bar-code` [string, default nil]: generated Boleto bar-code for payment. ex: \"34195819600000000621090063571277307144464000\"
    - `:status` [string, default nil]: current Boleto status. ex: \"registered\" or \"paid\"
    - `:transaction-ids` [list of strings, default nil]: ledger transaction ids linked to this boleto. ex: [\"19827356981273\"] 
    - `:created` [string, default nil]: creation datetime for the Boleto. ex: \"2020-03-26T19:32:35.418698+00:00\""
  (:refer-clojure :exclude [get set])
  (:import [com.starkbank Boleto])
  (:use [starkbank.user]
        [clojure.walk]))

(defn- clojure-descriptions-to-java
  ([clojure-map]
    (let [{
      amount "amount"
      text "text"
    }
    (stringify-keys clojure-map)]
      
      (java.util.HashMap.
        {
          "amount" (if (nil? amount) nil (Integer. amount))
          "text" text
        }
      ))))

(defn- clojure-to-java
  ([clojure-map]
    (let [{
      amount "amount"
      name "name"
      tax-id "tax-id"
      street-line-1 "street-line-1"
      street-line-2 "street-line-2"
      district "district"
      city "city"
      state-code "state-code"
      zip-code "zip-code"
      due "due"
      fine "fine"
      interest "interest"
      overdue-limit "overdue-limit"
      receiver-name "receiver-name"
      receiver-tax-id "receiver-tax-id"
      tags "tags"
      descriptions "descriptions"
      discounts "discounts"
    }
    (stringify-keys clojure-map)]

      (defn- apply-java-hashmap [x] (java.util.HashMap. x))
      
      (Boleto. (java.util.HashMap.
        {
          "amount" (if (nil? amount) nil (Integer. amount))
          "name" name
          "taxId" tax-id
          "streetLine1" street-line-1
          "streetLine2" street-line-2
          "district" district
          "city" city
          "stateCode" state-code
          "zipCode" zip-code
          "due" due
          "fine" (if (nil? fine) nil (double fine))
          "interest" (if (nil? interest) nil (double interest))
          "overdueLimit" (if (nil? overdue-limit) nil (Integer. overdue-limit))
          "receiverName" receiver-name
          "receiverTaxId" receiver-tax-id
          "tags" (if (nil? tags) nil (into-array String tags))
          "descriptions" (if (nil? descriptions) nil (java.util.ArrayList. (map clojure-descriptions-to-java descriptions)))
          "discounts" (if (nil? discounts) nil (java.util.ArrayList. (map apply-java-hashmap discounts)))
        }
      )))))

(defn- java-to-clojure
  ([java-object]
    (defn- java-description-to-map [description] 
      (if (nil? (.amount description))
        {:text (.text description)}
        {
          :amount (.amount description)
          :text (.text description)}))
    (defn- java-discount-to-map [discount] {
      :percentage (.percentage discount)
      :date (.date discount)})

    {
      :id (.id java-object)
      :amount (.amount java-object)
      :name (.name java-object)
      :tax-id (.taxId java-object)
      :street-line-1 (.streetLine1 java-object)
      :street-line-2 (.streetLine2 java-object)
      :district (.district java-object)
      :city (.city java-object)
      :state-code (.stateCode java-object)
      :zip-code (.zipCode java-object)
      :due (.due java-object)
      :fine (.fine java-object)
      :interest (.interest java-object)
      :overdue-limit (.overdueLimit java-object)
      :receiver-name (.receiverName java-object)
      :receiver-tax-id (.receiverTaxId java-object)
      :tags (into [] (.tags java-object))
      :descriptions (into [] (keywordize-keys (map java-description-to-map (.descriptions java-object))))
      :discounts (into [] (keywordize-keys (map java-discount-to-map (.discounts java-object))))
      :fee (.fee java-object)
      :our-number (.ourNumber java-object)
      :line (.line java-object)
      :bar-code (.barCode java-object)
      :status (.status java-object)
      :transaction-ids (into [] (.transactionIds java-object))
      :created (.created java-object)
    }))

(defn- clojure-query-to-java
  ([clojure-map]
    (let [{
        limit "limit"
        after "after"
        before "before"
        status "status"
        tags "tags"
        ids "ids"
      } (stringify-keys clojure-map)]
      (java.util.HashMap.
        {
          "limit" (if (nil? limit) nil (Integer. limit))
          "after" after
          "before" before
          "status" status
          "tags" (if (nil? tags) nil (into-array String tags))
          "ids" (if (nil? ids) nil (into-array String ids))
        }
      ))))

(defn- clojure-options-to-java
  [clojure-map]
  (let [{
    layout "layout"
    hidden-fields "hidden-fields"
  } (stringify-keys clojure-map)]
  (java.util.HashMap.
    {
      "layout" layout
      "hiddenFields" (if (nil? hidden-fields) nil (into-array String hidden-fields))
    }
  )))

(defn create
  "Send a list of Boleto maps for creation in the Stark Bank API

  ## Parameters (required):
    - `boletos` [list of Boleto maps]: list of Boleto maps to be created in the API

  ## Options:
    - `:user` [Project or Organization]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - list of Boleto maps with updated attributes"
  ([boletos]
    (def java-boletos (map clojure-to-java boletos))
    (def created-java-boletos (Boleto/create java-boletos))
    (map java-to-clojure created-java-boletos))

  ([boletos, user] 
    (def java-boletos (map clojure-to-java boletos))
    (def created-java-boletos (Boleto/create java-boletos (#'starkbank.user/get-java-user user)))
    (map java-to-clojure created-java-boletos)))

(defn query
  "Receive a stream of Boleto maps previously created in the Stark Bank API

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-3-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-3-10\"
    - `:status` [string, default nil]: filter for status of retrieved maps. ex: \"paid\" or \"registered\"
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:user` [Project or Organization]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - stream of Boleto maps with updated attributes"
  ([]
    (map java-to-clojure (Boleto/query)))

  ([params]
    (def java-params (clojure-query-to-java params))
    (map java-to-clojure (Boleto/query java-params)))

  ([params, user] 
    (def java-params (clojure-query-to-java params))
    (map java-to-clojure (Boleto/query java-params (#'starkbank.user/get-java-user user)))))

(defn get
  "Receive a single Boleto map previously created in the Stark Bank API by passing its id

  ## Parameters (required):
    - `id` [string]: map unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project or Organization]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - Boleto map with updated attributes"
  ([id]
    (java-to-clojure
      (Boleto/get id)))

  ([id, user]
    (java-to-clojure
      (Boleto/get
        id
        (#'starkbank.user/get-java-user user)))))

(defn delete
  "Delete a list of Boleto entities previously created in the Stark Bank API

  ## Parameters (required):
    - `id` [string]: Boleto unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project or Organization]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ##  Return:
    - deleted Boleto map with updated attributes"
  ([id]
    (java-to-clojure
      (Boleto/delete id)))

  ([id, user]
    (java-to-clojure
      (Boleto/delete
        id
        (#'starkbank.user/get-java-user user)))))

(defn pdf
  "Receive a single Boleto pdf file generated in the Stark Bank API by passing its id.

  ## Parameters (required):
    - `id` [string]: map unique id. ex: \"5656565656565656\"

  ## Options:
    - `:layout` [string]: Layout specification. Available options are \"default\" and \"booklet\"
    - `:hidden-fields` [list of strings]: List of string fields to be hidden in the Boleto pdf. ex: [\"customerAddress\"]
    - `:user` [Project or Organization]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - Boleto pdf file content"
  ([id]
    (clojure.java.io/input-stream
      (Boleto/pdf id)))

  ([id, user-or-options]
    (clojure.java.io/input-stream
      (Boleto/pdf
        id
        (#'starkbank.user/try-java-user user-or-options clojure-options-to-java ))))

  ([id, options, user]
    (clojure.java.io/input-stream
      (Boleto/pdf
        id
        (clojure-options-to-java options)
        (#'starkbank.user/get-java-user user)))))


(ns starkbank.boleto.log
  "Every time a Boleto entity is updated, a corresponding Boleto.Log
  is generated for the entity. This log is never generated by the
  user, but it can be retrieved to check additional information
  on the Boleto.

  ## Attributes:
    - `:id` [string]: unique id returned when the log is created. ex: \"5656565656565656\"
    - `:boleto` [Boleto]: Boleto entity to which the log refers to.
    - `:errors` [list of strings]: list of errors linked to this Boleto event
    - `:type` [string]: type of the Boleto event which triggered the log creation. ex: \"registered\" or \"paid\"
    - `:created` [string]: creation datetime for the log. ex: \"2020-03-26T19:32:35.418698+00:00\""
  (:refer-clojure :exclude [get set])
  (:import [com.starkbank Boleto$Log])
  (:require [starkbank.boleto :as boleto])
  (:use [starkbank.user]
        [clojure.walk]))

(defn- java-to-clojure
  ([java-object]
    {
      :id (.id java-object)
      :created (.created java-object)
      :errors (into [] (.errors java-object))
      :type (.type java-object)
      :boleto (#'boleto/java-to-clojure (.boleto java-object))
    }))

(defn- clojure-query-to-java
  ([clojure-map]
    (let [{
        limit "limit"
        after "after"
        before "before"
        types "types"
        boleto-ids "boleto-ids"
      } (stringify-keys clojure-map)]
      (java.util.HashMap.
        {
          "limit" (if (nil? limit) nil (Integer. limit))
          "after" after
          "before" before
          "types" (if (nil? types) nil (into-array String types))
          "boletoIds" (if (nil? boleto-ids) nil (into-array String boleto-ids))
        }
      ))))

(defn get
  "Receive a single Log map previously created by the Stark Bank API by passing its id

  ## Parameters (required):
    - `id` [string]: map unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project or Organization]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - Log map with updated attributes"
  ([id]
    (java-to-clojure
      (Boleto$Log/get id)))

  ([id, user]
    (java-to-clojure
      (Boleto$Log/get
        id
        (#'starkbank.user/get-java-user user)))))

(defn query
  "Receive a stream of Log maps previously created in the Stark Bank API

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-3-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-3-10\"
    - `:types` [list of strings, default nil]: filter for log event types. ex: \"paid\" or \"registered\"
    - `:boleto-ids` [list of strings, default nil]: list of Boleto ids to filter logs. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:user` [Project or Organization]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - stream of Log maps with updated attributes"
  ([]
    (map java-to-clojure (Boleto$Log/query)))

  ([params]
    (def java-params (clojure-query-to-java params))
    (map java-to-clojure (Boleto$Log/query java-params)))

  ([params, user] 
    (def java-params (clojure-query-to-java params))
    (map java-to-clojure (Boleto$Log/query java-params (#'starkbank.user/get-java-user user)))))
