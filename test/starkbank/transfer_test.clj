(ns starkbank.transfer-test
  (:use [clojure.test])
  (:require [starkbank.transfer :as transfer]
            [starkbank.transfer.log :as log]
            [starkbank.user-test :as user]
            [clojure.java.io :as io]
            [starkbank.utils.date :as date]
            [starkbank.utils.page :as page]))

(deftest create-get-delete-transfers
  (testing "create, get and delete transfers"
    (user/set-test-project)
    (def transfers (transfer/create
      [{
        :amount 200
        :name "Dumlocks von z'Blurbows"
        :tax-id "012.345.678-90"
        :bank-code "60701190"
        :branch-code "0001"
        :account-number "00000-0"
        :tags ["testing" "clojure"]
        :scheduled (date/future-datetime)
      }]))
    (transfer/get (:id (first transfers)))
    (transfer/delete (:id (first transfers)))))

(deftest query-pdf-transfers
  (testing "query transfers"
    (user/set-test-project)
    (def transfers (take 200 (transfer/query {:limit 3 :status "success"})))
    (is (= 3 (count transfers)))
    (is (map? (first transfers)))
    (def file-name "temp/transfer.pdf")
    (io/make-parents file-name)
    (io/copy (transfer/pdf (:id (first transfers))) (io/file file-name))))

(deftest page-transfer
  (testing "page transfer"
    (user/set-test-project)
    (def get-page (fn [params] (transfer/page params)))
    (def ids (page/get-ids get-page 2 {:limit 2}))
    (is (= 4 (count ids)))))


(deftest query-get-transfer-logs
  (testing "query and get transfer logs"
    (user/set-test-project)
    (def transfer-logs (log/query {:limit 1}))
    (is (= 1 (count transfer-logs)))
    (def transfer-log (log/get (:id (first transfer-logs))))
    (is (map? transfer-log))))

(deftest page-transfer-logs
  (testing "page invoice-logs"
    (user/set-test-project)
    (def get-page (fn [params] (log/page params)))
    (def ids (page/get-ids get-page 2 {:limit 2}))
    (is (= 4 (count ids)))))
