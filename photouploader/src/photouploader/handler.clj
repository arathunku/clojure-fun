(ns photouploader.handler
  (:require [compojure.core :refer [defroutes routes]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.multipart-params :refer [wrap-multipart-params]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [hiccup.middleware :refer [wrap-base-url]]
            [photouploader.routes.home :refer [home-routes]]
            [photouploader.routes.photos :refer [photos-routes]]))

(defn init []
  (println "photouploader is starting"))

(defn destroy []
  (println "photouploader is shutting down"))

(defroutes app-routes
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (-> (routes photos-routes home-routes app-routes)
      (handler/site)
      (wrap-base-url)
      (wrap-params)
      (wrap-multipart-params)))
