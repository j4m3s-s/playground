(ns experiments.k8s
  (:require [clojure.walk :as walk])
  (:gen-class))

; TODO: optional keys
; TODO: kustomize behavior (just call kustomize and merge output tbh)
; TODO: update-in function (I think it exists, not sure if it suits me)
; TODO: inherit macro
; TODO: macro for :selector.matchLabels.app
; TODO: deep-merge :selector.toto.a :selector.toto {:b :c :d :e}
; TODO: unit testing
; TODO: packaging

; Nice to have
; TODO: recursive nil pruning
; TODO: let-inherit macro
; TODO: def- macro
; TODO: contract checking OpenAPI
; TODO: manifest codegen
;   - read yaml
;   - transform to edn
;   - dispatch to a function for each type of resource
;   - gen code

;; DS Utils
(defn deep-merge
  [& maps]
  (if (every? map? maps) (apply merge-with deep-merge maps) (last maps)))

; FIXME: unit testing
#_ (deep-merge {:a {:b {:c :d}}}
            {:a {:b {:x :y}}})

(defn remove-empty-nils
  [m]
  (walk/postwalk
   (fn [x]
     (if (map? x)
       (->> (keep (fn [[k v]] (when (or (nil? v) (empty? v)) k)) x)
            (apply dissoc x))
       x))
   m))

; FIXME: unit testing
#_ (remove-empty-nils {:a nil :c {:b nil}})

;; Macro utils

(println {:toto.a.b :b
 :toto.a {:e :f}})

(defmacro inherit
  [& something]
  {:something something})

;; k8s external utils
(defn kustomize
  [filepath]
  (-> :command-exec filepath
      :out
      :parse-yaml-to-edn
      ))

;; k8s utils functions

(def ^:private -manifest-example
  {:apiVersion "v1"
   :kind "Service"
   :spec {:selector {:app "toto"
                     :component "toto"}
          :ports [{:protocol "TCP"
                   :port 80
                   :targetPort 80}]}
   })

(defn- extract-unique-tuple [resource]
  [(:kind resource)
   (get-in [:metadata :name] resource)
   (let [ns
         (get-in [:metadata :namespace] resource)]
     (or ns "default"))
   ])

; Basically a k8s conflict happens when :
; - two or more elements have the same tuple (name, ns, kind)
(defn- set-insert-throw
  [element container]
  (if (contains? element container)
    (throw (Exception. (str "Element " element " is already contained")))
    (merge container element)
    ))

(defn detect-conflicts
  [& elements]
  (-> elements
      (#(map extract-unique-tuple %))
      (#(reduce set-insert-throw #{} %))
      ))

;; k8s functions
(defn make-deployment
  [labels annotations component namespace replicas app containers]
  (let [labels (merge {:app app
                :component component} labels)]
    {:apiVersion "apps/v1"
     :kind "Deployment"
     :metadata {:labels labels
                :annotations annotations
                :name component
                :namespace namespace}
     :spec {:replicas replicas
            :selector {:matchLabels labels}
            :template {:metadata {:labels labels}
                       :spec {:containers containers}}}}))

(defn make-single-image-deployment
  [labels annotations component namespace replicas app image]
  (make-deployment labels annotations component namespace replicas app {:name app
                                                                        :image image}))

(defn make-service
  [metadata ]
  {:apiVersion "v1"
   :kind "Service"
   :metadata metadata})

#_ (make-deployment )
