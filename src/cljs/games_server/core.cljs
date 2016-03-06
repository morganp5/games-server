(ns games-server.core
  (:require [reagent.core :as r]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [goog.events :as events]
            [goog.history.EventType :as HistoryEventType]
            [markdown.core :refer [md->html]]
            [re-frame.core :refer [dispatch dispatch-sync]]
            [games-server.handlers.handlers]
            [games-server.handlers.subs]
            [games-server.snake :as view]
            [games-server.views.navbar :as navbar]
            [games-server.routes.url-routes]
            [ajax.core :refer [GET POST]])
  (:import goog.History))

(def pages
  {:snake #'view/game})

(defn page []
  [(pages (session/get :page))])

;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (doto (History.)
        (events/listen
          HistoryEventType/NAVIGATE
          (fn [event]
              (secretary/dispatch! (.-token event))))
        (.setEnabled true)))

;; -------------------------
;; Initialize app
(defn fetch-docs! []
  (GET (str js/context "/docs") {:handler #(session/put! :docs %)}))

(defn mount-components []
  (r/render [#'page] (.getElementById js/document "app")))

(defn init! []
  (fetch-docs!)
  (dispatch-sync [:initialize])
  (hook-browser-navigation!)
  (mount-components))
