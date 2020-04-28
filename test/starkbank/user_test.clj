(ns starkbank.user-test
  (:use [clojure.test])
  (:require [starkbank.user :as user]))

(deftest set-default-user-test
  (testing "Set default user"
    (-> 
      (user/project
        "sandbox"
        "9999999999999999"
        "-----BEGIN EC PRIVATE KEY-----
        MHUCAQEEIUozJdDjfNVL9ulX1CmRW7a7TgmeaFsem7G5GzFAyky2HaAHBgUrgQQA
        CqFEA0IABJlS4omSpIcq/MC1a39wProUxPlpcsirelSHOzGmwKJ4ZtYHhW7bYr1Y
        xX4Ae2b2ff/v/GNgn3nSsJ73QaUgn7s=
        -----END EC PRIVATE KEY-----")
      (user/set-default-user))))
