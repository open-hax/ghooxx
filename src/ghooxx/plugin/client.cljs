(ns ghooxx.plugin.client
  "HTTP client for talking to the ghooxx receiver service."
  (:require [clojure.string :as str]))

(defn- base-url []
  (or (aget js/process.env "GHOOXX_URL")
      "http://127.0.0.1:7999"))

(defn ^:async request!
  [method path body]
  (let [url (str (base-url) path)
        init #js {:method method
                  :headers #js {"Content-Type" "application/json"}}
        _ (when body (aset init "body" (js/JSON.stringify (clj->js body))))
        res (await (js/fetch url init))
        text (await (.text res))]
    (try
      (js/JSON.parse text)
      (catch :default _ text))))

(defn ^:async notify
  [payload]
  (await (request! "POST" "/api/v1/notify" payload)))

(defn ^:async watch
  [payload]
  (await (request! "POST" "/api/v1/watch" payload)))

(defn ^:async recent-events
  ([] (recent-events {}))
  ([params]
   (let [query (when (seq params)
                 (str "?" (str/join "&"
                         (map (fn [[k v]] (str (name k) "=" (js/encodeURIComponent (str v))))
                              params))))]
     (await (request! "GET" (str "/api/v1/events" (or query "")) nil)))))
