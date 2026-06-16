(ns ghooxx.server.api
  "OpenCode-compatible API surface for the ghooxx plugin tools."
  (:require [ghooxx.server.store :as store]))

(defn ^:async notify
  [request reply]
  (let [payload (js->clj (.-body request) :keywordize-keys true)
        event (assoc payload :source "plugin")
        stored (store/add-event! event)]
    (.send reply (clj->js {:ok true :id (:id stored)}))))

(defn ^:async watch
  [request reply]
  (let [payload (js->clj (.-body request) :keywordize-keys true)
        action (keyword (:action payload "add"))]
    (case action
      :add
      (let [watch (store/add-watch! (select-keys payload [:repo :events :channel]))]
        (.send reply (clj->js {:ok true :watch watch})))

      :list
      (.send reply (clj->js {:ok true :watches (store/list-watches (:repo payload))}))

      (.send reply (clj->js {:ok false :error (str "unknown action: " (name action))})))))

(defn ^:async events
  [request reply]
  (let [query (js->clj (.-query request) :keywordize-keys true)
        events (store/list-events query)]
    (.send reply (clj->js {:ok true :events events}))))

(defn ^:async health
  [_request reply]
  (.send reply (clj->js {:status "ok" :service "ghooxx"})))

(defn register-routes! [^js app]
  (.get app "/health" health)
  (.post app "/api/v1/notify" notify)
  (.post app "/api/v1/watch" watch)
  (.get app "/api/v1/events" events))
