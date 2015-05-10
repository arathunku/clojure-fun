(ns photouploader.test.handler
  (:use clojure.test
        ring.mock.request
        photouploader.handler))

(deftest test-app
  (testing "main route"
    (let [response (app (request :get "/"))]
      (is (= (:status response) 200)))))

