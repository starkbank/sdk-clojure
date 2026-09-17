(ns starkbank.verified-account-test
  (:use [clojure.test])
  (:require [starkbank.verified-account :as verified-account]
            [starkbank.verified-account.log :as log]
            [starkbank.utils.page :as page]
            [starkbank.utils.tax-id :as tax-id]
            [starkbank.utils.user :refer [set-project]]))

(set-project)

(deftest create-get-cancel-bank-details-verified-account
  (testing "create, get and cancel a verified account by bank details"
    (def accounts (verified-account/create
      [{
        :tax-id (tax-id/random-cpf)
        :bank-code "341"
        :branch-code "2201"
        :number "76543-8"
        :name "Daenerys Targaryen Stormborn"
        :type "checking"
        :tags ["verified-account-test"]
      }]))
    (def account (first accounts))
    (is (not (nil? (:id account))))
    (is (= (:id account) (:id (verified-account/get (:id account)))))
    ;; delete-id devolve o envelope cru da API ({:account {...} :message "..."}),
    ;; comportamento herdado de transfer/delete e boleto/delete neste SDK.
    ;; A conta nasce em "processing" e a verificacao e assincrona, entao cancelar
    ;; a recem-criada da invalidAction. Cancelamos uma que ja esteja "active".
    (when-let [cancelable (first (verified-account/query {:limit 1 :status "active"}))]
      (def canceled (verified-account/cancel (:id cancelable)))
      (is (= (:id cancelable) (get-in canceled [:account :id])))
      (is (= "canceled" (get-in canceled [:account :status]))))))

(deftest create-verified-account-by-pix-key
  (testing "create a verified account by pix key"
    (def accounts (verified-account/create
      [{
        :tax-id "039.946.040-36"
        :key-id "arya.stark@starkbank.com"
        :tags ["verified-account-test"]
      }]))
    (is (not (nil? (:id (first accounts)))))))

(deftest query-verified-accounts
  (testing "query verified accounts"
    (def accounts (take 3 (verified-account/query {:limit 3 :status "active"})))
    (doseq [account accounts]
      (is (not (nil? (:id account)))))))

(deftest page-verified-accounts
  (testing "page verified accounts"
    (def get-page (fn [params] (verified-account/page params)))
    (def ids (page/get-ids get-page 2 {:limit 2}))
    (is (<= (count ids) 4))))

(deftest query-get-verified-account-logs
  (testing "query and get verified account logs"
    (def logs (log/query {:limit 1}))
    (doseq [item logs]
      (is (map? item)))
    (when-let [log-entry (first logs)]
      (def fetched-log (log/get (:id log-entry)))
      (is (= (:id fetched-log) (:id log-entry))))))

(deftest page-verified-account-logs
  (testing "page verified account logs"
    (def get-page (fn [params] (log/page params)))
    (def ids (page/get-ids get-page 2 {:limit 2}))
    (is (<= (count ids) 4))))
