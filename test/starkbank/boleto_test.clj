(ns starkbank.boleto-test
  (:use [clojure.test])
  (:require [starkbank.boleto :as boleto]
            [starkbank.user-test :as user]
            [clojure.java.io :as io]))

(deftest create-pdf-delete-boletos
  (testing "create boletos"
    (user/set-default-user-test)
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
    (io/copy (boleto/pdf (:id (first boletos))) (io/file "temp/boleto.pdf"))
    (boleto/delete (:id (first boletos)))))

(deftest query-boletos
  (testing "query and get boletos"
    (user/set-default-user-test)
    (def boletos (take 200 (boleto/query {:limit 3})))))