(ns starkbank.invoice-pull-subscription
  "When you initialize an InvoicePullSubscription map, the entity will not be automatically
  sent to the Stark Bank API. The 'create' function sends the maps
  to the Stark Bank API and returns the list of created maps.

  ## Parameters (required):
    - `:start` [string]: subscription start date. ex: \"2022-04-01\"
    - `:interval` [string]: subscription installment interval. Options: \"week\", \"month\", \"quarter\", \"semester\", \"year\"
    - `:pull-mode` [string]: subscription pull mode. Options: \"manual\", \"automatic\". Automatic mode will create the InvoicePullRequests automatically
    - `:pull-retry-limit` [integer]: subscription pull retry limit. Options: 0 or 3.
    - `:type` [string]: subscription type. Options: \"push\", \"qrcode\", \"qrcodeAndPayment\", \"paymentAndOrQrcode\"

  ## Parameters (conditionally required):
    - `:amount` [integer, default 0]: subscription amount in cents. Required if an amount-min-limit is not informed. Minimum = 1 (R$ 0.01). ex: 100 (= R$ 1.00)
    - `:amount-min-limit` [integer, default nil]: subscription minimum amount in cents. Required if an amount is not informed. Minimum = 1 (R$ 0.01). ex: 100 (= R$ 1.00)

  ## Parameters (optional):
    - `:display-description` [string, default nil]: Invoice description to be shown to the payer. ex: \"Subscription payment\"
    - `:due` [string, default 2 days after creation]: date by which the payer must approve or deny the subscription, after which it auto-expires if unanswered. Applies to all subscription types (not push-only). ex: \"2022-04-08\"
    - `:external-id` [string, default nil]: string that must be unique among all your subscriptions. Duplicated external-ids will cause failures. ex: \"my-external-id\"
    - `:reference-code` [string, default nil]: reference code for reconciliation. ex: \"REF123456\"
    - `:end` [string, default nil]: subscription end date. ex: \"2023-04-01\"
    - `:data` [map, default nil]: additional data required by type: payer account details for \"push\", immediate-payment parameters for \"qrcodeAndPayment\"/\"paymentAndOrQrcode\"; not required for \"qrcode\"
    - `:name` [string, default nil]: subscription debtor name. ex: \"Iron Bank S.A.\"
    - `:tax-id` [string, default nil]: subscription debtor tax ID (CPF or CNPJ) with or without formatting. ex: \"01234567890\" or \"20.018.183/0001-80\"
    - `:tags` [list of strings, default nil]: list of strings for tagging

  ## Attributes (return-only):
    - `:id` [string, default nil]: unique id returned when the InvoicePullSubscription is created. ex: \"5656565656565656\"
    - `:status` [string, default nil]: current InvoicePullSubscription status. ex: \"active\", \"canceled\", \"created\" or \"expired\"
    - `:bacen-id` [string, default nil]: unique authentication id at the Central Bank. ex: \"RR2001818320250616dtsPkBVaBYs\"
    - `:brcode` [string, default nil]: BR Code string for the InvoicePullSubscription. ex: \"00020101021126580014br.gov.bcb.pix0114+5599999999990210starkbank.com.br520400005303986540410000000000005802BR5913Stark Bank S.A.6009SAO PAULO62070503***6304D2B1\"
    - `:installment-id` [string, default nil]: unique id of the installment related to this subscription. ex: \"5656565656565656\"
    - `:created` [string, default nil]: creation datetime for the InvoicePullSubscription. ex: \"2020-03-26T19:32:35.418698+00:00\"
    - `:updated` [string, default nil]: latest update datetime for the InvoicePullSubscription. ex: \"2020-03-26T19:32:35.418698+00:00\""
  (:refer-clojure :exclude [get])
  (:require [starkbank.utils.rest :refer [delete-id get-id get-page
                                          get-stream post-multi]]
            [starkbank.settings :refer [credentials]]))

(defn- resource []
  "invoice-pull-subscription")

(defn create
  "Send a list of InvoicePullSubscription maps for creation in the Stark Bank API.

  ## Parameters (required):
    - `subscriptions` [list of InvoicePullSubscription maps]: list of InvoicePullSubscription maps to be created in the API

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - list of InvoicePullSubscription maps with updated attributes"
  ([subscriptions]
    (-> (post-multi @credentials (resource) subscriptions {})))

  ([subscriptions, user]
    (-> (post-multi user (resource) subscriptions {}))))

(defn get
  "Receive a single InvoicePullSubscription map previously created in the Stark Bank API by passing its id

  ## Parameters (required):
    - `:id` [string]: map unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - InvoicePullSubscription map with updated attributes"
  ([id]
    (-> (get-id @credentials (resource) id {})))

  ([id, user]
    (-> (get-id user (resource) id {}))))

(defn query
  "Receive a stream of InvoicePullSubscription maps previously created in the Stark Bank API.
  Use this function instead of page if you want to stream the objects without worrying about cursors and pagination.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created or updated only after specified date. ex: \"2020-3-10\"
    - `:before` [string, default nil]: date filter for maps created or updated only before specified date. ex: \"2020-3-10\"
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:status` [string, default nil]: filter for status of retrieved maps. ex: \"active\", \"canceled\", \"created\" or \"expired\"
    - `:expand` [list of strings, default nil]: fields to expand information. ex: [\"data\"]
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - stream of InvoicePullSubscription maps with updated attributes"
  ([]
    (-> (get-stream @credentials (resource) {})))

  ([params]
    (-> (get-stream @credentials (resource) params)))

  ([params, user]
    (-> (get-stream user (resource) params))))

(defn page
  "Receive a list of up to 100 InvoicePullSubscription maps previously created in the Stark Bank API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created or updated only after specified date. ex: \"2020-3-10\"
    - `:before` [string, default nil]: date filter for maps created or updated only before specified date. ex: \"2020-3-10\"
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:status` [string, default nil]: filter for status of retrieved maps. ex: \"active\", \"canceled\", \"created\" or \"expired\"
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - map with :subscriptions and :cursor:
      - `:subscriptions`: list of InvoicePullSubscription maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page of InvoicePullSubscription maps"
  ([]
    (-> (get-page @credentials (resource) {})))

  ([params]
    (-> (get-page @credentials (resource) params)))

  ([params, user]
    (-> (get-page user (resource) params))))

(defn cancel
  "Cancel an InvoicePullSubscription entity previously created in the Stark Bank API. The subscription must currently have \"active\" status to be canceled.

  ## Parameters (required):
    - `:id` [string]: InvoicePullSubscription unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - canceled InvoicePullSubscription map"
  ([id]
    (-> (delete-id @credentials (resource) id)))

  ([id, user]
    (-> (delete-id user (resource) id))))
