(ns image-shower.util
  (:require [compojure.coercions :refer [as-int]]))

(defn as-int-within [min max & {:keys [default] :or {default 1}}]
  "Returns a function which takes something that can be converted into
an int. Returns default on fail, and max or min if the int is above or
below the threasholds."
  (fn [in]
    (let [n (as-int in)]
         (cond (nil? n) default
               (> min n) min
               (> n max) max
               :else n))))

(defn ceil [n]
  (int (Math/ceil n)))

(defn floor [n]
  (int (Math/floor n)))

(defn range-around [around length]
  "Generates 'length' numbers, spaced equaly on either side of 'around'"
  (let [q (/ length 2)]
    (range (- around (floor q))
           (+ around (ceil q)))))

(defn page-count [entry-count & {:keys [page-size] :or {page-size 10}}]
  (ceil (/ entry-count page-size)))

