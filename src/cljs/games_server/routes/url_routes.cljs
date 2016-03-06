(ns games-server.routes.url-routes
  (:require [secretary.core :as secretary :include-macros true]
            [reagent.session :as session]))


;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
                    (session/put! :page :snake))
