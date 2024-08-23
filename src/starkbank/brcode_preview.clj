(ns starkbank.brcode-preview
  "DEPRECATED
    A BrcodePreview is used to get information from a BR Code you received to check the informations before the payment.
  
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
      - `:amount` [integer]: Value in cents that this payment is expecting to receive. If 0, any value is accepted. ex: 123 (= R$1,23)
      - `:reconciliation-id` [string]: Reconciliation ID linked to this payment. ex: \"tx-id\", \"payment-123\""
  (:refer-clojure :exclude [get set update])
  (:require [starkbank.utils.rest :refer [get-stream]]
            [starkbank.settings :refer [credentials]]))

(defn- resource []
  "brcode-preview")

(defn query
  "Receive a stream of BrcodePreview maps previously created in the Stark Bank API

  ## Options:
    - `:brcodes` [list of strings]: List of brcodes to preview. ex: [\"00020126580014br.gov.bcb.pix0136a629532e-7693-4846-852d-1bbff817b5a8520400005303986540510.005802BR5908T'Challa6009Sao Paulo62090505123456304B14A\"]
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - stream of BrcodePreview maps with updated attributes"

  ([params]
    (-> (get-stream @credentials (resource) params)))

  ([params, user] 
    (-> (get-stream user (resource) params))))
