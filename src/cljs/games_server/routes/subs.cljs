(ns games-server.handlers.subs
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :refer
             [register-handler
              register-sub
              subscribe
              dispatch]]
            [games-server.ws :as ws]))
;; ---- Subscription Handlers ----

(register-sub
  :board
  (fn
    [db _]                         ;; db is the app-db atom
    (reaction (:board @db))))      ;; wrap the computation in a reaction

(register-sub
  :snake
  (fn
    [db _]
    (reaction (:body (:snake @db)))))

(register-sub
  :point
  (fn
    [db _]
    (reaction (:point @db))))

(register-sub
  :game
  (fn
    [db _]
    (reaction (:game @db))))

(register-sub
  :username
  (fn
    [db _]
    (reaction (:username @db))))

(register-sub
  :all-players-points
  (fn
    [db _]
    (reaction (:all-players-points @db))))