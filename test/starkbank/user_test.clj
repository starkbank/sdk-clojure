(ns starkbank.user-test
  (:use [clojure.test])
  (:require [starkbank.user]
            [starkbank.settings]))

(deftest set-test-project
  (testing "Set default user"
    (-> 
      (starkbank.user/project
        "sandbox"
        (System/getenv "SANDBOX_ID"); "9999999999999999"
        (System/getenv "SANDBOX_PRIVATE_KEY")); "-----BEGIN EC PRIVATE KEY-----\nMHUCAQEEIUozJdDjfNVL9ulX1CmRW7a7TgmeaFsem7G5GzFAyky2HaAHBgUrgQQA\nCqFEA0IABJlS4omSpIcq/MC1a39wProUxPlpcsirelSHOzGmwKJ4ZtYHhW7bYr1Y\nxX4Ae2b2ff/v/GNgn3nSsJ73QaUgn7s=\n-----END EC PRIVATE KEY-----"
      (starkbank.settings/user))))

(deftest get-test-organization
  (testing "Get organization user"
     (starkbank.user/organization
      "sandbox"
      (System/getenv "SANDBOX_ORGANIZATION_ID"); "9999999999999999"
      (System/getenv "SANDBOX_ORGANIZATION_PRIVATE_KEY")))); "-----BEGIN EC PRIVATE KEY-----\nMHUCAQEEIUozJdDjfNVL9ulX1CmRW7a7TgmeaFsem7G5GzFAyky2HaAHBgUrgQQA\nCqFEA0IABJlS4omSpIcq/MC1a39wProUxPlpcsirelSHOzGmwKJ4ZtYHhW7bYr1Y\nxX4Ae2b2ff/v/GNgn3nSsJ73QaUgn7s=\n-----END EC PRIVATE KEY-----"
