(def POSTGRES-USER "arathunku")
(def POSTGRES-PASSWORD "1234")

(defproject photouploader "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [compojure "1.3.3"]
                 [hiccup "1.0.5"]
                 [ring-server "0.4.0"]
                 [clj-time "0.9.0"]
                 [liberator "0.12.2"]
                 [cheshire "5.4.0"]
                 [lib-noir "0.9.9"]
                 [byte-streams "0.2.0"]
                 [cheshire "5.4.0"]
                 [joplin.core "0.2.12"]
                 [joplin.jdbc "0.2.12"]
                 [postgresql/postgresql "9.3-1101.jdbc4"]
                 [korma "0.4.0"]]
  :plugins [[lein-ring "0.8.12"]
            [joplin.lein "0.2.12"]]
  :ring {:handler       photouploader.handler/app
         :init          photouploader.handler/init
         :destroy       photouploader.handler/destroy
         :auto-reload?  true
         :auto-refresh? true}
  :joplin {:migrators {:jdbc-mig "src/migrators/jdbc"}
           :databases {
                       :sql-dev      {:type :jdbc, :url ~(str "jdbc:postgresql://127.0.0.1:5432/clojure-fun-photoupload?user=" POSTGRES-USER "&password=" POSTGRES-PASSWORD)}
                       :sql-test      {:type :jdbc, :url ~(str "jdbc:postgresql://127.0.0.1:5432/clojure-fun-photoupload-test?user=" POSTGRES-USER "&password=" POSTGRES-PASSWORD)}
                       }
           :environments {
                          :dev [{:db :sql-dev, :migrator :jdbc-mig}]
                          :test [{:db :sql-test, :migrator :jdbc-mig}]
                          }
           }

  :profiles
  {
   :uberjar    {:aot :all}
   :production {:ring {:open-browser? false, :stacktraces? true, :auto-reload? true}}
   :dev        {:dependencies [[ring-mock "0.1.5"]
                               [midje "1.6.3"]
                               [ring/ring-devel "1.3.2"]]
                :plugins      []}})

