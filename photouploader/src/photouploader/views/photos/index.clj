(ns photouploader.views.photos.index
  (:require [photouploader.views.layout :as layout]
            [hiccup.form :refer
             [form-to label submit-button file-upload]]))

(defn index
  [_]
  (layout/common
    [:div {:class "photos-list"}
     [:h1 "List of added photos, someday"]
     (form-to {:enctype "multipart/form-data"} [:post "/photos"]
      (label "photo" "Photos:")
      (file-upload :file)
      (submit-button "Add photo" ))]))
