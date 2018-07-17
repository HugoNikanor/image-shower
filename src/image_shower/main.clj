(ns image-shower.main
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [image-shower.core :refer [handler]])
  (:gen-class))

(defn -main [& args]
  (let [port (Integer/valueOf (or (System/getenv "port") "3000"))]
    (run-jetty handler {:port port})))
