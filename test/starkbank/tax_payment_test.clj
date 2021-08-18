(ns starkbank.tax-payment-test
  (:use [clojure.test])
  (:require [starkbank.tax-payment :as payment]
            [starkbank.tax-payment.log :as log]
            [starkbank.user-test :as user]
            [clojure.java.io :as io]
            [starkbank.utils.date :as date]
            [starkbank.utils.page :as page]))

(deftest create-get-pdf-delete-tax-payments
  (testing "create, get, pdf and delete tax payments"
    (user/set-test-project)
    (def payments (payment/create
      [{
        :amount 100
        :description "testing clojure"
        :bar-code (str "8566000" (format "%08d" (rand-int 100000000)) "00640074119002551100010601813")
        :scheduled (date/future-date)
        :tags ["testing" "clojure"]
      }]))
    (payment/get (:id (first payments)))
    (def file-name "temp/tax-payment.pdf")
    (io/make-parents file-name)
    (io/copy (payment/pdf (:id (first payments))) (io/file file-name))
    (payment/delete (:id (first payments)))))

(deftest query-tax-payments
  (testing "query tax payments"
    (user/set-test-project)
    (def payments (take 200 (payment/query {:limit 3})))
    (is (= 3 (count payments)))))

(deftest page-tax-payments
  (testing "page tax-payments"
    (user/set-test-project)
    (def get-page (fn [params] (payment/page params)))
    (def ids (page/get-ids get-page 2 {:limit 2}))
    (is (= 4 (count ids)))))

(deftest query-get-tax-payment-logs
  (testing "query and get tax payment logs"
    (user/set-test-project)
    (def payment-logs (log/query {:limit 1}))
    (is (= 1 (count payment-logs)))
    (def payment-log (log/get (:id (first payment-logs))))
    (is (not (nil? (:id payment-log))))
    (is (not (nil? (:errors payment-log))))
    (is (string? (:created payment-log)))
    (is (map? (:payment payment-log)))))

(deftest page-tax-payment-logs
  (testing "page tax-payment-logs"
    (user/set-test-project)
    (def get-page (fn [params] (log/page params)))
    (def ids (page/get-ids get-page 2 {:limit 2}))
    (is (= 4 (count ids)))))
