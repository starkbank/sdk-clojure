(ns starkbank.workspace-test
  (:use [clojure.test])
  (:require [starkbank.workspace :as workspace]
            [starkbank.user :as user]
            [clojure.java.io :as io]))

(deftest create-workspace
  (testing "create workspace"
    (def organization (user/organization
      "sandbox"
      (System/getenv "SANDBOX_ORGANIZATION_ID"); "9999999999999999"
      (System/getenv "SANDBOX_ORGANIZATION_PRIVATE_KEY")))
    (def uuid (java.util.UUID/randomUUID))
    (def workspace (workspace/create
      {
        :username (str "stark-v2-" uuid)
        :name (str "Stark V2: " uuid)
      }
      organization))
    (def workspace-id (:id workspace))
    (is (not (nil? workspace-id)))
    (def workspace-get (workspace/get workspace-id (user/organization-replace organization workspace-id)))
    (is (not (nil? (:id workspace-get))))))

(deftest query-workspaces
  (testing "query workspaces"
    (def organization (user/organization
      "sandbox"
      (System/getenv "SANDBOX_ORGANIZATION_ID"); "9999999999999999"
      (System/getenv "SANDBOX_ORGANIZATION_PRIVATE_KEY")))
    (def workspaces (take 200 (workspace/query {:limit 3} organization)))
    (is (<= (count workspaces) 3))))
