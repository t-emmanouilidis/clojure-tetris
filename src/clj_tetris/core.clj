(ns clj-tetris.core
  (:require [clj-tetris.view :as view])
  (:require [clj-tetris.piece-kind :refer :all]
            [clj-tetris.piece :as piece])
  (:import (clj_tetris.view GameView)
           (clj_tetris.piece Block)))

(def grid-size [10 20])

(def drop-off-pos
  (let [[size-x size-y] grid-size]
    [(/ size-x 2.0) (- size-y 3.0)]))

(def initial-view
  (let [current-piece (piece/create-piece drop-off-pos t-kind)]
    (GameView.
      (conj (piece/piece-current-blocks current-piece) (Block. [0 0] t-kind))
      grid-size
      current-piece)))

(defn current-piece-bounds-validator
  [{:keys [current-piece]}]
  (let [all-block-positions (map :position (piece/piece-current-blocks current-piece))]
    (if (some
          (fn [[x y]]
            (not
              (and
                (>= x 0)
                (< x (first grid-size))
                (>= y 0)
                (< y (last grid-size)))))
          all-block-positions)
      (throw (IllegalStateException. "Current piece reached the bounds!"))
      true)))

(def game-view (atom initial-view :validator current-piece-bounds-validator))

(defn reset-view
  []
  (do
    (swap! game-view (fn [current-state] initial-view))
    @game-view))

(defn move-left
  []
  (do
    (try
      (swap! game-view (fn [current-view] (view/move-view-left current-view)))
      (catch IllegalStateException ise (println "Moving left: " (.getMessage ise))))
    @game-view))

(defn move-right
  []
  (do
    (try
      (swap! game-view (fn [current-view] (view/move-view-right current-view)))
      (catch IllegalStateException ise (println "Moving right: " (.getMessage ise))))
    @game-view))

(defn rotate-cw
  []
  (do
    (try
      (swap! game-view (fn [current-view] (view/rotate-view-cw current-view)))
      (catch IllegalStateException ise (println "Rotating clock-wise: " (.getMessage ise))))
    @game-view))

(defn current-piece [] (:current-piece @game-view))
(defn current-piece-blocks [] (piece/piece-current-blocks (current-piece)))

