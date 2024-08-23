(ns starkbank.dict-key
  "DictKey represents a PIX key registered in Bacen's DICT system.
  
    ## Parameters (optional):
      - `:id` [string, default nil]: DictKey object unique id and PIX key itself. ex: \"tony@starkbank.com\", \"722.461.430-04\", \"20.018.183/0001-80\", \"+5511988887777\", \"b6295ee1-f054-47d1-9e90-ee57b74f60d9\"

    ## Attributes (return-only):
      - `:type` [string, default nil]: PIX key type. ex: \"email\", \"cpf\", \"cnpj\", \"phone\" or \"evp\"
      - `:name` [string, default nil]: account owner full name. ex: \"Tony Stark\"
      - `:tax-id [string, default nil]: tax ID (CPF or CNPJ) with or without formatting. ex: \"01234567890\" or \"20.018.183/0001-80\"
      - `:owner-type` [string, default nil]: PIX key owner type. ex \"naturalPerson\" or \"legalPerson\"
      - `:bank-name` [string, default nil]: bank name associated with the DICT key. ex: \"Stark Bank\"
      - `:ispb` [string, default nil]: ISPB code used for transactions. ex: \"20018183\"
      - `:branch-code` [string, default nil]: bank account branch code associated with the PIX key. ex: \"9585\"
      - `:account-number` [string, default nil]: bank account number associated with the PIX key. ex: \"9828282578010513\"
      - `:account-type` [string, default nil]: bank account type associated with the PIX key. ex: \"checking\", \"saving\", \"salary\" or \"payment\"
      - `:status` [string, default nil]: current PIX key status. ex: \"created\", \"registered\", \"canceled\" or \"failed\"
      - `:account-created` [string, default nil]: creation datetime of the bank account associated with the PIX key. ex: \"2020-11-05T14:55:08.812665+00:00\"
      - `:owned` [string, default nil]: datetime since when the current owner hold this PIX key. ex : \"2020-11-05T14:55:08.812665+00:00\"     
      - `:created` [string, default nil]: creation datetime for the PIX key. ex: \"2020-11-05T14:55:08.812665+00:00\""
  (:refer-clojure :exclude [get set update keys])
  (:require [starkbank.utils.rest :refer [get-id get-page get-stream]]
            [starkbank.settings :refer [credentials]]))

(defn- resource []
  "dict-key")

(defn get
  "Receive a single DictKey by passing its id

  ## Parameters (required):
    - `:id` [string]: DictKey object unique id and PIX key itself. ex: \"tony@starkbank.com\", \"722.461.430-04\", \"20.018.183/0001-80\", \"+5511988887777\", \"b6295ee1-f054-47d1-9e90-ee57b74f60d9\"

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - DictKey object with updated attributes"
  ([id]
    (-> (get-id @credentials (resource) id {})))

  ([id, user]
    (-> (get-id user (resource) id {}))))
  
(defn query
  "Receive a stream of DictKey maps previously created in the Stark Bank API.
  Use this function instead of page if you want to stream the objects without worrying about cursors and pagination.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-3-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-3-10\"
    - `:type` [string, default nil]: DictKey type. ex: \"cpf\", \"cnpj\", \"phone\", \"email\" or \"evp\"
    - `:status` [string, default nil]: filter for status of retrieved maps. ex: \"created\", \"paid\", \"canceled\" or \"overdue\"
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - stream of DictKey maps with updated attributes"
  ([]
    (-> (get-stream @credentials (resource) {})))

  ([params]
    (-> (get-stream @credentials (resource) params)))

  ([params, user] 
    (-> (get-stream user (resource) params))))

(defn page
  "Receive a list of up to 100 DictKey maps previously created in the Stark Bank API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-3-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-3-10\"
    - `:type` [string, default nil]: DictKey type. ex: \"cpf\", \"cnpj\", \"phone\", \"email\" or \"evp\"
    - `:status` [string, default nil]: filter for status of retrieved maps. ex: \"created\", \"paid\", \"canceled\" or \"overdue\"
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - map with :keys and :cursor:
      - `:keys`: list of key maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page of keys"
  ([]
   (-> (get-page @credentials (resource) {})))
  
  ([params]
   (-> (get-page @credentials (resource) params)))
  
  ([params, user]
   (-> (get-page user (resource) params))))
