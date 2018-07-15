(ns image-shower.db
  (:require (korma [db :refer :all]
                   [core :refer :all])))

(def db-spec (postgres
         {:db "image-shower"
          :user "hugo"}))

(defdb db db-spec)

(declare entry tag media tag_map page)

(defentity entry
  ;; (entity-fields :title :slug :timestamp :post_type :text)
  (has-many media)
  (many-to-many tag :tag_map)
  (has-many tag_map)
  (belongs-to page))

(defentity tag
  ;; (entity-fields :text)
  (many-to-many entry :tap_map))

(defentity media
  (entity-fields :url :alt)
  (belongs-to entry))

(defentity tag_map
  ;; (entity-fields :entry_id :tag_id)
  (belongs-to entry)
  (belongs-to tag)
  )

(defentity page
  (has-many entry))

;; I can't get fields to work for joined tables.
;; Documentation says to use :tags.id, but that does
;; nothing

;;; TODO this can't be a static variable, because if the data changes
;;; then the cache breaks and the library refuses to work.
(def q-base
  (-> (select* entry)
      (order :timestamp :desc)
      (with tag)
      (with media)
      (with page)))

(defn memv [collection item]
  (.contains collection item))

(comment
  (time (select entry
                (with tag)
                (with media)))
  "Elapsed time: 76.041317 msecs"

  (time (entries-tagged entries "cfnm"))
  "Elapsed time: 0.051676 msecs"

  (time (filter #(= 1262 (:id %))
                (get-entries)))
  )


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
                       (fields :entry_id)
                       (where {:tag_id [in (subselect
                                            tag
                                            (fields :id)
                                            (where {:text tag-name}))]}))]})))

(defn content-page [base n]
  (let [p-size 10]
    (-> base
        (limit p-size)
        (offset (* n p-size)))))

(defn page-filter [base p]
  (-> base
      (where {:page.name p})))

(defn pages []
  (-> (select* page)
      (with entry (fields "count(1)"))))

(comment (defn entry-count [page-name]
   (first (select page
                  (where {:name page-name})
                  (with entry)))))
