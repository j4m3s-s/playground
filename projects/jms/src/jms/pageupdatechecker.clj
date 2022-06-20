(ns jms.pageupdatechecker
  (:gen-class)
  (:require
   ;; http client
   [clj-http.client :as client]

   ;; clojure stdlib
   [clojure.string :as str]

   ;; advanced exceptions handling
   [slingshot.slingshot :as slg]

   ;; database handling
   [clojure.java.jdbc :as db]
   ;; SQL DSL
   [honey.sql.helpers :as sqlhelp]
   [honey.sql :as sql]

   ;; time API
   [clj-time.core :as t]
   [clj-time.coerce :as tc]))

; Manga Functions
(defn is-bha-available? [nb]
  (not
    (str/includes?
     (slg/try+
       (client/get (str "https://w2.read-heroacademia.com/manga/boku-no-hero-academia-chapter-" nb "/"))
       ; If it's not yet displayed on the site
       (catch [:status 404] {}
         "not available"))
       "not available")))

(def db
  {:classname "org.sqlite.JDBC"
   :subprotocol "sqlite"
   :subname "db.sqlite3" })

(defn init-db
  "Create DB and table"
  []
  (slg/try+
   (db/db-do-commands db
                      (db/create-table-ddl :page_update
                                           [[:timestamp :datatime :default :current_timestamp]
                                            [:name :string :unique]
                                            [:number :int]]
                                           {:conditional? true}))
   (catch Exception e
     (println (.getMessage e)))))

;; Init
(init-db)

;; Foreign keys
;; (db/execute! db "PRAGMA foreign_keys = ON")

;; Insertion
;; default values
(slg/try+
  (db/insert! db :page_update
   {:name "boku no hero academia"
    ;; starting point
    :number "354"})
  ; If it's already inside the DB, it breaks on unicity constraint
  ; but we don't care since we only care about empty DB
  ; TODO: Use more precised Exception
  (catch Exception _
    (println "Failed to insert initial bhna value")))

(defn get-last-chapter-checked
  [name]
  (db/query db
            (-> {:select [:number]
                 :from [:page_update]
                 :where [:= :page_update.name name]}
                (sql/format))))

(defn set-last-chapter-checked
  [name nb]
  (db/execute! db
            (->
             {:update :page_update
              :set {:number nb}
              :where [:= :page_update.name name]}
             (sql/format))))

;; Scheduler
;; TODO: make it thread safe
(def tasks-example
  "List of examples tasks for a scheduler interface"
  [{:name "Task name"
    :timer-fn #(t/now)
    :exec-fn #(println %)
    :one-shot false
    :pre-hook "toto"
    :post-hook "toto"}
   {:name "Task name"
    :timer-fn #(t/plus (t/now) (t/seconds 10))
    :exec-fn "toto"
    ; TODO: fix -> currently unused
    :one-shot false
    :pre-hook "toto"
    :post-hook "toto"}
   ])

; TODO: use https://github.com/finity-ai/clj-cron-parse
(def timed-tasks-array-example
  "Internal array for scheduler execution"
  (atom [{:next-schedule ((:timer-fn (first tasks-example)))
          :task (first tasks-example)}
         {:next-schedule ((:timer-fn (second tasks-example)))
          :task (second tasks-example)}]))

(defn create-scheduler
  []
  "toto")



;; Scheduler works this way
;;; Initial creation
;; 1. Create a timed task array by calling :timer-fn on each entry
;;    of tasks array
;; 2. Order by time the array
;; 3. Wait for next task
;;
;;; Each run
;; 1. Run current task
;; 2. Unqueue it
;; 3. Call :timer-fn on tasks, insert-sort in timed task array
;; 4. Sleep until next task

(defn run-scheduler
  "Blocking function"
  [_sch]
  (let [task (first @timed-tasks-array-example)
        rest (rest @timed-tasks-array-example)]
    ; execute task
    ((get-in task [:task :exec-fn]) task)
    ; get new task, insert and sort task array
    (let [next-schedule ((get-in task [:task :timer-fn]))
          ; TODO: fix sort
          new-task (assoc {} :next-schedule next-schedule :task task)
          new-timed-array (cons new-task rest)
          sorted-new-timed-array (sort-by #(tc/to-long (:next-schedule %)) new-timed-array)]
      (reset! timed-tasks-array-example sorted-new-timed-array))))

;(run-scheduler :t)

(def time-array
  [ (t/now) (t/plus (t/now) (t/seconds -20))])

;(sort-by tc/to-long time-array)

;(t/after? (first time-array) (second time-array))

(defn -main [& _] (println "toto"))
