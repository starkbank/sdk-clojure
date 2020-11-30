(ns starkbank.boleto-holmes-test
  (:use [clojure.test])
  (:require [starkbank.boleto-holmes :as holmes]
            [starkbank.boleto-holmes.log :as log]
            [starkbank.boleto :as boleto]
            [starkbank.user-test :as user]
            [clojure.java.io :as io]))

(deftest create-get-boleto-holmes
  (testing "create and get boleto holmes"
    (user/set-test-user)
    (def boleto (rand-nth (take 100 (boleto/query {:limit 100 :status "registered"}))))
    (def holmes (holmes/create
      [{
        :boleto-id (:id boleto)
        :tags ["testing" "clojure"]
      }]))
    (holmes/get (:id (first holmes)))))

(deftest query-boleto-holmes
  (testing "query boleto holmes"
    (user/set-test-user)
    (def holmes (take 200 (holmes/query {:limit 3})))
    (is (= 3 (count holmes)))))

(deftest query-get-boleto-holmes-logs
  (testing "query and get boleto holmes logs"
    (user/set-test-user)
    (def holmes-logs (log/query {:limit 1 :type "solved"}))
    (is (= 1 (count holmes-logs)))
    (def holmes-log (log/get (:id (first holmes-logs))))
    (is (map? (:holmes holmes-log)))
    (is (not (nil? (:id holmes-log))))
    (is (> (count (:result (:holmes holmes-log))) 0))
    (is (string? (:created holmes-log)))))
