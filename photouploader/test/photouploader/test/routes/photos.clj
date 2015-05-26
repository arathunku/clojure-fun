(ns photouploader.test.routes.photos
  (:require [midje.sweet :refer :all]
            [clojure.java.io :as io]
            [korma.db :as kdb]
            [korma.core :as kcore]
            [cheshire.core :as json]
            [ring.mock.request :refer :all :as mock]
            [photouploader.routes.photos :refer :all :as subject]
            [clojure.java.io :refer [file input-stream as-file]]))

(def POSTGRES-USER "arathunku")
(def POSTGRES-PASSWORD "1234")

(kcore/defentity photos
  (kcore/entity-fields :image_file_name))

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
        (-> response :body json/decode (get "errors") first) => "File is too big, I can't take it anymore"))

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
          (-> response :body json/decode (get "errors") first) => "Wrong dimensions.")))

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
          (println (kcore/select photos))
          (-> (kcore/select photos) first :image_file_name) => "test.png"
          (-> response :body json/decode (get-in ["photo" "image_file_name"])) => "test.png"
          (io/delete-file "public/assets/test.png"))))))

(facts "GET to /photos"
  (fact "returns file upload page"
    (let [response (subject/photos-routes (mock/request :get "/photos"))]
      (:status response) => 200
      (.contains (:body response) "Welcome to photouploader") => true)))
