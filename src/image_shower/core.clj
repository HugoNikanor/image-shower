(ns image-shower.core
  (:require [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            (compojure [core :refer :all]
                       [coercions :refer [as-int]]
                       [route :as route])
            (hiccup [page :refer [html5 include-css]]
                    [def :refer :all]
                    [util :refer :all]
                    [element :refer :all])
            (garden [core :refer [css]]
                    [units :refer :all :exclude [rem]])
            (image-shower [html :as html]
                          [db :refer :all])

            [korma.core :refer :all]
            ))

(def page-css
  (css
   [:.main {:padding (cm 1)}
    [:.tag {:font-size (em 0.8)
            :color "gray"}]
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

(defroutes app
  (route/files "/media" {:root "public/media"})
  (GET "/" []
       (html5
        (head)
        [:body
         [:div.card-columns.main
          (map #(html/post {} %)
               (-> q-base
                   (limit 100)
                   (offset 100)
                   (select))
               )
          ]]))
  (GET "/post/:id" [id :<< as-int
                    ]
       ;; It's currently an error to request a post which doesn't exists
       (html5
        (head)
        [:body
         (html/post {} (first (filter #(= id (:id %))
                                      (get-entries))))]))
  (GET "/tag/:tag" [tag]
       (html5
        (head)
        [:body
         [:div.card-columns.main
          (map #(html/post {} %)
               (filter #(memv (map :text (:tags %))
                              tag)
                       (-> q-base
                           (limit 100)
                           (select))))]]))
  (route/not-found "404 Page"))

(def handler
  (wrap-defaults app site-defaults))
