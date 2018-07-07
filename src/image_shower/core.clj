(ns image-shower.core
  (:require [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            (compojure [core :refer :all]
                       [coercions :refer [as-int]]
                       route)
            (hiccup [page :refer [html5]]
                    [def :refer :all]
                    [element :refer :all])
            (garden [core :refer [css]]
                    [units :refer :all :exclude [rem]])
            ))

(defn lorem [pars]
  "Function which gives a number of pre-generated
lorem ipsum text"
  (take pars
        ["Lorem ipsum dolor sit amet, at dico libris has. Labitur virtute
  no cum. Ius unum augue cu. Hinc intellegebat usu ne, quodsi phaedrum
  interpretaris ut per. Ius et mollis insolens, vel dolor aliquip ut, ut qui
  menandri tractatos quaerendum."

         "Tale ferri moderatius mel ea, unum lucilius disputationi sit an. Vix te cetero
integre invenire, malis saperet ponderum his ne. Magna dicta detraxit sea te,
vituperata definitiones mediocritatem in eos. Nam ad numquam consetetur
deterruisset."

         "Eum ut hinc falli, te movet ornatus fastidii mea. Ius epicurei sensibus eu. Usu
scripta dolores et, ius ut nisl esse legere. His sumo aeque epicurei no,
democritum appellantur no vix, viderer integre an vix. Id nam commodo civibus
philosophia, pri ei veniam vivendum elaboraret."

         "Ex etiam choro veritus sea, no falli nostrud nominati vix. Quis esse vel in,
mazim harum adversarium te cum. Mea possit forensibus an, id mei dicat ponderum
inimicus, qui no elit tollit. Te has erat pertinacia."

         "Agam recusabo volutpat no vix. Cibo soleat inimicus cum at. Mel illum numquam
habemus ei. Vim alia mazim atomorum ut, ad ius nisl imperdiet."

         "Integre omnesque an per, quis dicant putent sed ne, vim an omnium
iuvaret. Dolore tamquam pericula usu at, te his animal fierent oporteat. Eruditi
suscipit mea id, id sea ridens platonem. Ponderum pertinax ne eam, ut mea omnes
eloquentiam. In incorrupte cotidieque nec, quo id novum dicant rationibus, sea
in everti assentior. Ludus offendit intellegebat eam an. Ex eruditi corrumpit
rationibus est."

         "Docendi laboramus at pri, id essent ocurreret pro, mel at nullam deserunt
electram. Sea et eros debitis constituto, vivendum scripserit no usu, te
copiosae ocurreret eam. An ius eius illum facete, ut veri impedit petentium
sed. Per ex ocurreret scriptorem."

         "Pri agam elit ad, pri no primis nominavi erroribus, ei vix regione evertitur
torquatos. Nec prompta suavitate pertinacia eu, ad sint delicata explicari qui,
aperiam inermis menandri per ut. Ea mel minimum verterem argumentum, id qui amet
facilisis deseruisse. Duo reque qualisque an. Ad quo decore volutpat, vel idque
verear ancillae ex, eum ea tempor eleifend gubergren."

         "Vim dicat luptatum definiebas ei, habeo facete eu eum, ut eum graece gubergren
constituto. Graeco vituperatoribus ex quo. Dicat nulla pericula ut pri, ullum
exerci democritum mel cu, id vis invenire prodesset sadipscing. In semper omnium
mea, id sea facer essent evertitur."

         "Pro labore insolens constituam no. His dicit inimicus ex, percipit expetenda
  suavitate sit cu. Eu ubique aliquid sit. Mea ne solet corrumpit argumentum,
  sed te mazim epicuri. Vel id case adhuc recusabo."])
  )

(def page-css
  (css
   [:article {:max-width (px 500)
              :border "1px solid black"
              :padding (em 1)
              :margin (em 2)}
    [:* {:max-width "100%"}]
    [:.text {:border-bottom "1px solid gray"}]]
   ))

(defelem image-slide [url text]
  "One container containing an image with text"
  [:article
   [:div.image (image url)]
   [:div.text text]
   [:div.controll]]
  )

(defroutes app
  (GET "/" [] "ROOT")
  (GET "/request" request (str request))
  ;; These are just test things
  (context "/u" []
           (GET "/" [] "No such user")
           (GET "/:user" [user :as req] (str user req))
           (GET "/id/:id" [id :<< #(* 2 (as-int %))]
                (str id)))
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
  (GET "/page" []
       (html5
        [:head
         [:meta {:charset :utf-8}]
         [:title "Hornquist"]
         [:style page-css]]
        [:body
         [:div.articles
          (take 3
           (repeat
            (image-slide "https://i.imgur.com/uCAU4rJ.jpg"
                         (lorem 2))))
          ]
         ]))
  (route/not-found "Not found"))

(def handler
  (wrap-defaults app site-defaults))
