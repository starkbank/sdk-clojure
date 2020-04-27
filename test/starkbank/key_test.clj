(ns starkbank.key-test
  (:use [clojure.test]
        [starkbank.key]))

(deftest create-keys-test
  (testing "Create keys"
    (create)))

(deftest create-keys-test
  (testing "Create and save keys"
    (create "temp")))
