(ns image-shower.db
  (:require (korma [db :refer :all]
                   [core :refer :all])))

(def db-spec (postgres
         {:db "image-shower"
          :user "hugo"}))

(defdb db db-spec)

(declare entries tags media tag_map)

(defentity entries
  ;; (entity-fields :title :slug :timestamp :post_type :text)
  (has-many media {:fk :entry_id})
  (many-to-many tags :tag_map
                {:lfk :entry_id
                 :rfk :tag_id}))

(defentity tags
  (entity-fields :text)
  (many-to-many entries :tap_map
                {:lfk :tag_id
                 :rfk :entry_id}))

(defentity media
  (entity-fields :url :alt)
  (belongs-to entries {:fk :entry_id}))

;; (defentity tag_map)

(comment
  (use 'image-shower.db)
  (ns image-shower.db)

  ;; I can't get 'fields' to work for 'with' fields
  (select entries
          (with media)
          (with tags))
  )
