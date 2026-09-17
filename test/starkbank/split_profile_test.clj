(ns starkbank.split-profile-test
  "There is only one SplitProfile per workspace, so these tests never assert on
  large counts: 'put' always creates-or-updates that single profile, and
  query/page can only ever return zero or one map."
  (:use [clojure.test])
  (:require [starkbank.split-profile :as split-profile]
            [starkbank.split-profile.log :as log]
            [starkbank.utils.user :refer [set-project]]))

(set-project)

(deftest put-and-get-split-profile
  (testing "put and get split profile"
    (def profiles (split-profile/put [{:interval "week" :delay 604800 :tags ["testing" "clojure"]}]))
    (def profile (first profiles))
    (is (= "week" (:interval profile)))
    (is (= 604800 (:delay profile)))
    (is (= (:id profile) (:id (split-profile/get (:id profile)))))))

(deftest put-split-profile-with-zero-delay
  (testing "put split profile round-trips a legitimate delay of 0"
    (def profiles (split-profile/put [{:interval "day" :delay 0}]))
    (def profile (first profiles))
    (is (= "day" (:interval profile)))
    (is (= 0 (:delay profile)))))

(deftest query-split-profiles
  (testing "query split profiles - at most one per workspace"
    (def profiles (take 10 (split-profile/query {:limit 10})))
    (is (<= (count profiles) 1))
    (doseq [profile profiles]
      (is (map? profile)))))

(deftest page-split-profiles
  (testing "page split profiles - at most one per workspace"
    (def profiles (:profiles (split-profile/page {:limit 10})))
    (is (<= (count profiles) 1))
    (doseq [profile profiles]
      (is (map? profile)))))

(deftest query-and-get-split-profile-logs
  (testing "query and get split profile logs"
    (def logs (take 200 (log/query {:limit 1})))
    (doseq [item logs]
      (is (map? item))
      (is (map? (:profile item))))
    (when (seq logs)
      (def profile-log (log/get (:id (first logs))))
      (is (not (nil? (:id profile-log))))
      ;; :errors so vem preenchido em logs de falha; a API omite quando nao ha erro
      (is (or (nil? (:errors profile-log)) (sequential? (:errors profile-log))))
      (is (string? (:created profile-log)))
      (is (map? (:profile profile-log))))))

(deftest page-split-profile-logs
  (testing "page split profile logs"
    (def logs (:logs (log/page {:limit 10})))
    (doseq [item logs]
      (is (map? item)))))
