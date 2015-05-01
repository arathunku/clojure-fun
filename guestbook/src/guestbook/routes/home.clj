(ns guestbook.routes.home
  (:require [compojure.core :refer :all]
            [guestbook.views.layout :as layout]
            [hiccup.form :refer :all]
            [guestbook.models.db :as db]
            [ring.util.response :as resp]))

(defn format-timestamp [timestamp]
  (-> "dd/MM/yyyy"
      (java.text.SimpleDateFormat.)
      (.format timestamp)))

(defn show-guests []
  [:ul.guests
   (for [ { :keys [message name timestamp ]} (db/read-guests)]
         [:li
          [:blockquote message]
          [:p "-" [:cite name]]
          [:time (format-timestamp timestamp)]])])


(defn home [& [name message error]]
  (println "WORKS!")
  (layout/common
   [:h1 "Hello World!!!"]
   [:p "guestbook"]
   [:p error]

   (show-guests)
   [:hr]
   (form-to [:post "/messages"]
     [:p "Name:"]
     (text-field "name" name)
     [:p "Message"]
     (text-area {:rows 10 :cols 40} "message" message)
     [:br]
     (submit-button "comment"))))

(defn save-message [name message]
  (cond
   (empty? name)
   (home name message "Name left behind")
   (empty? message)
   (home name message "message left behind")
   :else
   (do
     (db/save-message name message)
     (resp/redirect "/"))))

(defroutes home-routes
           (GET "/" [] (home))
           (POST "/messages" [name message] (save-message name message)))
