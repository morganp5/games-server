(ns games-server.config
  (:require [selmer.parser :as parser]
            [clojure.tools.logging :as log]
            [games-server.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[games-server started successfully using the development profile]=-"))
   :middleware wrap-dev})
