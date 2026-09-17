(ns starkbank.corporate-purchase
  "Displays the CorporatePurchase maps created in your Workspace.

  ## Attributes (return-only):
    - `:id` [string]: unique id returned when CorporatePurchase is created. ex: \"5656565656565656\"
    - `:holder-id` [string]: card holder unique id. ex: \"5656565656565656\"
    - `:holder-name` [string]: card holder name. ex: \"Tony Stark\"
    - `:center-id` [string]: target cost center ID. ex: \"5656565656565656\"
    - `:card-id` [string]: unique id returned when CorporateCard is created. ex: \"5656565656565656\"
    - `:card-ending` [string]: last 4 digits of the card number. ex: \"1234\"
    - `:description` [string]: purchase descriptions. ex: \"myDescription\"
    - `:amount` [integer]: CorporatePurchase value in cents. Minimum = 0. ex: 1234 (= R$ 12.34)
    - `:tax` [integer]: IOF amount taxed for international purchases. ex: 1234 (= R$ 12.34)
    - `:issuer-amount` [integer]: issuer amount. ex: 1234 (= R$ 12.34)
    - `:issuer-currency-code` [string]: issuer currency code. ex: \"USD\"
    - `:issuer-currency-symbol` [string]: issuer currency symbol. ex: \"$\"
    - `:merchant-amount` [integer]: merchant amount. ex: 1234 (= R$ 12.34)
    - `:merchant-currency-code` [string]: merchant currency code. ex: \"USD\"
    - `:merchant-currency-symbol` [string]: merchant currency symbol. ex: \"$\"
    - `:merchant-category-code` [string]: merchant category code. ex: \"fastFoodRestaurants\"
    - `:merchant-category-type` [string]: merchant category type. ex: \"health\"
    - `:merchant-country-code` [string]: merchant country code. ex: \"USA\"
    - `:merchant-name` [string]: merchant name. ex: \"Google Cloud Platform\"
    - `:merchant-display-name` [string]: merchant name. ex: \"Google Cloud Platform\"
    - `:merchant-display-url` [string]: public merchant icon (png image). ex: \"https://sandbox.api.starkbank.com/v2/corporate-icon/merchant/ifood.png\"
    - `:merchant-fee` [integer]: fee charged by the merchant to cover specific costs, such as ATM withdrawal logistics, etc. ex: 200 (= R$ 2.00)
    - `:method-code` [string]: method code. ex: \"chip\", \"token\", \"server\", \"manual\", \"magstripe\" or \"contactless\"
    - `:tags` [list of strings]: list of strings for tagging returned by the sub-issuer during the authorization. ex: [\"travel\", \"food\"]
    - `:corporate-transaction-ids` [list of strings]: ledger transaction ids linked to this Purchase
    - `:status` [string]: current CorporatePurchase status. ex: \"approved\", \"canceled\", \"denied\", \"confirmed\" or \"voided\"
    - `:updated` [string]: latest update datetime for the CorporatePurchase. ex: \"2020-03-26T19:32:35.418698+00:00\"
    - `:created` [string]: creation datetime for the CorporatePurchase. ex: \"2020-03-26T19:32:35.418698+00:00\""
  (:refer-clojure :exclude [get])
  (:require [starkbank.utils.rest :refer [get-id get-page get-public-key
                                          get-stream]]
            [starkbank.utils.parse :refer [json-to-map transform-keys-to-kebab]]
            [starkbank.settings :refer [credentials]]
            [core-clojure.utils.case :refer [transform-keys-camel]]
            [cheshire.core :as cheshire])
  (:import [com.starkbank.ellipticcurve Ecdsa PublicKey Signature]
           [com.starkbank.ellipticcurve.utils ByteString]))

(defn- resource []
  "corporate-purchase")

(defn get
  "Receive a single CorporatePurchase map previously created in the Stark Bank API by passing its id

  ## Parameters (required):
    - `:id` [string]: map unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - CorporatePurchase map with updated attributes"
  ([id]
   (-> (get-id @credentials (resource) id {})))

  ([id, user]
   (-> (get-id user (resource) id {}))))

(defn query
  "Receive a stream of CorporatePurchase maps previously created in the Stark Bank API.
  Use this function instead of page if you want to stream the objects without worrying about cursors and pagination.

  ## Options:
    - `:ids` [list of strings, default nil]: purchase IDs
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-3-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-3-10\"
    - `:merchant-category-types` [list of strings, default nil]: merchant category type. ex: [\"health\"]
    - `:holder-ids` [list of strings, default nil]: card holder IDs. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:card-ids` [list of strings, default nil]: card IDs. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:status` [list of strings, default nil]: filter for status of retrieved maps. ex: [\"approved\", \"canceled\", \"denied\", \"confirmed\", \"voided\"]
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - stream of CorporatePurchase maps with updated attributes"
  ([]
   (-> (get-stream @credentials (resource) {})))

  ([params]
   (-> (get-stream @credentials (resource) params)))

  ([params, user]
   (-> (get-stream user (resource) params))))

(defn page
  "Receive a list of up to 100 CorporatePurchase maps previously created in the Stark Bank API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-3-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-3-10\"
    - `:merchant-category-types` [list of strings, default nil]: merchant category type. ex: [\"health\"]
    - `:holder-ids` [list of strings, default nil]: card holder IDs. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:card-ids` [list of strings, default nil]: card IDs. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:status` [list of strings, default nil]: filter for status of retrieved maps. ex: [\"approved\", \"canceled\", \"denied\", \"confirmed\", \"voided\"]
    - `:ids` [list of strings, default nil]: purchase IDs
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - map with :purchases and :cursor:
      - `:purchases`: list of CorporatePurchase maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page of purchases"
  ([]
   (-> (get-page @credentials (resource) {})))

  ([params]
   (-> (get-page @credentials (resource) params)))

  ([params, user]
   (-> (get-page user (resource) params))))

(defn parse
  "Create a single verified CorporatePurchase authorization request map from a content string.
  Use this function to parse and verify the authenticity of the authorization request received at the informed endpoint.
  Authorization requests are posted to your registered endpoint whenever CorporatePurchases are received.
  They present CorporatePurchase data that must be analyzed and answered with approval or declination.
  If the provided digital signature does not check out with the Stark Bank public key, an exception will be raised.
  If the authorization request is not answered within 2 seconds or is not answered with an HTTP status code 200 the
  CorporatePurchase will go through the pre-configured stand-in validation.

  ## Parameters (required):
    - `content` [string]: response content from request received at user endpoint (not parsed)
    - `signature` [string]: base-64 digital signature received at response header \"Digital-Signature\"

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - parsed CorporatePurchase map"
  ([content, signature]
   (-> (parse content signature @credentials)))

  ([content, signature, user]
   (let [public-key (PublicKey/fromPem (get-public-key user))
         stark-signature (Signature/fromBase64 (ByteString. (.getBytes signature)))]
     (if (Ecdsa/verify content stark-signature public-key)
       (transform-keys-to-kebab (json-to-map content))
       (throw (IllegalArgumentException. "Provided signature and content do not match Stark Bank public key"))))))

(defn response
  "Helps you respond CorporatePurchase requests.

  ## Parameters (required):
    - `status` [string]: sub-issuer response to the authorization. ex: \"approved\" or \"denied\"

  ## Parameters (conditionally required):
    - `:reason` [string, default nil]: denial reason. Options: \"other\", \"blocked\", \"lostCard\", \"stolenCard\", \"invalidPin\", \"invalidCard\", \"cardExpired\", \"issuerError\", \"concurrency\", \"standInDenial\", \"subIssuerError\", \"invalidPurpose\", \"invalidZipCode\", \"invalidWalletId\", \"inconsistentCard\", \"settlementFailed\", \"cardRuleMismatch\", \"invalidExpiration\", \"prepaidInstallment\", \"holderRuleMismatch\", \"insufficientBalance\", \"tooManyTransactions\", \"invalidSecurityCode\", \"invalidPaymentMethod\", \"confirmationDeadline\", \"withdrawalAmountLimit\", \"insufficientCardLimit\" or \"insufficientHolderLimit\"

  ## Parameters (optional):
    - `:amount` [integer, default nil]: amount in cents that was authorized. ex: 1234 (= R$ 12.34)
    - `:tags` [list of strings, default nil]: tags to filter retrieved map. ex: [\"tony\", \"stark\"]

  ## Return:
    - dumped JSON string that must be returned to us on the CorporatePurchase request"
  ([status]
   (response status {}))

  ([status, {:keys [amount reason tags]}]
   (->> {:authorization (into {} (remove (comp nil? val) {:status status :amount amount :reason reason :tags tags}))}
        (transform-keys-camel)
        (cheshire/generate-string))))
