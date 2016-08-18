(ns clj-tetris.core
  (:require [clj-tetris.view :as view])
  (:require [clj-tetris.piece-kind :refer :all]
            [clj-tetris.piece :as piece])
  (:import (clj_tetris.view GameView)))

(def grid-size [10 20])

(def drop-off-pos
  (let [[size-x size-y] grid-size]
    [(/ size-x 2.0) (- size-y 3.0)]))

(defn pos-not-in-bounds?
  [all-current-block-positions]
  (some
    (fn [[x y]] (not (and (>= x 0) (< x (first grid-size)) (>= y 0) (< y (last grid-size)))))
    all-current-block-positions))

(defn block-position-more-than-once?
  [all-block-positions]
  (some #(> (last %) 1) (frequencies all-block-positions)))

(defn current-piece-bounds-validator
  [{:keys [all-blocks current-piece]}]
  (let [all-block-positions (mapv :position all-blocks)
        all-current-block-positions (mapv :position (piece/piece-current-blocks current-piece))]
    (cond (pos-not-in-bounds? all-current-block-positions) (throw (IllegalStateException. "Current piece reached the bounds!"))
          (block-position-more-than-once? all-block-positions) (throw (IllegalStateException. "There is at least one block that overlaps with another!"))
          :else true)))

(defn initial-view
  [initial-blocks]
  (let [current-piece (piece/create-piece drop-off-pos t-kind)]
    (GameView.
      (into (piece/piece-current-blocks current-piece) initial-blocks)
      grid-size
      current-piece)))

(def game-view (atom (initial-view (vector)) :validator current-piece-bounds-validator))

(defn reset-view
  [initial-blocks]
  (swap! game-view (fn [current-state] (initial-view initial-blocks)))
  @game-view)

(defn move-left
  []
  (try
    (swap! game-view (fn [current-view] (view/move-view-left current-view)))
    (catch IllegalStateException ise (println "Moving left: " (.getMessage ise))))
  @game-view)

(defn move-right
  []
  (try
    (swap! game-view (fn [current-view] (view/move-view-right current-view)))
    (catch IllegalStateException ise (println "Moving right: " (.getMessage ise))))
  @game-view)

(defn spawn
  []
  (try
    (swap! game-view (fn [current-view] (view/spawn-new-piece current-view drop-off-pos)))
    (catch IllegalStateException ise (println "Spawning new piece: " (.getMessage ise))))
  @game-view)

(defn move-down
  []
  (try
    (swap! game-view (fn [current-view] (view/move-view-down current-view)))
    (catch IllegalStateException ise
      (do
        (println "Moving down: " (.getMessage ise))
        (spawn))))
  @game-view)

(defn rotate-cw
  []
  (try
    (swap! game-view (fn [current-view] (view/rotate-view-cw current-view)))
    (catch IllegalStateException ise (println "Rotating clock-wise: " (.getMessage ise))))
  @game-view)

(defn current-piece [] (:current-piece @game-view))
(defn current-piece-blocks [] (piece/piece-current-blocks (current-piece)))
