(ns starkbank.verified-transfer-test
  (:use [clojure.test])
  (:require [starkbank.verified-account :as verified-account]
            [starkbank.verified-transfer :as verified-transfer]
            [starkbank.transfer :as transfer]
            [starkbank.utils.tax-id :as tax-id]
            [starkbank.utils.user :refer [set-project]]))

(set-project)

(defn- active-account-id
  "Reuses an already active VerifiedAccount instead of creating a new one, since the
  API caps VerifiedAccount creation at three per tax id every 24h."
  []
  (if-let [existing (first (verified-account/query {:limit 1 :status "active"}))]
    (:id existing)
    (:id (first (verified-account/create
      [{
        :tax-id (tax-id/random-cpf)
        :bank-code "341"
        :branch-code "2201"
        :number "76543-8"
        :name "Daenerys Targaryen Stormborn"
        :type "checking"
        :tags ["verified-account-test"]
      }])))))

(deftest create-verified-transfer
  (testing "create a verified transfer from an active verified account"
    (def account-id (active-account-id))
    (def transfers (verified-transfer/create
      [{
        :amount 200
        :account-id account-id
        :description "Test description"
        :display-description "Test displayDescription"
        :tags ["verified-transfer-test"]
        :rules [{:key "resendingLimit" :value 5}]
      }]))
    (def transfer (first transfers))
    (is (not (nil? (:id transfer))))
    (is (= 200 (:amount transfer)))
    (is (not (nil? (:status transfer))))
    (def resending-limit-rule (first (filter #(= "resendingLimit" (:key %)) (:rules transfer))))
    (is (= 5 (:value resending-limit-rule)))
    (is (= (:id transfer) (:id (transfer/get (:id transfer)))))))
