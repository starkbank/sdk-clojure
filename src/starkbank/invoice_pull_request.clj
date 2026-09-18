(ns starkbank.invoice-pull-request
  "When you initialize an InvoicePullRequest map, the entity will not be automatically
  sent to the Stark Bank API. The 'create' function sends the maps
  to the Stark Bank API and returns the list of created maps.

  ## Parameters (required):
    - `:subscription-id` [string]: unique id of the InvoicePullSubscription related to the request. ex: \"5656565656565656\"
    - `:invoice-id` [string]: id of the invoice previously created to be sent for payment. ex: \"5656565656565656\"
    - `:due` [string]: payment scheduled date in UTC ISO format. ex: \"2023-10-28T17:59:26.249976+00:00\"

  ## Parameters (optional):
    - `:attempt-type` [string, default \"default\"]: attempt type for the payment. Options: \"default\", \"retry\". ex: \"retry\"
    - `:tags` [list of strings, default nil]: list of strings for tagging
    - `:external-id` [string, default nil]: a string that must be unique among all your InvoicePullRequests. Duplicated external-ids will cause failures. ex: \"my-external-id\"
    - `:display-description` [string, default nil]: description to be shown to the payer. ex: \"Payment for services\"

  ## Attributes (return-only):
    - `:id` [string, default nil]: unique id returned when the InvoicePullRequest is created. ex: \"5656565656565656\"
    - `:status` [string, default nil]: current InvoicePullRequest status. ex: \"pending\", \"scheduled\", \"success\", \"failed\" or \"canceled\"
    - `:installment-id` [string, default nil]: unique id of the installment related to this request. ex: \"5656565656565656\"
    - `:created` [string, default nil]: creation datetime for the InvoicePullRequest. ex: \"2020-03-26T19:32:35.418698+00:00\"
    - `:updated` [string, default nil]: latest update datetime for the InvoicePullRequest. ex: \"2020-03-26T19:32:35.418698+00:00\""
  (:refer-clojure :exclude [get])
  (:require [starkbank.utils.rest :refer [delete-id get-id get-page
                                          get-stream post-multi]]
            [starkbank.settings :refer [credentials]]))

(defn- resource []
  "invoice-pull-request")

(defn create
  "Send a list of InvoicePullRequest maps for creation in the Stark Bank API.

  ## Parameters (required):
    - `requests` [list of InvoicePullRequest maps]: list of InvoicePullRequest maps to be created in the API

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - list of InvoicePullRequest maps with updated attributes"
  ([requests]
    (-> (post-multi @credentials (resource) requests {})))

  ([requests, user]
    (-> (post-multi user (resource) requests {}))))

(defn get
  "Receive a single InvoicePullRequest map previously created in the Stark Bank API by passing its id

  ## Parameters (required):
    - `:id` [string]: map unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - InvoicePullRequest map with updated attributes"
  ([id]
    (-> (get-id @credentials (resource) id {})))

  ([id, user]
    (-> (get-id user (resource) id {}))))

(defn query
  "Receive a stream of InvoicePullRequest maps previously created in the Stark Bank API.
  Use this function instead of page if you want to stream the objects without worrying about cursors and pagination.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created or updated only after specified date. ex: \"2020-3-10\"
    - `:before` [string, default nil]: date filter for maps created or updated only before specified date. ex: \"2020-3-10\"
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:invoice-ids` [list of strings, default nil]: list of Invoice ids to filter retrieved maps. ex: [\"12376517623\", \"1928367198236\"]
    - `:subscription-ids` [list of strings, default nil]: list of InvoicePullSubscription ids to filter retrieved maps. ex: [\"12376517623\", \"1928367198236\"]
    - `:external-ids` [list of strings, default nil]: list of external ids to filter retrieved maps. ex: [\"my-external-id-1\", \"my-external-id-2\"]
    - `:status` [string, default nil]: filter for status of retrieved maps. ex: \"success\" or \"failed\"
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - stream of InvoicePullRequest maps with updated attributes"
  ([]
    (-> (get-stream @credentials (resource) {})))

  ([params]
    (-> (get-stream @credentials (resource) params)))

  ([params, user]
    (-> (get-stream user (resource) params))))

(defn page
  "Receive a list of up to 100 InvoicePullRequest maps previously created in the Stark Bank API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created or updated only after specified date. ex: \"2020-3-10\"
    - `:before` [string, default nil]: date filter for maps created or updated only before specified date. ex: \"2020-3-10\"
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:invoice-ids` [list of strings, default nil]: list of Invoice ids to filter retrieved maps. ex: [\"12376517623\", \"1928367198236\"]
    - `:subscription-ids` [list of strings, default nil]: list of InvoicePullSubscription ids to filter retrieved maps. ex: [\"12376517623\", \"1928367198236\"]
    - `:external-ids` [list of strings, default nil]: list of external ids to filter retrieved maps. ex: [\"my-external-id-1\", \"my-external-id-2\"]
    - `:status` [string, default nil]: filter for status of retrieved maps. ex: \"success\" or \"failed\"
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - map with :requests and :cursor:
      - `:requests`: list of InvoicePullRequest maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page of InvoicePullRequest maps"
  ([]
    (-> (get-page @credentials (resource) {})))

  ([params]
    (-> (get-page @credentials (resource) params)))

  ([params, user]
    (-> (get-page user (resource) params))))

(defn cancel
  "Cancel an InvoicePullRequest entity previously created in the Stark Bank API

  ## Parameters (required):
    - `:id` [string]: InvoicePullRequest unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - canceled InvoicePullRequest map"
  ([id]
    (-> (delete-id @credentials (resource) id)))

  ([id, user]
    (-> (delete-id user (resource) id))))
