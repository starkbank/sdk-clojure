(ns starkbank.transfer-test
  (:use [clojure.test])
  (:require [starkbank.transfer :as transfer]
            [starkbank.transfer.log :as log]
            [starkbank.user-test :as user]
            [clojure.java.io :as io]))

(deftest create-get-pdf-delete-transfers
  (testing "create, get, pdf and delete transfers"
    (user/set-default-user-test)
    (def transfers (transfer/create
      [{
        :amount 200
        :name "Dumlocks von z'Blurbows"
        :tax-id "012.345.678-90"
        :bank-code "341"
        :branch-code "0001"
        :account-number "00000-0"
        :tags ["testing" "clojure"]
      }]))
    (transfer/get (:id (first transfers)))))

(deftest query-transfers
  (testing "query transfers"
    (user/set-default-user-test)
    (def transfers (take 200 (transfer/query {:limit 3 :status "success"})))
    (io/copy (transfer/pdf (:id (first transfers))) (io/file "temp/transfer.pdf"))))

(deftest query-get-transfer-logs
  (testing "query and get transfer logs"
    (user/set-default-user-test)
    (def transfer-logs (log/query {:limit 1}))
    (def transfer-log (log/get (:id (first transfer-logs))))))
