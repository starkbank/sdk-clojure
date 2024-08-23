(ns starkbank.webhook-test
  (:use [clojure.test])
  (:require 
            [starkbank.utils.page :as page]
            [starkbank.utils.user :refer [set-project]]
            [starkbank.webhook :as webhook]))

(set-project)

(deftest create-get-delete-webhooks
  (testing "create, get and delete webhooks"
    (def webhook (webhook/create
                  {:url (str "https://webhook.site/" (java.util.UUID/randomUUID))
                   :subscriptions ["transfer", "boleto", "boleto-payment", "utility-payment"]}))
    (webhook/get (:id webhook))
    (def deleted-webhook (webhook/delete (:id webhook))) 
    (is (= "Webhook successfully deleted" (:message deleted-webhook)))
    ))

(deftest query-webhooks
  (testing "query webhooks"
    (def webhooks (take 200 (webhook/query {:limit 3})))
    (is (<= (count webhooks) 3))))

(deftest page-webhooks
  (testing "page webhooks"
    (def get-page (fn [params] (webhook/page params)))
    (def ids (page/get-ids get-page 2 {:limit 2}))
    (is (<= (count ids) 4))))
