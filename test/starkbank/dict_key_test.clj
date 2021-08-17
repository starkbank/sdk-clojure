(ns starkbank.dict-key-test
  (:use [clojure.test])
  (:require [starkbank.dict-key :as dict-key]
             [starkbank.user-test :as user]
            [starkbank.utils.page :as page]))
  
(deftest get-dict-key
  (testing "get dict-key"
    (user/set-test-project)
    (def pix-key "valid@sandbox.com")
    (def dict-key (dict-key/get pix-key))
    (is (= (:id dict-key) pix-key))))

(deftest query-dict-key
  (testing "query dict-key"
    (user/set-test-project)
    (def dict-keys (take 200 (dict-key/query {:limit 1 :status ["registered" "created"] :type "evp"})))
    (is (= 1 (count dict-keys)))
    (is (not (nil? (:id (first dict-keys)))))))

(deftest page-dict-key
  (testing "page dict-key"
    (user/set-test-project)
    (def get-page (fn [params] (dict-key/page params)))
    (def ids (page/get-ids get-page 2 {:limit 2}))
    (is (<= (count ids) 4))))
