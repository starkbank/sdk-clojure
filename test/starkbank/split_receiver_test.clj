(ns starkbank.split-receiver-test
  (:use [clojure.test])
  (:require [starkbank.split-receiver :as split-receiver]
            [starkbank.split-receiver.log :as log]
            [starkbank.utils.page :as page]
            [starkbank.utils.user :refer [set-project]]))

(set-project)

(deftest create-and-get-split-receivers
  (testing "create and get split receivers"
    (def receivers (split-receiver/create
      [{
        :name "Jon Snow"
        :tax-id "012.345.678-90"
        :bank-code "60701190"
        :branch-code "0001"
        :account-number "00000-0"
        :account-type "checking"
        :tags ["testing" "clojure"]
      }]))
    (doseq [receiver receivers]
      (is (= (:id receiver) (:id (split-receiver/get (:id receiver))))))))

(deftest query-split-receivers
  (testing "query split receivers"
    (def receivers (take 200 (split-receiver/query {:limit 3})))
    (doseq [receiver receivers]
      (is (map? receiver)))))

(deftest page-split-receivers
  (testing "page split receivers"
    (def get-page (fn [params] (split-receiver/page params)))
    (def ids (page/get-ids get-page 2 {:limit 2}))
    (is (= 4 (count ids)))))

(deftest query-and-get-split-receiver-logs
  (testing "query and get split receiver logs"
    (def logs (take 200 (log/query {:limit 1})))
    (doseq [item logs]
      (is (map? item))
      (is (map? (:receiver item))))
    (when (seq logs)
      (def receiver-log (log/get (:id (first logs))))
      (is (not (nil? (:id receiver-log))))
      ;; :errors so vem preenchido em logs de falha; a API omite quando nao ha erro
      (is (or (nil? (:errors receiver-log)) (sequential? (:errors receiver-log))))
      (is (string? (:created receiver-log)))
      (is (map? (:receiver receiver-log))))))

(deftest page-split-receiver-logs
  (testing "page split receiver logs"
    (def get-page (fn [params] (log/page params)))
    (def ids (page/get-ids get-page 2 {:limit 2}))
    (is (= 4 (count ids)))))
