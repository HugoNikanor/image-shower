(ns image-shower.html
  (:require (hiccup [def :refer [defelem]]
                    [util :refer [url url-encode]]
                    [element :refer :all])
            (image-shower [carousel :as carousel :refer [carousel]]
                          [util :as util :refer [ceil floor range-around]])))

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

(defelem nav-link [page-number & [label]]
  (let [l (str (or label page-number))]
    [:li.page-item (link-to {:class "page-link"}
                            (url "?p=" page-number)
                            l)]))

(defelem page-nav [current entry-count & {:keys [link-count] :or {link-count 5}}]
  "Navigation bar for previous and next page."
  (let [last-n (util/page-count entry-count)]
    [:nav {:aria-label "Page Navigation"}
     [:ul.pagination.justify-content-center
      [:li.page-item
       (nav-link {:class (when (= current 1) "disabled")} (- current 1) "<")
       (map (fn [i]
              (nav-link {:class (when (= i current) "active")} i))
            (filter #(>= last-n % 1) (range-around current link-count)))
       (nav-link {:class (when (= last-n current) "disabled")} (+ current 1) ">")]]]))


(defelem posts [lst & {:keys [current-page entry-count]}]
  "Formats a list of posts as bootstrap cards
current-page is which page the user is currently reading, and
entry-count is the max number of pages in the current context
(all posts, all posts tagged, ...)
"
  (let [pnav (when current-page (page-nav current-page entry-count))]
    [:div.card-columns
     pnav
     (map #(post {} %)
          lst)
     pnav]))

(defelem page-item [page]
  "Single item in page list"
  (link-to (url "/" (:name page))
           [:li.list-group-item
            {:class "justify-content-between d-flex align-items-center"}
            (or (:fancy_name page)
                (:name page))
            [:span.badge.badge-primary
             (-> page :entry first :count)]])
  )

(defelem page-list [pages]
  "List of all pages"
  [:ul.list-group
   (map #(page-item {} %) pages)])
