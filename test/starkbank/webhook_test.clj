(ns starkbank.webhook-test
  (:use [clojure.test])
  (:require [starkbank.webhook :as webhook]
            [starkbank.user-test :as user]
            [clojure.java.io :as io]))

(deftest create-get-delete-webhooks
  (testing "create, get and delete webhooks"
    (user/set-default-user-test)
    (def webhook (webhook/create
      {
        :url "https://webhook.site/60e9c18e-4b5c-4369-bda1-ab5fcd8e1b29"
        :subscriptions ["transfer", "boleto", "boleto-payment", "utility-payment"]
      }))
    (webhook/get (:id webhook))
    (def deleted-webhook (webhook/delete (:id webhook)))
    (is (not (nil? (:id deleted-webhook))))))

(deftest query-webhooks
  (testing "query webhooks"
    (user/set-default-user-test)
    (def webhooks (take 200 (webhook/query {:limit 3})))
    (is (= 3 (count webhooks)))))
