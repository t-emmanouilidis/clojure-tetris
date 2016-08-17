(ns clj-tetris.view
  (:require [clj-tetris.piece :as piece :refer :all]))

(defrecord GameView [all-blocks grid-size current-piece])

(defn remove-piece-from-view
  [view piece]
  (let [current-block-positions (map
                                  (fn [block] (:position block))
                                  (:current-blocks piece))
        view-blocks (:all-blocks view)]
    (filter
      (fn [block] (not (contains? current-block-positions (:position block))))
      view-blocks)))

(defn add-piece-to-view [view piece] (into (:all-blocks view) (:current-blocks piece)))

(defn move-view-by
  [view delta]
  (let [current-piece (:current-piece view)]
    (add-piece-to-view
      (remove-piece-from-view view current-piece)
      (piece/movie-piece current-piece delta))))

(defn move-view-left [view] (move-view-by view (vector -1.0 0.0)))
(defn move-view-right [view] (move-view-by view (vector 1.0 0.0)))
