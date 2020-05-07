(ns starkbank.key-test
  (:use [clojure.test])
  (:require [starkbank.key :as key]))

(deftest create-keys-test
  (testing "Create keys"
    (def key-pair (key/create))
    (is (map? key-pair))
    (is (string? (:private-pem key-pair)))
    (is (string? (:public-pem key-pair)))))

(deftest create-keys-test-save-files
  (testing "Create and save keys"
    (key/create "temp")))
