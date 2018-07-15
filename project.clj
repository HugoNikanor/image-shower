(defproject image-shower "0.1"
  :description "Web server for showing images"
  :url "http://github.com/hugonikanor/image-shower"
  :license {:name "The MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [ring/ring-defaults "0.3.1"] ; contains ring/core
                 [ring/ring-jetty-adapter "1.7.0-RC1"]
                 [ring/ring-codec "1.1.1"]
                 [compojure "1.6.1"]    ; defroutes
                 [hiccup "1.0.5"]       ; HTML
                 [garden "1.3.5"]       ; CSS
                 [korma "0.4.3"]        ; SQL
                 [org.postgresql/postgresql "9.4.1207"]
                 ]
  :plugins [[lein-ring "0.12.4"]]
  :ring {:handler image-shower.core/handler
         :nrepl {:start? true}
         })

