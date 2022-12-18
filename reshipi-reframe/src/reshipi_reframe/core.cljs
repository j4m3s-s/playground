(ns reshipi-reframe.core
  (:require [reagent.dom :as dom]
            [re-frame.core :as rf]
            [clojure.string :as str]
            [full.json :as json]
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

(defn ui
  []
  [:div
   [:h1 "test"]
   [:div "attttt"]
   [:div @(rf/subscribe [:count])]
   [:div (->
          (get-in @(rf/subscribe [:data]) [:results])
          first
          :name
          )
          ]
   ])

(defn render
  []
  (dom/render [ui]
              (js/document.getElementById "app")))

(defn run
  []
  (render))
