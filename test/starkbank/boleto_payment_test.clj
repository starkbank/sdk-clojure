(ns starkbank.boleto-payment-test
  (:use [clojure.test])
  (:require [starkbank.boleto-payment :as payment]
            [starkbank.boleto-payment.log :as log]
            [starkbank.user-test :as user]
            [clojure.java.io :as io]))

(deftest create-get-pdf-delete-boleto-payments
  (testing "create, get, pdf and delete boleto payments"
    (user/set-test-user)
    (def payments (payment/create
      [{
        :amount 100
        :tax-id "012.345.678-90"
        :description "testing clojure"
        :line (str "34191.09107 05447.947309 71444.640008 8 900000" (format "%08d" (rand-int 100000000)))
        :scheduled "2022-05-30"
        :tags ["testing" "clojure"]
      }]))
    (payment/get (:id (first payments)))
    (def file-name "temp/boleto-payment.pdf")
    (io/make-parents file-name)
    (io/copy (payment/pdf (:id (first payments))) (io/file file-name)))
    (payment/delete (:id (first payments))))

(deftest query-boleto-payments
  (testing "query boleto payments"
    (user/set-test-user)
    (def payments (take 200 (payment/query {:limit 3})))
    (is (= 3 (count payments)))))

(deftest query-get-boleto-payment-logs
  (testing "query and get boleto payment logs"
    (user/set-test-user)
    (def payment-logs (log/query {:limit 1}))
    (is (= 1 (count payment-logs)))
    (def payment-log (log/get (:id (first payment-logs))))
    (is (not (nil? (:id payment-log))))
    (is (not (nil? (:errors payment-log))))
    (is (string? (:created payment-log)))
    (is (map? (:payment payment-log)))))
