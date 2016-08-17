(ns clj-tetris.view
  (:require [clj-tetris.piece :as piece :refer :all]))

(defrecord GameView [all-blocks grid-size current-piece])

(defn- remove-piece-from-view
  [view piece]
  (let [grid-size (:grid-size view)
        current-block-positions (into #{} (map :position (piece-current-blocks piece)))
        view-blocks (:all-blocks view)
        blocks-without-current (filter
                                 (fn [block] (not (contains? current-block-positions (:position block))))
                                 view-blocks)]
    (GameView. blocks-without-current grid-size nil)))

(defn- add-piece-to-view [view moved-piece]
  (let [grid-size (:grid-size view)
        blocks-with-moved-current (into (:all-blocks view) (piece-current-blocks moved-piece))]
    (GameView. blocks-with-moved-current grid-size moved-piece)))

(defn- move-view-by
  [current-view delta]
  (let [current-piece (:current-piece current-view)
        moved-view (add-piece-to-view
                     (remove-piece-from-view current-view current-piece)
                     (piece/move-piece current-piece delta))]
    moved-view))

(defn move-view-left [view] (move-view-by view [-1.0 0.0]))
(defn move-view-right [view] (move-view-by view [1.0 0.0]))
