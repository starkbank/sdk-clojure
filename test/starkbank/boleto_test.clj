(ns starkbank.boleto-test
  (:use [clojure.test])
  (:require [starkbank.boleto :as boleto]
            [starkbank.boleto.log :as log]
            [starkbank.user-test :as user]
            [clojure.java.io :as io]))

(deftest create-get-pdf-delete-boletos
  (testing "create, get, pdf and delete boletos"
    (user/set-test-user)
    (def boletos (boleto/create
      [{
        :amount 50000
        :city "São Paulo"
        :district "Itaim Bibi"
        :name "Dumlocks von z'Blurbows"
        :state-code "SP"
        :street-line-1 "Rua do Dumlocks von z'Blurbows 200"
        :street-line-2  "ap 200"
        :tax-id "012.345.678-90"
        :zip-code "00000-000"
        :receiver-name "My Favorite Receiver"
        :receiver-tax-id "123.456.789-09"
        :tags ["testing" "clojure"]
        :due "2020-05-30"
        :discounts [
          {
            :percentage 5
            :date "2020-05-25"
          }
          {
            :percentage 3
            :date "2020-05-26"
          }
        ]
        :descriptions [
          {
            :amount 1000
            :text "batata"
          }
          {
            :text "uopa"
          }
        ]
      }]))
    (boleto/get (:id (first boletos)))
    (io/copy (boleto/pdf (:id (first boletos))) (io/file "temp/boleto.pdf"))
    (boleto/delete (:id (first boletos)))))

(deftest query-boletos
  (testing "query boletos"
    (user/set-test-user)
    (def boletos (take 200 (boleto/query {:limit 3})))
    (is (= 3 (count boletos)))))

(deftest query-get-boleto-logs
  (testing "query and get boleto logs"
    (user/set-test-user)
    (def boleto-logs (log/query {:limit 1}))
    (is (= 1 (count boleto-logs)))
    (def boleto-log (log/get (:id (first boleto-logs))))
    (is (not (nil? (:id boleto-log))))
    (is (not (nil? (:errors boleto-log))))
    (is (string? (:created boleto-log)))
    (is (map? (:boleto boleto-log)))))
