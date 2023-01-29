(ns reshipi-reframe.core
  (:require [reagent.dom :as dom]
            [reagent.ratom :as r]
            [re-frame.core :as rf]
            [cljs-http.client :as http]
            [cljs.core.async :refer [<! go]]
            [reshipi-reframe.routes :as routes]
            [clojure.string :as str]))

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
     :steps "- toto\n- tata",
     :created "2022-12-16",
     :modified "2022-12-16",
     :cook-time "00:00:10",
     :ingredients "- toto\n- titi",
     :ustensils "- toto\n- tata",
     :id 0,
     :total-time "00:00:10",
     :perform-time "00:00:10"}
   {:prep-time "00:00:10",
     :name "tata",
     :steps "- toto\n- tata",
     :created "2022-12-16",
     :modified "2022-12-16",
     :cook-time "00:00:10",
     :ingredients "- toto\n- tata",
     :ustensils "- toto\n- tata",
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

(defmacro infix
  [n]
  (if (list? n)
    (let [[arg1 op arg2] n]
      `(~op (infix ~arg1) (infix ~arg2)))
    n))

(defn enumerate
  [input]
  (map vector (range) input))

(defn set-panel
  ([panel-name]
   (rf/dispatch [:set-active-panel panel-name]))
  ([panel-name arg]
   (rf/dispatch [:set-active-panel panel-name arg])))

;; Components

(defn component-button-link
  [button-value css-class func]
  [:button {:on-click #(func)
            :class css-class} button-value])

(defn make-div
  [item]
  [:div item])

(defn make-line-separated-array
  [items]
  (-> items
      (str/split #"\n")
      (#(map make-div %))))

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

        ingredients (make-line-separated-array (:ingredients recipe))
        steps (make-line-separated-array (:steps recipe))
        ustensils (make-line-separated-array (:ustensils recipe))]
    [:div.container.my-24.px-6.mx-auto
     [:div.block.rounded-lg.shadow-lg.bg-white
      [:div.px-6.py-6
       [:h3.font-bold.text-gray-900.text-3xl.leading-tight.font-medium.mb-2 name]
       [:span.flex.items-center.mb-6
        [:div.text-gray-800.text-base.mr-4 "Total : " total-time]
        [:div.text-gray-600.text-sm.mr-2 "Cuisson : " cook-time]
        [:div.text-gray-600.text-sm "Préparation : " perform-time]]

       [:span.flex.items-center.mb-6
        [:div.text-gray-400.text-xs.mr-4 "Créée le " created]
        [:div.text-gray-400.text-xs "Dernière modification le " modified]]

       [:div ingredients]
       [:br]
       [:div ustensils]
       [:br]
       [:div steps]
       ]]]))

(defonce recipe-atom (r/atom
                      {:prep-time "00:00:10",
                       :name "toto",
                       :steps "- toto\n- tata",
                       :created "2022-12-16",
                       :modified "2022-12-16",
                       :cook-time "00:00:10",
                       :ingredients "- toto\n- tata",
                       :ustensils "- toto\n- tata",
                       :id 1,
                       :total-time "00:00:10",
                       :perform-time "00:00:10"}))

(defn swap-map-element
  [swap-atom map-entry new-value]
  (swap! swap-atom
           (fn [state] (merge state {map-entry (-> new-value .-target .-value)}))))

; TODO: provide a function to edit that knows the Recipe's id
(defn component-recipe-edit
  [_recipe]
  (let [
        name (:name @recipe-atom)
        _id (:id @recipe-atom)

        ;created (:created @recipe-atom)
        ;modified (:modified @recipe-atom)

        ;cook-time (:cook-time @recipe-atom)
        ;total-time (:total-time @recipe-atom)
        ;perform-time (:perform-time @recipe-atom)

        ingredients (:ingredients @recipe-atom)
        steps (:steps @recipe-atom)
        ustensils (:ustensils @recipe-atom)]
  [:div.container.my-24.px-6.mx-auto
   [:div.block.rounded-lg.shadow-lg.bg-white
    [:div.px-6.py-6
     [:h3.font-bold.text-gray-900.text-3xl.leading-tight.font-medium.mb-2 name]

     ; TODO: add time editor component
     ;[:span.flex.items-center.mb-6
     ; [:div.text-gray-800.text-base.mr-4 "Total : " total-time]
     ; [:div.text-gray-600.text-sm.mr-2 "Cuisson : " cook-time]
     ; [:div.text-gray-600.text-sm "Préparation : " perform-time]]

     ; FIXME: make network work
     ; FIXME: make newline/textarea work
     [:input {:type "text"
              :value ingredients
              :on-change (partial swap-map-element recipe-atom :ingredients)
              }]
     [:br]
     [:input {:type "text"
              :value ustensils
              :on-change (partial swap-map-element recipe-atom :ustensils)
              }]
     [:br]
     [:input {:type "text"
              :value steps
              :on-change (partial swap-map-element recipe-atom :steps)
              }]
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
      [:h3 [component-button-link name nil #(set-panel :recipe-panel index)]]
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

;; Views
; Technically they are just components. But in practice they're "top level"
; components in the sense that they have boilerplate for state/events handling.

(defn view-home
  []
  [:div
   ; FIXME: create proper styling
   [component-button-link "Home" nil #(set-panel :home-panel)]
   [component-button-link "recipe-1" nil #(set-panel :recipe-panel 1)]
   ; FIXME: Don't hardcode the recipes list here
   [component-recipes-list example-recipes-list]])

(defn view-recipe
  [_id]
  (let [recipe-id @(rf/subscribe [:active-panel-arg])
        recipe-values (nth (get-in @(rf/subscribe [:data]) [:results]) recipe-id)
        ]
    [:div
     [component-button-link "Home" nil #(set-panel :home-panel)]
                                        ; FIXME: use id to select recipe
     [component-recipe recipe-values]
     [component-recipe-edit recipe-values]]))

;; State handling for current panel

(rf/reg-event-db
 :set-active-panel
 ; FIXME: Is there a way to make this more proper ? Defmulti maybe?
 ; This looks like poor man's dispatch
 (fn [db args]
   (if (= 2 (count args))
     (let [active-panel (second args)]
       (assoc db :active-panel active-panel))
     (let [active-panel (second args)
           active-panel-arg (nth args 2)]
       (-> db
           (assoc :active-panel active-panel)
           (assoc :active-panel-arg active-panel-arg))))))

(rf/reg-sub
 :active-panel
 (fn [db _]
   (:active-panel db)))

(rf/reg-sub
 :active-panel-arg
 (fn [db _]
   (:active-panel-arg db)))

; Examples of use for panels
;(defmulti panels identity)
;(defmethod panels :home-panel [] [component-home-test])
;(defmethod panels :about-panel [] [component-about-test])
;(defmethod panels :default [] [component-home-test])

(defmulti panels identity)
(defmethod panels :home-panel [] [view-home])
(defmethod panels :recipe-panel [] [view-recipe])
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

(defn ^:dev/after-load run
  []
  ; The date/now is here to trigger hot reload automatically
  ; Maybe it shouldn't be here for production ?
  (dom/render [ui {:x (js/Date.now)}]
              ; FIXME automatic hot reload
              (js/document.getElementById "app")))

; FIXME small app to display FIXME/TODO in a code repository

; FIXME: fix create root warning in console wrt React changed the way to call its
; root render. This needs a new version release from reagent. See (notably)
; https://github.com/reagent-project/reagent/issues/583
; FIXME: routing, make the history/url part work
; See reitit.frontend for that. May mean we need to change router.
