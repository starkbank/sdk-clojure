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
  (:import [com.starkbank Invoice])
  (:use [starkbank.user]
        [clojure.walk]))

(defn- clojure-descriptions-to-java
  ([clojure-map]
    (let [{
      key "key"
      value "value"
    }
    (stringify-keys clojure-map)]
      
      (java.util.HashMap.
        {
          "key" key
          "value" value
        }
      ))))

(defn- clojure-to-java
  ([clojure-map]
    (let [{
      amount "amount"
      name "name"
      tax-id "tax-id"
      due "due"
      expiration "expiration"
      fine "fine"
      interest "interest"
      discounts "discounts"
      tags "tags"
      descriptions "descriptions"
    }
    (stringify-keys clojure-map)]

      (defn- apply-java-hashmap [x] (java.util.HashMap. x))
      
      (Invoice. (java.util.HashMap.
        {
          "amount" (if (nil? amount) nil (Long. amount))
          "name" name
          "taxId" tax-id
          "due" due
          "expiration" (if (nil? expiration) nil (Long. expiration))
          "fine" (if (nil? fine) nil (double fine))
          "interest" (if (nil? interest) nil (double interest))
          "discounts" (if (nil? discounts) nil (java.util.ArrayList. (map apply-java-hashmap discounts)))
          "tags" (if (nil? tags) nil (into-array String tags))
          "descriptions" (if (nil? descriptions) nil (java.util.ArrayList. (map clojure-descriptions-to-java descriptions)))
        }
      )))))

(defn- java-to-clojure
  ([java-object]
    (defn- java-description-to-map [description] 
      (if (nil? (.key description))
        {:key (.value description)}
        {
          :key (.key description)
          :value (.value description)}))
    (defn- java-discount-to-map [discount] {
      :percentage (.percentage discount)
      :due (.due discount)})

    {
      :id (.id java-object)
      :amount (.amount java-object)
      :name (.name java-object)
      :tax-id (.taxId java-object)
      :due (.due java-object)
      :expiration (.expiration java-object)
      :fine (.fine java-object)
      :interest (.interest java-object)
      :tags (into [] (.tags java-object))
      :descriptions (into [] (keywordize-keys (map java-description-to-map (.descriptions java-object))))
      :discounts (into [] (keywordize-keys (map java-discount-to-map (.discounts java-object))))
      :fee (.fee java-object)
      :pdf (.pdf java-object)
      :link (.link java-object)
      :nominal-amount (.nominalAmount java-object)
      :fine-amount (.fineAmount java-object)
      :interest-amount (.interestAmount java-object)
      :discount-amount (.discountAmount java-object)
      :transaction-ids (into [] (.transactionIds java-object))
      :brcode (.brcode java-object)
      :status (.status java-object)
      :created (.created java-object)
      :updated (.updated java-object)
    }))

(defn- java-payment-to-clojure
  ([java-object]
   (defn- java-description-to-map [description]
     (if (nil? (.key description))
       {:key (.value description)}
       {:key (.key description)
        :value (.value description)}))

   {
     :amount (.amount java-object)
     :name (.name java-object)
     :tax-id (.taxId java-object)
     :bank-code (.bankCode java-object)
     :branch-code (.branchCode java-object)
     :account-number (.accountNumber java-object)
     :account-type (.accountType java-object)
     :end-to-end-id (.endToEndId java-object)
     :method (.method java-object)
   }))
 
(defn- clojure-query-to-java
  ([clojure-map]
    (let [{
        limit "limit"
        after "after"
        before "before"
        status "status"
        tags "tags"
        ids "ids"
      } (stringify-keys clojure-map)]
      (java.util.HashMap.
        {
          "limit" (if (nil? limit) nil (Integer. limit))
          "after" after
          "before" before
          "status" status
          "tags" (if (nil? tags) nil (into-array String tags))
          "ids" (if (nil? ids) nil (into-array String ids))
        }
      ))))

(defn- clojure-page-to-java
  ([clojure-map]
    (let [{
        cursor "cursor"
        limit "limit"
        after "after"
        before "before"
        status "status"
        tags "tags"
        ids "ids"
      } (stringify-keys clojure-map)]
      (java.util.HashMap.
        {
          "cursor" cursor
          "limit" (if (nil? limit) nil (Integer. limit))
          "after" after
          "before" before
          "status" status
          "tags" (if (nil? tags) nil (into-array String tags))
          "ids" (if (nil? ids) nil (into-array String ids))
        }
      ))))

