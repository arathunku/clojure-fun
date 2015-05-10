(ns photouploader.models.photos
  (:require [korma.core :refer :all]
            [korma.db   :refer :all]
            [photouploader.db :as db]
            [clj-time.coerce :as time-coerce]
            [clj-time.core :as time]))

;(defn- validation
;  (join
;    (attr [:image_file_name] present)
;    (attr [:image_file_size] present)
;    (attr [:image_content_type] present)))

(defentity photos
  (entity-fields
    :id
    :image_content_type
    :image_file_name
    :image_file_size
    :image_updated_at)
  (database db/postgres))

(defn create
  [{:keys [:image_file_name
           :image_file_size
           :image_content_type]}]

  (insert photos
    (values {:image_file_name image_file_name
             :image_file_size image_file_size
             :image_content_type image_content_type
             :image_updated_at (time-coerce/to-sql-time (time/now))})))
