(ns starkbank.dict-key
    "DictKey represents a Pix key registered in Bacen's DICT system.
  
    ## Parameters (optional):
      - `:id` [string, default nil]: DictKey object unique id. ex: \"tony@starkbank.com\", \"722.461.430-04\", \"20.018.183/0001-80\", \"+5511988887777\", \"b6295ee1-f054-47d1-9e90-ee57b74f60d9\"

    ## Attributes (return-only):
      - `:type` [string, default nil]: DICT key type. ex: \"email\", \"cpf\", \"cnpj\", \"phone\" or \"evp\"
      - `:name` [string, default nil]: account owner full name. ex: \"Tony Stark\"
      - `:tax-id [string, default nil]: tax ID (CPF or CNPJ) with or without formatting. ex: \"01234567890\" or \"20.018.183/0001-80\"
      - `:owner-type` [string, default nil]: DICT key owner type. ex \"naturalPerson\" or \"legalPerson\"
      - `:bank-name` [string, default nil]: bank name associated with the DICT key. ex: \"Stark Bank\"
      - `:ispb` [string, default nil]: ISPB code used for transactions. ex: \"20018183\"
      - `:branch-code` [string, default nil]: encrypted bank account branch code associated with the DICT key. ex: \"ZW5jcnlwdGVkLWJyYW5jaC1jb2Rl\"
      - `:account-number` [string, default nil]: encrypted bank account number associated with the DICT key. ex: \"ZW5jcnlwdGVkLWFjY291bnQtbnVtYmVy\"
      - `:account-type` [string, default nil]: bank account type associated with the DICT key. ex: \"checking\", \"savings\", \"salary\" or \"payment\"
      - `:status` [string, default nil]: current DICT key status. ex: \"created\", \"registered\", \"canceled\" or \"failed\""
    (:refer-clojure :exclude [get set update keys])
    (:import [com.starkbank DictKey])
    (:use [starkbank.user]
          [clojure.walk]))

(defn- java-to-clojure
  ([java-object]
    {
      :id (.id java-object)
      :type (.type java-object)
      :name (.name java-object)
      :tax-id (.taxId java-object)
      :bank-name (.bankName java-object)
      :owner-type (.ownerType java-object)
      :ispb (.ispb java-object)
      :branch-code (.branchCode java-object)
      :account-number (.accountNumber java-object)
      :account-type (.accountType java-object)
      :status (.status java-object)
    }))

(defn- clojure-query-to-java
  ([clojure-map]
    (let [{
        limit "limit"
        after "after"
        before "before"
        status "status"
        sort "sort"
        type "type"
        ids "ids"
      } (stringify-keys clojure-map)]
      (java.util.HashMap.
        {
          "limit" (if (nil? limit) nil (Integer. limit))
          "after" after
          "before" before
          "status" (if (nil? status) nil (into-array String status))
          "sort" sort
          "type" type
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
        sort "sort"
        type "type"
        ids "ids"
      } (stringify-keys clojure-map)]
      (java.util.HashMap.
        {
          "cursor" cursor
          "limit" (if (nil? limit) nil (Integer. limit))
          "after" after
          "before" before
          "status" (if (nil? status) nil (into-array String status))
          "sort" sort
          "type" type
          "ids" (if (nil? ids) nil (into-array String ids))
        }
      ))))

(defn get
  "Receive a single DictKey by passing its id

  ## Parameters (required):
    - `:id` [string]: DictKey object unique id and PIX key itself. ex: \"tony@starkbank.com\", \"722.461.430-04\", \"20.018.183/0001-80\", \"+5511988887777\", \"b6295ee1-f054-47d1-9e90-ee57b74f60d9\"

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - DictKey object with updated attributes"
  ([id]
    (java-to-clojure
      (DictKey/get id)))

  ([id, user]
    (java-to-clojure
      (DictKey/get
        id
        (#'starkbank.user/get-java-user user)))))
  
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
    (map java-to-clojure (DictKey/query)))

  ([params]
    (def java-params (clojure-query-to-java params))
    (map java-to-clojure (DictKey/query java-params)))

  ([params, user] 
    (def java-params (clojure-query-to-java params))
    (map java-to-clojure (DictKey/query java-params (#'starkbank.user/get-java-user user)))))

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
    (def key-page (DictKey/page))
    (def cursor (.cursor key-page))
    (def keys (map java-to-clojure (.keys key-page)))
    {:keys keys, :cursor cursor})

  ([params]
    (def java-params (clojure-page-to-java params))
    (def key-page (DictKey/page java-params))
    {:keys (map java-to-clojure (.keys key-page)), :cursor (.cursor key-page)})

  ([params, user] 
    (def java-params (clojure-page-to-java params))
    (def key-page (DictKey/page java-params (#'starkbank.user/get-java-user user)))
    {:keys (map java-to-clojure (.keys key-page)), :cursor (.cursor key-page)}))
