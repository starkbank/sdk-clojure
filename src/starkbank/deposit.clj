(ns starkbank.deposit
  "Deposits represent non-reconciled cash-ins received by your account from external transfers or payments
  
    ## Parameters (required):
      - `:id` [string]: unique id associated with a Deposit when it is created. ex: \"5656565656565656\"
      - `:name` [string]: payer name. ex: \"Iron Bank S.A.\"
      - `:tax-id` [string]: payer tax ID (CPF or CNPJ). ex: \"012.345.678-90\" or \"20.018.183/0001-80\"
      - `:bank-code` [string]: payer bank code in Brazil. ex: \"20018183\" or \"341\"
      - `:branch-code` [string]: payer bank account branch. ex: \"1357-9\"
      - `:account-number` [string]: payer bank account number. ex: \"876543-2\"
      - `:amount` [integer]: Deposit value in cents. ex: 1234 (= R$ 12.34)
      - `:type` [string]: type of settlement that originated the deposit. ex: \"pix\" or \"ted\"
      - `:status` [string]: current Deposit status. ex: \"created\"
      - `:tags` [list of strings]: list of strings that are tagging the deposit. ex: [\"reconciliationId\", \"taxId\"]
      - `:fee` [integer]: fee charged when a deposit is created. ex: 50 (= R$ 0.50)
      - `:transaction-ids` [list of strings]: ledger transaction ids linked to this deposit (if there are more than one, all but first are reversals). ex: [\"19827356981273\"]
      - `:created` [string]: creation datetime for the Deposit. ex: \"2020-03-10 10:30:00.000\"
      - `:updated` [string]: latest update datetime for the Deposit. ex: \"2020-03-10 10:30:00.000\""
  (:refer-clojure :exclude [get set update])
  (:require [starkbank.utils.rest :refer [get-id get-page get-stream]]
            [starkbank.settings :refer [credentials]]))

(defn- resource []
  "deposit")

(defn query
  "Receive a stream of Deposit maps previously created in the Stark Bank API.
  Use this function instead of page if you want to stream the objects without worrying about cursors and pagination.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-3-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-3-10\"
    - `:status` [string, default nil]: filter for status of retrieved maps. ex: \"created\", \"paid\", \"canceled\" or \"overdue\"
    - `:sort` [string, default \"-created\"]: sort order considered in response. Valid options are \"created\" or \"-created\".
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - stream of Deposit maps with updated attributes"
  ([]
    (-> (get-stream @credentials (resource) {})))

  ([params]
    (-> (get-stream @credentials (resource) params)))

  ([params, user] 
    (-> (get-stream user (resource) params))))

(defn page
  "Receive a list of up to 100 Deposit maps previously created in the Stark Bank API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-3-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-3-10\"
    - `:status` [string, default nil]: filter for status of retrieved maps. ex: \"created\", \"paid\", \"canceled\" or \"overdue\"
    - `:sort` [string, default \"-created\"]: sort order considered in response. Valid options are \"created\" or \"-created\".
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - map with :deposits and :cursor:
      - `:deposits`: list of deposit maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page of deposits"
  ([]
   (-> (get-page @credentials (resource) {})))
  
  ([params]
   (-> (get-page @credentials (resource) params)))
  
  ([params, user]
   (-> (get-page user (resource) params))))
    
(defn get
  "Receive a single Deposit map previously created in the Stark Bank API by passing its id

  ## Parameters (required):
    - `:id` [string]: map unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - Deposit map with updated attributes"
  ([id]
    (-> (get-id @credentials (resource) id {})))

  ([id, user]
    (-> (get-id user (resource) id {}))))
