(ns starkbank.invoice-test
  (:use [clojure.test]) 
  (:require [clojure.java.io :as io]
            [starkbank.invoice :as invoice]
            [starkbank.invoice.log :as log]
            [starkbank.utils.date :as date]
            [starkbank.utils.page :as page]
            [starkbank.utils.user :refer [set-project]]))

(set-project)

(deftest create-get-pdf-update-invoices
  (testing "create, get, pdf and update invoices"
    (def invoices (invoice/create
      [{
        :amount 400000
        :due (date/future-datetime 5)
        :tax-id "012.345.678-90"
        :name "Iron Bank S.A."
        :expiration 123456789
        :fine 2.5
        :interest 1.3
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
      }
      {
        :amount 400000
        :tax-id "012.345.678-90"
        :name "Iron Bank S.A."
        :expiration 123456789
        :fine 2
        :interest 1.3
        :due (date/future-date 2)
        :discounts [
          {
            :percentage 2.5
            :due (date/future-date 2)
          }
        ]
      }
      ]))
    (doseq [invoice invoices]
      (is (= (:id invoice) (:id (invoice/get (:id invoice))))))
    (def file-name "temp/invoice.pdf")
    (io/make-parents file-name)
    (io/copy (invoice/pdf (:id (first invoices))) (io/file file-name))
    (invoice/update (:id (first invoices)) {:amount 10 :expiration 600 :due (date/future-datetime 10)})))

(deftest query-and-cancel-invoices
  (testing "query and cancel invoices"
    
    (def invoices (take 200 (invoice/query {:limit 50 :status "created"})))
    (is (= 50 (count invoices)))
    (invoice/update (:id (rand-nth invoices)) {:status "canceled"})))

(deftest page-invoices
  (testing "page invoices"
    
    (def get-page (fn [params] (invoice/page params)))
    (def ids (page/get-ids get-page 2 {:limit 2}))
    (is (= 4 (count ids)))))

(deftest query-qrcode-payment
  (testing "query, get qrcode and get payment information"
    
    (def invoices (take 200 (invoice/query {:limit 1 :status "paid"})))
    (def qrcode (invoice/qrcode (:id (first invoices))))
    (is (< 1000 (.length qrcode)))
    (def file-name "temp/invoice-qrcode.png")
    (io/make-parents file-name)
    (io/copy qrcode (io/file file-name))
    (def payment (invoice/payment (:id (first invoices))))))

(deftest query-get-invoice-logs
  (testing "query and get invoice logs"
    
    (def invoice-logs (log/query {:limit 1 :types ["reversed"]}))
    (is (= 1 (count invoice-logs)))
    (def invoice-log (log/get (:id (first invoice-logs))))
    (is (not (nil? (:id invoice-log))))
    (is (not (nil? (:errors invoice-log))))
    (is (string? (:created invoice-log)))
    (is (map? (:invoice invoice-log)))
    (def file-name "temp/invoice-log.pdf")
    (io/make-parents file-name)
    (io/copy (log/pdf (:id invoice-log)) (io/file file-name))))

(deftest page-invoice-logs
  (testing "page invoice-logs"
    
    (def get-page (fn [params] (invoice/page params)))
    (def ids (page/get-ids get-page 2 {:limit 2}))
    (is (= 4 (count ids)))))
