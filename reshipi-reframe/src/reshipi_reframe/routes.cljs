(ns reshipi-reframe.routes
  (:require [bidi.bidi :as bidi]
            [pushy.core :as pushy]
            [re-frame.core :as rf]))

; FIXME: make url recipe sharing work (this is super practical)
; FIXME: make "twitter cards" for link sharing
; FIXME: OpenGraph tags (same kind as twitter cards apparently ; created by FB)
; The above two are also related to server side rendering.
(def routes ["/" {"" :home
                  "about" :about}])

(defn- parse-url
  [url]
  (bidi/match-route routes url))

(defn- dispatch-route
  [matched-route]
  (let [panel-name (keyword (str (name  (:handler matched-route)) "-panel"))]
    (rf/dispatch [:set-active-panel panel-name])
    ))

(defn app-routes
  []
  (pushy/start! (pushy/pushy dispatch-route parse-url)))

(def url-for (partial bidi/path-for routes))
