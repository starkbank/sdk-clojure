(ns starkbank.utility-payment-test
  (:use [clojure.test])
  (:require [starkbank.utility-payment :as payment]
            [starkbank.utility-payment.log :as log]
            [starkbank.user-test :as user]
            [clojure.java.io :as io]))

(deftest create-get-pdf-delete-utility-payments
  (testing "create, get, pdf and delete utility payments"
    (user/set-test-user)
    (def payments (payment/create
      [{
        :amount 100
        :description "testing clojure"
        :bar-code (str "8364000" (format "%08d" (rand-int 100000000)) "01380076105302611108067159411")
        :scheduled "2022-05-30"
        :tags ["testing" "clojure"]
      }]))
    (payment/get (:id (first payments)))
    (def file-name "temp/utility-payment.pdf")
    (io/make-parents file-name)
    (io/copy (payment/pdf (:id (first payments))) (io/file file-name))
    (payment/delete (:id (first payments)))))

(deftest query-utility-payments
  (testing "query utility payments"
    (user/set-test-user)
    (def payments (take 200 (payment/query {:limit 3})))
    (is (= 3 (count payments)))))

(deftest query-get-utility-payment-logs
  (testing "query and get utility payment logs"
    (user/set-test-user)
    (def payment-logs (log/query {:limit 1}))
    (is (= 1 (count payment-logs)))
    (def payment-log (log/get (:id (first payment-logs))))
    (is (not (nil? (:id payment-log))))
    (is (not (nil? (:errors payment-log))))
    (is (string? (:created payment-log)))
    (is (map? (:payment payment-log)))))
