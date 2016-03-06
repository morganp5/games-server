(ns games-server.routes.home
  (:require [games-server.layout :as layout]
            [compojure.core :refer [defroutes GET]]
            [ring.util.http-response :as response]
            [clojure.java.io :as io]))

(defn snake []
  (layout/render "snake.html"))


(defroutes home-routes
           (GET "/" [] (snake))
           (GET "/docs" [] (response/ok (-> "docs/docs.md" io/resource slurp))))

