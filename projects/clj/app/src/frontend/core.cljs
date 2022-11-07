(ns frontend.core
  (:require [reagent.dom :as dom]
            [re-frame.core :as rf]))

(defn ui
  []
  [:div {:style {:color "darkred"}} "Hello world" ])

(defn render
  []
  (dom/render [ui]
              (js/document.getElementById "app")))

(defn run
  []
  (render))
