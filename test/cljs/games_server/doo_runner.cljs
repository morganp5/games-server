(ns games-server.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [games-server.core-test]))

(doo-tests 'games-server.core-test)

