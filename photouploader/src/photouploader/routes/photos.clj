(ns photouploader.routes.photos
  (:require [compojure.core :refer :all]
            [photouploader.views.photos.index :as photos-list-view]
            [liberator.core :refer [defresource]]
            [cheshire.core :as json]
            [liberator.representation :refer [ring-response]]
            [byte-streams :refer [to-input-stream]]
            [photouploader.models.photos :as photos-db]))

(defn- handle-photo-upload [file _ctx]
  (let [photo (photos-db/create! {:image file})
        errors (:errors photo [])]
    (if (empty? errors)
      {:status 200 :body {:photo photo}}
      {:status 422 :body {:errors errors}})))

(defn render-response [ctx]
  (let [{:keys [status body]} ctx]
    (ring-response {:headers {"Location" "/photos"}
                    :status  status
                    :body    (json/encode body)})))

(defresource photos-list
  :allowed-methods [:get]
  :handle-ok photos-list-view/index
  :available-media-types ["text/html"])

(defresource upload-photo [file]
  :allowed-methods [:post]
  :available-media-types ["text/html" "application/json "]
  :post! (partial handle-photo-upload file)
  :handle-created (partial render-response))

(defroutes photos-routes
  (GET "/photos" request photos-list)
  (POST "/photos" [file] (upload-photo file)))
