(ns image-shower.honey
  (:require (honeysql [core :as sql]
                      [helpers :refer :all :as helpers])
            [clojure.java.jdbc :as jdbc]))

(def db {:subprotocol "postgres"
         :subname "//localhost/image-shower"
         :user "hugo"})

(def base-query
  (-> (select :e.id :e.title :m.url :r.tag_id :t.text)
      (from [:entries :e])

      (join [:tag_map :r] [:= :e.id :r.entry_id])
      (left-join [:tags :t] [:= :t.id :r.tag_id])

      (right-join [:media :m] [:= :e.id :m.entry_id])
      ))

(def limited-query
  (-> base-query
      (where [:= :e.id 1262])))


(comment
 (def data
   (->> limited-query
        sql/format
        (jdbc/query db))))

(def data
 (jdbc/query
  db
  ["SELECT e.id, e.title, m.url, t.text AS tags
   FROM entries e
   INNER JOIN media m ON e.id = m.entry_id
   INNER JOIN tag_map tm ON e.id = tm.entry_id
   INNER JOIN tags t ON t.id = tm.tag_id
   WHERE e.id = 1262"]))

(def tags (set (map #(:tags %)
                    data)))

(def urls (set (map #(:url %)
                    data)))

(def record
  (assoc (first data)
         :tags tags
         :url urls))

;;; This can't be the best way to do it?
