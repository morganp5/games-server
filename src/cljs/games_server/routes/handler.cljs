(ns games-server.handlers.handlers
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :refer [register-handler
                                   register-sub
                                   dispatch]]
            [goog.events :as events]
            [games-server.utils :as utils]
            [games-server.ws :as ws]))

;; Define app data. We need to define our board, points and snake.
;; In our snake vector is in map desctibed position :position of every 'snake body' part.
;; First elemeny in vector id head of snake. Key :direction is the direction of the next move.

(def board [35 25])

(def snake {:direction [1 0]
            :body [[3 2] [2 2] [1 2] [0 2]]})

(def initial-state {:username           ""
                    :board              board
                    :snake              snake
                    :point              (utils/rand-free-position snake board)
                    :all-players-points {}
                    :game               true})

(register-handler  ;; setup initial state
  :initialize      ;; usage (submit [:initialize])
  (fn
    [db _]
    (if (not-empty (:username db))
      (merge db initial-state
             (select-keys db [:username :all-players-points]))
      db)))

(register-handler
  :next-state
  (fn
    [{:keys [snake board username] :as db} _]
    (if (:game db)
      (if (utils/collisions snake board)
        (-> db
            (assoc-in [:game] false)
            (assoc-in [:all-players-points (keyword username)] 0))
        (-> db
            (update-in [:snake] #(-> % (merge {:board board})
                                    (utils/move-snake)))
            (as-> after-move
                  (utils/process-move after-move)
                  after-move)))
      db)))

(register-handler
  :username-added
  (fn
    [db [_ username]]
    (-> db
        (assoc-in [:username] username)
        (assoc-in [:all-players-points] {(keyword username) 0}))))

(register-handler
  :update-player-points
  (fn
    [db points]
    (update-in db [:all-players-points] merge (second points))))

(register-handler
  :change-direction
  (fn [db [_ new-direction]]
    (update-in db [:snake :direction]
               (partial utils/change-snake-direction new-direction))))

;;Register global event listener for keydown event.
;;Processes key strokes according to `utils/key-code->move` mapping
(defonce key-handler
         (events/listen js/window "keydown"
                        (fn [e]
                          (let [key-code (.-keyCode e)]
                            (when (contains? utils/key-code->move key-code)
                              (dispatch [:change-direction (utils/key-code->move key-code)]))))))

