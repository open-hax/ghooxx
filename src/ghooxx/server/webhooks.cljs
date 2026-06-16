(ns ghooxx.server.webhooks
  "GitHub webhook ingestion."
  (:require [ghooxx.server.store :as store]))

(defn ^:async handle-github
  [request reply]
  (let [event-type (or (aget (.-headers request) "x-github-event") "unknown")
        payload (js->clj (.-body request) :keywordize-keys true)
        repo (or (:repository payload) (:repo payload) "unknown")
        event {:source "github"
               :event event-type
               :repo (if (map? repo) (:full_name repo) repo)
               :payload payload
               :signature (aget (.-headers request) "x-hub-signature-256")}
        stored (store/add-event! event)
        matches (store/matching-watches event)]
    (.send reply (clj->js {:ok true
                             :id (:id stored)
                             :matches (count matches)}))))

(defn register-routes! [^js app]
  (.post app "/webhook/github" handle-github))
