(ns games-server.utils
  (:require [reagent.core :as reagent :refer [atom]]))

(def key-code->move
  "Mapping from integer key code to direction vector corresponding to that key"
  {38 [0 -1]
   40 [0 1]
   39 [1 0]
   37 [-1 0]})

(defn rand-free-position
  "Fuction takes snake and board-size as arguments.,
  and returns random position not colliding wit snake body"
  [snake [x y]]
  (let [snake-positions-set (into #{} (:body snake))
        board-positions (for [x-pos (range x)
                              y-pos (range y)]
                          [x-pos y-pos])]
    (when-let [free-positions (seq (remove snake-positions-set board-positions))]
      (rand-nth free-positions))))

(defn collisions
  "Returns true if snake collision"
  [snake board]
  (let [{:keys [body direction]} snake
        future-x (+ (first direction) (ffirst body))
        future-y (+ (second direction) (second (first body)))]
    (contains? (into #{} (rest body)) [future-x future-y])))

(defn change-snake-direction
  "Changes snake head direction, only when it's perpendicular to the old head direction"
  [[new-x new-y] [x y]]
  (if (or (= x new-x)
          (= y new-y))
    [x y]
    [new-x new-y]))

(defn move-snake
  "Move the whole snake based on positions and directions for each snake body segments"
  [{:keys [direction body board] :as snake}]
  (let [head-new-position (mapv #(mod (+ %1 %2) %3)  direction (first body) board)]
    (update-in snake [:body] #(into [] (drop-last (cons head-new-position body))))))

(defn snake-tail
  "Computes x or y tail coordinate according to last 2 values of that coordinate"
  [coordinate-1 coordinate-2]
  (if (= coordinate-1 coordinate-2)
    coordinate-1
    (if (> coordinate-1 coordinate-2)
      (dec coordinate-2)
      (inc coordinate-2))))

(defn grow-snake
  "Append new tail body segment to snake"
  [{:keys [body direction] :as snake}]
  (let [[[first-x first-y] [sec-x sec-y]] (take-last 2 body)
        x (snake-tail first-x sec-x)
        y (snake-tail first-y sec-y)]
    (update-in snake [:body] #(conj % [x y]))))

(defn process-move
  "Evaluates new snake position in context of the whole game"
  [{:keys [snake point board username] :as db}]
  (if (= point (first (:body snake)))
    (-> db
        (update-in [:snake] grow-snake)
        (update-in [:all-players-points (keyword username)] inc)
        (assoc :point (rand-free-position snake board)))
    db))

(defn sort-by-score
  [scores]
  (into
    (sorted-map-by (fn [key1 key2]
                     (compare [(get scores key2) key2]
                              [(get scores key1) key1])))
    scores))