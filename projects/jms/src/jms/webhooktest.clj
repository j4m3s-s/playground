(ns jms.webhooktest
  (:gen-class)
  (:require [io.pedestal.http :as http]
            [clojure.data.json :as json]
            [environ.core :refer [env]]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]
            [malli.core :as m]
            [malli.provider :as mp]
            [clojure.walk :as walk]
            [slingshot.slingshot :refer [try+]]))

;; Gitea Webhook JSON Malli Schema
;; for reference : https://github.com/metosin/malli
;; TODO: verify exactly why {:closed true} can't be put on those :map
(def user-schema
  [:map {:closed true}
   [:name     :string]
   [:email    :string] ;; TODO: parse email
   [:username :string]])

(def gitea-user
  [:map {:closed true}
   [:id         :int]
   [:login      :string]
   [:full_name  :string]
   [:email      :string]
   [:avatar_url :string] ;; TODO: parse URL
   [:username   :string]])

(def gitea-webhook-schema
  [:map
   [:secret      :string]
   [:ref         :string]
   [:before      :string]
   [:after       :string]
   [:compare_url :string]
   [:commits [:vector
               [:map
                [:id         :string]
                [:message    :string]
                [:url        :string]
                [:author     user-schema]
                [:committer  user-schema]
                [:timestamp  :string] ;; TODO: Parse date
                ]]]
   [:repository
    [:map
      [:id                :int]
      [:owner             gitea-user]
      [:name              :string]
      [:full_name         :string]
      [:description       :string]
      [:private           boolean?]
      [:fork              boolean?]
      [:html_url          :string] ;; TODO: url
      [:ssh_url           :string] ;; TODO: url
      [:clone_url         :string] ;; TODO: url
      [:website           :string] ;; TODO: url
      [:stars_count       :int]
      [:forks_count       :int]
      [:watchers_count    :int]
      [:open_issues_count :int]
      [:default_branch    :string]
      [:created_at        :string] ;; TODO parse date
      [:updated_at        :string] ;; TODO parse date
      ]]
   [:pusher gitea-user]
   [:sender gitea-user]])

;;;; Routes functions
;;;; ----------------
;; basic hello world endpoint
(defn respond-hello [req]
  {:status 200 :body (str (:json-params req) (:headers req))})

;; webhook endpoint
;; TODO: verify correct use of error codes
(defn webhook-post [req]
  (cond
    (not= (get-in req [:headers "x-gitea-event"]) "push")
    {:status 400 :body (json/write-str {:error "Not a push event"})}

    (= false
       (try+
        (m/validate gitea-webhook-schema (:json-params req))
        (catch Exception e
          ; TODO: reflection
          (do (println (.getMessage e))
              false))))
    {:status 400 :body (json/write-str {:error "Non valid json for webhook"})}

    :else {:status 200 :body (json/write-str {:event "submitted"})}))

;; Router
(def routes
  (route/expand-routes [[["/" ^:interceptors [(body-params/body-params)]
                          {:post `respond-hello }]
                         ["/webhook" ^:interceptors [(body-params/body-params)]
                          {:post `webhook-post}]]]))

;; Main server definition
(def service-map {::http/routes routes
                  ::http/type   :jetty
                  ::http/host   "0.0.0.0"
                  ::http/join?  false
                  ::http/port   (Integer. (or (env :port) 5000))
                  ;; Container options contains jetty specific configuration
                  ;; http://pedestal.io/reference/jetty
                  ::http/container-options {:h2c true}})

;; Main
(defn -main [& _] (-> service-map http/create-server http/start))

;; TODO: Put that unit tests ; use clojure.walk/keywordize-keys
(def webhook-json-example
  (json/read-str
   "{
  \"secret\": \"3gEsCfjlV2ugRwgpU#w1*WaW*wa4NXgGmpCfkbG3\",
  \"ref\": \"refs/heads/develop\",
  \"before\": \"28e1879d029cb852e4844d9c718537df08844e03\",
  \"after\": \"bffeb74224043ba2feb48d137756c8a9331c449a\",
  \"compare_url\": \"http://localhost:3000/gitea/webhooks/compare/28e1879d029cb852e4844d9c718537df08844e03...bffeb74224043ba2feb48d137756c8a9331c449a\",
  \"commits\": [
    {
      \"id\": \"bffeb74224043ba2feb48d137756c8a9331c449a\",
      \"message\": \"Webhooks Yay!\",
      \"url\": \"http://localhost:3000/gitea/webhooks/commit/bffeb74224043ba2feb48d137756c8a9331c449a\",
      \"author\": {
        \"name\": \"Gitea\",
        \"email\": \"someone@gitea.io\",
        \"username\": \"gitea\"
      },
      \"committer\": {
        \"name\": \"Gitea\",
        \"email\": \"someone@gitea.io\",
        \"username\": \"gitea\"
      },
      \"timestamp\": \"2017-03-13T13:52:11-04:00\"
    }
  ],
  \"repository\": {
    \"id\": 140,
    \"owner\": {
      \"id\": 1,
      \"login\": \"gitea\",
      \"full_name\": \"Gitea\",
      \"email\": \"someone@gitea.io\",
      \"avatar_url\": \"https://localhost:3000/avatars/1\",
      \"username\": \"gitea\"
    },
    \"name\": \"webhooks\",
    \"full_name\": \"gitea/webhooks\",
    \"description\": \"\",
    \"private\": false,
    \"fork\": false,
    \"html_url\": \"http://localhost:3000/gitea/webhooks\",
    \"ssh_url\": \"ssh://gitea@localhost:2222/gitea/webhooks.git\",
    \"clone_url\": \"http://localhost:3000/gitea/webhooks.git\",
    \"website\": \"\",
    \"stars_count\": 0,
    \"forks_count\": 1,
    \"watchers_count\": 1,
    \"open_issues_count\": 7,
    \"default_branch\": \"master\",
    \"created_at\": \"2017-02-26T04:29:06-05:00\",
    \"updated_at\": \"2017-03-13T13:51:58-04:00\"
  },
  \"pusher\": {
    \"id\": 1,
    \"login\": \"gitea\",
    \"full_name\": \"Gitea\",
    \"email\": \"someone@gitea.io\",
    \"avatar_url\": \"https://localhost:3000/avatars/1\",
    \"username\": \"gitea\"
  },
  \"sender\": {
    \"id\": 1,
    \"login\": \"gitea\",
    \"full_name\": \"Gitea\",
    \"email\": \"someone@gitea.io\",
    \"avatar_url\": \"https://localhost:3000/avatars/1\",
    \"username\": \"gitea\"
  }
}"))

(= true (m/validate gitea-webhook-schema (walk/keywordize-keys webhook-json-example)))
