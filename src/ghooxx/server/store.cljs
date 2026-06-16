(ns ghooxx.server.store
  "In-memory event + watch store. Replace with Redis/Postgres for production.")

(defonce state (atom {:events []
                      :watches []}))

(defn add-event! [event]
  (let [stored (assoc event
                      :id (str (random-uuid))
                      :received-at (js/Date.))]
    (swap! state update :events conj stored)
    stored))

(defn list-events
  ([] (list-events {}))
  ([{:keys [repo event limit]
     :or {limit 50}}]
   (let [events (reverse (:events @state))
         filtered (if repo
                    (filter #(= repo (:repo %)) events)
                    events)
         filtered (if event
                    (filter #(= event (:event %)) filtered)
                    filtered)]
     (take limit filtered))))

(defn add-watch! [watch]
  (let [stored (assoc watch
                      :id (str (random-uuid))
                      :created-at (js/Date.))]
    (swap! state update :watches conj stored)
    stored))

(defn list-watches
  ([] (:watches @state))
  ([repo] (filter #(= repo (:repo %)) (:watches @state))))

(defn matching-watches [event]
  (filter (fn [watch]
            (and (= (:repo watch) (:repo event))
                 (or (empty? (:events watch))
                     (some #(= % (:event event)) (:events watch)))))
          (:watches @state)))
