(ns photouploader.routes.home
  (:require [compojure.core :refer :all]
            [photouploader.views.layout :as layout]
            [liberator.core
             :refer [defresource resource request-method-in]]))

(def media-type ["text/html"])

(defn home-layout [_]
  (layout/common
    [:div {:class "container"}
     [:a { :href "/photos"} "Photos" ]]))

(defresource home
  :allowed-methods [:get]
  :handle-ok home-layout
  :available-media-types media-type)

(defroutes home-routes
  (ANY "/" request home))
