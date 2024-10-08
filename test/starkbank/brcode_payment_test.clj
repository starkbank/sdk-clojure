(ns starkbank.brcode-payment-test
    (:use [clojure.test])
    (:require [clojure.java.io :as io]
              [starkbank.brcode-payment :as payment]
              [starkbank.brcode-payment.log :as log]
              [starkbank.invoice :as invoice] 
              [starkbank.utils.date :as date]
              [starkbank.utils.page :as page]
              [starkbank.utils.user :refer [set-project]]))

(set-project)

(deftest create-get-brcode-payments
  (testing "create, get, pdf and update brcode payments"
    (def invoices (invoice/create
                   [{:amount 40000
                     :due (date/future-datetime 5)
                     :tax-id "012.345.678-90"
                     :name "Iron Bank S.A.",
                     :expiration 123456789,
                     :fine 2.5,
                     :interest 1.3,
                     :discounts [{:percentage 5
                                  :due (date/future-datetime 2)}
                                 {:percentage 3
                                  :due (date/future-datetime 4)}]
                     :descriptions [{:key "Product X"
                                     :value "big"}]
                     :tags ["War supply",
                            "Invoice #1234"]}]
                   ))
    (def invoice (first invoices))

    (def payments (payment/create
                   [{:brcode (:brcode invoice)
                     :tax-id "20.018.183/0001-80"
                     :description "Tony Stark's Suit"
                     :amount (long (:amount invoice))
                     :scheduled (date/future-datetime 3)
                     :tags ["Stark" "Suit"]}]))
    (def assertion (payment/get (:id (first payments))))
    (is(= (:id assertion (:id (first payments)))))
    ))

(deftest query-pdf-cancel-brcode-payments
  (testing "query brcode payments"
    (def payments (take 200 (payment/query {:limit 2 :status "created"})))
    (is (= 2 (count payments)))
    (def file-name "temp/brcode-payment.pdf")
    (io/make-parents file-name)
    (io/copy (payment/pdf (:id (first payments))) (io/file file-name)))
    (payment/update (:id (first payments)) {:status "canceled"}))

(deftest page-brcode-payment
  (testing "page brcode-payment"
    (def get-page (fn [params] (payment/page params)))
    (def ids (page/get-ids get-page 2 {:limit 2}))
    (is (= 4 (count ids)))))

(deftest query-get-brcode-payment-logs
  (testing "query and get brcode payment logs"
    (def payment-logs (log/query {:limit 5}))
    (is (= 5 (count payment-logs)))
    (def payment-log (log/get (:id (first payment-logs))))
    (is (not (nil? (:id payment-log))))
    (is (not (nil? (:errors payment-log))))
    (is (string? (:created payment-log)))
    (is (map? (:payment payment-log)))))

(deftest page-brcode-payment-logs
  (testing "page brcode-payment-logs"
    (def get-page (fn [params] (log/page params)))
    (def ids (page/get-ids get-page 2 {:limit 2}))
    (is (= 4 (count ids)))))
