(ns games-server.views.navbar
  (:require [reagent.core :as reagent]
            [reagent.session :as session]))

(defn nav-link [uri title page collapsed?]
  [:ul.nav.navbar-nav>a.navbar-brand
   {:class    (when (= page (session/get :page)) "active")
    :href     uri
    :on-click #(reset! collapsed? true)}
   title])


(defn navbar []
 "Currently not used will be used when future games implemented"
  (let [collapsed? (reagent/atom true)]
    (fn []
      [:nav.navbar.navbar-light.bg-faded
       [:button.navbar-toggler.hidden-sm-up
        {:on-click #(swap! collapsed? not)} "☰"]
       [:div.collapse.navbar-toggleable-xs
        (when-not @collapsed? {:class "in"})
        [:a.navbar-brand {:href "#/"} "Games"]
        [:ul.nav.navbar-nav
         [nav-link "#/snake" "Snake" :about collapsed?]]]])))
