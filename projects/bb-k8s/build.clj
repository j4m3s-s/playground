(ns build
  (:require [clojure.tools.build.api :as b]))                     ; requiring tools.build

(def build-folder "target")
(def lib-name 'eu.j4m3s.b10s/project)
(def version "0.1.0")
(def jar-content (str build-folder "/classes"))
(def basis (b/create-basis {:project "deps.edn"}))
(def jar-file-name (format "%s/%s-%s.jar" build-folder (name lib-name) version))  ; path f


(defn clean [_]
  (b/delete {:path build-folder})                                 ; removing artifacts folder with (b/delete)
  (println (format "Build folder \"%s\" removed" build-folder)))

(defn jar [_]
  (clean nil)                                     ; clean leftovers

  (b/copy-dir {:src-dirs   ["src" "resources"]    ; prepare jar content
               :target-dir jar-content})

  (b/write-pom {:class-dir jar-content            ; create pom.xml
                :lib       lib-name
                :version   version
                :basis     basis
                :src-dirs  ["src"]})

  (b/jar {:class-dir jar-content                  ; create jar
          :jar-file  jar-file-name})
  (println (format "Jar file created: \"%s\"" jar-file-name)))

(def app-name "b10s")
(def uber-file-name (format "%s/%s-%s-standalone.jar" build-folder app-name version)) ; path for result uber file

(defn uber [_]
  (clean nil)

  (b/copy-dir {:src-dirs   ["resources"]         ; copy resources
               :target-dir jar-content})

  (b/compile-clj {:basis     basis               ; compile clojure code
                  :src-dirs  ["src"]
                  :class-dir jar-content})

  (b/uber {:class-dir jar-content                ; create uber file
           :uber-file uber-file-name
           :basis     basis
           :main      'script.main})                ; here we specify the entry point for uberjar

  (println (format "Uber file created: \"%s\"" uber-file-name)))
