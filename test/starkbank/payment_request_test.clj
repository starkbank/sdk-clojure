(ns starkbank.payment-request-test
  (:use [clojure.test])
  (:refer-clojure :exclude [type])
  (:require [starkbank.payment-request :as payment-request]
            [starkbank.user-test :as user]
            [clojure.java.io :as io]
            [starkbank.utils.date :as date]))

(deftest create-payment-requests
  (testing "create payment-requests"
    (user/set-test-project)
    (def type (rand-nth ["transfer", "brcode-payment", "boleto-payment", "utility-payment"]))
    (def payment (case type
      "transfer" {
          :amount 200
          :name "Dumlocks von z'Blurbows"
          :tax-id "012.345.678-90"
          :bank-code "60701190"
          :branch-code "0001"
          :account-number "00000-0"
          :tags ["testing" "clojure"]
        }
      "brcode-payment" {
          :tax-id "012.345.678-90"
          :description "testing clojure"
          :brcode "00020101021226890014br.gov.bcb.pix2567invoice-h.sandbox.starkbank.com/v2/d5b00b1994454706ba90a0387ff39b7952040000530398654040.005802BR5925Afel Tec Servicos Adminis6009Sao Paulo62070503***630475CE"
          :tags ["testing" "clojure"]
        }
      "boleto-payment" {
          :amount 100
          :tax-id "012.345.678-90"
          :description "testing clojure"
          :line (str "34191.09107 05447.947309 71444.640008 8 900000" (format "%08d" (rand-int 20000000)))
          :tags ["testing" "clojure"]
        }
      "utility-payment" {
          :amount 100
          :description "testing clojure"
          :bar-code (str "8364000" (format "%08d" (rand-int 20000000)) "01380076105302611108067159411")
          :tags ["testing" "clojure"]
        }))

    (def payment-requests (payment-request/create
      [{
        :type type
        :payment payment
        :center-id (System/getenv "SANDBOX_CENTER_ID")
        :tags ["testing" "clojure"]
        :due (date/future-date)
      }]))))

(deftest query-payment-requests
  (testing "query payment-requests"
    (user/set-test-project)
    (def payment-requests (take 200 (payment-request/query {
      :limit 3
      :status ["success"]
      :center-id (System/getenv "SANDBOX_CENTER_ID")})))
    (is (= 3 (count payment-requests)))
    (is (map? (first payment-requests)))))
