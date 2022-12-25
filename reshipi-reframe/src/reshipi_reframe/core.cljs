(ns reshipi-reframe.core
  (:require [reagent.dom :as dom]
            [re-frame.core :as rf]
            [cljs-http.client :as http]
            [cljs.core.async :refer [<! go]]
            [reshipi-reframe.routes :as routes]))

;; State store

(rf/reg-event-db
 :data
 (fn [db [_ new]]
   (assoc db :data new)))

;; State queries

(rf/reg-sub
 :data
 (fn [db _]
   (:data db)))

(rf/reg-sub
 :count
 (fn [db _]
   (get-in db [:data :count])))

;; Example data

; Network data
;(def test-data (go
;                 (let [data
;                       (get-in (<! (http/get "http://localhost:8000/api/v1/recipes")) [:body])]
;  (rf/dispatch [:data data]))))


(def example-recipes-list
  [{:prep-time "00:00:10",
     :name "toto",
     :steps [{:id 1, :position 0, :text "ds"}],
     :created "2022-12-16",
     :modified "2022-12-16",
     :cook-time "00:00:10",
     :ingredients [{:id 1, :name "fff"},
                   {:id 2, :name "fff1"}
                   ],
     :ustensils [{:id 1, :name "fds"}],
     :id 1,
     :total-time "00:00:10",
     :perform-time "00:00:10"}
   {:prep-time "00:00:10",
     :name "tata",
     :steps [{:id 1, :position 0, :text "ds"}],
     :created "2022-12-16",
     :modified "2022-12-16",
     :cook-time "00:00:10",
     :ingredients [{:id 1, :name "fff"}],
     :ustensils [{:id 1, :name "fds"}],
     :id 1,
     :total-time "00:00:10",
     :perform-time "00:00:10"}
   ])

; Small look-alike of the network data for local usage
; FIXME only load this on DEV
(rf/dispatch [:data {:results example-recipes-list}])

(def example-recipe-list
  (first example-recipes-list))

;; Utils

(defn enumerate
  [input]
  (map vector (range) input))

;; Components

(defn component-step
  [input & last]
  (let [step (second input)
        react-array-position (second input)

        _id (:id step)
        ; FIXME: sort by position
        _position (:position step)
        text (:text step)]
  [:li {:key (str react-array-position)
        :class (str (if last "" "border-b ") "px-6 py-2 border-gray-200")}
   text]))

(defn component-steps
  [steps]
  ; We need to get array position in keys of a list in React
  (let [arr (enumerate steps)]
    (if (empty? steps)
      [:div "Aucune étapes."]
      [:div "Étapes :"
       [:ul
        (map component-step (drop-last arr))
        (component-step (last arr) true)
        ]])))

(defn component-ingredient
  [input & last]
  (let [ingredient (second input)
        key (first input)
        name (:name ingredient)]
    ; NOTA BENE: We need a key with a number as a string for React to render
    ; effectively components and know whenever one of them changes.
    ; cf: https://reactjs.org/docs/lists-and-keys.html#keys
    [:li
     {:key key
      :class (str (if last "" "border-b ") "px-6 py-2 border-gray-200")}
     [:p name ]]))

(defn component-ingredients
  [ingredients]
  ; We map here to get a zipped iterator of (position, ingredient)
  (let [arr (enumerate ingredients)]
    [:div "Ingrédients :"
     ; We separate the last iteration to make last element styling different
     [:ul.bg-white.rounded-lg.w-96.text-gray-900
      (map component-ingredient (drop-last arr))
      (component-ingredient (last arr) true)]]))

(defn component-ustensil
  [ustensil & last]
  ; Currently it uses the same schema
  (component-ingredient ustensil last))

(defn component-ustensils
  [ustensils]
  (let [arr (enumerate ustensils)]
    (if (empty? ustensils)
      [:div]
      [:div "Ustensiles : "
       [:ul.bg-white.rounded-lg.w-96.text-gray-900
        (map component-ustensil (drop-last arr))
        (component-ustensil (last arr) true)]])))

