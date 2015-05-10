(ns photouploader.db
  (:require [clojure.string :as str]
            [korma.db :as db]))

(def POSTGRES-USER "arathunku")
(def POSTGRES-PASSWORD "1234")

(db/defdb postgres
  (db/postgres {:db "clojure-fun-photoupload"
             :user POSTGRES-USER
             :password POSTGRES-PASSWORD}))


