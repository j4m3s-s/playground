(ns script.main
  (:require [clojure.java.shell :refer [sh]]
            [clj-yaml.core :as yaml]
            [babashka.fs :as fs]
            [clojure.edn :as edn]
            [cheshire.core :as json])
  (:gen-class))

(defn kustomize-build
  [path]
  (yaml/parse-string (:out (sh "kustomize" "build" path)) :load-all true))

; Let's go simple :
; - b10s.edn (just merge the result of this)
;   Contains :kustomize [list of dirs]
;            :b10s [list of dirs]
;            :resources [{:apiVersion ...}]
; - b10s.bb execs it and returns edn out of it

(defn- k8s-merge-resources-top
  [acc resources]
  (if (empty? resources)
    nil
    (let [resource (first resources)
          remain (rest resources)

          kind (:kind resource)
          name (get-in resource [:metadata :name])
          namespace (get-in resource [:metadata :namespace])]
      (if (contains? acc [kind name namespace])
        (throw (Exception. (str "Error conflict on resource " name " in ns " namespace)))
        (recur (conj acc [kind name namespace]) remain)))))

; FIXME: what about non namespaced resources?
(defn k8s-merge-resources
  [resources]
  (if (= nil (k8s-merge-resources-top #{} resources))
    resources))

(defn b10s-edn-exec
  [path b10s-edn-path]
  (let [cfg (edn/read-string (slurp b10s-edn-path))]
    (k8s-merge-resources (flatten [
                                   (if (contains? cfg :kustomize)
                                     (kustomize-build (str path "/" (first (:kustomize cfg)))))
                                   (if (contains? cfg :resources)
                                     (:resources cfg))
                                   (if (contains? cfg :b10s)
                                     (eval (read-string
                                       (slurp (str path "/" (first (:b10s cfg)) "/b10s.bb")))))
                                   ]))
    ))

(defn mk-k8s-list
  "Generates a List manifest as k8s wants it"
  [elements]
  {:kind "List"
   :apiVersion "v1"
   :items elements
   :metadata {:resourceVersion ""}})

(defn b10s-exec
  [path]
  (let [b10s-edn-path (str path "/b10s.edn")]
    (if (fs/exists? b10s-edn-path)
      (json/generate-string (mk-k8s-list (b10s-edn-exec path b10s-edn-path))))
      ))

(defn -main [& args]
  (let [path (first args)]
    (println (b10s-exec path))
    ; FIXME: not sure why that's needed for faster exit
    (System/exit 0)))
