(ns starkbank.utils.user
  (:require [starkbank.settings :as stgs]
            [starkbank.user :refer [organization project]]))


(def starkbank-project (project
                        "sandbox"
                        (System/getenv "SANDBOX_ID"); "9999999999999999"
                        (System/getenv "SANDBOX_PRIVATE_KEY"); "-----BEGIN EC PRIVATE KEY-----\nMHUCAQEEIUozJdDjfNVL9ulX1CmRW7a7TgmeaFsem7G5GzFAyky2HaAHBgUrgQQA\nCqFEA0IABJlS4omSpIcq/MC1a39wProUxPlpcsirelSHOzGmwKJ4ZtYHhW7bYr1Y\nxX4Ae2b2ff/v/GNgn3nSsJ73QaUgn7s=\n-----END EC PRIVATE KEY-----")
                        ))

(def starkbank-organization (organization
                             "sandbox"
                             (System/getenv "SANDBOX_ID"); "9999999999999999"
                             (System/getenv "SANDBOX_PRIVATE_KEY"); "-----BEGIN EC PRIVATE KEY-----\nMHUCAQEEIUozJdDjfNVL9ulX1CmRW7a7TgmeaFsem7G5GzFAyky2HaAHBgUrgQQA\nCqFEA0IABJlS4omSpIcq/MC1a39wProUxPlpcsirelSHOzGmwKJ4ZtYHhW7bYr1Y\nxX4Ae2b2ff/v/GNgn3nSsJ73QaUgn7s=\n-----END EC PRIVATE KEY-----")
                             ))

(defn set-project []
  (stgs/user starkbank-project)
  )

(defn set-organization []
  (stgs/user starkbank-organization))
