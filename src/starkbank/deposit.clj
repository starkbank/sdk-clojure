(ns starkbank.deposit
    "Deposits represent non-reconciled cash-ins received by your account from external transfers or payments
  
    ## Parameters (required):
      - `:id` [string]: unique id associated with a Deposit when it is created. ex: \"5656565656565656\"
      - `:name` [string]: payer name. ex: \"Iron Bank S.A.\"
      - `:tax-id` [string]: payer tax ID (CPF or CNPJ). ex: \"012.345.678-90\" or \"20.018.183/0001-80\"
      - `:bank-code` [string]: payer bank code in Brazil. ex: \"20018183\" or \"341\"
      - `:branch-code` [string]: payer bank account branch. ex: \"1357-9\"
      - `:account-number` [string]: payer bank account number. ex: \"876543-2\"
      - `:amount` [integer]: Deposit value in cents. ex: 1234 (= R$ 12.34)
      - `:type` [string]: type of settlement that originated the deposit. ex: \"pix\" or \"ted\"
      - `:status` [string]: current Deposit status. ex: \"created\"
      - `:tags` [list of strings]: list of strings that are tagging the deposit. ex: [\"reconciliationId\", \"taxId\"]
      - `:fee` [integer]: fee charged when a deposit is created. ex: 50 (= R$ 0.50)
      - `:transaction-ids` [list of strings]: ledger transaction ids linked to this deposit (if there are more than one, all but first are reversals). ex: [\"19827356981273\"]
      - `:created` [string]: creation datetime for the Deposit. ex: \"2020-03-10 10:30:00.000\"
      - `:updated` [string]: latest update datetime for the Deposit. ex: \"2020-03-10 10:30:00.000\""
    (:refer-clojure :exclude [get set update])
    (:import [com.starkbank Deposit])
    (:use [starkbank.user]
          [clojure.walk]))

(defn- java-to-clojure
([java-object]
  {
    :id (.id java-object)
    :name (.name java-object)
    :tax-id (.taxId java-object)
    :bank-code (.bankCode java-object)
    :branch-code (.branchCode java-object)
    :account-number (.accountNumber java-object)
    :amount (.amount java-object)
    :type (.type java-object)
    :status (.status java-object)
    :tags (into [] (.tags java-object))
    :fee (.fee java-object)
    :transaction-ids (into [] (.transactionIds java-object))
    :created (.created java-object)
    :updated (.updated java-object)
  }))

(defn- clojure-query-to-java
([clojure-map]
  (let [{
      limit "limit"
      after "after"
      before "before"
      status "status"
      sort "sort"
      tags "tags"
      ids "ids"
    } (stringify-keys clojure-map)]
    (java.util.HashMap.
      {
        "limit" (if (nil? limit) nil (Integer. limit))
        "after" after
        "before" before
        "status" status
        "sort" sort
        "tags" (if (nil? tags) nil (into-array String tags))
        "ids" (if (nil? ids) nil (into-array String ids))
      }
    ))))

(defn- clojure-page-to-java
  ([clojure-map]
    (let [{
        cursor "cursor"
        limit "limit"
        after "after"
        before "before"
        status "status"
        sort "sort"
        tags "tags"
        ids "ids"
      } (stringify-keys clojure-map)]
      (java.util.HashMap.
        {
          "cursor" cursor
          "limit" (if (nil? limit) nil (Integer. limit))
          "after" after
          "before" before
          "status" status
          "sort" sort
          "tags" (if (nil? tags) nil (into-array String tags))
          "ids" (if (nil? ids) nil (into-array String ids))
        }
      ))))
      
(defn query
  "Receive a stream of Deposit maps previously created in the Stark Bank API.
  Use this function instead of page if you want to stream the objects without worrying about cursors and pagination.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-3-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-3-10\"
    - `:status` [string, default nil]: filter for status of retrieved maps. ex: \"created\", \"paid\", \"canceled\" or \"overdue\"
    - `:sort` [string, default \"-created\"]: sort order considered in response. Valid options are \"created\" or \"-created\".
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:user` [Project or Organization]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - stream of Deposit maps with updated attributes"
  ([]
    (map java-to-clojure (Deposit/query)))

  ([params]
    (def java-params (clojure-query-to-java params))
    (map java-to-clojure (Deposit/query java-params)))

  ([params, user] 
    (def java-params (clojure-query-to-java params))
    (map java-to-clojure (Deposit/query java-params (#'starkbank.user/get-java-user user)))))

(defn page
  "Receive a list of up to 100 Deposit maps previously created in the Stark Bank API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-3-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-3-10\"
    - `:status` [string, default nil]: filter for status of retrieved maps. ex: \"created\", \"paid\", \"canceled\" or \"overdue\"
    - `:sort` [string, default \"-created\"]: sort order considered in response. Valid options are \"created\" or \"-created\".
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:user` [Project or Organization]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - map with :deposits and :cursor:
      - `:deposits`: list of deposit maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page of deposits"
  ([]
    (def deposit-page (Deposit/page))
    (def cursor (.cursor deposit-page))
    (def deposits (map java-to-clojure (.deposits deposit-page)))
    {:deposits deposits, :cursor cursor})

  ([params]
    (def java-params (clojure-page-to-java params))
    (def deposit-page (Deposit/page java-params))
    {:deposits (map java-to-clojure (.deposits deposit-page)), :cursor (.cursor deposit-page)})

  ([params, user] 
    (def java-params (clojure-page-to-java params))
    (def deposit-page (Deposit/page java-params (#'starkbank.user/get-java-user user)))
    {:deposits (map java-to-clojure (.deposits deposit-page)), :cursor (.cursor deposit-page)}))
    
(defn get
  "Receive a single Deposit map previously created in the Stark Bank API by passing its id

  ## Parameters (required):
    - `:id` [string]: map unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project or Organization]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - Deposit map with updated attributes"
  ([id]
    (java-to-clojure
      (Deposit/get id)))

  ([id, user]
    (java-to-clojure
      (Deposit/get
        id
        (#'starkbank.user/get-java-user user)))))

(ns starkbank.deposit.log
  "Every time a Deposit entity is updated, a corresponding Deposit.Log
  is generated for the entity. This log is never generated by the
  user, but it can be retrieved to check additional information
  on the Deposit.

  ## Attributes:
    - `:id` [string]: unique id returned when the log is created. ex: \"5656565656565656\"
    - `:deposit` [Deposit]: Deposit entity to which the log refers to.
    - `:errors` [list of strings]: list of errors linked to this Deposit event
    - `:type` [string]: type of the Deposit event which triggered the log creation. ex: \"registered\" or \"paid\"
    - `:created` [string]: creation datetime for the log. ex: \"2020-03-26T19:32:35.418698+00:00\""
  (:refer-clojure :exclude [get set])
  (:import [com.starkbank Deposit$Log])
  (:require [starkbank.deposit :as deposit])
  (:use [starkbank.user]
        [clojure.walk]))

(defn- java-to-clojure
  ([java-object]
    {
      :id (.id java-object)
      :created (.created java-object)
      :errors (into [] (.errors java-object))
      :type (.type java-object)
      :deposit (#'deposit/java-to-clojure (.deposit java-object))
    }))

(defn- clojure-query-to-java
  ([clojure-map]
    (let [{
        limit "limit"
        after "after"
        before "before"
        types "types"
        deposit-ids "deposit-ids"
      } (stringify-keys clojure-map)]
      (java.util.HashMap.
        {
          "limit" (if (nil? limit) nil (Integer. limit))
          "after" after
          "before" before
          "types" (if (nil? types) nil (into-array String types))
          "depositIds" (if (nil? deposit-ids) nil (into-array String deposit-ids))
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
        deposit-ids "deposit-ids"
      } (stringify-keys clojure-map)]
      (java.util.HashMap.
        {
          "cursor" cursor
          "limit" (if (nil? limit) nil (Integer. limit))
          "after" after
          "before" before
          "types" (if (nil? types) nil (into-array String types))
          "depositIds" (if (nil? deposit-ids) nil (into-array String deposit-ids))
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
      (Deposit$Log/get id)))

  ([id, user]
    (java-to-clojure
      (Deposit$Log/get
        id
        (#'starkbank.user/get-java-user user)))))

(defn query
  "Receive a stream of Log maps previously created in the Stark Bank API.
  Use this function instead of page if you want to stream the objects without worrying about cursors and pagination.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-3-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-3-10\"
    - `:types` [list of strings, default nil]: filter for log event types. ex: \"paid\" or \"registered\"
    - `:deposit-ids` [list of strings, default nil]: list of Deposit ids to filter logs. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - stream of Log maps with updated attributes"
  ([]
    (map java-to-clojure (Deposit$Log/query)))

  ([params]
    (def java-params (clojure-query-to-java params))
    (map java-to-clojure (Deposit$Log/query java-params)))

  ([params, user] 
    (def java-params (clojure-query-to-java params))
    (map java-to-clojure (Deposit$Log/query java-params (#'starkbank.user/get-java-user user)))))

(defn page
  "Receive a list of up to 100 Deposit.Log maps previously created in the Stark Bank API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-3-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-3-10\"
    - `:types` [list of strings, default nil]: filter for log event types. ex: \"paid\" or \"registered\"
    - `:deposit-ids` [list of strings, default nil]: list of Deposit ids to filter logs. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - map with :deposits and :cursor:
      - `:deposits`: list of deposit maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page of deposits"
  ([]
    (def log-page (Deposit$Log/page))
    (def cursor (.cursor log-page))
    (def logs (map java-to-clojure (.logs log-page)))
    {:logs logs, :cursor cursor})

  ([params]
    (def java-params (clojure-page-to-java params))
    (def log-page (Deposit$Log/page java-params))
    {:logs (map java-to-clojure (.logs log-page)), :cursor (.cursor log-page)})

  ([params, user] 
    (def java-params (clojure-page-to-java params))
    (def log-page (Deposit$Log/page java-params (#'starkbank.user/get-java-user user)))
    {:logs (map java-to-clojure (.logs log-page)), :cursor (.cursor log-page)}))
    