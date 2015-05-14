(ns photouploader.models.photos
  (:import (java.io FileOutputStream))
  (:require [korma.core :refer :all]
            [korma.db :refer :all]
            [clj-time.coerce :as time-coerce]
            [clj-time.core :as time]
            [photouploader.services.validators.bytes-stream :refer [size-validator]]
            [photouploader.services.validators.photo :refer [dimensions-validator]]
            [photouploader.models.errors :refer [add-error]]))

(defentity photos
  (entity-fields
    :id
    :image_content_type
    :image_file_name
    :image_file_size
    :image_updated_at))

(defn create-at-db! [photo]
  {:pre [(empty? (get photo :errors []))]}
  (insert photos
    (values (merge photo {:image_updated_at (time-coerce/to-sql-time (time/now))}))))

(defn- photo-presence-validator [response]
  (let [filename (:filename (:image response))]
    (if (empty? filename)
      (add-error response "Missing file")
      response)))

(defn- rename [obj old-name new-name]
  (merge {new-name (old-name obj)} (dissoc obj old-name)))

(defn- validate-photo [image]
  (-> {:errors [] :image image}
    (photo-presence-validator)
    (rename :image :file)
    (size-validator 200000)
    (rename :file :image)
    (dimensions-validator 200 200)))

(defn- save-file [bytes-stream filename path]
  (with-open [out (FileOutputStream. (str path filename))]
    (.write out (into-array Byte/TYPE bytes-stream))))

(defn create! [{:keys [image]}]
  (let [filename (:filename image)
        photo-validation (validate-photo image)
        errors (:errors photo-validation)
        photo {:image_file_name    filename
               :image_file_size    (count (:image photo-validation))
               :image_content_type "image/png"}]

    (if (empty? errors)
      (try
        (transaction
          (create-at-db! photo)
          (save-file (:image photo-validation) filename "public/assets/"))
          photo
        (catch Exception e
          (rollback)
          (throw (Exception. e))))
      (merge photo {:errors errors}))))

