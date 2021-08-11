(ns starkbank.workspace-test
  (:use [clojure.test])
  (:require [starkbank.workspace :as workspace]
            [starkbank.user :as user]
            [clojure.java.io :as io]))

(deftest create-workspace
  (testing "create and patch workspace"
    (def organization (user/organization
      "sandbox"
      (System/getenv "SANDBOX_ORGANIZATION_ID"); "9999999999999999"
      (System/getenv "SANDBOX_ORGANIZATION_PRIVATE_KEY")))
    (def uuid (java.util.UUID/randomUUID))
    (def workspace (workspace/create
      {
        :username (str "stark-v2-" uuid)
        :name (str "Stark V2: " uuid)
        :allowed-tax-ids ["359.536.680-82", "20.018.183/0001-80"]
      }
      organization))
    (def workspace-id (:id workspace))
    (is (not (nil? workspace-id)))
    (def workspace-get (workspace/get workspace-id (user/organization-replace organization workspace-id)))
    (is (not (nil? (:id workspace-get))))

    (def updated-uuid (java.util.UUID/randomUUID))
    (def update
      {
        :username (str "stark-v2-" updated-uuid)
        :name (str "Stark V2: " updated-uuid)
        :allowed-tax-ids ["964.480.450-31", "263.122.860-02"]
      })
    (def updated-workspace (workspace/update
      workspace-id
      update
      (user/organization-replace organization workspace-id)))
    (is (= (:username update) (:username updated-workspace)))
    (is (= (:name update) (:name updated-workspace)))
    (is (= (:allowed-tax-ids update) (:allowed-tax-ids updated-workspace)))))

(deftest query-workspaces
  (testing "query workspaces"
    (def organization (user/organization
      "sandbox"
      (System/getenv "SANDBOX_ORGANIZATION_ID"); "9999999999999999"
      (System/getenv "SANDBOX_ORGANIZATION_PRIVATE_KEY")))
    (def workspaces (take 200 (workspace/query {:limit 3} organization)))
    (is (<= (count workspaces) 3))))
