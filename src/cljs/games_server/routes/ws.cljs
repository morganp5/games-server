(ns games-server.ws
  (:require [taoensso.sente :as sente]))

(let [connection (sente/make-channel-socket! "/ws" {:type :auto})]
  (def ch-chsk (:ch-recv connection))
  ; ChannelSocket's receive channel
  (def send-players-update! (:send-fn connection)))

(defn state-handler
  [{:keys [?data]}]
  (.log js/console (str "state changed: " ?data)))

(defn handshake-handler
  [{:keys [?data]}]
  (.log js/console (str "connection established: " ?data)))

(defn default-event-handler
  [ev-msg]
  (.log js/console (str "Unhandled event: " (:event ev-msg))))

(defn event-msg-handler
  [& [{:keys [response-handler state handshake]
       :or {state state-handler
            handshake handshake-handler}}]]
  (fn [ev-msg]
    (case (:id ev-msg)
      :chsk/handshake (handshake ev-msg)
      :chsk/state (state ev-msg)
      :chsk/recv (response-handler ev-msg)
      (default-event-handler ev-msg))))

(def router (atom nil))

(defn stop-router! []
  (when-let [stop-f @router] (stop-f)))

(defn start-router!
  [response-handler]
  (stop-router!)
  (reset! router (sente/start-chsk-router!
                   ch-chsk
                   (event-msg-handler
                     {:response-handler response-handler
                      :state handshake-handler
                      :handshake state-handler}))))

