(ns starkbank.corporate-card
  "The CorporateCard map displays the information of the cards created in your Workspace.
  Sensitive information will only be returned when the :expand option is used, to avoid security concerns.
  When you initialize a CorporateCard, the entity will not be automatically
  sent to the Stark Bank API. The 'create' function sends the map
  to the Stark Bank API and returns the created map.
  If the CorporateCard was not used in the last purchase, this resource will return it.

  ## Parameters (required):
    - `:holder-id` [string]: card holder unique id. ex: \"5656565656565656\"

  ## Attributes (return-only):
    - `:id` [string, default nil]: unique id returned when CorporateCard is created. ex: \"5656565656565656\"
    - `:holder-name` [string, default nil]: card holder name. ex: \"Tony Stark\"
    - `:display-name` [string, default nil]: card displayed name. ex: \"ANTHONY STARK\"
    - `:rules` [list of maps, default nil]: [EXPANDABLE] list of CorporateRule maps with spending rules applied to the card. Each rule has `:name`, `:amount`, `:interval`, `:schedule`, `:purposes`, `:currency-code`, `:categories`, `:countries`, `:methods`, `:id`, `:counter-amount`, `:currency-symbol` and `:currency-name` keys.
    - `:tags` [list of strings, default nil]: list of strings for tagging. ex: [\"travel\", \"food\"]
    - `:street-line-1` [string, default sub-issuer street line 1]: card holder main address. ex: \"Av. Paulista, 200\"
    - `:street-line-2` [string, default sub-issuer street line 2]: card holder address complement. ex: \"Apto. 123\"
    - `:district` [string, default sub-issuer district]: card holder address district/neighbourhood. ex: \"Bela Vista\"
    - `:city` [string, default sub-issuer city]: card holder address city. ex: \"Rio de Janeiro\"
    - `:state-code` [string, default sub-issuer state code]: card holder address state. ex: \"GO\"
    - `:zip-code` [string, default sub-issuer zip code]: card holder address zip code. ex: \"01311-200\"
    - `:type` [string, default nil]: card type. ex: \"virtual\"
    - `:status` [string, default nil]: current CorporateCard status. ex: \"active\", \"blocked\", \"canceled\" or \"expired\"
    - `:number` [string, default nil]: [EXPANDABLE] masked card number. Expand to unmask the value. ex: \"123\"
    - `:security-code` [string, default nil]: [EXPANDABLE] masked card verification value (cvv). Expand to unmask the value. ex: \"123\"
    - `:expiration` [string, default nil]: [EXPANDABLE] masked card expiration datetime. Expand to unmask the value. ex: \"2020-03-10T10:30:00.000000+00:00\"
    - `:updated` [string, default nil]: latest update datetime for the CorporateCard. ex: \"2020-03-10T10:30:00.000000+00:00\"
    - `:created` [string, default nil]: creation datetime for the CorporateCard. ex: \"2020-03-10T10:30:00.000000+00:00\""
  (:refer-clojure :exclude [get update])
  (:require [starkbank.utils.rest :refer [delete-id get-id get-page get-stream
                                          patch-id post-raw]]
            [starkbank.settings :refer [credentials]]))

(defn- resource []
  "corporate-card")

(defn create
  "Send a CorporateCard map for creation in the Stark Bank API.

  ## Parameters (required):
    - `card` [CorporateCard map]: CorporateCard map to be created in the API.

  ## Parameters (optional):
    - `:expand` [list of strings, default nil]: fields to expand information. ex: [\"rules\", \"securityCode\", \"number\", \"expiration\"]
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - CorporateCard map with updated attributes"
  ([card]
   (-> (post-raw @credentials (str (resource) "/token") card {} nil true)
       (:content)
       (:card)))

  ([card params]
   (-> (post-raw @credentials (str (resource) "/token") card params nil true)
       (:content)
       (:card)))

  ([card params user]
   (-> (post-raw user (str (resource) "/token") card params nil true)
       (:content)
       (:card))))

(defn query
  "Receive a stream of CorporateCard maps previously created in the Stark Bank API.
  Use this function instead of page if you want to stream the objects without worrying about cursors and pagination.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-3-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-3-10\"
    - `:status` [list of strings, default nil]: filter for status of retrieved maps. ex: [\"active\", \"blocked\", \"canceled\", \"expired\"]
    - `:types` [list of strings, default nil]: card type. ex: [\"virtual\"]
    - `:holder-ids` [list of strings, default nil]: card holder IDs. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:expand` [list of strings, default nil]: fields to expand information. ex: [\"rules\", \"securityCode\", \"number\", \"expiration\"]
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - stream of CorporateCard maps with updated attributes"
  ([]
   (-> (get-stream @credentials (resource) {})))

  ([params]
   (-> (get-stream @credentials (resource) params)))

  ([params, user]
   (-> (get-stream user (resource) params))))

(defn page
  "Receive a list of up to 100 CorporateCard maps previously created in the Stark Bank API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-3-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-3-10\"
    - `:status` [list of strings, default nil]: filter for status of retrieved maps. ex: [\"active\", \"blocked\", \"canceled\", \"expired\"]
    - `:types` [list of strings, default nil]: card type. ex: [\"virtual\"]
    - `:holder-ids` [list of strings, default nil]: card holder IDs. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:expand` [list of strings, default nil]: fields to expand information. ex: [\"rules\", \"securityCode\", \"number\", \"expiration\"]
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - map with :cards and :cursor:
      - `:cards`: list of CorporateCard maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page of cards"
  ([]
   (-> (get-page @credentials (resource) {})))

  ([params]
   (-> (get-page @credentials (resource) params)))

  ([params, user]
   (-> (get-page user (resource) params))))

(defn get
  "Receive a single CorporateCard map previously created in the Stark Bank API by passing its id

  ## Parameters (required):
    - `:id` [string]: map unique id. ex: \"5656565656565656\"

  ## Options:
    - `:expand` [list of strings, default nil]: fields to expand information. ex: [\"rules\", \"securityCode\", \"number\", \"expiration\"]
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - CorporateCard map with updated attributes"
  ([id]
   (-> (get-id @credentials (resource) id {})))

  ([id params]
   (-> (get-id @credentials (resource) id params)))

  ([id params user]
   (-> (get-id user (resource) id params))))

(defn update
  "Update a CorporateCard by passing id.

  ## Parameters (required):
    - `:id` [string]: CorporateCard id. ex: \"5656565656565656\"

  ## Parameters (optional):
    - `:status` [string, default nil]: you may block the CorporateCard by passing \"blocked\" or activate by passing \"active\" in the status
    - `:display-name` [string, default nil]: card displayed name. ex: \"ANTHONY EDWARD\"
    - `:pin` [string, default nil]: you may unlock your physical card by passing its PIN. This is also the PIN you use to authorize a purchase.
    - `:rules` [list of maps, default nil]: list of maps with \"amount\": integer, \"currencyCode\": string, \"id\": string, \"interval\": string, \"name\": string pairs.
    - `:tags` [list of strings, default nil]: list of strings for tagging. ex: [\"tony\", \"stark\"]
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - target CorporateCard with updated attributes"
  ([id, params]
   (-> (patch-id @credentials (resource) params id)))

  ([id, params, user]
   (-> (patch-id user (resource) params id))))

(defn cancel
  "Cancel a CorporateCard entity previously created in the Stark Bank API.

  ## Parameters (required):
    - `:id` [string]: CorporateCard unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - canceled CorporateCard map"
  ([id]
   (-> (delete-id @credentials (resource) id)))

  ([id, user]
   (-> (delete-id user (resource) id))))
