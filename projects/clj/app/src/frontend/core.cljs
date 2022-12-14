(ns frontend.core
  (:require [reagent.dom :as dom]
            [re-frame.core :as rf]
            [clojure.string :as str]))

;; function dispatch event
(defn dispatch-timer-event
  []
  (rf/dispatch (let [now (js/Date.)]
                 [:timer now])))

;; Timer call
(defonce do-timer (js/setInterval dispatch-timer-event))

;; Event handler
(rf/reg-event-db
 :initialize
 (fn [_ _]
   {:time (js/Date.)
    :time-color "#f88"}))

(rf/reg-event-db
 :time-color-change
 (fn [db [_ new-color]]
   (assoc db :time-color new-color)))

(rf/reg-event-db
 :timer
 (fn [db [_ new-time]]
   (assoc db :time new-time)))

;; Query

(rf/reg-sub
 :time
 (fn [db _]
   (:time db)))

(rf/reg-sub
 :time-color
 (fn [db _]
   (:time-color db)))

(defn clock
  []
  [:div.example-clock
   {:style {:color @(rf/subscribe [:time-color])}}
   (-> @(rf/subscribe [:time])
       .toString
       (str/split " ")
       first)])

(defn color-input
  []
  [:div.color-input
   "Time color:"
   [:input {:type "text"
            :value @(rf/subscribe [:time-color])
            :on-change #(rf/dispatch [:time-color-change (-> % .-target .-value)])}]])

(defn ui
  []
  [:div
   [:h1 "hello !"]
   [clock]
   [color-input]])

(defn render
  []
  (dom/render [ui]
              (js/document.getElementById "app")))

(defn run
  []
  (rf/dispatch-sync [:initialize])
  (render))
