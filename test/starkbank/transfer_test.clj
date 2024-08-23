(ns starkbank.transfer-test
  (:use [clojure.test])
  (:require [clojure.java.io :as io]
            [starkbank.transfer :as transfer]
            [starkbank.transfer.log :as log]
            [starkbank.utils.date :as date]
            [starkbank.utils.page :as page]
            [starkbank.utils.user :refer [set-project]]))

(set-project)

(deftest create-get-delete-transfers
  (testing "create, get and delete transfers"
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
    (def transfers (transfer/query {:limit 3 :status "success"}))
    (doseq [item transfers]
      (is (map? item))) 
    (def file-name "temp/transfer.pdf") 
    (io/make-parents file-name)
    (io/copy (transfer/pdf (:id (first transfers))) (io/file file-name))))

(deftest page-transfer
  (testing "page transfer"
    (def get-page (fn [params] (transfer/page params)))
    (def ids (page/get-ids get-page 2 {:limit 2}))
    (is (= 4 (count ids)))))


(deftest query-get-transfer-logs
  (testing "query and get transfer logs"
    (def transfer-logs (log/query {:limit 1}))
    (doseq [item transfer-logs]
      (is (map? item))) 
    (def transfer-log (log/get (:id (first transfer-logs))))
    (is (map? transfer-log))))

(deftest page-transfer-logs
  (testing "page invoice-logs"
    (def get-page (fn [params] (log/page params)))
    (def ids (page/get-ids get-page 2 {:limit 2}))
    (is (= 4 (count ids)))))
