(ns image-shower.core
  (:require [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.util.codec :refer [form-decode-str]]
            (compojure [core :refer :all]
                       [coercions :refer [as-int]]
                       [route :as route])
            (image-shower [html :as html]
                          [util :as util]
                          [db :refer :all
                           :exclude [page]
                           :rename {page-filter page}])
            (korma [core :refer :all]
                   [db :as db]))
  (:refer-clojure :exclude [update]))

(defn page-fix [page-name]
  "Local helper function for :<< binding of page."
  (->> page-name
       entry-count
       util/page-count
       (util/as-int-within 1)))

(defn app [config]
  (compojure.core/routes
   (GET "/" []
     (html/full-page "/"
       "Page List"
       (html/page-list (-> (pages) (select)))))
   (context "/:page-name" [page-name]
     (route/files "/media" {:root (str (:data-path config)
                                       "/" page-name)})
     (GET "/" [p :<< (page-fix page-name)]
       (html/full-page (str "/" page-name)
         (fancy-name page-name)
         (html/posts {:class "main"}
                     (-> (entry-base)
                         (page page-name)
                         (content-page (- p 1))
                         (select))
                     :current-page p
                     :entry-count (entry-count page-name)
                     )))

     (GET "/tag/:tag" [tag p :<< (page-fix page-name)]
       (let [t (form-decode-str tag)]
         (html/full-page (str "/" page-name)
           (str t "@" (fancy-name page-name))
           (html/posts {:class "main"}
                       (-> (entry-base)
                           (page page-name)
                           (tagged t)
                           (content-page (- p 1))
                           (select))
                       :current-page p
                       :entry-count (entry-count page-name t)
                       ))))

     (GET "/post/:id" [id :<< as-int]
       ;; Requesting nonexistant id leads to empty page
       (html/full-page (str "/" page-name)
         (str id "@" (fancy-name page-name))
         (html/posts
          (-> (entry-base)
              (page page-name)
              (where {:id id})
              (limit 1)
              (select))))))

   (route/not-found "404 Page")))

(defn logger [handler]
  (fn [req]
    (println (str (:remote-addr req) "->" (:uri req)))
    (handler req)))


(defn make-handler [config]
  (let [database
        (db/create-db
         ((case (:db-type config)
            sqlite3  db/sqlite3
            postgres db/postgres
            (throw (new Exception)))
          (:db-args config)))]

    (db/default-connection database)
    (-> config
        app
        ;; logger
        (wrap-defaults site-defaults))))

(def handler
  ;; TODO how do I send configuration to 'lein ring server-headless'?
  ;; possibly an environment variable
  (let [config
        {:port 3000
         :host "0.0.0.0"
         :db-type 'sqlite3
         :db-args {:db "image-shower.db" }
         :data-path "public/"
         }]
    (make-handler config)))
