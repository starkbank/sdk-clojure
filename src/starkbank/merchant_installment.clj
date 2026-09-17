(ns starkbank.merchant-installment
  "MerchantInstallment maps are created for every installment in a MerchantPurchase; they track their own due
  date and settlement lifecycle.

  ## Attributes (return-only):
    - `:id` [string, default nil]: unique id returned when the MerchantInstallment is created. ex: \"5656565656565656\"
    - `:amount` [integer, default nil]: installment amount in cents. ex: 1234 (= R$ 12.34)
    - `:fee` [integer, default nil]: fee charged in cents. ex: 200 (= R$ 2.00)
    - `:funding-type` [string, default nil]: funding type. ex: \"credit\" or \"debit\"
    - `:network` [string, default nil]: card network. ex: \"visa\"
    - `:purchase-id` [string, default nil]: id of the MerchantPurchase linked to the installment. ex: \"5656565656565656\"
    - `:status` [string, default nil]: current MerchantInstallment status. ex: \"created\", \"paid\", \"canceled\" or \"voided\"
    - `:transaction-ids` [list of strings, default nil]: ledger transaction ids linked to this installment. ex: [\"19827356981273\"]
    - `:tags` [list of strings, default nil]: list of strings for tagging. ex: [\"travel\", \"food\"]
    - `:due` [string, default nil]: expected settlement date for the installment. ex: \"2020-03-10T10:30:00.000000+00:00\"
    - `:created` [string, default nil]: creation datetime for the MerchantInstallment. ex: \"2020-03-10T10:30:00.000000+00:00\"
    - `:updated` [string, default nil]: latest update datetime for the MerchantInstallment. ex: \"2020-03-10T10:30:00.000000+00:00\""
  (:refer-clojure :exclude [get])
  (:require [starkbank.utils.rest :refer [get-id get-page get-stream]]
            [starkbank.settings :refer [credentials]]))

(defn- resource []
  "merchant-installment")

(defn get
  "Receive a single MerchantInstallment map previously created in the Stark Bank API by passing its id

  ## Parameters (required):
    - `:id` [string]: map unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - MerchantInstallment map with updated attributes"
  ([id]
   (-> (get-id @credentials (resource) id {})))

  ([id, user]
   (-> (get-id user (resource) id {}))))

(defn query
  "Receive a stream of MerchantInstallment maps previously created in the Stark Bank API.
  Use this function instead of page if you want to stream the objects without worrying about cursors and pagination.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-3-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-3-10\"
    - `:status` [string, default nil]: filter for status of retrieved maps. ex: \"created\", \"paid\", \"canceled\" or \"voided\"
    - `:purchase-ids` [list of strings, default nil]: list of MerchantPurchase ids to filter installments. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - stream of MerchantInstallment maps with updated attributes"
  ([]
   (-> (get-stream @credentials (resource) {})))

  ([params]
   (-> (get-stream @credentials (resource) params)))

  ([params, user]
   (-> (get-stream user (resource) params))))

(defn page
  "Receive a list of up to 100 MerchantInstallment maps previously created in the Stark Bank API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-3-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-3-10\"
    - `:status` [string, default nil]: filter for status of retrieved maps. ex: \"created\", \"paid\", \"canceled\" or \"voided\"
    - `:purchase-ids` [list of strings, default nil]: list of MerchantPurchase ids to filter installments. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - map with :installments and :cursor:
      - `:installments`: list of MerchantInstallment maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page of installments"
  ([]
   (-> (get-page @credentials (resource) {})))

  ([params]
   (-> (get-page @credentials (resource) params)))

  ([params, user]
   (-> (get-page user (resource) params))))
