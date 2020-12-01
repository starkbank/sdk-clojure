(ns starkbank.invoice-test
  (:use [clojure.test])
  (:require [starkbank.invoice :as invoice]
            [starkbank.invoice.log :as log]
            [starkbank.user-test :as user]
            [clojure.java.io :as io]
            [starkbank.utils.date :as date]))

(deftest create-get-pdf-update-invoices
  (testing "create, get, pdf and update invoices"
    (user/set-test-user)
    (def invoices (invoice/create
      [{
        :amount 400000
        :due (date/future-datetime 5)
        :tax-id "012.345.678-90"
        :name "Iron Bank S.A.",
        :expiration 123456789,
        :fine 2.5,
        :interest 1.3,
        :discounts [
          {
            :percentage 5
            :due (date/future-datetime 2)
          }
          {
            :percentage 3
            :due (date/future-datetime 4)
          }
        ]
        :descriptions [
          {
            :key "Product X"
            :value "big"
          }
        ]
        :tags [
          "War supply",
          "Invoice #1234"
        ]
      }]))
    (invoice/get (:id (first invoices)))
    (def file-name "temp/invoice.pdf")
    (io/make-parents file-name)
    (io/copy (invoice/pdf (:id (first invoices))) (io/file file-name))
    (invoice/update (:id (first invoices)) {:amount 10 :expiration 600 :due (date/future-datetime 10)})))

(deftest query-and-cancel-invoices
  (testing "query and cancel invoices"
    (user/set-test-user)
    (def invoices (take 200 (invoice/query {:limit 50 :status "created"})))
    (is (= 50 (count invoices)))
    (invoice/update (:id (rand-nth invoices)) {:status "canceled"})))

(deftest qrcode
  (testing "qrcode"
    (user/set-test-user)
    (def invoices (take 200 (invoice/query {:limit 1})))
    (def qrcode (invoice/qrcode (:id (first invoices))))
    (is (< 1000 (.available qrcode)))
    (def file-name "temp/invoice-qrcode.png")
    (io/make-parents file-name)
    (io/copy qrcode (io/file file-name))))

(deftest query-get-invoice-logs
  (testing "query and get invoice logs"
    (user/set-test-user)
    (def invoice-logs (log/query {:limit 1}))
    (is (= 1 (count invoice-logs)))
    (def invoice-log (log/get (:id (first invoice-logs))))
    (is (not (nil? (:id invoice-log))))
    (is (not (nil? (:errors invoice-log))))
    (is (string? (:created invoice-log)))
    (is (map? (:invoice invoice-log)))))
