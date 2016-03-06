(ns games-server.routes.ws
  (:require [compojure.core :refer [GET POST defroutes]]
            [mount.core :refer [defstate]]
            [taoensso.sente :as sente]
            [taoensso.sente.server-adapters.immutant
             :refer [sente-web-server-adapter]]))

(defonce channels (atom #{}))

(let [connection (sente/make-channel-socket!
                   sente-web-server-adapter
                   {:user-id-fn
                    (fn [ring-req] (get-in ring-req [:params :client-id]))})]
  (def ring-ajax-post (:ajax-post-fn connection))
  (def ring-ajax-get-or-ws-handshake (:ajax-get-or-ws-handshake-fn connection))
  (def ch-chsk (:ch-recv connection))
  (def chsk-send! (:send-fn connection))
  (def connected-uids (:connected-uids connection)))

(defn handle-player-update!
  [{:keys [id client-id ?data]}]
  (println "\n\n+++++++ GOT PLAYER UPDATE:" id (keys ?data))
  (when (= id :snake/update-players)
    (let [response ?data]
      (if (:errors response)
        (println "\n\n+++++++ ERROR: " id (:errors response))
        (doseq [uid (:any @connected-uids)]
          (if (not= uid client-id)
            (chsk-send! uid [:snake/update-players response])))))))

(defn stop-router!
  [stop-fn]
  (when stop-fn (stop-fn)))

(defn start-router! []
  (println "\n\n+++++++ STARTING ROUTER! +++++++\n\n")
  (sente/start-chsk-router! ch-chsk handle-player-update!))

(defstate router
          :start (start-router!)
          :stop (stop-router! router))

(defroutes websocket-routes
           (GET "/ws" req (ring-ajax-get-or-ws-handshake req))
           (POST "/ws" req (ring-ajax-post req)))