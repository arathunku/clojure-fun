(ns photouploader.views.photos.index
  (:require [photouploader.views.layout :as layout]
            [hiccup.form :refer
             [form-to label submit-button]]))

(defn index
  [_]
  (layout/common
    [:div {:class "photos-list"}
     [:h1 "List of added photos, someday"]
     (form-to {:enctype "multiplart/form-data"} [:post "/photos"]
      (label "photo" "Photos:")
      [:input {:name "file" :type "file" :size "20"}]
      (submit-button "Add photo" ))]))
