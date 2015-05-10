(ns photouploader.routes.photos
  (:import (javax.imageio ImageIO)
           (java.io ByteArrayInputStream))
  (:require [compojure.core :refer :all]
            [photouploader.views.photos.index :as photos-list-view]
            [liberator.core :refer [defresource]]
            [cheshire.core :as json]
            [liberator.representation :refer [ring-response]]
            [byte-streams :refer [to-input-stream]]
            [photouploader.models.photos :as photos-db]))

(defn- handle-photo-stream [file size]
  (let [bytes-stream (:bytes-stream file)
        filename (:filename file)]
    (if (empty? filename)
      {:error "Missing file" :image nil}
      (with-open [in bytes-stream]
        (with-open [out (java.io.BufferedOutputStream. (java.io.FileOutputStream. (str "public/assets/" filename)))]
          (let [buffer (make-array Byte/TYPE 1)]
            (loop [g (.read in buffer)
                   r 0
                   full-image (conj [] (first buffer))]
              (cond
                (> r size)
                {:error "File is too big, I can't take it anymore" :image nil}
                :else
                (if (= g -1)
                  {:error nil :image full-image :filename filename}
                  (do
                    (.write out buffer 0 g)
                    (recur
                      (.read in buffer)
                      (+ r g)
                      (conj full-image (first buffer)))))))))))))

(defn- validate-photo-dimensions [response max-width max-height]
  (let [{:keys [image filename]} response]
    (if image
      (let [img (ImageIO/read ^java.io.InputStream (ByteArrayInputStream. (byte-array image)))
            width (.getWidth img)
            height (.getHeight img)]
        (if (and (>= height max-height) (>= width max-width))
          {:success "Photo saved!" :image image :filename filename}
          {:error "Wrong dimensions." :image image :filename filename}))
      response)))

(defn- save-image [{:keys [error image filename] :as response}]
  (if (empty? error)
    (do
      (photos-db/create {:image_file_name    filename
                         :image_content_type "image/png"
                         :image_file_size    (count image)})
      {:response {:filename filename}})
    {:response response}))

(defn- handle-photo-upload [file _ctx]
  (-> file
    (handle-photo-stream 200000)
    (validate-photo-dimensions 200 200)
    (save-image)))

(defn- handle-photo-created [ctx]
  (let [response (:response ctx)
        error (:error response)
        status (if (empty? error) 200 422)]

  (ring-response {:headers {"Location" "/photos"}
                  :status  status
                  :body    (json/encode response)})))

(defresource photos-list
  :allowed-methods [:get]
  :handle-ok photos-list-view/index
  :available-media-types ["text/html"])

(defresource upload-photo [file]
  :allowed-methods [:post]
  :available-media-types ["text/html" "application/json "]
  :post! (partial handle-photo-upload file)
  :handle-created (partial handle-photo-created))


(defroutes photos-routes
  (GET "/photos" request photos-list)
  (POST "/photos" [file] (upload-photo file)))
