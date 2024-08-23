(ns starkbank.user-test
  (:use [clojure.test])
  (:require [starkbank.user]
            [starkbank.settings]))

(deftest set-test-project
  (testing "Set default user"
    (-> 
      (starkbank.user/project
        "sandbox"
        "6293779816382464"
        "-----BEGIN EC PRIVATE KEY-----\nMHQCAQEEIMYJ8sHNdOaDgmHqre26O2hByw1LDwMOgjuX67jiW6QYoAcGBSuBBAAK\noUQDQgAE8O6e/QoLFA51pIdOSJI/C34q7zPLo/f3GKCoJS1VYJBxpihqb1brcB4X\nNQfJu/7wSFc/Id/y1yPtHVMOFfRRQg==\n-----END EC PRIVATE KEY-----"
      )
     (starkbank.settings/user)))

(deftest get-test-organization
  (testing "Get organization user"
     (starkbank.user/organization
      "sandbox"
      "6293779816382464"
      "-----BEGIN EC PRIVATE KEY-----\nMHQCAQEEIMYJ8sHNdOaDgmHqre26O2hByw1LDwMOgjuX67jiW6QYoAcGBSuBBAAK\noUQDQgAE8O6e/QoLFA51pIdOSJI/C34q7zPLo/f3GKCoJS1VYJBxpihqb1brcB4X\nNQfJu/7wSFc/Id/y1yPtHVMOFfRRQg==\n-----END EC PRIVATE KEY-----"
      )))
)