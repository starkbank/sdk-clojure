(ns starkbank.invoice-pull-request-test
  (:use [clojure.test])
  (:require [starkbank.invoice :as invoice]
            [starkbank.invoice-pull-request :as invoice-pull-request]
            [starkbank.invoice-pull-request.log :as log]
            [starkbank.invoice-pull-subscription :as invoice-pull-subscription]
            [starkbank.utils.date :as date]
            [starkbank.utils.page :as page]
            [starkbank.utils.user :refer [set-project]]))

(set-project)

(defn- create-invoice-id []
  (:id (first (invoice/create
    [{
      :amount 400000
      :tax-id "012.345.678-90"
      :name "Iron Bank S.A."
    }]))))

(defn- create-subscription-id []
  (:id (first (invoice-pull-subscription/create
    [{
      :start (date/future-date 5)
      :interval "month"
      :pull-mode "manual"
      :pull-retry-limit 3
      :type "qrcodeAndPayment"
      :data {:amount 400000}
      :name "Iron Bank S.A."
      :tax-id "012.345.678-90"
      :tags ["invoice-pull-request-test"]
    }]))))

(deftest create-get-cancel-invoice-pull-requests
  (testing "create, get and cancel invoice pull requests"
    (def request (first (invoice-pull-request/create
      [{
        :invoice-id (create-invoice-id)
        :subscription-id (create-subscription-id)
        :due (date/future-datetime 5)
        :attempt-type "default"
        :tags ["invoice-pull-request-test"]
      }])))
    (is (not (nil? (:id request))))
    (is (= (:id request) (:id (invoice-pull-request/get (:id request)))))
    (def canceled (invoice-pull-request/cancel (:id request)))
    (is (= (:id canceled) (:id request)))))

(deftest query-invoice-pull-requests
  (testing "query invoice pull requests"
    (def requests (take 2 (invoice-pull-request/query {:limit 2})))
    (doseq [request requests]
      (is (not (nil? (:id request)))))))

(deftest page-invoice-pull-requests
  (testing "page invoice pull requests"
    (def get-page (fn [params] (invoice-pull-request/page params)))
    (def ids (page/get-ids get-page 2 {:limit 2}))
    (is (<= (count ids) 4))))

(deftest query-get-invoice-pull-request-logs
  (testing "query and get invoice pull request logs"
    (def logs (log/query {:limit 1}))
    (doseq [item logs]
      (is (map? item)))
    (when-let [log-entry (first logs)]
      (def fetched-log (log/get (:id log-entry)))
      (is (= (:id fetched-log) (:id log-entry))))))

(deftest page-invoice-pull-request-logs
  (testing "page invoice pull request logs"
    (def get-page (fn [params] (log/page params)))
    (def ids (page/get-ids get-page 2 {:limit 2}))
    (is (<= (count ids) 4))))