; FIXME: change title to current recipe
; FIXME: make date "human readable" instead of "00:00:10"
(defn component-recipe
  [recipe]
  (let [name (:name recipe)
        _id (:id recipe)

        created (:created recipe)
        modified (:modified recipe)

        cook-time (:cook-time recipe)
        total-time (:total-time recipe)
        perform-time (:perform-time recipe)

        ingredients (:ingredients recipe)
        steps (:steps recipe)
        ustensils (:ustensils recipe)]
    [:div.container.my-24.px-6.mx-auto
     [:div.block.rounded-lg.shadow-lg.bg-white
      [:div.px-6.py-6
       [:h3.font-bold.text-gray-900.text-3xl.leading-tight.font-medium.mb-2 name]
       [:span.flex.items-center.mb-6
        [:div.text-gray-800.text-base.mr-4 "Total : " total-time]
        [:div.text-gray-600.text-sm.mr-2 "Cuisson : " cook-time]
        [:div.text-gray-600.text-sm "Préparation : " perform-time]]

       [:span.flex.items-center.mb-6
        [:div.text-gray-400.text-xs.mr-4 "Créé le " created]
        [:div.text-gray-400.text-xs "Dernière modification le " modified]]

       [:div
        (component-ingredients ingredients)]
       [:div
        (component-ustensils ustensils)]
       (component-steps steps)
       ]]]))

; TODO: maybe add a small description ?
(defn component-recipe-list
  [input & last]
  (let [recipe (second input)
        index (first input)
        name (:name recipe)
        total-time (:total-time recipe)]
    [:li
     {:key index
      :class (str (if last "" "border-b ") "border-gray-200 w-full")}
     [:div.px-4.py-2
      [:h3 name]
      [:div.text-gray-500.text-base "Temps total " total-time]]]
  ))

(defn component-recipes-list
  [recipes]
  [:div
   [:ul.bg-white.rounded-lg.border.border-gray-200.w-96.text-gray-900
    (map component-recipe-list (enumerate recipes))]])

(defn component-link-test
  []
  [:div [:button {:on-click #(rf/dispatch [:set-active-panel :home-panel])} "home" ]
   [:button {:on-click #(rf/dispatch [:set-active-panel :about-panel])} "about" ]])

(defn component-home-test
  []
  [:div
   [component-link-test]
   [:div "Home sweet home"]])

(defn component-about-test
  []
  [:div
   [component-link-test]
   [:div "About Roundabout?"]])

(defn component-button-link
  [button-value panel-symbol css-class]
  [:button {:on-click #(rf/dispatch [:set-active-panel panel-symbol])
            :class css-class} button-value])

;; Views
; Technically they are just components. But in practice they're "top level"
; components in the sense that they have boilerplate for state/events handling.

(defn view-home
  []
  [:div
   [component-button-link "Home" :home-panel]
   ; FIXME: Don't hardcode the recipes list here
   [component-recipes-list example-recipes-list]])

;; State handling for current panel

(rf/reg-event-db
 :set-active-panel
 (fn [db [_ active-panel]]
   (assoc db :active-panel active-panel)))

(rf/reg-sub
 :active-panel
 (fn [db _]
   (:active-panel db)))

; Examples of use for panels
;(defmulti panels identity)
;(defmethod panels :home-panel [] [component-home-test])
;(defmethod panels :about-panel [] [component-about-test])
;(defmethod panels :default [] [component-home-test])

(defmulti panels identity)
(defmethod panels :home-panel [] [view-home])
(defmethod panels :default [] [view-home])

;; Main UI

(defn ui
  []
  (let [active-panel (rf/subscribe [:active-panel])]
       [:div
        ;[:div @(rf/subscribe [:count])]
        ;[:div (->
        ;       (get-in @(rf/subscribe [:data]) [:results])
        ;       first
        ;       :name
        ;       )
        ;       ]
        ;[component-recipe example-recipe-list]
        ;[component-recipes-list example-recipes-list]
        (panels @active-panel)
        ]))

(defn render
  []
  (dom/render [ui]
              ; FIXME automatic hot reload
              (js/document.getElementById "app")))

; FIXME small app to display FIXME/TODO in a code repository

; FIXME: fix create root warning in console wrt React changed the way to call its
; root render. This needs a new version release from reagent. See (notably)
; https://github.com/reagent-project/reagent/issues/583
; FIXME: routing, make the history/url part work
; See reitit.frontend for that. May mean we need to change router.

(defn run
  []
  (render))
