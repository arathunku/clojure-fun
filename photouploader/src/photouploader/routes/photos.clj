(ns photouploader.routes.photos
  (:import (javax.imageio ImageIO)
           (org.apache.commons.io IOUtils)
           (java.io ByteArrayInputStream))
  (:require [compojure.core :refer :all]
            [photouploader.views.photos.index :as photos-list-view]
            [clojure.java.io :as io]
            [liberator.core :refer [defresource]]
            [byte-streams :refer [to-input-stream]]
            [photouploader.models.photos :as photos-db]))

(defn- handle-photo-stream [file size]
  (println "handle photo-stream")
  (let [bytes-stream (:bytes-stream file)
        filename (:filename file)]
    (if (empty? filename)
      {:error "Missing file" :image nil}
      (with-open [in bytes-stream]
        (with-open [out (java.io.BufferedOutputStream. (java.io.FileOutputStream. (str "/home/arathunku/Downloads/assets/" filename)))]
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
                         :image_file_size    (count image)}))
    (println error)))

(defn handle-photo-upload [file _ctx]
  (println (-> file
             (handle-photo-stream 200000)
             (validate-photo-dimensions 10 10)
             (save-image))))

(defresource photos-list
  :allowed-methods [:get]
  :handle-ok photos-list-view/index
  :available-media-types ["text/html"])

(defresource upload-photo [file]
  :allowed-methods [:post]
  :available-media-types ["text/html" "application/json "]
  :post! (partial handle-photo-upload file)
  :post-redirect? true
  :location (fn [ctx] "/photos"))


(defroutes photos-routes
  (GET "/photos" request photos-list)
  (POST "/photos" [file] (upload-photo file)))
