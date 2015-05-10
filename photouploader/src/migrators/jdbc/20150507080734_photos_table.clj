(ns migrators.jdbc.20150507080734-photos-table
  (:use [joplin.jdbc.database]
        [clojure.java.jdbc :as sql]))

(defn up [db]
  (sql/db-do-commands db
    (println "Migrating.")
    (sql/create-table-ddl :photos
        [:id "serial PRIMARY KEY"]
        [:image_file_name "character varying(255) NOT NULL"]
        [:image_content_type "character varying(255)"]
        [:image_file_size "integer"]
        [:image_updated_at "timestamp without time zone"])))

(defn down [db]
  (sql/db-do-commands db
    (sql/drop-table-ddl :photos)))
