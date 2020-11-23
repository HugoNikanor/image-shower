(ns image-shower.db
  (:require (korma [db :as db :refer [defdb]]
                   [core :refer :all]))
  (:refer-clojure :exclude [update]))

;; I can't get fields to work for joined tables.
;; Documentation says to use :tags.id, but that does
;; nothing


;;; Database entities

(declare entry tag media tag_map page)

(defentity entry
  (has-many media)
  (many-to-many tag :tag_map)
  (has-many tag_map)
  (belongs-to page))

(defentity tag
  (many-to-many entry :tap_map)
  (has-many tag_map))

(defentity media
  (entity-fields :url :alt)
  (belongs-to entry))

(defentity tag_map
  (belongs-to entry)
  (belongs-to tag))

(defentity page
  (has-many entry))

;;; Collection of query filters to be used on the form
;;; (-> (select* entry)
;;;     (tagged "test")
;;;     select)

(defn entry-base []
  (-> (select* entry)
      (order :timestamp :desc)
      (with tag)
      (with media)
      (with page)))

(defn content-page [base n & {:keys [page-size] :or {page-size 10}}]
  "Limits query to a single page, with size page-size,
and the n'th page, starting from 1."
  (-> base
      (limit page-size)
      (offset (* n page-size))))

(defn page-filter [base p]
  "Limits query to entries belonging to page."
  (-> base
      (where {:page.name p})))

(defn tagged [base tag-name]
  "Limit base query to only posts tagged with tag. Equivalent to the
query: 
    SELECT * FROM tag_map
    LEFT JOIN entries ON entries.id = entry.id
    WHERE tag_id = (SELECT id FROM tags WHERE text = 'cfnm')
which I'm not sure is better.
"
  (-> base
      (where {:id [in (subselect
                       tag_map
                       (with tag)
                       (fields :entry_id)
                       (where {:tag.text tag-name}))]})))

;;; Direct functions which return some form of information about the database 

(defn pages []
  "Returns a list of pages."
  (-> (select* page)
      (with entry (fields ["count(1)" :count]))))

(defn entry-count [page-name & [tag]]
  "Returns the number of entries in the page named page-name"
  (let [base (-> (select* entry)
                 (with page)
                 (where {:page.name page-name})
                 (fields ["count(1)" :count]))]
    (let [q (if tag (tagged base tag)
                base)]
      (-> q select first :count))))

(defn fancy-name [page-name]
  "Returns a better looking name for the page, if available."
  (let [r (first (select page
                   (where {:name page-name})))]
    (or (:fancy_name r)
        (:name r))))
