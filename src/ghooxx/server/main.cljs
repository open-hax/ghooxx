(ns ghooxx.server.main
  "Ghooxx receiver service entrypoint."
  (:require ["fastify" :as Fastify]
            [ghooxx.server.webhooks :as webhooks]
            [ghooxx.server.api :as api]))

(defn main
  []
  (let [port-env (aget js/process.env "GHOOXX_PORT")
        port (if port-env (js/parseInt port-env) 7999)
        host (or (aget js/process.env "GHOOXX_HOST") "127.0.0.1")
        app (Fastify #js {:logger true})]
    (.register app (js/require "@fastify/formbody"))
    (webhooks/register-routes! app)
    (api/register-routes! app)
    (-> (.listen app #js {:port port :host host})
         (.then (fn [address]
                  (js/console.log (str "ghooxx receiver listening on " address))))
         (.catch (fn [err]
                   (js/console.error "failed to start ghooxx receiver" err)
                   (js/process.exit 1))))))

(set! *main-cli-fn* main)
