(ns starkbank.dynamic-brcode-test
  (:use [clojure.test])
  (:require [starkbank.dynamic-brcode :as dynamic-brcode]
            [starkbank.utils.page :as page]
            [starkbank.utils.user :refer [set-project]]))

(set-project)

(deftest create-get-dynamic-brcodes
  (testing "create and get dynamic brcodes"
    (def brcodes (dynamic-brcode/create
      [{
        :amount 400000
        :expiration 3600
        :display-description "Payment for service #1234"
        :tags ["dynamic-brcode-test"]
        :rules [{:key "allowedTaxIds" :value ["012.345.678-90"]}]
      }]))
    (def brcode (first brcodes))
    (is (not (nil? (:uuid brcode))))
    (is (= (:uuid brcode) (:uuid (dynamic-brcode/get (:uuid brcode)))))))

(deftest query-dynamic-brcodes
  (testing "query dynamic brcodes"
    (def brcodes (take 2 (dynamic-brcode/query {:limit 2})))
    (doseq [brcode brcodes]
      (is (not (nil? (:uuid brcode)))))))

(deftest page-dynamic-brcodes
  (testing "page dynamic brcodes"
    (def get-page (fn [params] (dynamic-brcode/page params)))
    (def ids (page/get-ids get-page 2 {:limit 2}))
    (is (<= (count ids) 4))))
