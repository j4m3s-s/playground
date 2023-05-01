(ns api.db)

(def blog-posts
  {"0" {:id "0"
        :content "Ho"
        :title "ho"
        :created 0
        :tags ["ho"]}
   "1" {:id "1"
        :content "Ha"
        :title "ha"
        :created 1
        :tags ["ha"]}})

(defn resolve-blog-post
  [_context args _value]
  (blog-posts (:id args)))

(defn resolve-blog-posts
  [_context _args _value]
  (map second blog-posts))

(defn resolve-tags
  [])

(defn create-blog-post
  [])
