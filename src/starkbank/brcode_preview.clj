(ns starkbank.brcode-preview
    "A BrcodePreview is used to get information from a BR Code you received to check the informations before the payment.
  
    ## Parameters (required):
      - `:id` [string]: BrcodePreview object unique id and PIX key itself. ex: \"tony@starkbank.com\", \"722.461.430-04\", \"20.018.183/0001-80\", \"+5511988887777\", \"b6295ee1-f054-47d1-9e90-ee57b74f60d9\"
  
    ## Attributes (return-only): 
      - `:status` [string]: Payment status. ex: \"active\", \"paid\", \"canceled\" or \"unknown\"
      - `:name` [string]: Payment receiver name. ex: \"Tony Stark\"
      - `:tax-id` [string]: Payment receiver tax ID. ex: \"012.345.678-90\"
      - `:bank-code` [string]: Payment receiver bank code. ex: \"20018183\"
      - `:branch-code` [string]: Payment receiver branch code. ex: \"0001\"
      - `:account-number` [string]: Payment receiver account number. ex: \"1234567\"
      - `:account-type` [string]: Payment receiver account type. ex: \"checking\"
      - `:allow-change` [bool]: If True, the payment is able to receive amounts that are diferent from the nominal one. ex: True or False
      - `:amount` [long]: Value in cents that this payment is expecting to receive. If 0, any value is accepted. ex: 123 (= R$1,23)
      - `:reconciliation-id` [string]: Reconciliation ID linked to this payment. ex: \"tx-id\", \"payment-123\""
    (:refer-clojure :exclude [get set update])
    (:import [com.starkbank BrcodePreview])
    (:use [starkbank.user]
          [clojure.walk]))

(defn- java-to-clojure
  ([java-object]
    {
      :id (.id java-object)
      :status (.status java-object)
      :name (.name java-object)
      :tax-id (.taxId java-object)
      :bank-code (.bankCode java-object)
      :branch-code (.branchCode java-object)
      :account-number (.accountNumber java-object)
      :account-type (.accountType java-object)
      :allow-change (.allowChange java-object)
      :amount (.amount java-object)
      :reconciliation-id (.reconciliationId java-object)
    }))

(defn- clojure-query-to-java
  ([clojure-map]
    (let [{
        brcodes "brcodes"
      } (stringify-keys clojure-map)]
      (java.util.HashMap.
        {
          "brcodes" (into-array String brcodes)
        }
      ))))
  
(defn query
  "Receive a stream of BrcodePreview maps previously created in the Stark Bank API

  ## Options:
    - `:brcodes` [list of strings]: List of brcodes to preview. ex: [\"00020126580014br.gov.bcb.pix0136a629532e-7693-4846-852d-1bbff817b5a8520400005303986540510.005802BR5908T'Challa6009Sao Paulo62090505123456304B14A\"]
    - `:user` [Project]: Project map returned from starkbank.user/project. Only necessary if starkbank.user/set has not been set.

  ## Return:
    - stream of BrcodePreview maps with updated attributes"

  ([params]
    (def java-params (clojure-query-to-java params))
    (map java-to-clojure (BrcodePreview/query java-params)))

  ([params, user] 
    (def java-params (clojure-query-to-java params))
    (map java-to-clojure (BrcodePreview/query java-params (#'starkbank.user/get-java-project user)))))
