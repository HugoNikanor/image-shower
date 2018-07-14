(ns image-shower.html
  (:require (hiccup [page :refer [html5]]
                    [def :refer :all]
                    [util :refer [url url-encode]]
                    [element :refer :all])
            (garden [core :refer [css]]
                    [units :refer :all :exclude [rem]])))

(defelem tag [t]
  (link-to {:class "card-link tag"}
           (url "/tag/" (url-encode t))
           t))

(defelem header-link [id]
  (link-to {:class "card-link"}
           (url "/post/" id)
           id))

(defelem post [entry]
  "Formats a single post into an HTML document"
  [:article.post.card
   [:header.card-header (header-link (:id entry))]
   ;; TODO show multiple images
   (case (:post_type entry)
     "photo" (let [img (first (:media entry))]
               (image {:class "card-img-top"}
                      (url "/media/" (:url img))))
     "text" nil
     "video" (let [vid (first (:media entry))]
               [:video.card-img-top
                {:controls true}
                [:source {:src (url "/media/" (:url vid))}]])
     
     )
   [:main.card-body
    (comment [:h4.card-title (:title entry)])
    (:text entry)]
   [:footer.card-footer
    (map tag (map :text (:tags entry)))]])
