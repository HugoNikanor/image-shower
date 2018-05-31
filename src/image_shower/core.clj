(ns image-shower.core
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [hiccup.core :refer [html]]
            [hiccup.page :refer [html5]]
            [garden.core :refer [css]]
            ))

(defroutes app
  (GET "/" [] "ROOT")
  (GET "/test" []
       (html5
        [:head
         [:meta {:charset :utf-8}]
         [:title "Hornquist"]
         [:style (css [:body
                       {:display :flex
                        :align-items "center"
                        :justify-content "center"
                        :margin "0px"
                        :width "100vw"
                        :height "100vh"}])]]
        [:body "This page intentionally left blank."]))
  (route/not-found "Not found"))

(def handler
  (wrap-defaults app site-defaults))
