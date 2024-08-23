(ns starkbank.tax-payment-test
  (:use [clojure.test])
  (:require [clojure.java.io :as io]
            [starkbank.tax-payment :as payment]
            [starkbank.tax-payment.log :as log]
            [starkbank.utils.date :as date]
            [starkbank.utils.page :as page]
            [starkbank.utils.user :refer [set-project]]))

(set-project)

(deftest create-get-pdf-delete-tax-payments
  (testing "create, get, pdf and delete tax payments"
    (def payments (payment/create
      [{
        :description "testing clojure"
        :bar-code (str "8566000" (format "%08d" (rand-int 100000000)) "00640074119002551100010601813")
        :scheduled (date/future-date)
        :tags ["testing" "clojure"]
      }]))
    (payment/get (:id (first payments)))
    (def file-name "temp/tax-payment.pdf")
    (io/make-parents file-name)
    (io/copy (payment/pdf (:id (first payments))) (io/file file-name))
    (payment/delete (:id (first payments)))
    
    ))

(deftest query-tax-payments
  (testing "query tax payments"
    (def payments (take 200 (payment/query {:limit 3})))
    (is (= 3 (count payments)))))

(deftest page-tax-payments
  (testing "page tax-payments"
    (def get-page (fn [params] (payment/page params)))
    (def ids (page/get-ids get-page 2 {:limit 2}))
    (is (= 4 (count ids)))))

(deftest query-get-tax-payment-logs
  (testing "query and get tax payment logs"
    (def payment-logs (log/query {:limit 1}))
    (is (= 1 (count payment-logs)))
    (def payment-log (log/get (:id (first payment-logs))))
    (is (not (nil? (:id payment-log))))
    (is (not (nil? (:errors payment-log))))
    (is (string? (:created payment-log)))
    (is (map? (:payment payment-log)))))

(deftest page-tax-payment-logs
  (testing "page tax-payment-logs"
    (def get-page (fn [params] (log/page params)))
    (def ids (page/get-ids get-page 2 {:limit 2}))
    (is (= 4 (count ids)))))
