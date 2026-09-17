(ns starkbank.corporate-withdrawal
  "The CorporateWithdrawal maps created in your Workspace return cash from your Corporate balance to your
  Banking balance.
  When you initialize a CorporateWithdrawal map, the entity will not be automatically
  sent to the Stark Bank API. The 'create' function sends the map
  to the Stark Bank API and returns the created map.

  ## Parameters (required):
    - `:amount` [integer]: CorporateWithdrawal value in cents. Minimum = 0 (any value will be accepted). ex: 1234 (= R$ 12.34)
    - `:external-id` [string]: CorporateWithdrawal external ID. ex: \"12345\"

  ## Parameters (optional):
    - `:tags` [list of strings, default nil]: list of strings for tagging. ex: [\"tony\", \"stark\"]

  ## Attributes (return-only):
    - `:id` [string, default nil]: unique id returned when CorporateWithdrawal is created. ex: \"5656565656565656\"
    - `:transaction-id` [string, default nil]: Stark Bank ledger transaction id linked to this CorporateWithdrawal
    - `:corporate-transaction-id` [string, default nil]: corporate ledger transaction id linked to this CorporateWithdrawal
    - `:updated` [string, default nil]: latest update datetime for the CorporateWithdrawal. ex: \"2020-03-26T19:32:35.418698+00:00\"
    - `:created` [string, default nil]: creation datetime for the CorporateWithdrawal. ex: \"2020-03-26T19:32:35.418698+00:00\""
  (:refer-clojure :exclude [get])
  (:require [starkbank.utils.rest :refer [get-id get-page get-stream post-single]]
            [starkbank.settings :refer [credentials]]))

(defn- resource []
  "corporate-withdrawal")

(defn create
  "Send a single CorporateWithdrawal map for creation in the Stark Bank API.

  ## Parameters (required):
    - `withdrawal` [CorporateWithdrawal map]: CorporateWithdrawal map to be created in the API.

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - CorporateWithdrawal map with updated attributes"
  ([withdrawal]
   (-> (post-single @credentials (resource) withdrawal {})))

  ([withdrawal, user]
   (-> (post-single user (resource) withdrawal {}))))

(defn get
  "Receive a single CorporateWithdrawal map previously created in the Stark Bank API by passing its id

  ## Parameters (required):
    - `:id` [string]: map unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - CorporateWithdrawal map with updated attributes"
  ([id]
   (-> (get-id @credentials (resource) id {})))

  ([id, user]
   (-> (get-id user (resource) id {}))))

(defn query
  "Receive a stream of CorporateWithdrawal maps previously created in the Stark Bank API.
  Use this function instead of page if you want to stream the objects without worrying about cursors and pagination.

  ## Options:
    - `:external-ids` [list of strings, default nil]: external IDs. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-3-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-3-10\"
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - stream of CorporateWithdrawal maps with updated attributes"
  ([]
   (-> (get-stream @credentials (resource) {})))

  ([params]
   (-> (get-stream @credentials (resource) params)))

  ([params, user]
   (-> (get-stream user (resource) params))))

(defn page
  "Receive a list of up to 100 CorporateWithdrawal maps previously created in the Stark Bank API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:external-ids` [list of strings, default nil]: external IDs. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-3-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-3-10\"
    - `:limit` [integer, default 100]: maximum number of maps to be retrieved. Max = 100. ex: 35
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - map with :withdrawals and :cursor:
      - `:withdrawals`: list of CorporateWithdrawal maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page of withdrawals"
  ([]
   (-> (get-page @credentials (resource) {})))

  ([params]
   (-> (get-page @credentials (resource) params)))

  ([params, user]
   (-> (get-page user (resource) params))))
