(ns clj-tetris.core
  (:require [clj-tetris.view :as view])
  (:require [clj-tetris.piece-kind :refer :all]
            [clj-tetris.piece :as piece]
            [clj-tetris.piece-kind]))

(def grid-size [10 20])
(def mini-grid-size [4 4])

(def drop-off-pos
  (let [[size-x size-y] grid-size]
    [(/ size-x 2.0) size-y]))

(defn current-piece-bounds-validator
  [{:keys [all-blocks current-piece]}]
  (let [all-block-positions (mapv :position all-blocks)
        all-current-block-positions (mapv :position (piece/piece-current-blocks current-piece))]
    (cond (view/current-piece-out-of-bounds? all-current-block-positions grid-size) (throw (IllegalStateException. "Current piece reached the bounds!"))
          (view/block-position-more-than-once? all-block-positions) (throw (IllegalStateException. "There is at least one block that overlaps with another!"))
          :else true)))

(defn initial-view
  [initial-blocks next-piece-kinds]
  (view/create-initial-view initial-blocks grid-size next-piece-kinds drop-off-pos))

(def game-view (atom (initial-view [] []) :validator current-piece-bounds-validator))

(defn reset-view
  [initial-blocks next-piece-kinds]
  (swap! game-view (fn [current-state] (initial-view initial-blocks next-piece-kinds)))
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
    (catch IllegalStateException ise
      (println "Spawning new piece: " (.getMessage ise))))
  @game-view)

(defn clear-full-rows
  []
  (try
    (swap! game-view (fn [current-view] (view/clear-full-rows current-view)))
    (catch IllegalStateException ise
      (println "Clearing full rows: " (.getMessage ise))))
  @game-view)

(defn move-down
  []
  (try
    (swap! game-view (fn [current-view] (view/move-view-down current-view)))
    (catch IllegalStateException ise
      (println "Moving down: " (.getMessage ise))
      (clear-full-rows)
      (spawn)))
  @game-view)

(defn rotate-cw
  []
  (try
    (swap! game-view (fn [current-view] (view/rotate-view-cw current-view)))
    (catch IllegalStateException ise (println "Rotating clock-wise: " (.getMessage ise))))
  @game-view)

(defn drop-down
  []
  (try
    (swap! game-view
           (fn
             [current-view]
             (view/drop-view current-view)))
    (catch IllegalStateException ise (println "Dropping: " (.getMessage ise))))
  @game-view)

(defn piece-blocks [piece] (piece/piece-current-blocks piece))
