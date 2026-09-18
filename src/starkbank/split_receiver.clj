(ns starkbank.split-receiver
  "When you initialize a SplitReceiver map, the entity will not be automatically
  sent to the Stark Bank API. The 'create' function sends the maps
  to the Stark Bank API and returns the list of created maps.

  ## Parameters (required):
    - `:name` [string]: receiver full name. ex: \"Anthony Edward Stark\"
    - `:tax-id` [string]: receiver tax ID (CPF or CNPJ) with or without formatting. ex: \"01234567890\" or \"20.018.183/0001-80\"
    - `:bank-code` [string]: code of the receiver bank institution in Brazil. An 8-digit ISPB creates a Pix SplitReceiver; any other value issues a TED. ex: \"20018183\"
    - `:branch-code` [string]: receiver bank account branch. Use '-' in case there is a verifier digit. ex: \"1357-9\"
    - `:account-number` [string]: receiver bank account number. Use '-' before the verifier digit. ex: \"876543-2\"
    - `:account-type` [string]: receiver bank account type. This parameter only has effect on Pix SplitReceivers. ex: \"checking\", \"savings\", \"salary\" or \"payment\"

  ## Parameters (optional):
    - `:tags` [list of strings, default []]: list of strings for tagging. ex: [\"travel\", \"food\"]

  ## Attributes (return-only):
    - `:id` [string]: unique id returned when SplitReceiver is created. ex: \"5656565656565656\"
    - `:status` [string]: current SplitReceiver status. ex: \"created\", \"canceled\" or \"updated\"
    - `:created` [string]: creation datetime for the SplitReceiver. ex: \"2020-03-10 10:30:00.000000+00:00\"
    - `:updated` [string]: update datetime for the SplitReceiver. ex: \"2020-03-10 10:30:00.000000+00:00\""
  (:refer-clojure :exclude [get])
  (:require [starkbank.utils.rest :refer [get-id get-page get-stream post-multi]]
            [starkbank.settings :refer [credentials]]))

(defn- resource []
  "split-receiver")

(defn create
  "Send a list of SplitReceiver maps for creation in the Stark Bank API.

  ## Parameters (required):
    - `receivers` [list of SplitReceiver maps]: list of SplitReceiver maps to be created in the API

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - list of SplitReceiver maps with updated attributes"
  ([receivers]
   (-> (post-multi @credentials (resource) receivers {})))

  ([receivers, user]
   (-> (post-multi user (resource) receivers {}))))

(defn get
  "Receive a single SplitReceiver map previously created in the Stark Bank API by passing its id

  ## Parameters (required):
    - `:id` [string]: map unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - SplitReceiver map with updated attributes"
  ([id]
   (-> (get-id @credentials (resource) id {})))

  ([id, user]
   (-> (get-id user (resource) id {}))))

(defn query
  "Receive a stream of SplitReceiver maps previously created in the Stark Bank API.
  Use this function instead of page if you want to stream the objects without worrying about cursors and pagination.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-3-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-3-10\"
    - `:transaction-ids` [list of strings, default nil]: list of transaction ids linked to the desired SplitReceivers. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:tax-id` [string, default nil]: filter for SplitReceivers sent to the specified tax ID. ex: \"012.345.678-90\"
    - `:sort` [string, default \"-created\"]: sort order considered in response. Valid options are \"created\", \"-created\", \"updated\" or \"-updated\".
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:status` [string, default nil]: filter for status of retrieved maps. ex: \"created\", \"canceled\" or \"updated\"
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - stream of SplitReceiver maps with updated attributes"
  ([]
   (-> (get-stream @credentials (resource) {})))

  ([params]
   (-> (get-stream @credentials (resource) params)))

  ([params, user]
   (-> (get-stream user (resource) params))))

(defn page
  "Receive a list of up to 100 SplitReceiver maps previously created in the Stark Bank API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-3-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-3-10\"
    - `:sort` [string, default \"-created\"]: sort order considered in response. Valid options are \"created\", \"-created\", \"updated\" or \"-updated\".
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:status` [string, default nil]: filter for status of retrieved maps. ex: \"created\", \"canceled\" or \"updated\"
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - map with :receivers and :cursor:
      - `:receivers`: list of SplitReceiver maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page of split receivers"
  ([]
   (-> (get-page @credentials (resource) {})))

  ([params]
   (-> (get-page @credentials (resource) params)))

  ([params, user]
   (-> (get-page user (resource) params))))
