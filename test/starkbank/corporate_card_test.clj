(ns starkbank.corporate-card-test
  (:use [clojure.test])
  (:require [starkbank.corporate-card :as card]
            [starkbank.corporate-card.log :as log]
            [starkbank.corporate-holder :as holder]
            [starkbank.utils.page :as page]
            [starkbank.utils.user :refer [set-project]]))

(set-project)

(defn- example-holder []
  {:name (str "Iron Bank S.A." (java.util.UUID/randomUUID))
   :tags ["testing" "clojure"]
   :permissions [{:owner-id (System/getenv "SANDBOX_ID") :owner-type "project"}]})

(defn- create-holder []
  (first (holder/create [(example-holder)])))

(deftest create-update-cancel-cards
  (testing "create, update and cancel cards"
    (def created-holder (create-holder))
    (def created-card (card/create {:holder-id (:id created-holder)} {:expand ["securityCode"]}))
    (is (not (nil? (:id created-card))))
    (def updated-card (card/update (:id created-card) {:display-name "Updated Name" :tags ["testing"]}))
    (is (= "Updated Name" (:display-name updated-card)))
    ;; delete-id devolve o envelope cru da API, herdado de transfer/delete
    (def canceled-card (card/cancel (:id created-card)))
    (is (= "canceled" (or (:status canceled-card) (get-in canceled-card [:card :status]))))))

(deftest query-and-get-cards
  (testing "query and get cards"
    (def cards (take 200 (card/query {:limit 1})))
    (when (not (empty? cards))
      (def queried-card (card/get (:id (first cards))))
      (is (= (:id (first cards)) (:id queried-card))))))

(deftest page-cards
  (testing "page cards"
    (def get-page (fn [params] (card/page params)))
    (def ids (page/get-ids get-page 2 {:limit 2}))
    ;; o sandbox pode ter menos cartoes que o total pedido
    (is (<= (count ids) 4))))

(deftest query-card-logs
  (testing "query and get card logs"
    (def card-logs (take 1 (log/query {:limit 1})))
    (when (not (empty? card-logs))
      (def card-log (log/get (:id (first card-logs))))
      (is (not (nil? (:id card-log)))))))

(deftest page-card-logs
  (testing "page card logs"
    (def get-page (fn [params] (log/page params)))
    (def ids (page/get-ids get-page 2 {:limit 2}))
    (is (= 4 (count ids)))))
