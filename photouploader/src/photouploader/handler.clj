(ns photouploader.handler
  (:import org.apache.commons.io.IOUtils)
  (:require [compojure.core :refer [defroutes routes]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [ring.middleware.multipart-params.byte-array :refer [byte-array-store]]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [hiccup.middleware :refer [wrap-base-url]]
            [photouploader.routes.home :refer [home-routes]]
            [photouploader.routes.photos :refer [photos-routes]]
            [photouploader.db :as db]
            [byte-streams :refer [seq-of]]))

(defn stream-byte-array-store
  []
  (fn [item]
    (-> (select-keys item [:filename :content-type])
      (assoc :bytes-stream (IOUtils/toBufferedInputStream ^java.io.InputStream (:stream item))))))

(defn init []
  (println "photouploader is starting"))

(defn destroy []
  (println "photouploader is shutting down"))

(defroutes app-routes
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (-> (routes photos-routes home-routes app-routes)
    ;; TODO: ASK: Why do I've to have (byte-array-store) in brackets?
      (handler/site {:multipart {:store (stream-byte-array-store)}})))
