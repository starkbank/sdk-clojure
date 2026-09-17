(ns starkbank.verified-transfer
  "When you initialize a VerifiedTransfer map, the entity will not be automatically
  created in the Stark Bank API. The 'create' function sends the maps
  to the Stark Bank API and returns the list of created maps.

  ## Parameters (required):
    - `:amount` [integer]: transfer value in cents. ex: 1234 (= R$ 12.34)
    - `:account-id` [string]: receiver's VerifiedAccount ID. ex: \"5656565656565656\"

  ## Parameters (optional):
    - `:external-id` [string, default nil]: url safe string that must be unique among all your transfers. Duplicated external-ids will cause failures. By default, this parameter will block any transfer that repeats amount and receiver information on the same date. ex: \"my-internal-id-123456\"
    - `:scheduled` [string, default now]: date or datetime when the transfer will be processed. May be pushed to next business day if necessary. ex: \"2020-11-12T00:14:22.806+00:00\" or \"2020-11-30\"
    - `:description` [string, default nil]: optional description to override default description to be shown in the bank statement. ex: \"Payment for service #1234\"
    - `:display-description` [string, default nil]: optional description to be shown in the receiver bank interface. ex: \"Payment for service #1234\"
    - `:tags` [list of strings, default nil]: list of strings for reference when searching for verified transfers. ex: [\"employees\" \"monthly\"]
    - `:rules` [list of maps, default nil]: list of rule maps for modifying VerifiedTransfer behavior, each with a :key and :value pair. ex: [{:key \"resendingLimit\" :value 5}]

  ## Attributes (return-only):
    - `:id` [string, default nil]: unique id returned when the VerifiedTransfer is created. ex: \"5656565656565656\"
    - `:fee` [integer, default nil]: fee charged when the transfer is created. ex: 200 (= R$ 2.00)
    - `:status` [string, default nil]: current verified transfer status. ex: \"created\", \"processing\", \"success\" or \"failed\"
    - `:transaction-ids` [list of strings, default nil]: ledger transaction ids linked to this transfer (if there are two, second is the chargeback). ex: [\"19827356981273\"]
    - `:metadata` [map, default nil]: map used to store additional information about the VerifiedTransfer.
    - `:created` [string, default nil]: creation datetime for the verified transfer. ex: \"2020-03-26T19:32:35.418698+00:00\"
    - `:updated` [string, default nil]: latest update datetime for the verified transfer. ex: \"2020-03-26T19:32:35.418698+00:00\"

  NOTE: the API does not accept an :account-type field for VerifiedTransfer (sending it returns
  an \"invalidJson: Unknown parameters in transfer: accountType\" error), so this field is
  intentionally not part of the map, even though it is mistakenly documented in some other SDKs."
  (:require [starkbank.utils.rest :refer [post-multi]]
            [starkbank.settings :refer [credentials]]))

(defn- resource []
  "verified-transfer")

(defn create
  "Send a list of VerifiedTransfer maps for creation in the Stark Bank API.

  ## Parameters (required):
    - `verified-transfers` [list of VerifiedTransfer maps]: list of VerifiedTransfer maps to be created in the API

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - list of VerifiedTransfer maps with updated attributes"
  ([verified-transfers]
    (-> (post-multi @credentials (resource) verified-transfers {})))

  ([verified-transfers, user]
    (-> (post-multi user (resource) verified-transfers {}))))
