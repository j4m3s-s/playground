(ns reshipi-reframe.core
  (:require [reagent.dom :as dom]
            [re-frame.core :as rf]
            [clojure.string :as str]
            [cljs-http.client :as http]
            [cljs.core.async :refer [<! go]]))


(rf/reg-event-db
 :data
 (fn [db [_ new]]
   (assoc db :data new)))

(rf/reg-sub
 :data
 (fn [db _]
   (:data db)))

(rf/reg-sub
 :count
 (fn [db _]
   (get-in db [:data :count])))

(def test-data (go
                 (let [data
                       (get-in (<! (http/get "http://localhost:8000/api/v1/recipes")) [:body])]
  (rf/dispatch [:data data]))))


(def example-recipe-list
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
  (first example-recipe-list))

(defn component-step
  [step]
  (let [_id (:id step)
        ; FIXME: sort by position
        _position (:position step)
        text (:text step)]
  [:li text]))

(defn component-steps
  [steps]
  [:div "Étapes :"
   [:ul (map component-step steps)]])

(defn component-ingredient
  [ingredient]
  (let [name (:name ingredient)]
    [:li name ]))

(defn component-ingredients
  [ingredients]
  [:div "Ingrédients :"
   [:ul (map component-ingredient ingredients)]])

(defn component-ustensil
  [ustensil]
  ; Currently it uses the same schema
  (component-ingredient ustensil))

(defn component-ustensils
  [ustensils]
  [:div "Ustensiles : "
   [:ul (map component-ustensil ustensils)]])

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
     [:h1 name]
     [:div "Temps de cuisson : " cook-time]
     [:div "Temps total : " total-time]
     [:div "Temps de préparation : " perform-time]

     [:div "Créé le " created]
     [:div "Dernière modification le " modified]

     (component-ingredients ingredients)
     (component-ustensils ustensils)
     (component-steps steps)
     ]))

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
