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

(defn systemd-stop
  [service]
  (sh "systemctl" "stop" service))

(defn systemd-start
  [service]
  (sh "systemctl" "start" service))

;; State

(def last-ping-1-1-1-1 (atom (now)))
(def last-ping-8-8-8-8 (atom (now)))

;; Main Functions

(defn launch-ping-watch
  [atom addr]
  (while true
    (when (not= 0 (:exit (sh "ping" "-c" "1" "-w" "1" addr)))
      (swap! atom (now))
      (async-wait-timeout))))

(def service-list
  ["gitea" "k3s"])

(defn launch-power-watch
  []
  (while true
    (let [elapsed (- (now) @last-ping-8-8-8-8)]
      (if (> elapsed internet-timeout)
       (map systemd-stop service-list)
       ; It's idenpotent to call start on an already started unit
       ; Except if it's simple and does not end semi-immediately
       (map systemd-start service-list)))
      (async-wait-timeout)))

; Entrypoint
(defn -main [& args]
  (go
    (launch-ping-watch last-ping-1-1-1-1 "1.1.1.1")
    (launch-ping-watch last-ping-8-8-8-8 "8.8.8.8")
    (launch-power-watch))
  (<!! (chan)))
