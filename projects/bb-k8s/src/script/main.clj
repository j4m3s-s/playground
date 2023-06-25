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

(defn mk-k8s-list
  "Generates a List manifest as k8s wants it"
  [elements]
  {:kind "List"
   :apiVersion "v1"
   :items elements
   :metadata {:resourceVersion ""}})


(defn -main [& args]
  (let [; We assume it's used as kustomize build ...
        _cmd (first args)

        path (second args)]
    (println (b10s-exec path))
    ; FIXME: not sure why that's needed for faster exit
    (System/exit 0)))
