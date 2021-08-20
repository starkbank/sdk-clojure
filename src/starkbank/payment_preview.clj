(ns starkbank.payment-preview
  "A PaymentPreview is used to get information from a payment code you received to check the information before paying it.
  This resource can be used to preview BR Codes and bar codes of boleto, tax and utility payments

  ## Parameters (required):
    - `:id` [string]: Main identification of the payment. This should be the BR Code for Pix payments and lines or bar codes for payment slips. ex: \"34191.09008 63571.277308 71444.640008 5 81960000000062\", \"00020126580014br.gov.bcb.pix0136a629532e-7693-4846-852d-1bbff817b5a8520400005303986540510.005802BR5908T'Challa6009Sao Paulo62090505123456304B14A\"

  ## Parameters (optional):
    - `:scheduled` [string, default now]: intended payment date. Right now, this parameter only has effect on brcode-previews. ex: \"2021-03-11\"

  Attributes (return-only):
  - type [string]: Payment type. ex: \"brcode-payment\", \"boleto-payment\", \"utility-payment\" or \"tax-payment\"
  - payment [brcode-preview, boleto-preview, utility-preview or tax-preview]: Information preview of the informed payment."
  
  (:refer-clojure :exclude [get set])
  (:require [starkbank.brcode-preview :as brcode-preview])
  (:import [com.starkbank PaymentPreview])
  (:use [starkbank.user]
        [clojure.walk]))

(defn- clojure-to-java
  ([clojure-map]
    (let [{
      id "id"
      scheduled "scheduled"
    }
    (stringify-keys clojure-map)]
      (PaymentPreview. (java.util.HashMap.
        {
          "id" id
          "scheduled" scheduled
        }
      )))))

(defn- java-to-clojure
  ([java-object]
    {
      :id (.id java-object)
      :scheduled (.scheduled java-object)
      :type (.type java-object)
      :payment (into (sorted-map) (.payment java-object))
    }))

(defn create
  "Send a list of PaymentPreviews maps for creation in the Stark Bank API

  ## Parameters (required):
    - `previews` [list of PaymentPreview maps]: list of PaymentPreview maps to be created in the API

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - list of PaymentPreview maps with updated attributes"
  ([previews]
    (def java-previews (map clojure-to-java previews))
    (def created-java-previews (PaymentPreview/create java-previews))
    (map java-to-clojure created-java-previews))

  ([previews, user]
    (def java-previews (map clojure-to-java previews))
    (def created-java-previews (PaymentPreview/create java-previews (#'starkbank.user/get-java-user user)))
    (map java-to-clojure created-java-previews)))
