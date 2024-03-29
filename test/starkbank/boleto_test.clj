(ns starkbank.boleto-test
  (:use [clojure.test])
  (:require [starkbank.boleto :as boleto]
            [starkbank.boleto.log :as log]
            [starkbank.user-test :as user]
            [clojure.java.io :as io]
            [starkbank.utils.date :as date]
            [starkbank.utils.page :as page]))

(deftest create-get-pdf-delete-boletos
  (testing "create, get, pdf and delete boletos"
    (user/set-test-project)
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
        :due (date/future-date 5)
        :discounts [
          {
            :percentage 5
            :date (date/future-date 2)
          }
          {
            :percentage 3
            :date (date/future-date 4)
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
    (def file-name "temp/boleto.pdf")
    (io/make-parents file-name)
    (io/copy (boleto/pdf (:id (first boletos)) {:layout "booklet" :hidden-fields ["customerAddress"]}) (io/file file-name))
    (boleto/delete (:id (first boletos)))))

(deftest query-boletos
  (testing "query boletos"
    (user/set-test-project)
    (def boletos (take 200 (boleto/query {:limit 3})))
    (is (= 3 (count boletos)))))

(deftest page-boletos
  (testing "page boletos"
    (user/set-test-project)
    (def get-page (fn [params] (boleto/page params)))
    (def ids (page/get-ids get-page 2 {:limit 2}))
    (is (= 4 (count ids)))))

(deftest query-get-boleto-logs
  (testing "query and get boleto logs"
    (user/set-test-project)
    (def boleto-logs (log/query {:limit 1}))
    (is (= 1 (count boleto-logs)))
    (def boleto-log (log/get (:id (first boleto-logs))))
    (is (not (nil? (:id boleto-log))))
    (is (not (nil? (:errors boleto-log))))
    (is (string? (:created boleto-log)))
    (is (map? (:boleto boleto-log)))))

(deftest page-boleto-logs
  (testing "page boleto-logs"
    (user/set-test-project)
    (def get-page (fn [params] (log/page params)))
    (def ids (page/get-ids get-page 2 {:limit 2}))
    (is (= 4 (count ids)))))
