(ns starkbank.utility-payment-test
  (:use [clojure.test])
  (:require [clojure.java.io :as io]
            [starkbank.utility-payment :as payment]
            [starkbank.utility-payment.log :as log]
            [starkbank.utils.date :as date]
            [starkbank.utils.page :as page]
            [starkbank.utils.user :refer [set-project]]))

(set-project)

(deftest create-get-pdf-delete-utility-payments
  (testing "create, get, pdf and delete utility payments"
    (def payments (payment/create
      [{
        :description "testing clojure"
        :bar-code (str "8364000" (format "%08d" (rand-int 100000000)) "01380076105302611108067159411")
        :scheduled (date/future-date)
        :tags ["testing" "clojure"]
      }]))
    (payment/get (:id (first payments)))
    (def file-name "temp/utility-payment.pdf")
    (io/make-parents file-name)
    (io/copy (payment/pdf (:id (first payments))) (io/file file-name))
    (payment/delete (:id (first payments)))))

(deftest query-utility-payments
  (testing "query utility payments"
    (def payments (take 200 (payment/query {:limit 3})))
    (is (= 3 (count payments)))))

(deftest page-utility-payments
  (testing "page utility-payments"
    (def get-page (fn [params] (payment/page params)))
    (def ids (page/get-ids get-page 2 {:limit 2}))
    (is (= 4 (count ids)))))

(deftest query-get-utility-payment-logs
  (testing "query and get utility payment logs"
    (def payment-logs (log/query {:limit 1}))
    (is (= 1 (count payment-logs)))
    (def payment-log (log/get (:id (first payment-logs))))
    (is (not (nil? (:id payment-log))))
    (is (not (nil? (:errors payment-log))))
    (is (string? (:created payment-log)))
    (is (map? (:payment payment-log)))))

(deftest page-utility-payment-logs
  (testing "page utility-payment-logs"
    (def get-page (fn [params] (log/page params)))
    (def ids (page/get-ids get-page 2 {:limit 2}))
    (is (= 4 (count ids)))))
