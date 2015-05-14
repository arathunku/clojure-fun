(ns photouploader.models.errors)

(defn add-error
  [obj msg & [additional-params]]
  {:pre [(not (nil? obj))
         (string? msg)]}
  (if-let [errors (:errors obj)]
    (merge obj {:errors (conj errors msg)} additional-params)
    {}))
