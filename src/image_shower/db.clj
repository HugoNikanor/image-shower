(ns image-shower.db
  (:require (korma [db :refer :all]
                   [core :refer :all])))

(def db-spec (postgres
         {:db "image-shower"
          :user "hugo"}))

(defdb db db-spec)

(declare entries tags media tag_map testtable)

(defentity entries
  ;; (entity-fields :title :slug :timestamp :post_type :text)
  (has-many media {:fk :entry_id})
  (has-one testtable {:rfk :entry_id
                      :lfk :entry_id})
  (many-to-many tags :tag_map
                {:lfk :entry_id
                 :rfk :tag_id})
  (belongs-to tag_map {:rfk :entry_id
                      :lfK :id}))

(defentity tags
  ;; (entity-fields :text)
  (many-to-many entries :tap_map
                {:lfk :tag_id
                 :rfk :entry_id}))

(defentity media
  (entity-fields :url :alt)
  (belongs-to entries {:fk :entry_id}))

(defentity tag_map
  ;; (entity-fields :entry_id :tag_id)
  (has-one entries {:fk :entry_id})
  (has-one tags {:fk :tag_id}))

(defentity testtable
  (belongs-to entries {:lfk :entry_id
                       :rfk :entry_id}))

;; I can't get fields to work for joined tables.
;; Documentation says to use :tags.id, but that does
;; nothing

(def q-base
  (-> (select* entries)
      (with tags)
      (with media)
      )
  )

(defn memv [collection item]
  (.contains collection item))

(comment
  (time (select entries
                (with tags)
                (with media)))
  "Elapsed time: 76.041317 msecs"

  (time (entries-tagged entries "cfnm"))
  "Elapsed time: 0.051676 msecs"

  (time (filter #(= 1262 (:id %))
                (get-entries)))
  )


(defn tagged [tag]
  "Returns a base from which all posts tagged with tag (string) can be
selected. This whole thing can be expressed as 
    SELECT * FROM tag_map
    LEFT JOIN entries ON entries.id = entry.id
    WHERE tag_id = (SELECT id FROM tags WHERE text = 'cfnm')
Which I'm not sure is better.
"
  (-> q-base
      (where {:id [in (subselect
                       tag_map
                       (fields :entry_id)
                       (where {:tag_id [in (subselect
                                            tags
                                            (fields :id)
                                            (where {:text tag}))]}))]})))

