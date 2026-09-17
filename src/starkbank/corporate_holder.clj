(ns starkbank.corporate-holder
  "The CorporateHolder describes a card holder that may group several cards.
  When you initialize a CorporateHolder map, the entity will not be automatically
  sent to the Stark Bank API. The 'create' function sends the maps
  to the Stark Bank API and returns the list of created maps.

  ## Parameters (required):
    - `:name` [string]: cardholder name. ex: \"Tony Stark\"

  ## Parameters (optional):
    - `:center-id` [string, default nil]: target cost center ID. ex: \"5656565656565656\"
    - `:permissions` [list of maps, default nil]: list of Permission maps representing access granted to an user for a particular cardholder. Each map has `:owner-id` and `:owner-type` keys and, on return, `:owner-email`, `:owner-name`, `:owner-picture-url`, `:owner-status` and `:created` keys.
    - `:rules` [list of maps, default nil]: [EXPANDABLE] list of CorporateRule maps with the holder spending rules. Each rule has `:name`, `:amount`, `:interval`, `:schedule`, `:purposes`, `:currency-code`, `:categories`, `:countries`, `:methods`, `:id`, `:counter-amount`, `:currency-symbol` and `:currency-name` keys.
    - `:tags` [list of strings, default nil]: list of strings for tagging. ex: [\"travel\", \"food\"]

  ## Attributes (return-only):
    - `:id` [string, default nil]: unique id returned when CorporateHolder is created. ex: \"5656565656565656\"
    - `:status` [string, default nil]: current CorporateHolder status. ex: \"active\", \"blocked\" or \"canceled\"
    - `:updated` [string, default nil]: latest update datetime for the CorporateHolder. ex: \"2020-03-26T19:32:35.418698+00:00\"
    - `:created` [string, default nil]: creation datetime for the CorporateHolder. ex: \"2020-03-26T19:32:35.418698+00:00\""
  (:refer-clojure :exclude [get update])
  (:require [starkbank.utils.rest :refer [delete-id get-id get-page get-stream
                                          patch-id post-multi]]
            [starkbank.settings :refer [credentials]]))

(defn- resource []
  "corporate-holder")

(defn create
  "Send a list of CorporateHolder maps for creation in the Stark Bank API.

  ## Parameters (required):
    - `holders` [list of CorporateHolder maps]: list of CorporateHolder maps to be created in the API

  ## Parameters (optional):
    - `:expand` [list of strings, default nil]: fields to expand information. Options: [\"rules\"]
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - list of CorporateHolder maps with updated attributes"
  ([holders]
   (-> (post-multi @credentials (resource) holders {})))

  ([holders params]
   (-> (post-multi @credentials (resource) holders params)))

  ([holders params user]
   (-> (post-multi user (resource) holders params))))

(defn query
  "Receive a stream of CorporateHolder maps previously created in the Stark Bank API.
  Use this function instead of page if you want to stream the objects without worrying about cursors and pagination.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-3-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-3-10\"
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:status` [list of strings, default nil]: filter for status of retrieved maps. ex: [\"active\", \"blocked\", \"canceled\"]
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:expand` [list of strings, default nil]: fields to expand information. Options: [\"rules\"]
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - stream of CorporateHolder maps with updated attributes"
  ([]
   (-> (get-stream @credentials (resource) {})))

  ([params]
   (-> (get-stream @credentials (resource) params)))

  ([params, user]
   (-> (get-stream user (resource) params))))

(defn page
  "Receive a list of up to 100 CorporateHolder maps previously created in the Stark Bank API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-3-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-3-10\"
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:status` [list of strings, default nil]: filter for status of retrieved maps. ex: [\"active\", \"blocked\", \"canceled\"]
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:expand` [list of strings, default nil]: fields to expand information. Options: [\"rules\"]
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - map with :holders and :cursor:
      - `:holders`: list of CorporateHolder maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page of holders"
  ([]
   (-> (get-page @credentials (resource) {})))

  ([params]
   (-> (get-page @credentials (resource) params)))

  ([params, user]
   (-> (get-page user (resource) params))))

(defn get
  "Receive a single CorporateHolder map previously created in the Stark Bank API by passing its id

  ## Parameters (required):
    - `:id` [string]: map unique id. ex: \"5656565656565656\"

  ## Options:
    - `:expand` [list of strings, default nil]: fields to expand information. Options: [\"rules\"]
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - CorporateHolder map with updated attributes"
  ([id]
   (-> (get-id @credentials (resource) id {})))

  ([id params]
   (-> (get-id @credentials (resource) id params)))

  ([id params user]
   (-> (get-id user (resource) id params))))

(defn update
  "Update a CorporateHolder by passing id.

  ## Parameters (required):
    - `:id` [string]: CorporateHolder id. ex: \"5656565656565656\"

  ## Parameters (optional):
    - `:center-id` [string, default nil]: target cost center ID. ex: \"5656565656565656\"
    - `:permissions` [list of maps, default nil]: list of Permission maps representing access granted to an user for a particular cardholder.
    - `:status` [string, default nil]: you may block the CorporateHolder by passing \"blocked\" in the status
    - `:name` [string, default nil]: card holder name.
    - `:rules` [list of maps, default nil]: list of maps with \"amount\": integer, \"currencyCode\": string, \"id\": string, \"interval\": string, \"name\": string pairs
    - `:tags` [list of strings, default nil]: list of strings for tagging
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - target CorporateHolder with updated attributes"
  ([id, params]
   (-> (patch-id @credentials (resource) params id)))

  ([id, params, user]
   (-> (patch-id user (resource) params id))))

(defn cancel
  "Cancel a CorporateHolder entity previously created in the Stark Bank API.

  ## Parameters (required):
    - `:id` [string]: CorporateHolder unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - canceled CorporateHolder map"
  ([id]
   (-> (delete-id @credentials (resource) id)))

  ([id, user]
   (-> (delete-id user (resource) id))))
