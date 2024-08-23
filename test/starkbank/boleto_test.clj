(ns starkbank.boleto-test
  (:use [clojure.test])
  (:require [clojure.java.io :as io]
            [starkbank.boleto :as boleto]
            [starkbank.boleto.log :as log]
            [starkbank.utils.user :refer [set-project]]))

(set-project)

(deftest create-get-pdf-delete-boletos
  (testing "create, get, pdf and delete boletos"
    (def boletos (boleto/create
                  [{:amount 50000
                    :city "São Paulo"
                    :district "Itaim Bibi"
                    :name "Dumlocks von z'Blurbows"
                    :state-code "SP"
                    :street-line-1 "Rua do Dumlocks von z'Blurbows 200"
                    :street-line-2  "ap 200"
                    :tax-id "012.345.678-90"
                    :zip-code "00000-000"
                    :receiver-name "My Favorite Receiver"
                    :receiver-tax-id "123.456.789-09"}]))
    (boleto/get (:id (first boletos)))
    (def file-name "temp/boleto.pdf")
    (io/make-parents file-name)
    (io/copy (boleto/pdf (:id (first boletos))
                         {:layout "booklet" :hidden-fields ["customerAddress"]})
             (io/file file-name))
    (boleto/delete (:id (first boletos)))))

(deftest query-boletos
  (testing "query boletos"
    (def boletos (take 200 (boleto/query {:limit 3})))
    (is (= 3 (count boletos)))))

(deftest page-boletos
  (testing "page boletos"
    (def get-page (boleto/page {:limit 3}))
    (is (= (str (type get-page)) "class clojure.lang.PersistentArrayMap"))))

(deftest query-get-boleto-logs
  (testing "query and get boleto logs"
    (def boleto-logs (log/query {:limit 1}))
    (is (= 1 (count boleto-logs)))
    (def boleto-log (log/get (:id (first boleto-logs))))
    (is (not (nil? (:id boleto-log))))
    (is (not (nil? (:errors boleto-log))))
    (is (string? (:created boleto-log)))
    (is (map? (:boleto boleto-log)))))

(deftest page-boleto-logs
  (testing "page boleto-logs"
    (def get-page (log/page {:limit 3}))
    (is (= (str (type get-page)) "class clojure.lang.PersistentArrayMap"))))
