(ns starkbank.boleto-holmes-test
  (:use [clojure.test])
  (:require [starkbank.boleto :as boleto]
            [starkbank.boleto-holmes :as holmes]
            [starkbank.boleto-holmes.log :as log]
            [starkbank.user-test :as user]
            [starkbank.utils.user :refer [set-project]]))

(set-project)

(deftest create-get-boleto-holmes
  (testing "create and get boleto holmes"
    (def boleto (boleto/query {:limit 10 :status "registered"})) 
    (def holmes (holmes/create
      [{
        :boleto-id (:id (first boleto))
        :tags ["testing" "clojure"]
      }]))
    (holmes/get (:id (first holmes)))
    )
  )

(deftest query-boleto-holmes
  (testing "query boleto holmes"
    (user/set-test-project)
    (def holmes (take 200 (holmes/query {:limit 3}))) 
    (doseq [item holmes]
      (is (string? (:boleto-id item)))
      )
    ))

(deftest page-boleto-holmes
  (testing "page boleto-holmes"
    (user/set-test-project)
    (def get-page (holmes/page {:limit 3}))
    (is (= (str (type get-page)) "class clojure.lang.PersistentArrayMap"))
    (doseq [item (:content get-page)]
      (is (= (:id (holmes/get (:id item))) (:id item))))
    ))

(deftest query-get-boleto-holmes-logs
  (testing "query and get boleto holmes logs"
    (user/set-test-project)
    (def holmes-logs (log/query {:limit 1 :types "solved"}))
    (is (= 1 (count holmes-logs)))
    (def holmes-log (log/get (:id (first holmes-logs))))
    (is (map? (:holmes holmes-log)))
    (is (not (nil? (:id holmes-log))))
    (is (string? (:created holmes-log)))))

(deftest page-boleto-holmes-log
  (testing "page boleto-holmes-log"
    (user/set-test-project)
    (def log-page (log/page {:limit 4}))
    (is (= (count (:content log-page)) 4))
    ))
