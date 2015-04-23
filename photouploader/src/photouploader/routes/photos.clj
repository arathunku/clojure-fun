(ns photouploader.routes.photos
  (:require [compojure.core :refer :all]
            [photouploader.views.photos.index :as photos-list-view]
            [liberator.representation :refer (ring-response)]
            [ring.util.response :refer (redirect)]
            [liberator.core
             :refer [defresource resource request-method-in]]))

(defn handle-photo-upload [file _ctx]
  (println "works-2")
  (if (not (empty? file))
    println "I've got a file."))

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
