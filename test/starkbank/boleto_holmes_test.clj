(ns starkbank.boleto-holmes-test
  (:use [clojure.test])
  (:require [starkbank.boleto-holmes :as holmes]
            [starkbank.boleto-holmes.log :as log]
            [starkbank.boleto :as boleto]
            [starkbank.user-test :as user]
            [clojure.java.io :as io]
            [starkbank.utils.page :as page]))

(deftest create-get-boleto-holmes
  (testing "create and get boleto holmes"
    (user/set-test-project)
    (def boleto (rand-nth (take 100 (boleto/query {:limit 100 :status "registered"}))))
    (def holmes (holmes/create
      [{
        :boleto-id (:id boleto)
        :tags ["testing" "clojure"]
      }]))
    (holmes/get (:id (first holmes)))))

(deftest query-boleto-holmes
  (testing "query boleto holmes"
    (user/set-test-project)
    (def holmes (take 200 (holmes/query {:limit 3})))
    (is (= 3 (count holmes)))))

(deftest page-boleto-holmes
  (testing "page boleto-holmes"
    (user/set-test-project)
    (def get-page (fn [params] (holmes/page params)))
    (def ids (page/get-ids get-page 2 {:limit 2}))
    (is (= 4 (count ids)))))

(deftest query-get-boleto-holmes-logs
  (testing "query and get boleto holmes logs"
    (user/set-test-project)
    (def holmes-logs (log/query {:limit 1 :type "solved"}))
    (is (= 1 (count holmes-logs)))
    (def holmes-log (log/get (:id (first holmes-logs))))
    (is (map? (:holmes holmes-log)))
    (is (not (nil? (:id holmes-log))))
    (is (string? (:created holmes-log)))))

(deftest page-boleto-holmes-log
  (testing "page boleto-holmes-log"
    (user/set-test-project)
    (def get-page (fn [params] (log/page params)))
    (def ids (page/get-ids get-page 2 {:limit 2}))
    (is (= 4 (count ids)))))
