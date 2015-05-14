(ns photouploader.services.validators.bytes-stream
  (:require [byte-streams :refer [to-input-stream]]
            [photouploader.models.errors :refer [add-error]]))

(defn size-validator [response size]
  (if-let [file (:file response)]
    (let [bytes-stream (:bytes-stream file)]
      (with-open [in bytes-stream]
        (let [buffer (make-array Byte/TYPE 1)]
          (loop [g (.read in buffer)
                 r 0
                 full-file (conj [] (first buffer))]
            (if (> r size)
              (add-error response "File is too big, I can't take it anymore" {:file nil})
              (if (= g -1)
                (merge response {:file full-file})
                (recur
                  (.read in buffer)
                  (+ r g)
                  (conj full-file (first buffer)))))))))
    response))
