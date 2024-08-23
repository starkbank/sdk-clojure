(ns starkbank.transaction
  "A Transaction is a transfer of funds between workspaces inside Stark Bank.
  Transactions created by the user are only for internal transactions.
  Other operations (such as transfer or charge-payment) will automatically
  create a transaction for the user which can be retrieved for the statement.
  When you initialize a Transaction, the entity will not be automatically
  created in the Stark Bank API. The 'create' function sends the maps
  to the Stark Bank API and returns the list of created maps.

  ## Parameters (required):
    - `:amount` [integer]: amount in cents to be transferred. ex: 1234 (= R$ 12.34)
    - `:description` [string]: text to be displayed in the receiver and the sender statements (Min. 10 characters). ex: \"funds redistribution\"
    - `:external-id` [string]: unique id, generated by user, to avoid duplicated transactions. ex: \"transaction ABC 2020-03-30\"
    - `:received-id` [string]: unique id of the receiving workspace. ex: \"5656565656565656\"

  ## Parameters (optional):
    - `:tags` [list of strings]: list of strings for reference when searching transactions (may be empty). ex: [\"abc\", \"test\"]

  ## Attributes (return-only):
    - `:id` [string, default nil]: unique id returned when the Transaction is created. ex: \"7656565656565656\"
    - `:sender-id` [string]: unique id of the sending workspace. ex: \"5656565656565656\"
    - `:fee` [integer, default nil]: fee charged when the transaction is created. ex: 200 (= R$ 2.00)
    - `:source` [string, default nil]: locator of the entity that generated the transaction. ex: \"charge/18276318736\" or \"transfer/19381639871263/chargeback\"
    - `:balance` [integer, default nil]: account balance after transaction was processed. ex: 100000000 (= R$ 1,000,000.00)
    - `:created` [string, default nil]: creation datetime for the transaction. ex: \"2020-03-26T19:32:35.418698+00:00\""
  (:refer-clojure :exclude [get set])
  (:require [starkbank.transaction :as transaction]
            [starkbank.utils.rest :refer [get-id get-page get-stream
                                          post-multi]]
            [starkbank.settings :refer [credentials]]))

(defn- resource []
  "transaction")

(defn create
  "Send a list of Transaction entities for creation in the Stark Bank API

  ## Parameters (required):
    - `transactions` [list of Transaction entities]: list of Transaction entities to be created in the API

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - list of Transaction maps with updated attributes"
  ([transactions]
    (-> (post-multi @credentials (resource) transactions {})))

  ([transactions, user] 
    (-> (post-multi user (resource) transactions {}))))

(defn query
  "Receive a stream of Transaction entities previously created in the Stark Bank API.
  Use this function instead of page if you want to stream the objects without worrying about cursors and pagination.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of entities to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for entities created only after specified date. ex: \"2020-3-10\"
    - `:before` [string, default nil]: date filter for entities created only before specified date. ex: \"2020-3-10\"
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved objects. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:external-ids` [list of strings, default nil]: list of external ids to filter retrieved entities. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:tags` [list of strings, default nil]: list of strings for tagging
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - stream of Transaction maps with updated attributes"
  ([]
    (-> (get-stream @credentials (resource) {})))

  ([params]
    (-> (get-stream @credentials (resource) params)))

  ([params, user] 
    (-> (get-stream user (resource) params)))
  )

(defn page
  "Receive a list of up to 100 Transaction maps previously created in the Stark Bank API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default nil]: maximum number of entities to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for entities created only after specified date. ex: \"2020-3-10\"
    - `:before` [string, default nil]: date filter for entities created only before specified date. ex: \"2020-3-10\"
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved objects. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:external-ids` [list of strings, default nil]: list of external ids to filter retrieved entities. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:tags` [list of strings, default nil]: list of strings for tagging
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - map with :transactions and :cursor:
      - `:transactions`: list of transaction maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page of transactions"
  ([]
   (-> (get-page @credentials (resource) {})))
  
  ([params]
   (-> (get-page @credentials (resource) params)))
  
  ([params, user]
   (-> (get-page user (resource) params)))
  )

(defn get
  "Receive a single Transaction entity previously created in the Stark Bank API by passing its id

  ## Parameters (required):
    - `:id` [string]: entity unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - Transaction map with updated attributes"
  ([id]
    (-> (get-id @credentials (resource) id {})))

  ([id, user]
    (-> (get-id user (resource) id {})))
  )
