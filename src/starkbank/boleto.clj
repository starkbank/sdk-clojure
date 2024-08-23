(ns starkbank.boleto
  "When you initialize a Boleto map, the entity will not be automatically
  sent to the Stark Bank API. The 'create' function sends the maps
  to the Stark Bank API and returns the list of created maps.

  ## Parameters (required):
    - `:amount` [integer]: Boleto value in cents. Minimum amount = 200 (R$2,00). ex: 1234 (= R$ 12.34)
    - `:name` [string]: payer full name. ex: \"Anthony Edward Stark\"
    - `:tax-id` [string]: payer tax ID (CPF or CNPJ) with or without formatting. ex: \"01234567890\" or \"20.018.183/0001-80\"
    - `:street-line-1` [string]: payer main address. ex: Av. Paulista, 200
    - `:street-line-2` [string]: payer address complement. ex: Apto. 123
    - `:district` [string]: payer address district / neighbourhood. ex: Bela Vista
    - `:city` [string]: payer address city. ex: Rio de Janeiro
    - `:state-code` [string]: payer address state. ex: GO
    - `:zip-code` [string]: payer address zip code. ex: 01311-200

  ## Parameters (optional):
    - `:due` [string, default today + 2 days]: Boleto due date in ISO format. ex: 2020-04-30
    - `:fine` [float, default 0.0]: Boleto fine for overdue payment in %. ex: 2.5
    - `:interest` [float, default 0.0]: Boleto monthly interest for overdue payment in %. ex: 5.2
    - `:overdue-limit` [integer, default 59]: limit in days for payment after due date. ex: 7 (max: 59)
    - `:receiver-name` [string]: receiver (Sacador Avalista) full name. ex: \"Anthony Edward Stark\"
    - `:receiver-tax-id` [string]: receiver (Sacador Avalista) tax ID (CPF or CNPJ) with or without formatting. ex: \"01234567890\" or \"20.018.183/0001-80\"
    - `:descriptions` [list of maps, default nil]: list of maps with :text (string) and :amount (int, optional) pairs
    - `:discounts` [list of maps, default nil]: list of maps with :percentage (float) and :date (string) pairs
    - `:tags` [list of strings]: list of strings for tagging

  ## Attributes (return-only):
    - `:id` [string, default nil]: unique id returned when the Boleto is created. ex: \"5656565656565656\"
    - `:our-number` [string, default nil]: Reference number registered at the settlement bank. ex: \"10131474\"
    - `:fee` [integer, default nil]: fee charged when the Boleto is paid. ex: 200 (= R$ 2.00)
    - `:line` [string, default nil]: generated Boleto line for payment. ex: \"34191.09008 63571.277308 71444.640008 5 81960000000062\"
    - `:bar-code` [string, default nil]: generated Boleto bar-code for payment. ex: \"34195819600000000621090063571277307144464000\"
    - `:status` [string, default nil]: current Boleto status. ex: \"registered\" or \"paid\"
    - `:transaction-ids` [list of strings, default nil]: ledger transaction ids linked to this boleto. ex: [\"19827356981273\"] 
    - `:created` [string, default nil]: creation datetime for the Boleto. ex: \"2020-03-26T19:32:35.418698+00:00\""
  (:refer-clojure :exclude [get set])
  (:require [starkbank.settings :refer [credentials]]
            [starkbank.utils.rest :refer [post-multi get-stream get-page get-id get-content delete-id]]))

(defn- resource []
  "boleto") 

(defn create
  "Send a list of Boleto maps for creation in the Stark Bank API

  ## Parameters (required):
    - `boletos` [list of Boleto maps]: list of Boleto maps to be created in the API

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - list of Boleto maps with updated attributes"
  ([boletos]
   (-> (post-multi @credentials (resource) boletos {})))

  ([boletos, user]
   (-> (post-multi user (resource) boletos {})))
  )

(defn query
  "Receive a stream of Boleto maps previously created in the Stark Bank API.
  Use this function instead of page if you want to stream the objects without worrying about cursors and pagination.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-3-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-3-10\"
    - `:status` [string, default nil]: filter for status of retrieved maps. ex: \"paid\" or \"registered\"
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - stream of Boleto maps with updated attributes"
  ([]
    (-> (get-stream @credentials (resource) {})))

  ([params]
    (-> (get-stream @credentials (resource) params)))

  ([params, user] 
    (-> (get-stream user (resource) params))))

(defn page
  "Receive a list of up to 100 Boleto maps previously created in the Stark Bank API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-3-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-3-10\"
    - `:status` [string, default nil]: filter for status of retrieved maps. ex: \"paid\" or \"registered\"
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - map with :boletos and :cursor:
      - `:boletos`: list of boleto maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page of boletos"
  ([]
   (-> (get-page @credentials (resource) {})))
  
  ([params]
   (-> (get-page @credentials (resource) params)))
  
  ([params, user]
   (-> (get-page user (resource) params))))

(defn get
  "Receive a single Boleto map previously created in the Stark Bank API by passing its id

  ## Parameters (required):
    - `:id` [string]: map unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - Boleto map with updated attributes"
  ([id]
    (-> (get-id @credentials (resource) id {})))

  ([id, user]
   (-> (get-id user (resource) id {})))
  )

(defn delete
  "Delete a list of Boleto entities previously created in the Stark Bank API

  ## Parameters (required):
    - `:id` [string]: Boleto unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ##  Return:
    - deleted Boleto map"
  ([id]
    (-> (delete-id @credentials (resource) id)))

  ([id, user]
    (-> (delete-id user (resource) id))))

(defn pdf
  "Receive a single Boleto pdf file generated in the Stark Bank API by passing its id.

  ## Parameters (required):
    - `:id` [string]: map unique id. ex: \"5656565656565656\"

  ## Options:
    - `:layout` [string]: Layout specification. Available options are \"default\" and \"booklet\"
    - `:hidden-fields` [list of strings]: List of string fields to be hidden in the Boleto pdf. ex: [\"customerAddress\"]
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - Boleto pdf file content"
  ([id]
   (-> (get-content @credentials (resource) id "pdf" {})))

  ([id, user-or-options]
   (if (contains? user-or-options :private-key)
     (-> (get-content user-or-options (resource) id "pdf" {}))
     (-> (get-content @credentials (resource) id "pdf" user-or-options))))

  ([id, options, user]
    (-> (get-content user (resource) id "pdf" options)))
  )
