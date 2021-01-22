(ns starkbank.dict-key
    "DictKey represents a PIX key registered in Bacen's DICT system.
  
    ## Parameters (optional):
      - `:id` [string, default nil]: DictKey object unique id and PIX key itself. ex: \"tony@starkbank.com\", \"722.461.430-04\", \"20.018.183/0001-80\", \"+5511988887777\", \"b6295ee1-f054-47d1-9e90-ee57b74f60d9\"

    ## Attributes (return-only):
      - `:type` [string, default nil]: PIX key type. ex: \"email\", \"cpf\", \"cnpj\", \"phone\" or \"evp\"
      - `:name` [string, default nil]: account owner full name. ex: \"Tony Stark\"
      - `:tax-id [string, default nil]: tax ID (CPF or CNPJ) with or without formatting. ex: \"01234567890\" or \"20.018.183/0001-80\"
      - `:owner-type` [string, default nil]: PIX key owner type. ex \"naturalPerson\" or \"legalPerson\"
      - `:ispb` [string, default nil]: ISPB code used for transactions. ex: \"20018183\"
      - `:branch-code` [string, default nil]: bank account branch code associated with the PIX key. ex: \"9585\"
      - `:account-number` [string, default nil]: bank account number associated with the PIX key. ex: \"9828282578010513\"
      - `:account-type` [string, default nil]: bank account type associated with the PIX key. ex: \"checking\", \"saving\" e \"salary\"
      - `:status` [string, default nil]: current PIX key status. ex: \"created\", \"registered\", \"canceled\" or \"failed\"
      - `:account-created` [string, default nil]: creation datetime of the bank account associated with the PIX key. ex: \"2020-11-05T14:55:08.812665+00:00\"
      - `:owned` [string, default nil]: datetime since when the current owner hold this PIX key. ex : \"2020-11-05T14:55:08.812665+00:00\"     
      - `:created` [string, default nil]: creation datetime for the PIX key. ex: \"2020-11-05T14:55:08.812665+00:00\""
    (:refer-clojure :exclude [get set update])
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
      :owner-type (.ownerType java-object)
      :ispb (.ispb java-object)
      :branch-code (.branchCode java-object)
      :account-number (.accountNumber java-object)
      :account-type (.accountType java-object)
      :status (.status java-object)
      :account-created (.accountCreated java-object)
      :owned (.owned java-object)
      :created (.created java-object)
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

(defn get
  "Receive a single DictKey by passing its id

  ## Parameters (required):
    - `id` [string]: DictKey object unique id and PIX key itself. ex: \"tony@starkbank.com\", \"722.461.430-04\", \"20.018.183/0001-80\", \"+5511988887777\", \"b6295ee1-f054-47d1-9e90-ee57b74f60d9\"

  ## Options:
    - `:user` [Project or Organization]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

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
  "Receive a stream of DictKey maps associated with your Stark Bank Workspace

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-3-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-3-10\"
    - `:type` [string, default None]: DictKey type. ex: \"cpf\", \"cnpj\", \"phone\", \"email\" or \"evp\"
    - `:status` [string, default nil]: filter for status of retrieved maps. ex: \"created\", \"paid\", \"canceled\" or \"overdue\"
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:user` [Project or Organization]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

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