(defn- clojure-update-to-java
  ([clojure-map]
   (let [{
     status "status"
     amount "amount"
     due "due"
     expiration "expiration"
    } (stringify-keys clojure-map)]
     (java.util.HashMap.
      {
        "status" status
        "amount" (if (nil? amount) nil (Long. amount))
        "due" due
        "expiration" (if (nil? expiration) nil (Long. expiration))
      }))))

(defn create
  "Send a list of Invoice maps for creation in the Stark Bank API

  ## Parameters (required):
    - `invoices` [list of Invoice maps]: list of Invoice maps to be created in the API

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - list of Invoice maps with updated attributes"
  ([invoices]
    (def java-invoices (map clojure-to-java invoices))
    (def created-java-invoices (Invoice/create java-invoices))
    (map java-to-clojure created-java-invoices))

  ([invoices, user] 
    (def java-invoices (map clojure-to-java invoices))
    (def created-java-invoices (Invoice/create java-invoices (#'starkbank.user/get-java-user user)))
    (map java-to-clojure created-java-invoices)))

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
    (map java-to-clojure (Invoice/query)))

  ([params]
    (def java-params (clojure-query-to-java params))
    (map java-to-clojure (Invoice/query java-params)))

  ([params, user] 
    (def java-params (clojure-query-to-java params))
    (map java-to-clojure (Invoice/query java-params (#'starkbank.user/get-java-user user)))))

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
    (def invoice-page (Invoice/page))
    (def cursor (.cursor invoice-page))
    (def invoices (map java-to-clojure (.invoices invoice-page)))
    {:invoices invoices, :cursor cursor})

  ([params]
    (def java-params (clojure-page-to-java params))
    (def invoice-page (Invoice/page java-params))
    {:invoices (map java-to-clojure (.invoices invoice-page)), :cursor (.cursor invoice-page)})

  ([params, user] 
    (def java-params (clojure-page-to-java params))
    (def invoice-page (Invoice/page java-params (#'starkbank.user/get-java-user user)))
    {:invoices (map java-to-clojure (.invoices invoice-page)), :cursor (.cursor invoice-page)}))

(defn get
  "Receive a single Invoice map previously created in the Stark Bank API by passing its id

  ## Parameters (required):
    - `:id` [string]: map unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - Invoice map with updated attributes"
  ([id]
    (java-to-clojure
      (Invoice/get id)))

  ([id, user]
    (java-to-clojure
      (Invoice/get
        id
        (#'starkbank.user/get-java-user user)))))

(defn pdf
  "Receive a single Invoice pdf file generated in the Stark Bank API by passing its id.

  ## Parameters (required):
    - `:id` [string]: map unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - Invoice pdf file content"
  ([id]
    (clojure.java.io/input-stream
      (Invoice/pdf id)))

  ([id, user]
    (clojure.java.io/input-stream
      (Invoice/pdf
        id
        (#'starkbank.user/get-java-user user)))))

(defn qrcode
  "Receive a single Invoice QRCode in png format generated in the Stark Bank API by the invoice ID.

  ## Parameters (required):
    - `:id` [string]: map unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - Invoice QR Code png blob"
    ([id]
      (Invoice/qrcode id))
  
    ([id, user]
      (Invoice/qrcode
        id
        (#'starkbank.user/get-java-user user))))

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
    (java-payment-to-clojure
      (Invoice/payment id)))

  ([id, user]
    (java-payment-to-clojure
      (Invoice/payment
        id
        (#'starkbank.user/get-java-user user)))))

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
   (java-to-clojure
    (Invoice/update id (clojure-update-to-java params))))

  ([id, params, user]
   (java-to-clojure
    (Invoice/update
     id
     (clojure-update-to-java params)
     (#'starkbank.user/get-java-user user)))))


(ns starkbank.invoice.log
  "Every time an Invoice entity is updated, a corresponding Invoice.Log
  is generated for the entity. This log is never generated by the
  user, but it can be retrieved to check additional information
  on the Invoice.

  ## Attributes:
    - `:id` [string]: unique id returned when the log is created. ex: \"5656565656565656\"
    - `:invoice` [Invoice]: Invoice entity to which the log refers to.
    - `:errors` [list of strings]: list of errors linked to this Invoice event
    - `:type` [string]: type of the Invoice event which triggered the log creation. ex: \"registered\" or \"paid\"
    - `:created` [string]: creation datetime for the log. ex: \"2020-03-26T19:32:35.418698+00:00\""
  (:refer-clojure :exclude [get set])
  (:import [com.starkbank Invoice$Log])
  (:require [starkbank.invoice :as invoice])
  (:use [starkbank.user]
        [clojure.walk]))

(defn- java-to-clojure
  ([java-object]
    {
      :id (.id java-object)
      :created (.created java-object)
      :errors (into [] (.errors java-object))
      :type (.type java-object)
      :invoice (#'invoice/java-to-clojure (.invoice java-object))
    }))

(defn- clojure-query-to-java
  ([clojure-map]
    (let [{
        limit "limit"
        after "after"
        before "before"
        types "types"
        invoice-ids "invoice-ids"
      } (stringify-keys clojure-map)]
      (java.util.HashMap.
        {
          "limit" (if (nil? limit) nil (Integer. limit))
          "after" after
          "before" before
          "types" (if (nil? types) nil (into-array String types))
          "invoiceIds" (if (nil? invoice-ids) nil (into-array String invoice-ids))
        }
      ))))

(defn- clojure-page-to-java
  ([clojure-map]
    (let [{
        cursor "cursor"
        limit "limit"
        after "after"
        before "before"
        types "types"
        invoice-ids "invoice-ids"
      } (stringify-keys clojure-map)]
      (java.util.HashMap.
        {
          "cursor" cursor
          "limit" (if (nil? limit) nil (Integer. limit))
          "after" after
          "before" before
          "types" (if (nil? types) nil (into-array String types))
          "invoiceIds" (if (nil? invoice-ids) nil (into-array String invoice-ids))
        }
      ))))

(defn get
  "Receive a single Log map previously created by the Stark Bank API by passing its id

  ## Parameters (required):
    - `:id` [string]: map unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - Log map with updated attributes"
  ([id]
    (java-to-clojure
      (Invoice$Log/get id)))

  ([id, user]
    (java-to-clojure
      (Invoice$Log/get
        id
        (#'starkbank.user/get-java-user user)))))

(defn query
  "Receive a stream of Log maps previously created in the Stark Bank API.
  Use this function instead of page if you want to stream the objects without worrying about cursors and pagination.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-3-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-3-10\"
    - `:types` [list of strings, default nil]: filter for log event types. ex: \"paid\" or \"registered\"
    - `:invoice-ids` [list of strings, default nil]: list of Invoice ids to filter logs. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - stream of Log maps with updated attributes"
  ([]
    (map java-to-clojure (Invoice$Log/query)))

  ([params]
    (def java-params (clojure-query-to-java params))
    (map java-to-clojure (Invoice$Log/query java-params)))

  ([params, user] 
    (def java-params (clojure-query-to-java params))
    (map java-to-clojure (Invoice$Log/query java-params (#'starkbank.user/get-java-user user)))))

(defn page
  "Receive a list of up to 100 Invoice.Log maps previously created in the Stark Bank API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-3-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-3-10\"
    - `:types` [list of strings, default nil]: filter for log event types. ex: \"paid\" or \"registered\"
    - `:invoice-ids` [list of strings, default nil]: list of Invoice ids to filter logs. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - map with :logs and :cursor:
      - `:logs`: list of log maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page of logs"
  ([]
    (def log-page (Invoice$Log/page))
    (def cursor (.cursor log-page))
    (def logs (map java-to-clojure (.logs log-page)))
    {:logs logs, :cursor cursor})

  ([params]
    (def java-params (clojure-page-to-java params))
    (def log-page (Invoice$Log/page java-params))
    {:logs (map java-to-clojure (.logs log-page)), :cursor (.cursor log-page)})

  ([params, user] 
    (def java-params (clojure-page-to-java params))
    (def log-page (Invoice$Log/page java-params (#'starkbank.user/get-java-user user)))
    {:logs (map java-to-clojure (.logs log-page)), :cursor (.cursor log-page)}))

(defn pdf
  "Receive a single Invoice pdf file generated in the Stark Bank API by passing its id.

  ## Parameters (required):
    - `:id` [string]: map unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - Invoice pdf file content"
  ([id]
    (clojure.java.io/input-stream
      (Invoice$Log/pdf id)))

  ([id, user]
    (clojure.java.io/input-stream
      (Invoice$Log/pdf
        id
        (#'starkbank.user/get-java-user user)))))
