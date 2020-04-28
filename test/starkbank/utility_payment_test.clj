(ns starkbank.utility-payment-test
  (:use [clojure.test])
  (:require [starkbank.utility-payment :as payment]
            [starkbank.utility-payment.log :as log]
            [starkbank.user-test :as user]
            [clojure.java.io :as io]))

(deftest create-get-pdf-delete-utility-payments
  (testing "create, get, pdf and delete utility payments"
    (user/set-default-user-test)
    (def payments (payment/create
      [{
        :amount 100
        :description "testing clojure"
        :bar-code (str "8364000" (format "%08d" (rand-int 100000000)) "01380076105302611108067159411")
        :scheduled "2020-05-30"
        :tags ["testing" "clojure"]
      }]))
    (payment/get (:id (first payments)))
    (io/copy (payment/pdf (:id (first payments))) (io/file "temp/utility-payment.pdf"))
    (payment/delete (:id (first payments)))))

(deftest query-utility-payments
  (testing "query utility payments"
    (user/set-default-user-test)
    (def payments (take 200 (payment/query {:limit 3})))))

(deftest query-get-utility-payment-logs
  (testing "query and get utility payment logs"
    (user/set-default-user-test)
    (def payment-logs (log/query {:limit 1}))
    (def payment-log (log/get (:id (first payment-logs))))))
