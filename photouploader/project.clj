(defproject photouploader "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [compojure "1.3.3"]
                 [hiccup "1.0.5"]
                 [ring-server "0.4.0"]
                 [liberator "0.12.2"]
                 [cheshire "5.4.0"]
                 [lib-noir "0.9.9"]
                 [byte-streams "0.2.0"]]
  :plugins [[lein-ring "0.8.12"]]
  :ring {:handler photouploader.handler/app
         :init photouploader.handler/init
         :destroy photouploader.handler/destroy
         :auto-reload? true
         :auto-refresh? true}
  :profiles
  {:uberjar {:aot :all}
   :production
   {:ring
    {:open-browser? true, :stacktraces? true, :auto-reload? true}}
   :dev
   {:dependencies [[ring-mock "0.1.5"] [ring/ring-devel "1.3.2"]]}})
