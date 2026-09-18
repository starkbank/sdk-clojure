(ns starkbank.merchant-purchase
  "The MerchantPurchase resource can be used to charge customers with credit or debit cards. A card that has
  never been used before must first go through an approved MerchantSession Purchase before it can be charged
  directly through a MerchantPurchase.

  ## Parameters (required):
    - `:card-id` [string]: id of the MerchantCard to be charged. ex: \"5656565656565656\"
    - `:amount` [integer]: amount in cents to be received. ex: 100 (= R$ 1.00)
    - `:funding-type` [string]: funding type used for the purchase. ex: \"credit\" or \"debit\"

  ## Parameters (conditionally required):
    - `:billing-city` [string, default nil]: card holder's billing city. Required when :challenge-mode is \"enabled\". ex: \"Sao Paulo\"
    - `:billing-country-code` [string, default nil]: card holder's billing country code. Required when :challenge-mode is \"enabled\". ex: \"BRA\"
    - `:billing-state-code` [string, default nil]: card holder's billing state code. Required when :challenge-mode is \"enabled\". ex: \"SP\"
    - `:billing-street-line-1` [string, default nil]: card holder's billing street line. Required when :challenge-mode is \"enabled\". ex: \"Rua do Holder Name, 123\"
    - `:billing-street-line-2` [string, default nil]: card holder's billing street line complement. Required when :challenge-mode is \"enabled\". ex: \"Apto 12\"
    - `:billing-zip-code` [string, default nil]: card holder's billing zip code. Required when :challenge-mode is \"enabled\". ex: \"01234-567\"
    - `:holder-email` [string, default nil]: card holder's email. Required when :challenge-mode is \"enabled\". ex: \"tony@starkbank.com\"
    - `:holder-phone` [string, default nil]: card holder's phone. Required when :challenge-mode is \"enabled\". ex: \"11111111111\"
    - `:metadata` [map, default nil]: must include :user-agent, :timezone-offset, :user-ip and :language for the 3DS challenge when :challenge-mode is \"enabled\".

  ## Parameters (optional):
    - `:holder-name` [string, default nil]: card holder's name. ex: \"Tony Stark\"
    - `:challenge-mode` [string, default \"enabled\"]: whether 3DS holder verification is used. ex: \"enabled\" or \"disabled\"
    - `:installment-count` [integer, default 1]: number of purchase installments. ex: 12
    - `:tags` [list of strings, default nil]: list of strings for tagging. ex: [\"travel\", \"food\"]

  ## Attributes (return-only):
    - `:id` [string, default nil]: unique id returned when the MerchantPurchase is created. ex: \"5656565656565656\"
    - `:holder-id` [string, default nil]: id of the MerchantCard's holder. ex: \"5656565656565656\"
    - `:card-ending` [string, default nil]: last 4 digits of the charged card number. ex: \"1234\"
    - `:challenge-url` [string, default nil]: URL of the 3DS holder verification challenge, when :challenge-mode is \"enabled\". ex: \"https://sandbox.api.starkbank.com/v2/merchant-challenge/5656565656565656\"
    - `:currency-code` [string, default nil]: currency in which the MerchantPurchase was charged. ex: \"BRL\"
    - `:end-to-end-id` [string, default nil]: unique transaction id given by the card network. ex: \"db6ff17f-e6c7-4d78-b8ba-c19e28aef7a5\"
    - `:fee` [integer, default nil]: fee charged by this MerchantPurchase. ex: 200 (= R$ 2.00)
    - `:network` [string, default nil]: card network. ex: \"visa\"
    - `:source` [string, default nil]: source of the MerchantPurchase. ex: \"merchant-purchase\"
    - `:status` [string, default nil]: current MerchantPurchase status. ex: \"created\", \"approved\", \"denied\", \"confirmed\", \"paid\", \"pending\", \"canceled\", \"voided\" or \"failed\"
    - `:soft-descriptor` [string, default nil]: text that will be shown in the card holder's bank statement. ex: \"starkbank\"
    - `:created` [string, default nil]: creation datetime for the MerchantPurchase. ex: \"2020-03-10T10:30:00.000000+00:00\"
    - `:updated` [string, default nil]: latest update datetime for the MerchantPurchase. ex: \"2020-03-10T10:30:00.000000+00:00\""
  (:refer-clojure :exclude [get update])
  (:require [starkbank.utils.rest :refer [get-id get-page get-stream
                                          patch-id post-single]]
            [starkbank.settings :refer [credentials]]))

(defn- resource []
  "merchant-purchase")

(defn create
  "Charge a card that has been previously saved via an approved MerchantSession Purchase. The card must already
  have an approved MerchantSession Purchase before it can be charged here.

  ## Parameters (required):
    - `purchase` [MerchantPurchase map]: MerchantPurchase map to be created in the API

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - MerchantPurchase map with updated attributes"
  ([purchase]
   (-> (post-single @credentials (resource) purchase {})))

  ([purchase, user]
   (-> (post-single user (resource) purchase {}))))

(defn get
  "Receive a single MerchantPurchase map previously created in the Stark Bank API by passing its id

  ## Parameters (required):
    - `:id` [string]: map unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - MerchantPurchase map with updated attributes"
  ([id]
   (-> (get-id @credentials (resource) id {})))

  ([id, user]
   (-> (get-id user (resource) id {}))))

(defn query
  "Receive a stream of MerchantPurchase maps previously created in the Stark Bank API.
  Use this function instead of page if you want to stream the objects without worrying about cursors and pagination.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-3-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-3-10\"
    - `:status` [string, default nil]: filter for status of retrieved maps. ex: \"created\", \"approved\", \"denied\", \"confirmed\", \"paid\", \"pending\", \"canceled\", \"voided\" or \"failed\"
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:holder-id` [string, default nil]: filter for purchases made with a specific MerchantCard's holder id. ex: \"5656565656565656\"
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - stream of MerchantPurchase maps with updated attributes"
  ([]
   (-> (get-stream @credentials (resource) {})))

  ([params]
   (-> (get-stream @credentials (resource) params)))

  ([params, user]
   (-> (get-stream user (resource) params))))

(defn page
  "Receive a list of up to 100 MerchantPurchase maps previously created in the Stark Bank API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-3-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-3-10\"
    - `:status` [string, default nil]: filter for status of retrieved maps. ex: \"created\", \"approved\", \"denied\", \"confirmed\", \"paid\", \"pending\", \"canceled\", \"voided\" or \"failed\"
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:holder-id` [string, default nil]: filter for purchases made with a specific MerchantCard's holder id. ex: \"5656565656565656\"
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - map with :purchases and :cursor:
      - `:purchases`: list of MerchantPurchase maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page of purchases"
  ([]
   (-> (get-page @credentials (resource) {})))

  ([params]
   (-> (get-page @credentials (resource) params)))

  ([params, user]
   (-> (get-page user (resource) params))))

(defn update
  "Update a MerchantPurchase by passing its id. If the purchase is \"approved\", only amount=0 with
  status=\"canceled\" is allowed, which cancels the authorization. If the purchase is \"confirmed\", status can be
  set to \"reversed\" with a lower amount, which debits and reverses the difference (partially or totally); a
  partial reversal leaves status \"confirmed\", a full reversal moves it to \"voided\".

  ## Parameters (required):
    - `:id` [string]: MerchantPurchase id. ex: \"5656565656565656\"

  ## Parameters (optional):
    - `:amount` [integer, default nil]: new amount; 0 to cancel an approved purchase, or a lower value to partially/fully reverse a confirmed one. ex: 200 (= R$ 2.00)
    - `:status` [string, default nil]: \"canceled\" (from approved) or \"reversed\" (from confirmed)
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - target MerchantPurchase with updated attributes"
  ([id, params]
   (-> (patch-id @credentials (resource) params id)))

  ([id, params, user]
   (-> (patch-id user (resource) params id))))
