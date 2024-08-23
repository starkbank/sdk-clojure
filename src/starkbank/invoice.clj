(ns starkbank.invoice
  "When you initialize an Invoice map, the entity will not be automatically
  sent to the Stark Bank API. The 'create' function sends the maps
  to the Stark Bank API and returns the list of created maps.
  To create scheduled Invoices, which will display the discount, interest, etc. on the final users banking interface,
  use dates instead of datetimes on the \"due\" and \"discounts\" fields.

  ## Parameters (required):
    - `:amount` [integer]: Invoice value in cents. Minimum amount = 0 (any value will be accepted). ex: 1234 (= R$ 12.34)
    - `:name` [string]: payer name. ex: \"Iron Bank S.A.\"
    - `:tax-id` [string]: payer tax ID (CPF or CNPJ) with or without formatting. ex: \"01234567890\" or \"20.018.183/0001-80\"

  ## Parameters (optional):
    - `:due` [string, default now + 2 days]: Invoice due date in ISO format. ex: \"2020-11-27T01:24:01.665-00:00\"
    - `:expiration` [integer, default 5097600 (59 days)]: time interval in seconds between due date and expiration date. ex 123456789
    - `:fine` [float, default 2.0]: Invoice fine for overdue payment in %. ex: 2.5
    - `:interest` [float, default 1.0]: Invoice monthly interest for overdue payment in %. ex: 5.2
    - `:discounts` [list of maps, default nil]: list of maps with :percentage (float) and :due (string) pairs
    - `:tags` [list of strings, default nil]: list of strings for tagging
    - `:descriptions` [list of maps, default nil]: list of maps with :key (string) and :value (string) pairs

  ## Attributes (return-only):
    - `:fee` [integer, default nil]: fee charged by this Invoice. ex: 65 (= R$ 0.65)
    - `:pdf` [string, default nil]: public Invoice PDF URL. ex: \"https://invoice.starkbank.com/pdf/d454fa4e524441c1b0c1a729457ed9d8\"
    - `:link` [string, default nil]: public Invoice webpage URL. ex: \"https://my-workspace.sandbox.starkbank.com/invoicelink/d454fa4e524441c1b0c1a729457ed9d8\"
    - `:nominal-amount` [integer, default nil]: Invoice emission value in cents (will change if invoice is updated, but not if it's paid). ex: 400000
    - `:fine-amount` [integer, default nil]: Invoice fine value calculated over nominal-amount. ex: 20000
    - `:interest-amount` [integer, default nil]: Invoice interest value calculated over nominal-amount. ex: 10000
    - `:discount-amount` [integer, default nil]: Invoice discount value calculated over nominal-amount. ex: 3000
    - `:transaction-ids` [list of strings]: ledger transaction ids linked to this invoice (if there are more than one, all but first are reversals). ex: [\"19827356981273\"]
    - `:id` [string, default nil]: unique id returned when the Invoice is created. ex: \"5656565656565656\"
    - `:brcode` [string, default nil]: BR Code for the Invoice payment. ex: \"00020101021226800014br.gov.bcb.pix2558invoice.starkbank.com/f5333103-3279-4db2-8389-5efe335ba93d5204000053039865802BR5913Arya Stark6009Sao Paulo6220051656565656565656566304A9A0\"
    - `:status` [string, default nil]: current Invoice status. ex: \"registered\" or \"paid\"
    - `:created` [string, default nil]: creation datetime for the Invoice. ex: \"2020-03-26T19:32:35.418698+00:00\"
    - `:updated` [string, default nil]: latest update datetime for the Invoice. ex: \"2020-03-26T19:32:35.418698+00:00\""
  (:refer-clojure :exclude [get set update])
  (:require [starkbank.utils.rest :refer [get-content get-id get-page
                                          get-stream get-sub-resource patch-id
                                          post-multi]]
            [starkbank.settings :refer [credentials]]))

(defn- resource []
  "invoice")

(defn- sub-resource []
  "payment")

(defn create
  "Send a list of Invoice maps for creation in the Stark Bank API

  ## Parameters (required):
    - `invoices` [list of Invoice maps]: list of Invoice maps to be created in the API

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - list of Invoice maps with updated attributes"
  ([invoices]
    (-> (post-multi @credentials (resource) invoices {})))

  ([invoices, user] 
    (-> (post-multi user (resource) invoices {}))))

(defn query
  "Receive a stream of Invoice maps previously created in the Stark Bank API.
  Use this function instead of page if you want to stream the objects without worrying about cursors and pagination.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-3-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-3-10\"
    - `:status` [string, default nil]: filter for status of retrieved maps. ex: \"created\", \"paid\", \"canceled\" or \"overdue\"
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - stream of Invoice maps with updated attributes"
  ([]
    (-> (get-stream @credentials (resource) {})))

  ([params]
    (-> (get-stream @credentials (resource) params)))

  ([params, user] 
    (-> (get-stream user (resource) params))))

(defn page
  "Receive a list of up to 100 Invoice maps previously created in the Stark Bank API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-3-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-3-10\"
    - `:status` [string, default nil]: filter for status of retrieved maps. ex: \"created\", \"paid\", \"canceled\" or \"overdue\"
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - map with :invoices and :cursor:
      - `:invoices`: list of invoice maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page of invoices"
  ([]
   (-> (get-page @credentials (resource) {})))
  
  ([params]
   (-> (get-page @credentials (resource) params)))
  
  ([params, user]
   (-> (get-page user (resource) params))))

(defn get
  "Receive a single Invoice map previously created in the Stark Bank API by passing its id

  ## Parameters (required):
    - `:id` [string]: map unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - Invoice map with updated attributes"
  ([id]
    (-> (get-id @credentials (resource) id {})))

  ([id, user]
    (-> (get-id user (resource) id {}))))

(defn pdf
  "Receive a single Invoice pdf file generated in the Stark Bank API by passing its id.

  ## Parameters (required):
    - `:id` [string]: map unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - Invoice pdf file content"
  ([id]
    (-> (get-content @credentials (resource) id "pdf" {})))

  ([id, user]
    (-> (get-content user (resource) id "pdf" {}))))

(defn qrcode
  "Receive a single Invoice QRCode in png format generated in the Stark Bank API by the invoice ID.

  ## Parameters (required):
    - `:id` [string]: map unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - Invoice QR Code png blob"
  ([id]
   (-> (get-content @credentials (resource) id "qrcode" {})))

  ([id, user]
   (-> (get-content user (resource) id "qrcode" {}))))


(defn payment
  "Receive the invoice.payment sub-resource associated with a paid invoice.

  ## Parameters (required):
    - `:id` [string]: map unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - Invoice payment information:
      - amount [integer]: amount in cents that was paid. ex: 1234 (= R$ 12.34)
      - name [string]: payer full name. ex: \"Anthony Edward Stark\"
      - tax-id [string]: payer tax ID (CPF or CNPJ). ex: \"20.018.183/0001-80\"
      - bank-code [string]: code of the payer bank institution in Brazil. ex: \"20018183\"
      - branch-code [string]: payer bank account branch. ex: \"1357-9\"
      - account-number [string]: payer bank account number. ex: \"876543-2\"
      - account-type [string]: payer bank account type. ex: \"checking\", \"savings\", \"salary\" or \"payment\"
      - end-to-end-id [string]: central bank's unique transaction ID. ex: \"E79457883202101262140HHX553UPqeq\"
      - method [string]: payment method that was used. ex: \"pix\""
  ([id]
    (-> (get-sub-resource @credentials (resource) id (sub-resource) {})))

  ([id, user]
   (-> (get-sub-resource user (resource) id (sub-resource) {})))
  )

(defn update
  "Update an Invoice by passing id.

  ## Parameters (required):
    - `:id` [list of strings]: Invoice unique ids. ex: \"5656565656565656\"

  ## Parameters (optional):
    - `:status` [string, default nil]: If the Invoice hasn't been paid yet, you may cancel it by passing \"canceled\" in the status
    - `:amount` [integer, default nil]: If the Invoice hasn't been paid yet, you may update its amount by passing the desired amount integer
    - `:due` [string, default today + 2 days]: Invoice due date in UTC ISO format. ex: \"2020-11-25T17:59:26.249976+00:00\"
    - `:expiration` [DateInterval or integer, default nil]: time interval in seconds between due date and expiration date. ex 123456789
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - target Invoice with updated attributes"
  ([id, params]
   (-> (patch-id @credentials (resource) params id)))

  ([id, params, user]
   (-> (patch-id user (resource) params id)))
  )
