(ns reshipi-reframe.core
  (:require [reagent.dom :as dom]
            [re-frame.core :as rf]
            [cljs-http.client :as http]
            [cljs.core.async :refer [<! go]]))

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

(def test-data (go
                 (let [data
                       (get-in (<! (http/get "http://localhost:8000/api/v1/recipes")) [:body])]
  (rf/dispatch [:data data]))))


(def example-recipes-list
  [{:prep-time "00:00:10",
     :name "toto",
     :steps [{:id 1, :position 0, :text "ds"}],
     :created "2022-12-16",
     :modified "2022-12-16",
     :cook-time "00:00:10",
     :ingredients [{:id 1, :name "fff"}],
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

(def example-recipe-list
  (first example-recipes-list))

;; Utils

(defn enumerate
  [input]
  (map vector (range) input))

;; Components

(defn component-step
  [input]
  (let [step (second input)
        react-array-position (second input)

        _id (:id step)
        ; FIXME: sort by position
        _position (:position step)
        text (:text step)]
  [:li {:key (str react-array-position) } text]))

(defn component-steps
  [steps]
  ; We need to get array position in keys of a list in React
  (let [arr (enumerate steps)]
    [:div "Étapes :"
     [:ul (map component-step arr)]]))

(defn component-ingredient
  [input]
  (let [ingredient (second input)
        key (first input)
        name (:name ingredient)]
    ; NOTA BENE: We need a key with a number as a string for React to render
    ; effectively components and know whenever one of them changes.
    ; cf: https://reactjs.org/docs/lists-and-keys.html#keys
    [:li {:key key } [:p name ]]))

(defn component-ingredients
  [ingredients]
  ; We map here to get a zipped iterator of (position, ingredient)
  (let [arr (enumerate ingredients)]
    [:div "Ingrédients :"
     [:ul (map component-ingredient arr)]]))

(defn component-ustensil
  [ustensil]
  ; Currently it uses the same schema
  (component-ingredient ustensil))

(defn component-ustensils
  [ustensils]
  (let [arr (enumerate ustensils)]
    [:div "Ustensiles : "
     [:ul (map component-ustensil arr)]]))

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
    [:div
     [:h3.font-bold.text-gray-900.text-3xl.leading-tight.font-medium.mb-2 name]
     [:div
        [:div.text-gray-800.text-base "Temps total : " total-time]
        [:div.text-gray-600.text-sm "Temps de cuisson : " cook-time]
        [:div.text-gray-600.text-sm "Temps de préparation : " perform-time]]

     [:div.text-gray-600.text-xs "Créé le " created]
     [:div.text-gray-600.text-xs "Dernière modification le " modified]

     (component-ingredients ingredients)
     (component-ustensils ustensils)
     (component-steps steps)
     ]))

;; Main UI

(defn ui
  []
  [:div
   ;[:div @(rf/subscribe [:count])]
   ;[:div (->
   ;       (get-in @(rf/subscribe [:data]) [:results])
   ;       first
   ;       :name
   ;       )
   ;       ]
   [component-recipe example-recipe-list]
   ])

(defn render
  []
  (dom/render [ui]
              (js/document.getElementById "app")))

(defn run
  []
  (render))
