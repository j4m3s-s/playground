(ns b10s.main
  (:require [clojure.java.shell :refer [sh]]
            [clj-yaml.core :as yaml]
            [babashka.fs :as fs]
            [clojure.edn :as edn]
            [cheshire.core :as json])
  (:gen-class))

;; k8s utils

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
(defn- k8s-merge-resources
  [resources]
  (if (= nil (k8s-merge-resources-top #{} resources))
    resources))

(defn- mk-k8s-list
  "Generates a List manifest as k8s wants it"
  [elements]
  {:kind "List"
   :apiVersion "v1"
   :items elements
   :metadata {:resourceVersion ""}})

;; Utils

(defn- get-current-directory
  []
  (.getCanonicalPath (java.io.File. ".")))

; Changed dynamically via binding to reflect where the current working directory
; is. Note that file opened need to concatenate manually this path since there's
; no simple way to change current directory in Clojure/Java.
(def ^:dynamic *pwd* (get-current-directory))

(defmacro use-directory
  "Dir must be a relative directory"
  [dir form]
  `(binding [*pwd* (str *pwd* "/" ~dir)]
     ~form))

(defn file
  [filename]
  (str *pwd* "/" filename))

(defn- get-ns-name
  [content]
  (-> (read-string content)
      second))

;; Main b10s functions

(defn b10s-exec
  [filename]
  (let [content (slurp (file filename))
        ns-name (get-ns-name content)
        _ (if (re-find #"b10s" (str ns-name))
            (do
              (println (str "Error: File " filename " has a namespace which uses b10s, exiting"))
              (System/exit 1))
            (load-string content))
        build-fn (ns-resolve ns-name (symbol "build"))]
    (build-fn)))

#_ (b10s-exec "/test/toto.clj")

(defn kustomize-build
  [path]
  (yaml/parse-string (:out (sh "kustomize" "build" path)) :load-all true))

;; Main

; If there's a b10s.bb, load it
; Otherwise if there's a kustomization.yaml, switch to kustomize
(defn- -main [& args]
  (let [; We assume it's used as kustomize build ...
        _cmd (first args)
        path (second args)]
    (use-directory
     path
     (println (json/generate-string
               (if (fs/exists? (file "b10s.bb"))
                 (b10s-exec "b10s.bb")
                 (if (fs/exists? (file "kustomization.yaml"))
                   (kustomize-build (file "kustomization.yaml"))
                   ; Error out otherwise
                   (System/exit 1))))))
    ; FIXME: not sure why that's needed for faster exit
    (System/exit 0)))