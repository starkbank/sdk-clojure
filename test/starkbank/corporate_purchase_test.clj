(ns starkbank.corporate-purchase-test
  (:use [clojure.test])
  (:require [starkbank.corporate-purchase :as purchase]
            [starkbank.corporate-purchase.log :as log]
            [starkbank.utils.page :as page]
            [starkbank.utils.user :refer [set-project]]))

(set-project)

(deftest query-and-get-purchases
  (testing "query and get purchases"
    (def purchases (take 200 (purchase/query {:limit 1})))
    (when (not (empty? purchases))
      (def queried-purchase (purchase/get (:id (first purchases))))
      (is (= (:id (first purchases)) (:id queried-purchase))))))

(deftest page-purchases
  (testing "page purchases"
    (def get-page (fn [params] (purchase/page params)))
    (def ids (page/get-ids get-page 2 {:limit 2}))
    (is (= 4 (count ids)))))

(deftest query-purchase-logs
  (testing "query and get purchase logs"
    (def purchase-logs (take 1 (log/query {:limit 1})))
    (when (not (empty? purchase-logs))
      (def purchase-log (log/get (:id (first purchase-logs))))
      (is (not (nil? (:id purchase-log)))))))

(deftest page-purchase-logs
  (testing "page purchase logs"
    (def get-page (fn [params] (log/page params)))
    (def ids (page/get-ids get-page 2 {:limit 2}))
    (is (= 4 (count ids)))))

(def content "{\"acquirerId\": \"236090\", \"amount\": 100, \"cardId\": \"5671893688385536\", \"cardTags\": [], \"endToEndId\": \"2fa7ef9f-b889-4bae-ac02-16749c04a3b6\", \"holderId\": \"5917814565109760\", \"holderTags\": [], \"isPartialAllowed\": false, \"issuerAmount\": 100, \"issuerCurrencyCode\": \"BRL\", \"merchantAmount\": 100, \"merchantCategoryCode\": \"bookStores\", \"merchantCountryCode\": \"BRA\", \"merchantCurrencyCode\": \"BRL\", \"merchantFee\": 0, \"merchantId\": \"204933612653639\", \"merchantName\": \"COMPANY 123\", \"methodCode\": \"token\", \"purpose\": \"purchase\", \"score\": null, \"tax\": 0, \"walletId\": \"\"}")
(def valid-signature "MEUCIBxymWEpit50lDqFKFHYOgyyqvE5kiHERi0ZM6cJpcvmAiEA2wwIkxcsuexh9BjcyAbZxprpRUyjcZJ2vBAjdd7o28Q=")
(def invalid-signature "MEUCIQDOpo1j+V40DNZK2URL2786UQK/8mDXon9ayEd8U0/l7AIgYXtIZJBTs8zCRR3vmted6Ehz/qfw1GRut/eYyvf1yOk=")

(deftest parse-valid-purchase
  (testing "parse a valid corporate purchase authorization request"
    (def parsed-purchase (purchase/parse content valid-signature))
    (is (= "5671893688385536" (:card-id parsed-purchase)))))

(deftest parse-invalid-purchase
  (testing "parse an invalid corporate purchase authorization request"
    (is (thrown? IllegalArgumentException (purchase/parse content invalid-signature)))))

(deftest response-approved
  (testing "build an approved response"
    (def response (purchase/response "approved" {:amount 1000 :tags ["tony" "stark"]}))
    (is (re-find #"approved" response))))

(deftest response-denied
  (testing "build a denied response"
    (def response (purchase/response "denied" {:reason "other" :tags ["tony" "stark"]}))
    (is (re-find #"denied" response))))
