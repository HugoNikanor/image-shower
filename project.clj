(defproject image-shower "0.2"
  :description "Web server for showing images"
  :url "http://github.com/hugonikanor/image-shower"
  :license {:name "The MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [ring "1.6.1"]
                 [ring/ring-defaults "0.3.1"]
                 [compojure "1.6.1"]    ; defroutes
                 [hiccup "1.0.5"]       ; HTML
                 [garden "1.3.5"]       ; CSS
                 [korma "0.5.0-RC1"]        ; SQL
                 [org.postgresql/postgresql "9.4.1207"]
                 [org.xerial/sqlite-jdbc "3.27.2.1"]
                 ]
  :plugins [[lein-ring "0.12.4"]]
  :ring {:handler image-shower.core/handler
         :nrepl {:start? true}}
  :main image-shower.main
  :profiles {:uberjar {:aot :all
                       :main image-shower.main}})

