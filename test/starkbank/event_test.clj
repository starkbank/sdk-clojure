(ns starkbank.event-test
  (:use [clojure.test])
  (:require [starkbank.event :as event]
            [starkbank.event.attempt :as attempt]
            [starkbank.user-test :as user]
            [clojure.java.io :as io]
            [starkbank.utils.page :as page]))

(deftest page-events
  (testing "page events"
    (user/set-test-project)
    (def get-page (fn [params] (event/page params)))
    (def ids (page/get-ids get-page 2 {:limit 2}))
    (is (= 4 (count ids)))))

(deftest query-attempt-events
  (testing "query and attempt events"
    (user/set-test-project)
    (def event (take 2 (event/query {:limit 2 :is-delivered false})))
    (def query-attempt (first (attempt/query {:limit 1 :event-ids (:id event)})))
    (def get-attempt (attempt/get (:id query-attempt)))
    (is (= (:id query-attempt) (:id get-attempt)))))

(deftest page-event-attempts
  (testing "page event-attempts"
    (user/set-test-project)
    (def get-page (fn [params] (attempt/page params)))
    (def ids (page/get-ids get-page 2 {:limit 2}))
    (is (= 4 (count ids)))))

(deftest query-get-update-delete-events
  (testing "query, get, update and delete events"
    (user/set-test-project)
    (def event (rand-nth (take 200 (event/query {:limit 100 :is-delivered false}))))
    (is not (:is-delivered event))
    (event/get (:id event))
    (event/update (:id event) {:is-delivered true})
    (event/delete (:id event))))

(deftest parse-events
  (testing "parse events"
    (user/set-test-project)
    (def event (event/parse 
      "{\"event\": {\"log\": {\"transfer\": {\"status\": \"processing\", \"updated\": \"2020-04-03T13:20:33.485644+00:00\", \"fee\": 160, \"name\": \"Lawrence James\", \"accountNumber\": \"10000-0\", \"id\": \"5107489032896512\", \"tags\": [], \"taxId\": \"91.642.017/0001-06\", \"created\": \"2020-04-03T13:20:32.530367+00:00\", \"amount\": 2, \"transactionIds\": [\"6547649079541760\"], \"bankCode\": \"01\", \"branchCode\": \"0001\"}, \"errors\": [], \"type\": \"sending\", \"id\": \"5648419829841920\", \"created\": \"2020-04-03T13:20:33.164373+00:00\"}, \"subscription\": \"transfer\", \"id\": \"6234355449987072\", \"created\": \"2020-04-03T13:20:40.784479+00:00\"}}"
      "MEYCIQCmFCAn2Z+6qEHmf8paI08Ee5ZJ9+KvLWSS3ddp8+RF3AIhALlK7ltfRvMCXhjS7cy8SPlcSlpQtjBxmhN6ClFC0Tv6"))
    (is (not (nil? (:id event))))))
