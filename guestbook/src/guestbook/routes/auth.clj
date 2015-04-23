(ns guestbook.routes.auth
  (:require [compojure.core :refer [defroutes GET POST]]
            [guestbook.views.layout :as layout]
            [noir.response :refer [redirect]]
            [hiccup.form :refer
             [form-to label text-field password-field submit-button]]))

(defn control [field name text]
  (list (label name text)
        (field name)
        [:br]))

(defn registration-page []
  (layout/common
    (form-to [:post "/register"]
             (control text-field :id "screen name")
             (control password-field :password "password")
             (control password-field :password-confirmation "Retype password")
             (submit-button "login"))))

(defn registration-submit-user
  [id password password-confirmation]
  (cond
    (= password password-confirmation)
    (redirect "/")
    :else
    (redirect "/register")))

(defroutes auth-routes
 (GET "/register" [_] (registration-page))
 (POST "/register" [id password password-confirmation]
       (registration-submit-user id password password-confirmation)))
