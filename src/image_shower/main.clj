(ns image-shower.main
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [image-shower.core :refer [make-handler]]
            [clojure.java.io :as io])
  (:gen-class))


(defn parse-args [args]
  (if (empty? args) {}
      (let [[argc dict]
            (case (first args)
              "--port" [2 {:port (Integer/valueOf (nth args 1))}]
              "--host" [2 {:host (nth args 1)}]
              "--file" [2 {:config-file (nth args 1)}]
              "--help" [1 {:help true}]
              :default (do
                         (prn "Warning, unknown flag/argument" (first args))
                         [1 {}]))]
        (merge dict (parse-args (drop argc args))))))

(def default-config
 {:port 3000
  :host "0.0.0.0"
  :db-type 'postgres
  :db-args {:db "image-shower" :user "hugo"}
  :data-path "/usr/local/var/image-shower"
  })

(defn find-config-file [spec]
  (cond (empty? spec)
        ;; this should ideally throw something more specific, but
        ;; FileNotFoundException is already taken
          (throw (new Exception))
        (vector? (first spec))
          (let [[fname arg] (first spec)]
            (if (.exists (io/file fname))
              fname
              (throw (new java.io.FileNotFoundException fname))))
        :default
          (let [fname (first spec)]
            (if (.exists (io/file fname))
              fname
              (find-config-file (rest spec))))))


(defn print-help []
  (println "Usage:
--port :: 3000
--host :: 0.0.0.0
--file :: config file, {/etc,~/.config}/image-shower/config.clj
--help"))

(defn xdg-config-home []
  (or (System/getenv "XDG_CONFIG_HOME")
      (str (System/getenv "HOME")
           "/.config")))

(defn -main [& args]
  (def opts (parse-args args))

  (when (:help opts) (print-help) (System/exit 0))


  (def config
    (merge (try
             (read-string
              (slurp
               (find-config-file
                [[(:config-file opts) :fail-if-missing]
                 (str (xdg-config-home) "/image-shower/config.clj")
                 "/etc/image-shower/config.clj"])))
             (catch java.io.FileNotFoundException e
               (println (str e))
               (System/exit 2))
             (catch Exception _
               default-config))
           opts))

  (prn "Running with the following settings"
       config)

  (run-jetty (make-handler config)
             {:port (:port config)
              :host (:host config)}))
