(defproject jms "0.1.0"
  :description "Internal monorepo for cli tools and servers."
  :url "https://git.j4m3s.eu/monorepojitori"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [org.clojure/core.match "1.0.0"]]
  :repl-options {:init-ns jms.core}
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}}
  :main jms.core)
