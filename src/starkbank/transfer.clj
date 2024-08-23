(ns starkbank.transfer
  "When you initialize a Transfer, the entity will not be automatically
  created in the Stark Bank API. The 'create' function sends the maps
  to the Stark Bank API and returns the list of created maps.

  ## Parameters (required):
    - `:amount` [integer]: amount in cents to be transferred. ex: 1234 (= R$ 12.34)
    - `:name` [string]: receiver full name. ex: \"Anthony Edward Stark\"
    - `:tax-id` [string]: receiver tax ID (CPF or CNPJ) with or without formatting. ex: \"01234567890\" or \"20.018.183/0001-80\"
    - `:bank-code` [string]: code of the receiver bank institution in Brazil. If an ISPB (8 digits) is informed, a PIX transfer will be created, else a TED will be issued. ex: \"20018183\" or \"341\"
    - `:branch-code` [string]: receiver bank account branch. Use '-' in case there is a verifier digit. ex: \"1357-9\"
    - `:account-number` [string]: receiver bank account number. Use '-' before the verifier digit. ex: \"876543-2\"

  ## Parameters (optional):
    - `:account-type` [string, \"checking\"]: receiver bank account type. This parameter only has effect on Pix Transfers. ex: \"checking\", \"savings\", \"salary\" or \"payment\"
    - `:external-id` [string, default nil]: url safe string that must be unique among all your transfers. Duplicated external-ids will cause failures. By default, this parameter will block any transfer that repeats amount and receiver information on the same date. ex: \"my-internal-id-123456\"
    - `:scheduled` [string, default now]: date or datetime when the transfer will be processed. May be pushed to next business day if necessary. ex: \"2021-03-11T08:00:00.000000+00:00\"
    - `:description` [string, default nil]: optional description to override default description to be shown in the bank statement. ex: \"Payment for service #1234\"
    - `:tags` [list of strings]: list of strings for reference when searching for transfers. ex: [\"employees\", \"monthly\"]

  Attributes (return-only):
    - `:id` [string, default nil]: unique id returned when the transfer is created. ex: \"5656565656565656\"
    - `:fee` [integer, default nil]: fee charged when the transfer is created. ex: 200 (= R$ 2.00)
    - `:status` [string, default nil]: current transfer status. ex: \"processing\" or \"success\"
    - `:transaction-ids` [list of strings, default nil]: ledger transaction ids linked to this transfer (if there are two, second is the chargeback). ex: [\"19827356981273\"]
    - `:created` [string, default nil]: creation datetime for the transfer. ex: \"2020-03-26T19:32:35.418698+00:00\"
    - `:updated` [string, default nil]: latest update datetime for the transfer. ex: \"2020-03-26T19:32:35.418698+00:00\""
  (:refer-clojure :exclude [get set])
  (:require [starkbank.utils.rest :refer [delete-id get-content get-id
                                          get-page get-stream post-multi]]
            [starkbank.settings :refer [credentials]]))

(defn- resource []
  "transfer")

(defn create
  "Send a list of Transfer maps for creation in the Stark Bank API

  ## Parameters (required):
    - `transfers` [list of Transfer maps]: list of Transfer maps to be created in the API

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - list of Transfer maps with updated attributes"
  ([transfers]
    (-> (post-multi @credentials (resource) transfers {})))

  ([transfers, user]
   (-> (post-multi user (resource) transfers {}))))

(defn query
  "Receive a stream of Transfer maps previously created in the Stark Bank API.
  Use this function instead of page if you want to stream the objects without worrying about cursors and pagination.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created or updated only after specified date. ex: ~D[2020-03-25]
    - `:before` [string, default nil]: date filter for maps created or updated only before specified date. ex: ~D[2020-03-25]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved objects. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:transaction-ids` [list of strings, default nil]: list of transaction IDs linked to the desired transfers. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:tax-id` [string, default nil]: filter for transfers sent to the specified tax ID. ex: \"012.345.678-90\"
    - `:status` [string, default nil]: filter for status of retrieved maps. ex: \"processing\" or \"success\"
    - `:sort` [string, default \"-created\"]: sort order considered in response. Valid options are \"created\", \"-created\", \"updated\" or \"-updated\".
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - stream of Transfer maps with updated attributes"
  ([]
    (-> (get-stream @credentials (resource) {})))

  ([params]
    (-> (get-stream @credentials (resource) params)))

  ([params, user] 
    (-> (get-stream user (resource) params))))

(defn page
  "Receive a list of up to 100 Transfer maps previously created in the Stark Bank API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created or updated only after specified date. ex: ~D[2020-03-25]
    - `:before` [string, default nil]: date filter for maps created or updated only before specified date. ex: ~D[2020-03-25]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved objects. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:transaction-ids` [list of strings, default nil]: list of transaction IDs linked to the desired transfers. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:tax-id` [string, default nil]: filter for transfers sent to the specified tax ID. ex: \"012.345.678-90\"
    - `:status` [string, default nil]: filter for status of retrieved maps. ex: \"processing\" or \"success\"
    - `:sort` [string, default \"-created\"]: sort order considered in response. Valid options are \"created\", \"-created\", \"updated\" or \"-updated\".
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - map with :transfers and :cursor:
      - `:transfers`: list of transfer maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page of transfers"
  ([]
   (-> (get-page @credentials (resource) {})))
  
  ([params]
   (-> (get-page @credentials (resource) params)))
  
  ([params, user]
   (-> (get-page user (resource) params))))

(defn get
  "Receive a single Transfer map previously created in the Stark Bank API by passing its id

  ## Parameters (required):
    - `:id` [string]: map unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - Transfer map with updated attributes"
  ([id]
    (-> (get-id @credentials (resource) id {})))

  ([id, user]
    (-> (get-id user (resource) id {}))))

(defn delete
  "Cancel a single scheduled Transfer entity previously created in the Stark Bank API by passing its id

  ## Parameters (required):
    - `:id` [string]: entity unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - canceled Transfer entity with updated attributes"
  ([id]
   (-> (delete-id @credentials (resource) id)))

  ([id, user]
   (-> (delete-id user (resource) id))))

(defn pdf
  "Receive a single Transfer pdf receipt file generated in the Stark Bank API by passing its id.
  Only valid for transfers with \"processing\" or \"success\" status.

  ## Parameters (required):
    - `:id` [string]: map unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - Transfer pdf file content"
  ([id]
    (-> (get-content @credentials (resource) id "pdf" {})))

  ([id, user]
    (-> (get-content user (resource) id "pdf" {}))))
