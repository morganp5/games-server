(ns games-server.config
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[games-server started successfully]=-"))
   :middleware identity})
