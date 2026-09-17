(ns starkbank.corporate-invoice-test
  (:use [clojure.test])
  (:require [starkbank.corporate-invoice :as invoice]
            [starkbank.utils.page :as page]
            [starkbank.utils.user :refer [set-project]]))

(set-project)

(deftest create-invoices
  (testing "create invoices"
    (def created-invoice (invoice/create {:amount 100 :tags ["testing" "clojure"]}))
    (is (not (nil? (:id created-invoice))))))

(deftest query-invoices
  (testing "query invoices"
    (def invoices (take 200 (invoice/query {:limit 1})))
    (is (<= (count invoices) 1))))

(deftest page-invoices
  (testing "page invoices"
    (def get-page (fn [params] (invoice/page params)))
    (def ids (page/get-ids get-page 2 {:limit 2}))
    (is (= 4 (count ids)))))
