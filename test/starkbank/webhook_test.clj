(ns starkbank.webhook-test
  (:use [clojure.test])
  (:require [starkbank.webhook :as webhook]
            [starkbank.user-test :as user]
            [clojure.java.io :as io]))

(deftest create-get-delete-webhooks
  (testing "create, get and delete webhooks"
    (user/set-test-project)
    (def webhook (webhook/create
      {
        :url (str "https://webhook.site/" (java.util.UUID/randomUUID))
        :subscriptions ["transfer", "boleto", "boleto-payment", "utility-payment"]
      }))
    (webhook/get (:id webhook))
    (def deleted-webhook (webhook/delete (:id webhook)))
    (is (not (nil? (:id deleted-webhook))))))

(deftest query-webhooks
  (testing "query webhooks"
    (user/set-test-project)
    (def webhooks (take 200 (webhook/query {:limit 3})))
    (is (<= (count webhooks) 3))))
