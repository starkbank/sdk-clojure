(ns starkbank.merchant-session
  "The MerchantSession resource allows you to create a session prior to a purchase. Sessions are used to guide
  the card holder's verification (3DS) challenge and define the parameters accepted for a purchase, such as
  funding type, expiration, allowed installments, etc. Its uuid must be used by the card holder's client
  application to create a MerchantSession Purchase (via the 'purchase' function). After :expiration seconds,
  the session can no longer be used.

  ## Parameters (required):
    - `:allowed-funding-types` [list of strings]: funding types accepted in this session. ex: [\"credit\", \"debit\"]
    - `:allowed-installments` [list of maps]: allowed amount/installment-count combinations, each with :total-amount and :count keys. ex: [{:total-amount 0 :count 1} {:total-amount 120 :count 2}]
    - `:expiration` [integer]: seconds from creation until the session expires. ex: 3600 (1 hour)

  ## Parameters (optional):
    - `:allowed-ips` [list of strings, default nil]: IPs allowed to create a purchase with this session. ex: [\"191.255.100.100\"]
    - `:challenge-mode` [string, default \"enabled\"]: whether 3DS holder verification is required. ex: \"enabled\" or \"disabled\"
    - `:tags` [list of strings, default nil]: list of strings for tagging. ex: [\"travel\", \"food\"]

  ## Attributes (return-only):
    - `:id` [string, default nil]: unique id returned when the MerchantSession is created. ex: \"5656565656565656\"
    - `:uuid` [string, default nil]: unique uuid returned when the MerchantSession is created, used by the client-side SDK to reference this session. ex: \"0bb894a2697d41d99fe02cad2c00c9bc\"
    - `:holder-id` [string, default nil]: id of the MerchantCard's holder linked to an approved purchase in this session. ex: \"5656565656565656\"
    - `:soft-descriptor` [string, default nil]: text that will be shown in the card holder's bank statement. ex: \"starkbank\"
    - `:status` [string, default nil]: current MerchantSession status. ex: \"active\", \"expired\" or \"success\"
    - `:created` [string, default nil]: creation datetime for the MerchantSession. ex: \"2020-03-10T10:30:00.000000+00:00\"
    - `:updated` [string, default nil]: latest update datetime for the MerchantSession. ex: \"2020-03-10T10:30:00.000000+00:00\""
  (:refer-clojure :exclude [get])
  (:require [starkbank.utils.rest :refer [get-id get-page get-stream
                                          post-single post-sub-resource]]
            [starkbank.settings :refer [credentials]]))

(defn- resource []
  "merchant-session")

(defn- purchase-resource []
  "purchase")

(defn create
  "Create a session the card holder's application can use to create a new MerchantPurchase (via the 'purchase'
  function). The session's uuid must be used for that call; after :expiration seconds, it can no longer be used.

  ## Parameters (required):
    - `session` [MerchantSession map]: MerchantSession map to be created in the API

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - MerchantSession map with updated attributes (including uuid)"
  ([session]
   (-> (post-single @credentials (resource) session {})))

  ([session, user]
   (-> (post-single user (resource) session {}))))

(defn get
  "Receive a single MerchantSession map previously created in the Stark Bank API by passing its id

  ## Parameters (required):
    - `:id` [string]: map unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - MerchantSession map with updated attributes"
  ([id]
   (-> (get-id @credentials (resource) id {})))

  ([id, user]
   (-> (get-id user (resource) id {}))))

(defn query
  "Receive a stream of MerchantSession maps previously created in the Stark Bank API.
  Use this function instead of page if you want to stream the objects without worrying about cursors and pagination.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-3-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-3-10\"
    - `:status` [string, default nil]: filter for status of retrieved maps. ex: \"active\", \"expired\" or \"success\"
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:holder-id` [string, default nil]: filter for sessions linked to a specific MerchantCard's holder id. ex: \"5656565656565656\"
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - stream of MerchantSession maps with updated attributes"
  ([]
   (-> (get-stream @credentials (resource) {})))

  ([params]
   (-> (get-stream @credentials (resource) params)))

  ([params, user]
   (-> (get-stream user (resource) params))))

(defn page
  "Receive a list of up to 100 MerchantSession maps previously created in the Stark Bank API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-3-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-3-10\"
    - `:status` [string, default nil]: filter for status of retrieved maps. ex: \"active\", \"expired\" or \"success\"
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:holder-id` [string, default nil]: filter for sessions linked to a specific MerchantCard's holder id. ex: \"5656565656565656\"
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - map with :sessions and :cursor:
      - `:sessions`: list of MerchantSession maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page of sessions"
  ([]
   (-> (get-page @credentials (resource) {})))

  ([params]
   (-> (get-page @credentials (resource) params)))

  ([params, user]
   (-> (get-page user (resource) params))))

(defn purchase
  "Create a MerchantPurchase directly from the card holder's client application using a MerchantSession uuid
  previously created by the merchant.
  **Note**: This function must be called from your front-end to ensure that sensitive card data does not pass
  through the back-end of your integration.

  ## Parameters (required):
    - `uuid` [string]: MerchantSession uuid. ex: \"0bb894a2697d41d99fe02cad2c00c9bc\"
    - `purchase-params` [map]: map with the purchase data, described below

  ## purchase-params (required):
    - `:amount` [integer]: amount in cents. ex: 100 (= R$ 1.00)
    - `:card-expiration` [string]: card expiration date. ex: \"2035-01\"
    - `:card-number` [string]: card number. ex: \"5448280000000007\"
    - `:card-security-code` [string]: card security code. ex: \"123\"
    - `:holder-name` [string]: card holder's name. ex: \"Holder Name\"
    - `:funding-type` [string]: funding type. ex: \"credit\" or \"debit\"

  ## purchase-params (conditionally required):
    - `:billing-city`, `:billing-country-code`, `:billing-state-code`, `:billing-street-line-1`, `:billing-street-line-2`, `:billing-zip-code`, `:holder-email`, `:holder-phone` [string]: required when the session's :challenge-mode is \"enabled\", optional otherwise.
    - `:metadata` [map]: must include :user-agent, :timezone-offset, :user-ip and :language when 3DS is enabled.

  ## purchase-params (optional):
    - `:installment-count` [integer, default 1]: number of purchase installments. ex: 12
    - `:card-id` [string, default nil]: id of a previously saved MerchantCard, used instead of raw card data. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - MerchantPurchase map with updated attributes"
  ([uuid, purchase-params]
   (-> (post-sub-resource @credentials (resource) uuid (purchase-resource) purchase-params {})))

  ([uuid, purchase-params, user]
   (-> (post-sub-resource user (resource) uuid (purchase-resource) purchase-params {}))))
