(ns image-shower.carousel
  (:require (hiccup [def :refer [defelem]]
                    [element :refer [image]])))

(declare item link indicator carousel)

(defelem carousel [head & tail]
  "Bootstrap carousel. Takes a list of hiccup components,
which becomes the slides of the carousel.
Has next and prev arrows, along with current slide info.
TODO look into using entries.id instead of gensym since that
     would work better with the cache."
  (let [id (gensym "carousel")]
    [:div.carousel.slide {:id id :data-interval "false"}
     [:ol.carousel-indicators
      (cons (indicator {:class "active"} id 0)
            (map-indexed (fn [idx _] (indicator id (+ 1 idx)))
                         tail))]
     [:div.carousel-inner (cons head tail)]
     (link {:href (str "#" id)} "prev" "Previous")
     (link {:href (str "#" id)} "next" "Next")]))

(defelem item [url & [active]]
  "One slide in a carousel, always an item because I only want items in carousels."
  [:div.carousel-item {:class (when active "active")}
   (image {:class "d-block w-100"}
          url)])

(defelem link [dir text]
  "Carousel prev/next button link"
  [:a {:class (str "carousel-control-" dir)
       :role "button"
       :data-slide dir}
   [:span {:class (str "carousel-control-" dir "-icon")
           :aria-hidden "true"}]
   [:span.sr-only text]])

(defelem indicator [id idx]
  "Carousel current slide indicator component."
  [:li {:data-target (str "#" id)
        :data-slide-to idx
        :class (when (zero? idx) "active")}])

