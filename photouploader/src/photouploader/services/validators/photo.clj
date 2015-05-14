(ns photouploader.services.validators.photo
  (:import (javax.imageio ImageIO)
           (java.io ByteArrayInputStream InputStream))
  (:require [photouploader.models.errors :refer [add-error]]))


(defn dimensions-validator [response max-width max-height]
  (if-let [image (:image response)]
    (let [img (ImageIO/read ^InputStream (ByteArrayInputStream. (byte-array image)))
          width (.getWidth img)
          height (.getHeight img)]
      (if (and (>= height max-height) (>= width max-width))
        response
        (add-error response "Wrong dimensions.")))
    response))
