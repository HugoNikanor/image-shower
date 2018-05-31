(ns image-shower.core
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            ))

(defroutes app
  (GET "/" [] "ROOT")
  (route/not-found "Not found"))

(def handler
  (wrap-defaults app site-defaults))
