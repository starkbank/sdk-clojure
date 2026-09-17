(ns starkbank.corporate-transaction
  "The CorporateTransaction map is created in your Workspace to represent each balance shift.

  ## Attributes (return-only):
    - `:id` [string]: unique id returned when CorporateTransaction is created. ex: \"5656565656565656\"
    - `:amount` [integer]: CorporateTransaction value in cents. ex: 1234 (= R$ 12.34)
    - `:balance` [integer]: balance amount of the Workspace at the instant of the Transaction in cents. ex: 200 (= R$ 2.00)
    - `:description` [string]: CorporateTransaction description. ex: \"Buying food\"
    - `:source` [string]: source of the transaction. ex: \"corporate-purchase/5656565656565656\"
    - `:tags` [list of strings]: list of strings inherited from the source resource. ex: [\"tony\", \"stark\"]
    - `:created` [string]: creation datetime for the CorporateTransaction. ex: \"2020-03-26T19:32:35.418698+00:00\""
  (:refer-clojure :exclude [get])
  (:require [starkbank.utils.rest :refer [get-id get-page get-stream]]
            [starkbank.settings :refer [credentials]]))

(defn- resource []
  "corporate-transaction")

(defn get
  "Receive a single CorporateTransaction map previously created in the Stark Bank API by passing its id

  ## Parameters (required):
    - `:id` [string]: map unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - CorporateTransaction map with updated attributes"
  ([id]
   (-> (get-id @credentials (resource) id {})))

  ([id, user]
   (-> (get-id user (resource) id {}))))

(defn query
  "Receive a stream of CorporateTransaction maps previously created in the Stark Bank API.
  Use this function instead of page if you want to stream the objects without worrying about cursors and pagination.

  ## Options:
    - `:source` [string, default nil]: source of the transaction. ex: \"corporate-purchase/5656565656565656\"
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:external-ids` [list of strings, default nil]: external IDs. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-3-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-3-10\"
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - stream of CorporateTransaction maps with updated attributes"
  ([]
   (-> (get-stream @credentials (resource) {})))

  ([params]
   (-> (get-stream @credentials (resource) params)))

  ([params, user]
   (-> (get-stream user (resource) params))))

(defn page
  "Receive a list of up to 100 CorporateTransaction maps previously created in the Stark Bank API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:source` [string, default nil]: source of the transaction. ex: \"corporate-purchase/5656565656565656\"
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:external-ids` [list of strings, default nil]: external IDs. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-3-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-3-10\"
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:limit` [integer, default 100]: maximum number of maps to be retrieved. Max = 100. ex: 35
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - map with :transactions and :cursor:
      - `:transactions`: list of CorporateTransaction maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page of transactions"
  ([]
   (-> (get-page @credentials (resource) {})))

  ([params]
   (-> (get-page @credentials (resource) params)))

  ([params, user]
   (-> (get-page user (resource) params))))
