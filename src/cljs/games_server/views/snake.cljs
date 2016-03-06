(ns games-server.snake
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [reagent.core :as reagent :refer [atom]]
            [re-frame.core :refer [subscribe dispatch]]
            [re-frame.core :refer [dispatch dispatch-sync]]
            [ajax.core :refer [GET]]
            [games-server.ws :as ws]
            [games-server.modal :as modal]
            [games-server.utils :as utils]))

(defn render-board
  "Renders game board area with snake and point to catch"
  []
  (let [board (subscribe [:board])
        snake (subscribe [:snake])
        point (subscribe [:point])]
    (fn []
      (let [[width height] @board
            snake-positions (into #{} @snake)
            current-point @point
            cells (for [y (range height)]
                    (into [:tr]
                          (for [x (range width)
                                :let [current-pos [x y]]]
                            (cond
                              (snake-positions current-pos) [:td.snake-on-cell]
                              (= current-pos current-point) [:td.point]
                              :else [:td.cell]))))]
         [:table.stage {:style {:height 377
                                     :width 527}}(into[:tbody]
              cells)]))))

(defn game-over
  "Renders game over overlay if game is finished"
  []
  (let [game-state (subscribe [:game])]
    (fn []
      (if @game-state
        [:div]
        [:div.overlay
         [:div.play {:on-click #(dispatch [:initialize])}
          [:h1 "↺" ]]]))))

(defn send-points
  []
  (let [points (subscribe [:all-players-points])]
    (ws/send-players-update! [:snake/update-players @points] 8000)))

(defn table-row
  [username [user points]]
  ^{:key (str user)}
    [(if (= username (str (name user))) :tr.user :tr)
     [:td.col-xs-9 (name user)]
     [:td.col-xs-3 (str points)]])

(defn score
  "Renders player's score"
  []
  (let [player-points (subscribe [:all-players-points])
        username (subscribe  [:username])]
    (fn []
      [:table.score
       [:tbody
        [:tr
         [:th.col-xs-9.head "Username"]
         [:th.col-xs-3.head "Points"]]
        (map (partial table-row @username) (take 5 (utils/sort-by-score @player-points)))]])))

(defn ws-response-handler
  []
  (fn [{[_ player-points] :?data}]
    (dispatch [:update-player-points player-points])))

(defonce snake-moving
         (js/setInterval #(dispatch [:next-state]) 125))

(defn game
  "Main rendering function"
  []
    (ws/start-router! (ws-response-handler))
    (send-points)
    [:div
     [modal/modal]
     [game-over]
     [score]
     [render-board]])