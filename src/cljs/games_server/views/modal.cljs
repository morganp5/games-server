(ns games-server.modal
  (:require [reagent.core :as reagent]
            [re-frame.core :refer [subscribe dispatch]]))

(defn atom-input 
  [value]
  [:input {:type "text"
           :value @value
           :on-change #(reset! value (-> % .-target .-value))}])

(defn modal-hiccup
  []
  (let [input-box-data (reagent/atom "")]
    [:div.modal.fade {:id "modal"}
     [:div.modal-dialog
      [:div.modal-content
       [:div.modal-header
        [:button.close {:type "button",:data-dismiss "modal", :aria-label "Close"}
         [:span {:aria-hidden "true"} "x"]]
        [:h4.modal-title "User Details"]]
       [:div.modal-body.col-xs-12
        [:div.col-xs-9
         [:p"Enter a Name: " [atom-input input-box-data]]]
        [:div.col-xs-3
         [:button.btn.btn-default
          {:type "button",
           :data-dismiss "modal"
           :on-click #(dispatch [:username-added @input-box-data])} "Ok"]]]
       [:div.modal-footer]]]]))


(defn modal
  []
  (.ready (js/jQuery "document") #(.modal (js/jQuery "#modal") "show"))
  [modal-hiccup])


