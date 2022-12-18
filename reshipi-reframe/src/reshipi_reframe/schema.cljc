(ns reshipi-reframe.schema
  (:require [malli.core :as m]
            [cheshire.core :as json]
            [clojure.walk :refer [keywordize-keys]]))

(def example-json
"{
    \"count\": 1,
    \"next\": null,
    \"previous\": null,
    \"results\": [
        {
            \"id\": 1,
            \"name\": \"toto\",
            \"cook_time\": \"00:00:10\",
            \"prep_time\": \"00:00:10\",
            \"perform_time\": \"00:00:10\",
            \"total_time\": \"00:00:10\",
            \"created\": \"2022-12-16\",
            \"modified\": \"2022-12-16\",
            \"ingredients\": [
                {
                    \"id\": 1,
                    \"name\": \"fff\"
                }
            ],
            \"ustensils\": [
                {
                    \"id\": 1,
                    \"name\": \"fds\"
                }
            ],
            \"steps\": [
                {
                    \"id\": 1,
                    \"position\": 0,
                    \"text\": \"ds\"
                }
            ]
        }
    ]
}
")

(def ingredient-schema
  (m/schema [:map {:closed true}
             [:id int?]
             [:name string?]
             ]))

(def ingredient-list-schema [:sequential ingredient-schema])

;; Currently they are the same, so re-using everything.
(def ustensil-schema ingredient-schema)
(def ustensil-list-schema ingredient-list-schema)

(def step-schema
  (m/schema [:map {:closed true}
             [:id int?]
             [:position int?]
             ;; FIXME: limit the size of the string
             [:text string?]]))

(def step-list-schema
  (m/schema [:sequential step-schema]))


(def recipe-schema
  (m/schema [:map
             [:count integer?]
             [:next nil?]
             [:previous nil?]
             [:results [:sequential [:map
                        [:id integer?]
                        [:name string?]

                        ; FIXME make stronger check on durations
                        [:cook_time string?]
                        [:prep_time string?]
                        [:perform_time string?]
                        [:total_time string?]

                        ; FIXME make stronger check on dates
                        [:created string?]
                        [:modified string?]

                        [:ingredients ingredient-list-schema]
                        [:ustensils ustensil-list-schema]
                        [:steps step-list-schema]]]]]))

(comment
  (m/validate ingredient-schema {:id 1 :name "toto"})
  (m/validate ingredient-list-schema [{:id 1 :name "toto"} {:id 2 :name "toto"}])

  (-> (json/parse-string example-json)
      keywordize-keys
      (#(m/validate recipe-schema %))
      ))
