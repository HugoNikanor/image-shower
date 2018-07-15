(ns image-shower.core
  (:require [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.util.codec :refer [form-decode-str]]
            (compojure [core :refer :all]
                       [coercions :refer [as-int]]
                       [route :as route])
            (hiccup [page :refer [html5 include-css include-js]]
                    [def :refer :all]
                    [util :refer :all :exclude [url-encode]]
                    [element :refer :all])
            (garden [core :refer [css]]
                    [units :refer :all :exclude [rem]])
            (image-shower [html :as html]
                          [util :as util]
                          [db :refer :all
                           :exclude [page]
                           :rename {page-filter page}])
            [korma.core :refer :all])
  (:refer-clojure :exclude [update]))

(def page-css
  (css
   [:.main {:padding (em 1)}
    [:.tag {:font-size (em 0.8)
            ;; (comment :color "gray")
            }]
    (comment [:.post {:width "25rem"}])]))

(defelem not-implemented [feature]
  "Dummy page for features not yet done."
  [:body [:pre (str feature " is not yet implemented")]])

(defelem head []
  "Common HTML HEAD items."
  [:head
   [:meta {:charset "utf-8"}]
   [:meta {:name "viewport"
           :content "width=device-width, initial-scale=1, shrink-to-fit=no"}]
   [:title "Image Shower"]
   (include-css "https://stackpath.bootstrapcdn.com/bootstrap/4.1.2/css/bootstrap.min.css")
   [:style page-css]])

(defn full-page [site & elems]
  (with-base-url site
    (html5
        (head)
        [:body elems
         (include-js "https://code.jquery.com/jquery-3.3.1.slim.min.js"
                     "https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js"
                     "https://stackpath.bootstrapcdn.com/bootstrap/4.1.2/js/bootstrap.min.js")])))

(defn page-fix [page-name]
  "Local helper function for :<< binding of page."
  (->> page-name
       entry-count
       util/page-count
       (util/as-int-within 1)))

(defroutes app
  (GET "/" []
    (full-page "/"
      [:h1 "Page List"]
      (html/page-list (-> (pages) (select)))))

  (context "/:page-name" [page-name]
    (route/files "/media" {:root (str "public/" (str "/" page-name))})
    (GET "/" [p :<< (page-fix page-name)]
      (full-page (str "/" page-name)
        (html/posts {:class "main"}
                    (-> (entry-base)
                        (page page-name)
                        (content-page (- p 1))
                        (select))
                    :current-page p
                    :entry-count (entry-count page-name)
                    )))

    (GET "/tag/:tag" [tag p :<< (page-fix page-name)]
      (full-page (str "/" page-name)
        (html/posts {:class "main"}
                    (-> (entry-base)
                        (page page-name)
                        (tagged (form-decode-str tag))
                        (content-page (- p 1))
                        (select))
                    :current-page p
                    :entry-count (entry-count page-name tag)
                    )))

    (GET "/post/:id" [id :<< as-int]
      ;; Requesting nonexistant id leads to empty page
      (full-page (str "/" page-name)
        (html/posts
         (-> (entry-base)
             (page page-name)
             (where {:id id})
             (limit 1)
             (select))))))

  (route/not-found "404 Page"))

(def handler
  (wrap-defaults app site-defaults))
