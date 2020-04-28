(ns starkbank.transfer
  "When you initialize a Transfer, the entity will not be automatically
  created in the Stark Bank API. The 'create' function sends the maps
  to the Stark Bank API and returns the list of created maps.

  ## Parameters (required):
    - `:amount` [integer]: amount in cents to be transferred. ex: 1234 (= R$ 12.34)
    - `:name` [string]: receiver full name. ex: \"Anthony Edward Stark\"
    - `:tax-id` [string]: receiver tax ID (CPF or CNPJ) with or without formatting. ex: \"01234567890\" or \"20.018.183/0001-80\"
    - `:bank-code` [string]: receiver 1 to 3 digits of the bank institution in Brazil. ex: \"200\" or \"341\"
    - `:branch-code` [string]: receiver bank account branch. Use '-' in case there is a verifier digit. ex: \"1357-9\"
    - `:account-number` [string]: Receiver Bank Account number. Use '-' before the verifier digit. ex: \"876543-2\"

  ## Parameters (optional):
    - `:tags` [list of strings]: list of strings for reference when searching for transfers. ex: [\"employees\", \"monthly\"]

  Attributes (return-only):
    - `:id` [string, default nil]: unique id returned when Transfer is created. ex: \"5656565656565656\"
    - `:fee` [integer, default nil]: fee charged when transfer is created. ex: 200 (= R$ 2.00)
    - `:status` [string, default nil]: current boleto status. ex: \"registered\" or \"paid\"
    - `:transaction-ids` [list of strings, default nil]: ledger transaction ids linked to this transfer (if there are two, second is the chargeback). ex: [\"19827356981273\"]
    - `:created` [string, default nil]: creation datetime for the transfer. ex: \"2020-03-26T19:32:35.418698+00:00\"
    - `:updated` [string, default nil]: latest update datetime for the transfer. ex: \"2020-03-26T19:32:35.418698+00:00\""
  (:import [com.starkbank Transfer])
  (:use [starkbank.user]
        [clojure.walk]))

(defn- clojure-to-java
  ([clojure-map]
    (let [{
      amount "amount"
      name "name"
      tax-id "tax-id"
      bank-code "bank-code"
      branch-code "branch-code"
      account-number "account-number"
      tags "tags"
    }
    (stringify-keys clojure-map)]
      
      (Transfer. (java.util.HashMap.
        {
          "amount" (if (nil? amount) nil (Integer. amount))
          "name" name
          "taxId" tax-id
          "bankCode" bank-code
          "branchCode" branch-code
          "accountNumber" account-number
          "tags" (if (nil? tags) nil (into-array String tags))
        }
      )))))

(defn- java-to-clojure
  ([java-object]
    {
      :id (.id java-object)
      :amount (.amount java-object)
      :name (.name java-object)
      :tax-id (.taxId java-object)
      :bank-code (.bankCode java-object)
      :branch-code (.branchCode java-object)
      :account-number (.accountNumber java-object)
      :tags (into [] (.tags java-object))
      :fee (.fee java-object)
      :status (.status java-object)
      :created (.created java-object)
      :updated (.updated java-object)
      :transaction-ids (into [] (.transactionIds java-object))
    }))

(defn- clojure-query-to-java
  ([clojure-map]
    (let [{
        limit "limit"
        after "after"
        before "before"
        transaction-ids "transaction-ids"
        status "status"
        sort "sort"
        tags "tags"
      } (stringify-keys clojure-map)]
      (java.util.HashMap.
        {
          "limit" (if (nil? limit) nil (Integer. limit))
          "after" after
          "before" before
          "transactionIds" (if (nil? transaction-ids) nil (into-array String transaction-ids))
          "status" status
          "sort" sort
          "tags" (if (nil? tags) nil (into-array String tags))
        }
      ))))

(defn create
  "Send a list of Transfer maps for creation in the Stark Bank API

  ## Parameters (required):
    - `transfers` [list of Transfer maps]: list of Transfer maps to be created in the API

  ## Options:
    - `:user` [Project]: Project map returned from starkbank.user/project. Only necessary if starkbank.user/set-default-user has not been set.

  ## Return:
    - list of Transfer maps with updated attributes"
  ([transfers]
    (def java-transfers (map clojure-to-java transfers))
    (def created-java-transfers (Transfer/create java-transfers))
    (map java-to-clojure created-java-transfers))

  ([transfers, user]
    (def java-transfers (map clojure-to-java transfers))
    (def created-java-transfers (Transfer/create java-transfers (#'starkbank.user/get-java-project user)))
    (map java-to-clojure created-java-transfers)))

(defn query
  "Receive a stream of Transfer maps previously created in the Stark Bank API

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created or updated only after specified date. ex: ~D[2020-03-25]
    - `:before` [string, default nil]: date filter for maps created or updated only before specified date. ex: ~D[2020-03-25]
    - `:transaction-ids` [list of strings, default nil]: list of transaction IDs linked to the desired transfers. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:status` [string, default nil]: filter for status of retrieved maps. ex: \"paid\" or \"registered\"
    - `:sort` [string, default \"-created\"]: sort order considered in response. Valid options are \"created\", \"-created\", \"updated\" or \"-updated\".
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:user` [Project]: Project map returned from starkbank.user/project. Only necessary if starkbank.user/set-default-user has not been set.

  ## Return:
    - stream of Transfer maps with updated attributes"
  ([]
    (map java-to-clojure (Transfer/query)))

  ([params]
    (def java-params (clojure-query-to-java params))
    (map java-to-clojure (Transfer/query java-params)))

  ([params, user] 
    (def java-params (clojure-query-to-java params))
    (map java-to-clojure (Transfer/query java-params (#'starkbank.user/get-java-project user)))))

(defn get
  "Receive a single Transfer map previously created in the Stark Bank API by passing its id

  ## Parameters (required):
    - `id` [string]: map unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project]: Project map returned from starkbank.user/project. Only necessary if starkbank.user/set-default-user has not been set.

  ## Return:
    - Transfer map with updated attributes"
  ([id]
    (java-to-clojure
      (Transfer/get id)))

  ([id, user]
    (java-to-clojure
      (Transfer/get
        id
        (#'starkbank.user/get-java-project user)))))

(defn pdf
  "Receive a single Transfer pdf receipt file generated in the Stark Bank API by passing its id.
  Only valid for transfers with \"processing\" or \"success\" status.

  ## Parameters (required):
    - `id` [string]: map unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project]: Project map returned from starkbank.user/project. Only necessary if starkbank.user/set-default-user has not been set.

  ## Return:
    - Transfer pdf file content"
  ([id]
    (clojure.java.io/input-stream
      (Transfer/pdf id)))

  ([id, user]
    (clojure.java.io/input-stream
      (Transfer/pdf
        id
        (#'starkbank.user/get-java-project user)))))


(ns starkbank.transfer.log
  "Every time a Transfer entity is modified, a corresponding Transfer.Log
  is generated for the entity. This log is never generated by the
  user.

  ## Attributes:
    - `:id` [string]: unique id returned when the log is created. ex: \"5656565656565656\"
    - `:transfer` [Transfer]: Transfer entity to which the log refers to.
    - `:errors` [list of strings]: list of errors linked to this BoletoPayment event.
    - `:type` [string]: type of the Transfer event which triggered the log creation. ex: \"processing\" or \"success\"
    - `:created` [string]: creation datetime for the transfer. ex: \"2020-03-26T19:32:35.418698+00:00\""
  (:import [com.starkbank Transfer$Log])
  (:require [starkbank.transfer :as transfer])
  (:use [starkbank.user]
        [clojure.walk]))

(defn- java-to-clojure
  ([java-object]
    {
      :id (.id java-object)
      :created (.created java-object)
      :errors (into [] (.errors java-object))
      :transfer (#'transfer/java-to-clojure (.transfer java-object))
    }))

(defn- clojure-query-to-java
  ([clojure-map]
    (let [{
        limit "limit"
        after "after"
        before "before"
        types "types"
        transfer-ids "transfer-ids"
      } (stringify-keys clojure-map)]
      (java.util.HashMap.
        {
          "limit" (if (nil? limit) nil (Integer. limit))
          "after" after
          "before" before
          "types" (if (nil? types) nil (into-array String types))
          "transferIds" (if (nil? transfer-ids) nil (into-array String transfer-ids))
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
      (Transfer$Log/get id)))

  ([id, user]
    (java-to-clojure
      (Transfer$Log/get
        id
        (#'starkbank.user/get-java-project user)))))

(defn query
  "Receive a stream of Log maps previously created in the Stark Bank API

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-3-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-3-10\"
    - `:types` [list of strings, default nil]: filter retrieved maps by types. ex: \"success\" or \"failed\"
    - `:transfer-ids` [list of strings, default nil]: list of Transfer ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:user` [Project]: Project map returned from starkbank.user/project. Only necessary if starkbank.user/set-default-user has not been set.

  ## Return:
    - stream of Log maps with updated attributes"
  ([]
    (map java-to-clojure (Transfer$Log/query)))

  ([params]
    (def java-params (clojure-query-to-java params))
    (map java-to-clojure (Transfer$Log/query java-params)))

  ([params, user] 
    (def java-params (clojure-query-to-java params))
    (map java-to-clojure (Transfer$Log/query java-params (#'starkbank.user/get-java-project user)))))
