(ns starkbank.payment-request-test
  (:use [clojure.test])
  (:refer-clojure :exclude [type])
  (:require [core-clojure.utils.rest :as rest]
            [starkbank.invoice :as invoice]
            [starkbank.payment-request :as payment-request]
            [starkbank.utils.date :as date]
            [starkbank.utils.page :as page]
            [starkbank.utils.user :refer [set-project]]))

(set-project)
  
(def cost-center 
  (System/getenv "SANDBOX_ID"); "9999999999999999"
  )

(deftest create-payment-requests
  (testing "create payment-requests"
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
          :brcode (:brcode (first (invoice/query {:limit 1})))
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
        :center-id cost-center
        :tags ["testing" "clojure"]
        :due (date/future-date)
      }]))))

(deftest query-payment-requests
  (testing "query payment-requests"
    (def payment-requests (take 200 (payment-request/query {
                                                            :limit 3
                                                            :status ["success"]
                                                            :center-id cost-center})))
    (is (= 3 (count payment-requests)))
    (is (map? (first payment-requests)))))

(deftest page-payment-request
  (testing "page payment-request"
    (def get-page (fn [params] (payment-request/page params)))
    (def ids (page/get-ids get-page 2 {:limit 2 :center-id cost-center}))
    (println ids)
    (is (= 4 (count ids)))))
