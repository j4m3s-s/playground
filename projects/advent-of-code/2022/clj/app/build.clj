(ns build
  (:require [clojure.tools.build.api :as b]))

(def build-folder "target")

(defn clean [_]
  (b/delete {:path build-folder})
  (println (format "Build folder \"%s\" removed" build-folder)))

(def jar-content (str build-folder "/classes"))

(def lib-name 'com.github.YOURNAME/mymath-lib)
(def version "0.0.1")

(def basis (b/create-basis {:project "deps.edn"}))
(def jar-file-name (format "%s/%s-%s.jar" build-folder (name lib-name) version))  ; path for result jar file

(def app-name "myapp")
(def uber-file-name (format "%s/%s-%s-standalone.jar" build-folder app-name version)) ; path for result uber file


(defn clean [_]
  (b/delete {:path build-folder})
  (println (format "Build folder \"%s\" removed" build-folder)))

(defn jar [_]
  (clean nil)
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
           :main      'app.core})                ; here we specify the entry point for uberjar

  (println (format "Uber file created: \"%s\"" uber-file-name)))
