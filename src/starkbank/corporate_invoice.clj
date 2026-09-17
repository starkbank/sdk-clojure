(ns starkbank.corporate-invoice
  "The CorporateInvoice maps created in your Workspace load your Corporate balance when paid.
  When you initialize a CorporateInvoice map, the entity will not be automatically
  sent to the Stark Bank API. The 'create' function sends the map
  to the Stark Bank API and returns the created map.

  ## Parameters (required):
    - `:amount` [integer]: CorporateInvoice value in cents. ex: 1234 (= R$ 12.34)

  ## Parameters (optional):
    - `:tags` [list of strings, default nil]: list of strings for tagging. ex: [\"travel\", \"food\"]

  ## Attributes (return-only):
    - `:id` [string, default nil]: unique id returned when CorporateInvoice is created. ex: \"5656565656565656\"
    - `:name` [string, default sub-issuer name]: payer name. ex: \"Iron Bank S.A.\"
    - `:tax-id` [string, default sub-issuer tax ID]: payer tax ID (CPF or CNPJ) with or without formatting. ex: \"01234567890\" or \"20.018.183/0001-80\"
    - `:brcode` [string, default nil]: BR Code for the Invoice payment. ex: \"00020101021226930014br.gov.bcb.pix2571brcode-h.development.starkbank.com/v2/d7f6546e194d4c64a153e8f79f1c41ac5204000053039865802BR5925Stark Bank S.A. - Institu6009Sao Paulo62070503***63042109\"
    - `:due` [string, default nil]: Invoice due and expiration date in UTC ISO format. ex: \"2020-03-10T10:30:00.000000+00:00\"
    - `:link` [string, default nil]: public Invoice webpage URL. ex: \"https://starkbank-card-issuer.development.starkbank.com/invoicelink/d7f6546e194d4c64a153e8f79f1c41ac\"
    - `:status` [string, default nil]: current CorporateInvoice status. ex: \"created\", \"expired\", \"overdue\" or \"paid\"
    - `:corporate-transaction-id` [string, default nil]: ledger transaction id linked to this CorporateInvoice. ex: \"corporate-invoice/5656565656565656\"
    - `:updated` [string, default nil]: latest update datetime for the CorporateInvoice. ex: \"2020-03-26T19:32:35.418698+00:00\"
    - `:created` [string, default nil]: creation datetime for the CorporateInvoice. ex: \"2020-03-26T19:32:35.418698+00:00\""
  (:require [starkbank.utils.rest :refer [get-page get-stream post-single]]
            [starkbank.settings :refer [credentials]]))

(defn- resource []
  "corporate-invoice")

(defn create
  "Send a single CorporateInvoice map for creation in the Stark Bank API.

  ## Parameters (required):
    - `invoice` [CorporateInvoice map]: CorporateInvoice map to be created in the API.

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - CorporateInvoice map with updated attributes"
  ([invoice]
   (-> (post-single @credentials (resource) invoice {})))

  ([invoice, user]
   (-> (post-single user (resource) invoice {}))))

(defn query
  "Receive a stream of CorporateInvoice maps previously created in the Stark Bank API.
  Use this function instead of page if you want to stream the objects without worrying about cursors and pagination.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-3-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-3-10\"
    - `:status` [list of strings, default nil]: filter for status of retrieved maps. ex: [\"created\", \"expired\", \"overdue\", \"paid\"]
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - stream of CorporateInvoice maps with updated attributes"
  ([]
   (-> (get-stream @credentials (resource) {})))

  ([params]
   (-> (get-stream @credentials (resource) params)))

  ([params, user]
   (-> (get-stream user (resource) params))))

(defn page
  "Receive a list of up to 100 CorporateInvoice maps previously created in the Stark Bank API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-3-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-3-10\"
    - `:status` [list of strings, default nil]: filter for status of retrieved maps. ex: [\"created\", \"expired\", \"overdue\", \"paid\"]
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - map with :invoices and :cursor:
      - `:invoices`: list of CorporateInvoice maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page of invoices"
  ([]
   (-> (get-page @credentials (resource) {})))

  ([params]
   (-> (get-page @credentials (resource) params)))

  ([params, user]
   (-> (get-page user (resource) params))))
