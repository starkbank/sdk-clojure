(ns starkbank.invoice-pull-subscription-test
  (:use [clojure.test])
  (:require [starkbank.invoice-pull-subscription :as invoice-pull-subscription]
            [starkbank.invoice-pull-subscription.log :as log]
            [starkbank.utils.date :as date]
            [starkbank.utils.page :as page]
            [starkbank.utils.user :refer [set-project]]))

(set-project)

(defn- base-subscription []
  {:start (date/future-date 5)
   :end (date/future-date 35)
   :interval "month"
   :pull-mode "manual"
   :pull-retry-limit 3
   :amount-min-limit 5000
   :display-description "Dragon Travel Fare"
   :reference-code "contract-12345"
   :name "Jon Snow"
   :tax-id "012.345.678-90"
   :tags ["invoice-pull-subscription-test"]})

(deftest create-get-cancel-push-invoice-pull-subscription
  (testing "create, get and cancel a push invoice pull subscription"
    (def subscription (first (invoice-pull-subscription/create
      [(merge (base-subscription)
        {:type "push"
         :data {:account-number "9123900000" :bank-code "05097757" :branch-code "1126" :tax-id "20.018.183/0001-80"}})])))
    (is (not (nil? (:id subscription))))
    (is (= (:id subscription) (:id (invoice-pull-subscription/get (:id subscription)))))
    (def canceled (invoice-pull-subscription/cancel (:id subscription)))
    (is (= (:id canceled) (:id subscription)))))

(deftest create-qrcode-invoice-pull-subscription
  (testing "create a qrcode invoice pull subscription"
    (def subscription (first (invoice-pull-subscription/create
      [(merge (base-subscription) {:type "qrcode"})])))
    (is (not (nil? (:id subscription))))))

(deftest create-qrcode-and-payment-invoice-pull-subscription
  (testing "create a qrcodeAndPayment invoice pull subscription"
    (def subscription (first (invoice-pull-subscription/create
      [(merge (base-subscription) {:type "qrcodeAndPayment" :data {:amount 400000}})])))
    (is (not (nil? (:id subscription))))))

(deftest query-invoice-pull-subscriptions
  (testing "query invoice pull subscriptions"
    (def subscriptions (take 2 (invoice-pull-subscription/query {:limit 2})))
    (doseq [subscription subscriptions]
      (is (not (nil? (:id subscription)))))))

(deftest page-invoice-pull-subscriptions
  (testing "page invoice pull subscriptions"
    (def get-page (fn [params] (invoice-pull-subscription/page params)))
    (def ids (page/get-ids get-page 2 {:limit 2}))
    (is (<= (count ids) 4))))

(deftest query-get-invoice-pull-subscription-logs
  (testing "query and get invoice pull subscription logs"
    (def logs (log/query {:limit 1}))
    (doseq [item logs]
      (is (map? item)))
    (when-let [log-entry (first logs)]
      (def fetched-log (log/get (:id log-entry)))
      (is (= (:id fetched-log) (:id log-entry))))))

(deftest page-invoice-pull-subscription-logs
  (testing "page invoice pull subscription logs"
    (def get-page (fn [params] (log/page params)))
    (def ids (page/get-ids get-page 2 {:limit 2}))
    (is (<= (count ids) 4))))
