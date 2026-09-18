(ns starkbank.verified-account
  "When you initialize a VerifiedAccount map, the entity will not be automatically
  created in the Stark Bank API. The 'create' function sends the maps
  to the Stark Bank API and returns the list of created maps.

  ## Parameters (required):
    - `:tax-id` [string]: receiver tax ID (CPF or CNPJ) with or without formatting. ex: \"01234567890\" or \"20.018.183/0001-80\"

  ## Parameters (conditionally required):
    - `:bank-code` [string]: code of the receiver bank institution in Brazil. If an ISPB (8 digits) is informed, a Pix transfer will be created, else a TED will be issued. The bank-code parameter is required if verifying with bank details. ex: \"20018183\" or \"341\"
    - `:branch-code` [string]: receiver bank account branch. Use '-' in case there is a verifier digit. ex: \"1357-9\". The branch-code parameter is required if verifying with bank details.
    - `:key-id` [string]: pix key identifier. ex: \"tony@starkbank.com\", \"012.345.678-90\". The key-id parameter is required if verifying with Pix key.
    - `:name` [string]: receiver full name. ex: \"Anthony Edward Stark\". The name parameter is required if verifying with bank details.
    - `:number` [string]: receiver bank account number. Use '-' before the verifier digit. ex: \"876543-2\". The number parameter is required if verifying with bank details.
    - `:type` [string]: verified account type. ex: \"checking\", \"savings\", \"salary\" or \"payment\". The type parameter is required if verifying with bank details.

  ## Parameters (optional):
    - `:tags` [list of strings, default nil]: list of strings for reference when searching for verified accounts. ex: [\"employees\" \"monthly\"]

  ## Attributes (return-only):
    - `:id` [string, default nil]: unique id returned when the VerifiedAccount is created. ex: \"5656565656565656\"
    - `:bank-name` [string, default nil]: bank name associated with the verified account. ex: \"Stark Bank\"
    - `:status` [string, default nil]: current verified account status. ex: \"creating\", \"created\", \"processing\", \"active\", \"failed\" or \"canceled\"
    - `:created` [string, default nil]: creation datetime for the verified account. ex: \"2020-03-26T19:32:35.418698+00:00\"
    - `:updated` [string, default nil]: latest update datetime for the verified account. ex: \"2020-03-26T19:32:35.418698+00:00\""
  (:refer-clojure :exclude [get])
  (:require [starkbank.utils.rest :refer [delete-id get-id get-page
                                          get-stream post-multi]]
            [starkbank.settings :refer [credentials]]))

(defn- resource []
  "verified-account")

(defn create
  "Send a list of VerifiedAccount maps for creation in the Stark Bank API.

  ## Parameters (required):
    - `verified-accounts` [list of VerifiedAccount maps]: list of VerifiedAccount maps to be created in the API

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - list of VerifiedAccount maps with updated attributes"
  ([verified-accounts]
    (-> (post-multi @credentials (resource) verified-accounts {})))

  ([verified-accounts, user]
    (-> (post-multi user (resource) verified-accounts {}))))

(defn get
  "Receive a single VerifiedAccount map previously created in the Stark Bank API by passing its id

  ## Parameters (required):
    - `:id` [string]: map unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - VerifiedAccount map with updated attributes"
  ([id]
    (-> (get-id @credentials (resource) id {})))

  ([id, user]
    (-> (get-id user (resource) id {}))))

(defn cancel
  "Cancel a VerifiedAccount entity previously created in the Stark Bank API

  ## Parameters (required):
    - `:id` [string]: VerifiedAccount unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - canceled VerifiedAccount map"
  ([id]
    (-> (delete-id @credentials (resource) id)))

  ([id, user]
    (-> (delete-id user (resource) id))))

(defn query
  "Receive a stream of VerifiedAccount maps previously created in the Stark Bank API.
  Use this function instead of page if you want to stream the objects without worrying about cursors and pagination.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created or updated only after specified date. ex: \"2020-3-10\"
    - `:before` [string, default nil]: date filter for maps created or updated only before specified date. ex: \"2020-3-10\"
    - `:status` [string, default nil]: filter for status of retrieved maps. ex: \"creating\", \"created\", \"processing\", \"active\", \"failed\" or \"canceled\"
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - stream of VerifiedAccount maps with updated attributes"
  ([]
    (-> (get-stream @credentials (resource) {})))

  ([params]
    (-> (get-stream @credentials (resource) params)))

  ([params, user]
    (-> (get-stream user (resource) params))))

(defn page
  "Receive a list of up to 100 VerifiedAccount maps previously created in the Stark Bank API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created or updated only after specified date. ex: \"2020-3-10\"
    - `:before` [string, default nil]: date filter for maps created or updated only before specified date. ex: \"2020-3-10\"
    - `:status` [string, default nil]: filter for status of retrieved maps. ex: \"creating\", \"created\", \"processing\", \"active\", \"failed\" or \"canceled\"
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - map with :verified-accounts and :cursor:
      - `:verified-accounts`: list of VerifiedAccount maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page of VerifiedAccount maps"
  ([]
    (-> (get-page @credentials (resource) {})))

  ([params]
    (-> (get-page @credentials (resource) params)))

  ([params, user]
    (-> (get-page user (resource) params))))
