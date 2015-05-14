(ns photouploader.test.routes.photos
  (:require (korma [db :as kdb]))
  (:require [midje.sweet :refer :all]
            [clojure.java.io :as io]
            [cheshire.core :as json]
            [ring.mock.request :refer :all :as mock]
            [photouploader.routes.photos :refer :all :as subject]
            [clojure.java.io :refer [file input-stream as-file]]))

(def POSTGRES-USER "arathunku")
(def POSTGRES-PASSWORD "1234")

(defn set-db []
  (kdb/defdb postgres
    (kdb/postgres {:db       "clojure-fun-photoupload-test"
                   :user     POSTGRES-USER
                   :password POSTGRES-PASSWORD})))

(with-state-changes [(before :facts (set-db))
                     (around :facts (kdb/transaction ?form (kdb/rollback)))]

  (facts "POST to /photos"
    (fact "returns error on lack of a file"
      (let [response (subject/photos-routes (mock/request :post "/photos"))]
        (:status response) => 422
        (first (get (json/decode (:body response)) "errors")) => "Missing file"))

    (fact "validates file upload size"
      (let [filecontent {:bytes-stream (input-stream (make-array Byte/TYPE 200001))
                         :content-type "image/png"
                         :filename     "test.png"}
            request (assoc
                      (mock/request :post "/photos")
                      :params {:file filecontent})
            response (subject/photos-routes request)]
        (:status response) => 422
        (first (get (json/decode (:body response)) "errors")) => "File is too big, I can't take it anymore"))

    (fact "returns error about dimensions"
      (with-open [in (input-stream (file "test/photouploader/test/fixtures/image_too_small.png"))]
        (let [filecontent {:bytes-stream in
                           :content-type "image/png"
                           :filename     "test.png"}
              request (assoc
                        (mock/request :post "/photos")
                        :params {:file filecontent})
              response (subject/photos-routes request)]

          (:status response) => 422
          (first (get (json/decode (:body response)) "errors")) => "Wrong dimensions.")))

    (fact "creates photo and returns its id"
      (with-open [in (input-stream (file "test/photouploader/test/fixtures/image.png"))]
        (let [filecontent {:bytes-stream in
                           :content-type "image/png"
                           :filename     "test.png"}
              request (assoc
                        (mock/request :post "/photos")
                        :params {:file filecontent})
              response (subject/photos-routes request)]

          (:status response) => 200
          (.exists (as-file "public/assets/test.png"))  => true
          (get-in (json/decode (:body response)) ["photo" "image_file_name"]) => "test.png"
          (io/delete-file "public/assets/test.png"))))))

(facts "GET to /photos"
  (fact "returns file upload page"
    (let [response (subject/photos-routes (mock/request :get "/photos"))]
      (:status response) => 200
      (.contains (:body response) "Welcome to photouploader") => true)))
