(ns starkbank.dynamic-brcode
  "When you initialize a DynamicBrcode map, the entity will not be automatically
  sent to the Stark Bank API. The 'create' function sends the maps
  to the Stark Bank API and returns the list of created maps.

  DynamicBrcodes are conciliated BR Codes that can be used to receive Pix transactions in a convenient way.
  When a DynamicBrcode is paid, a Deposit is created with the tags parameter containing the character
  \"dynamic-brcode/\" followed by the DynamicBrcode's uuid \"dynamic-brcode/{uuid}\" for conciliation.
  Additionally, all tags passed on the DynamicBrcode will be transferred to the respective Deposit resource.

  ## Parameters (required):
    - `:amount` [integer]: DynamicBrcode value in cents. Minimum = 0 (any value will be accepted). ex: 1234 (= R$ 12.34)

  ## Parameters (optional):
    - `:expiration` [integer, default 3600 (1 hour)]: time interval in seconds between due date and expiration date. ex 123456789
    - `:tags` [list of strings, default nil]: list of strings for tagging. When paid, the resulting Deposit's tags will include these plus \"dynamic-brcode/{uuid}\" for conciliation.
    - `:display-description` [string, default nil]: optional description to be shown in the receiver bank interface. ex: \"Payment for service #1234\"
    - `:rules` [list of maps, default nil]: list of rule maps for modifying DynamicBrcode behavior, each with a :key and :value pair. ex: [{:key \"allowedTaxIds\" :value [\"012.345.678-90\" \"20.018.183/0001-80\"]}]

  ## Attributes (return-only):
    - `:id` [string, default nil]: id returned on creation, this is the BR code. ex: \"00020126360014br.gov.bcb.pix0114+552840092118152040000530398654040.095802BR5915Jamie Lannister6009Sao Paulo620705038566304FC6C\"
    - `:uuid` [string, default nil]: unique uuid returned when the DynamicBrcode is created. ex: \"4e2eab725ddd495f9c98ffd97440702d\"
    - `:picture-url` [string, default nil]: public QR Code (png image) URL. ex: \"https://sandbox.api.starkbank.com/v2/dynamic-brcode/d3ebb1bd92024df1ab6e5a353ee799a4.png\"
    - `:updated` [string, default nil]: update datetime for the DynamicBrcode. ex: \"2020-03-26T19:32:35.418698+00:00\"
    - `:created` [string, default nil]: creation datetime for the DynamicBrcode. ex: \"2020-03-26T19:32:35.418698+00:00\""
  (:refer-clojure :exclude [get])
  (:require [starkbank.utils.rest :refer [get-id get-page get-stream
                                          post-multi]]
            [starkbank.settings :refer [credentials]]))

(defn- resource []
  "dynamic-brcode")

(defn create
  "Send a list of up to 100 DynamicBrcode maps for creation in the Stark Bank API.

  ## Parameters (required):
    - `dynamic-brcodes` [list of DynamicBrcode maps]: list of DynamicBrcode maps to be created in the API

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - list of DynamicBrcode maps with updated attributes"
  ([dynamic-brcodes]
    (-> (post-multi @credentials (resource) dynamic-brcodes {})))

  ([dynamic-brcodes, user]
    (-> (post-multi user (resource) dynamic-brcodes {}))))

(defn get
  "Receive a single DynamicBrcode map previously created in the Stark Bank API by passing its uuid

  ## Parameters (required):
    - `:uuid` [string]: map unique uuid. ex: \"901e71f2447c43c886f58366a5432c4b\"

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - DynamicBrcode map with updated attributes"
  ([uuid]
    (-> (get-id @credentials (resource) uuid {})))

  ([uuid, user]
    (-> (get-id user (resource) uuid {}))))

(defn query
  "Receive a stream of DynamicBrcode maps previously created in the Stark Bank API.
  Use this function instead of page if you want to stream the objects without worrying about cursors and pagination.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-3-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-3-10\"
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:uuids` [list of strings, default nil]: list of uuids to filter retrieved maps. ex: [\"901e71f2447c43c886f58366a5432c4b\", \"4e2eab725ddd495f9c98ffd97440702d\"]
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - stream of DynamicBrcode maps with updated attributes"
  ([]
    (-> (get-stream @credentials (resource) {})))

  ([params]
    (-> (get-stream @credentials (resource) params)))

  ([params, user]
    (-> (get-stream user (resource) params))))

(defn page
  "Receive a list of up to 100 DynamicBrcode maps previously created in the Stark Bank API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-3-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-3-10\"
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:uuids` [list of strings, default nil]: list of uuids to filter retrieved maps. ex: [\"901e71f2447c43c886f58366a5432c4b\", \"4e2eab725ddd495f9c98ffd97440702d\"]
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - map with :dynamic-brcodes and :cursor:
      - `:dynamic-brcodes`: list of DynamicBrcode maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page of DynamicBrcode maps"
  ([]
    (-> (get-page @credentials (resource) {})))

  ([params]
    (-> (get-page @credentials (resource) params)))

  ([params, user]
    (-> (get-page user (resource) params))))
