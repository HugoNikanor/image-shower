(ns image-shower.html
  (:require (hiccup [page :refer [html5]]
                    [def :refer :all]
                    [util :refer [url url-encode]]
                    [element :refer :all])
            (garden [core :refer [css]]
                    [units :refer :all :exclude [rem]])))

(defelem tag [t]
  (link-to {:class "card-link text-muted tag"}
           (url "/tag/" (url-encode t))
           t))

(defelem header-link [id]
  (link-to {:class "card-link"}
           (url "/post/" id)
           id))

(defelem carousel-item [url & [active]]
  [:div.carousel-item {:class (when active "active")}
   (image {:class "d-block w-100"}
          url)])

(defelem carousel-link [dir text]
  [:a {:class (str "carousel-control-" dir)
       :role "button"
       :data-slide dir}
   [:span {:class (str "carousel-control-" dir "-icon")
           :aria-hidden "true"}]
   [:span.sr-only text]])

(defelem carousel-indicator [id idx]
  [:li {:data-target (url "#" id)
        :data-slide-to idx
        :class (when (zero? idx) "active")}])

(defelem carousel [head & tail]
  (let [id (gensym "carousel")]
    [:div.carousel.slide {:id id :data-interval "false"}
     [:ol.carousel-indicators
      (cons (carousel-indicator {:class "active"} id 0)
            (map-indexed (fn [idx _] (carousel-indicator id (+ 1 idx)))
                         tail))]
     [:div.carousel-inner (cons head tail)]
     (carousel-link {:href (url "#" id)} "prev" "Previous")
     (carousel-link {:href (url "#" id)} "next" "Next")]))

(defelem card-image-top [entry]
  (let [imgs (:media entry)]
    (if (= 1 (count imgs))
      (image {:class "card-img-top"}
             (url "/media/" (:url (first imgs))))
      (apply carousel {:class "card-img-top"}
       (map-indexed (fn [idx image]
                      (carousel-item (url "/media/" (:url image))
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
    (map tag (map :text (:tags entry)))]])

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
        [:a.btn.btn-outline-secondary {:href (url base "?p=" (- cur 1))} "<"]
        [:a.btn.btn-outline-secondary {:href (url base "?p=" (+ cur 1))} ">"]]]]]))

(defelem posts [data lst]
  (let [pnav (when data (page-nav {} data))]
    [:div.card-columns
     pnav
     (map #(post {} %)
          lst)
     pnav]))
