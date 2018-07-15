(ns image-shower.html
  (:require (hiccup [def :refer [defelem]]
                    [util :refer [url url-encode]]
                    [element :refer :all])
            (image-shower [carousel :as carousel :refer [carousel]])))

(defelem tag [t]
  (link-to {:class "card-link text-muted tag"}
           (url "/tag/" (url-encode t))
           t))

(defelem header-link [id]
  (link-to {:class "card-link"}
           (url "/post/" id)
           id))

(defelem card-image-top [entry]
  (let [imgs (:media entry)]
    (if (= 1 (count imgs))
      (image {:class "card-img-top"}
             (url "/media/" (:url (first imgs))))
      (apply carousel {:class "card-img-top"}
       (map-indexed (fn [idx image]
                      (carousel/item (url "/media/" (:url image))
                                     (zero? idx)))
         imgs)))))

(defelem post [entry]
  "Formats a single post into an HTML document"
  [:article.post.card
   [:header.card-header (header-link (:id entry))]
   ;; TODO show multiple images
   (case (:post_type entry)
     "photo" (card-image-top {} entry)
     "text" nil
     "video" (let [vid (first (:media entry))]
               [:video.card-img-top
                {:controls true
                 :preload "metadata"}
                [:source {:src (url "/media/" (:url vid))}]])
     
     )
   (when-not (empty? (:text entry))
     [:main.card-body
      (comment [:h4.card-title (:title entry)])
      (:text entry)])
   [:footer.card-footer
    (map tag (map :text (:tag entry)))]])

(defelem page-nav [data]
  (let [{cur :page
         base :uri} data]
    [:div.card.bg-info.container
     [:header.card-header
      "Change Page"]
     [:main.card-body
      [:div.row
       [:p.col-sm (str cur)]
       [:div.btn-group.col-sm {:role "group"}
        [:a.btn.btn-outline-secondary {:href (url "?p=" (- cur 1))} "<"]
        [:a.btn.btn-outline-secondary {:href (url "?p=" (+ cur 1))} ">"]]]]]))

(defelem posts [data lst]
  (let [pnav (when data (page-nav {} data))]
    [:div.card-columns
     pnav
     (map #(post {} %)
          lst)
     pnav]))
