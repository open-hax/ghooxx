(ns ghooxx.plugin.entry
  (:require ["@opencode-ai/plugin" :refer [tool]]
            [ghooxx.plugin.schema :as schema]
            [ghooxx.plugin.client :as client]))

(defn- make-tool
  [{:keys [name description args execute]}]
  (tool
   #js {:description description
        :args (schema/object (or args {}))
        :execute (fn [js-args _ctx]
                   (execute (js->clj js-args :keywordize-keys true)))}))

(def github-notify-tool
  (make-tool
   {:name "github_notify"
    :description "Send a one-time GitHub event notification into ghooxx."
    :args {:repo [:string {:min 1}]
           :event [:string {:min 1}]
           :payload [:any]
           :channels {:optional true :default ["default"]
                      :type :any}}
    :execute (fn [args]
               (client/notify args))}))

(def github-watch-tool
  (make-tool
   {:name "github_watch"
    :description "Register or list watches for GitHub events."
    :args {:action [:string {:min 1}]
           :repo [:string {:min 1}]
           :events {:optional true :default []
                    :type :any}
           :channel {:optional true :type :string}}
    :execute (fn [args]
               (client/watch args))}))

(defn GhooxxPlugin []
  #js {"tool" #js {"github_notify" github-notify-tool
                   "github_watch" github-watch-tool}})

(defn ^:export init []
  (GhooxxPlugin))
