(ns image-shower.html
  (:require (hiccup [page :refer [html5]]
                    [def :refer :all]
                    [util :refer [url-encode]]
                    [element :refer :all])
            (garden [core :refer [css]]
                    [units :refer :all :exclude [rem]])))

(defelem tag [t]
  (link-to {:class "card-link tag"}
           (str "/tag/" (url-encode t))
           t))

(defelem post [entry]
  "Formats a single post into an HTML document"
  [:article.post.card
   [:h4.card-title (:title entry)]
   ;; TODO show multiple images
   (case (:post_type entry)
     "photo" (let [img (first (:media entry))]
               (image {:class "card-img-top"}
                      (:url img)))
     "text" nil
     "video" (let [vid (first (:media entry))]
               [:video.card-img-top
                {:controls true}
                [:source {:src vid}]])
     
     )
   [:div.card-body
    ;; TODO ensure this gets HTML formatted
    (:text entry)]
   [:div.card-footer
    (map tag (map :text (:tags entry)))]])
