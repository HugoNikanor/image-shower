(ns image-shower.html
  (:require (hiccup [def :refer [defelem]]
                    [page :refer [html5 include-css include-js]]
                    [util :refer [url url-encode with-base-url]]
                    [element :refer :all])
             (garden [core :refer [css]]
                     [units :refer :all :exclude [rem]])
             (image-shower [carousel :as carousel :refer [carousel]]
                          [util :as util :refer [ceil floor range-around]])))

(defelem tag [t]
  (link-to {:class "text-muted tag"}
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
                [:source {:src (url "/media/" (:url vid))}]]))
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

(def page-css
  (css
   [:blockquote {:border-left-width (px 2)
                 :border-left-style "solid"
                 :border-left-color "grey"
                 :padding-left (em 0.5)}]
   [:.main {:padding (em 1)}
    [:.tag {:font-size (em 0.8)
            :display 'inline-block
            :margin-right (em 1)
            }]]))

(defelem head [title]
  "Common HTML HEAD items."
  [:head
   [:meta {:charset "utf-8"}]
   [:meta {:name "viewport"
           :content "width=device-width, initial-scale=1, shrink-to-fit=no"}]
   [:title (str title (when title " | ") "Image Shower")]
   (include-css "https://stackpath.bootstrapcdn.com/bootstrap/4.1.2/css/bootstrap.min.css")
   [:style page-css]])

(defn full-page [site title & elems]
  (with-base-url site
    (html5
        (head title)
        [:body elems
         (include-js "https://code.jquery.com/jquery-3.3.1.slim.min.js"
                     "https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js"
                     "https://stackpath.bootstrapcdn.com/bootstrap/4.1.2/js/bootstrap.min.js")])))
