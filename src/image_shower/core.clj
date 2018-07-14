(ns image-shower.core
  (:require [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.util.codec :refer [form-decode-str]]
            (compojure [core :refer :all]
                       [coercions :refer [as-int]]
                       [route :as route])
            (hiccup [page :refer [html5 include-css]]
                    [def :refer :all]
                    [util :refer :all :exclude [url-encode]]
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

(defn safe-as-int [n]
  (or (as-int n) 1))

(defroutes app
  (route/files "/media" {:root "public/media"})
  (GET "/" [p :<< safe-as-int :as {uri :uri}]
    (html5 (head)
        [:body
         (html/posts {:class "main"}
                     {:uri uri
                      :page p}
                     (-> q-base
                         (page (- p 1))
                         (select)))]))

  (GET "/tag/:tag" [tag p :<< safe-as-int :as {uri :uri}]
    (html5 (head)
        [:body
         (html/posts {:class "main"}
                     {:uri uri
                      :page p}
                     (-> q-base
                         (tagged (form-decode-str tag))
                         (page (- p 1))
                         (select)))]))

  (GET "/post/:id" [id :<< as-int]
    ;; Requesting nonexistant id leads to empty page
    (html5 (head)
        [:body
         (html/posts
          false
          (-> q-base
              (where {:id id})
              (limit 1)
              (select)))]))
  (route/not-found "404 Page"))

(def handler
  (wrap-defaults app site-defaults))
