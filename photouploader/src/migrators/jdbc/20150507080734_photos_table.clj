(ns migrators.jdbc.20150507080734-photos-table
  (:use [joplin.jdbc.database]
        [clojure.java.jdbc :as sql]))

(defn up [db]
  (sql/with-connection db
    (try
      (println "Migrating.")
      (sql/create-table :photos
        [:id "serial PRIMARY KEY"]
        [:image_file_name "character varying(255) NOT NULL"]
        [:image_content_type "character varying(255)"]
        [:image_file_size "integer"]
        [:image_updated_at "timestamp without time zone"])
      (println "Done.")
      (catch Exception e
        (do
          (println (.getNextException e))
          (throw (Exception. "Error in migration")))))))

(defn down [db]
  (sql/with-connection db
    (sql/drop-table :photos)))
