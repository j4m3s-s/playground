(ns powerwatchd.core
  (:require [clojure.java.shell :refer [sh]]
            [clojure.core.async :refer [timeout <! go chan <!!]])
  (:gen-class))

;; Main constants

; 5 minutes
(def internet-timeout
  (* 1000 60 5))

; 15s
(def wait-timeout
  (* 1000 15))

;; Utils

(defn now
  []
  (System/currentTimeMillis))

(defn async-wait-timeout
  []
  (<! (timeout wait-timeout)))

;; State

(def last-ping-1-1-1-1 (atom (now)))
(def last-ping-8-8-8-8 (atom (now)))

;; Main Functions

(defn launch-ping-watch
  [atom addr]
  (while true
    (if (not= 0 (:exit (sh "ping" "-c" "1" "-w" "1" addr)))
      (do (swap! atom (now))
          (async-wait-timeout)))))

(defn launch-power-watch
  []
  (while true
    (let [elapsed (- (now) @last-ping-8-8-8-8)]
      (if (> elapsed internet-timeout)
        (println "Oh no")
        (async-wait-timeout)))))

; Entrypoint
(defn -main [& args]
  (go
    (launch-ping-watch last-ping-1-1-1-1 "1.1.1.1")
    (launch-ping-watch last-ping-8-8-8-8 "8.8.8.8")
    (launch-power-watch))
  (<!! (chan)))
