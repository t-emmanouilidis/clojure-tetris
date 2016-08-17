(ns clj-tetris.core
  (:require [clj-tetris.piece :refer :all])
  (:require [clj-tetris.view :refer :all])
  (:require [clj-tetris.piece-kind :refer :all])
  (:import (clj_tetris.view GameView)
           (clj_tetris.piece Block)))

(def grid-size [10 20])

(def drop-off-pos
  (let [[size-x size-y] grid-size]
    [(/ size-x 2.0) (- size-y 3.0)]))

(def game-view
  (let [current-piece (create-piece drop-off-pos t-kind)]
    (atom
      (GameView.
        (conj (piece-current-blocks current-piece) (Block. (vector 0 0) t-kind))
        grid-size
        current-piece))))

(defn move-left
  []
  (swap! game-view (fn [current-view] (move-view-left current-view))))

(defn move-right
  []
  (swap! game-view (fn [current-view] (move-view-right current-view))))
