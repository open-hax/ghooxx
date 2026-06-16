(ns ghooxx.plugin.client
  "HTTP client for talking to the ghooxx receiver service."
  (:require [clojure.string :as str]))

(defn- base-url []
  (or (aget js/process.env "GHOOXX_URL")
      "http://127.0.0.1:7999"))

(defn- request!
  [method path body]
  (let [url (str (base-url) path)
        init #js {:method method
                  :headers #js {"Content-Type" "application/json"}}
        _ (when body (aset init "body" (js/JSON.stringify (clj->js body))))]
    (-> (js/fetch url init)
        (.then (fn [res] (.text res)))
        (.then (fn [text]
                 (try
                   (js/JSON.parse text)
                   (catch :default _ text)))))))

(defn notify
  [payload]
  (request! "POST" "/api/v1/notify" payload))

(defn watch
  [payload]
  (request! "POST" "/api/v1/watch" payload))

(defn recent-events
  ([] (recent-events {}))
  ([params]
   (let [query (when (seq params)
                 (str "?" (str/join "&"
                         (map (fn [[k v]] (str (name k) "=" (js/encodeURIComponent (str v))))
                              params))))]
     (request! "GET" (str "/api/v1/events" (or query "")) nil))))
