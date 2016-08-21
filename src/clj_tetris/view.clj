(ns clj-tetris.view
  (:require [clj-tetris.piece :as piece])
  (:require [clj-tetris.piece-kind :refer :all])
  (:import (clj_tetris.piece Block Piece)))

(defrecord GameView [all-blocks grid-size current-piece])

(defn remove-piece-from-view
  [view piece]
  (let [grid-size (:grid-size view)
        current-block-positions (into #{} (map :position (piece/piece-current-blocks piece)))
        view-blocks (:all-blocks view)
        blocks-without-current (filter
                                 #(not (contains? current-block-positions (:position %)))
                                 view-blocks)]
    (GameView. blocks-without-current grid-size nil)))

(defn add-piece-to-view [view new-piece]
  (let [grid-size (:grid-size view)
        blocks-with-moved-current (into (:all-blocks view) (piece/piece-current-blocks new-piece))]
    (GameView. blocks-with-moved-current grid-size new-piece)))

(defn- move-view-by
  [current-view delta]
  (let [current-piece (:current-piece current-view)]
    (add-piece-to-view
      (remove-piece-from-view current-view current-piece)
      (piece/move-piece current-piece delta))))

(defn move-view-left [view] (move-view-by view [-1.0 0.0]))
(defn move-view-right [view] (move-view-by view [1.0 0.0]))
(defn move-view-down [view] (move-view-by view [0.0 -1.0]))

(defn rotate-view-cw
  [current-view]
  (let [current-piece (:current-piece current-view)]
    (add-piece-to-view
      (remove-piece-from-view current-view current-piece)
      (piece/rotate-piece current-piece))))

(defn spawn-new-piece
  [current-view drop-off-pos]
  (add-piece-to-view current-view (piece/create-piece drop-off-pos t-kind)))

(defn is-row-full?
  [view row-num]
  (let [size-x (first (:grid-size view))]
    (= (count (filter
                #(= (last %) row-num)
                (map :position (:all-blocks view))))
       size-x)))

(defn blocks-above-row
  [view row-num]
  (let [all-blocks (:all-blocks view)]
    (filterv
      #(> (last (:position %)) row-num)
      all-blocks)))

(defn blocks-below-row
  [view row-num]
  (let [all-blocks (:all-blocks view)]
    (filterv
      #(< (last (:position %)) row-num)
      all-blocks)))

(defn move-upper-blocks-down
  [view row-num]
  (let [upper-blocks (blocks-above-row view row-num)]
    (mapv #(Block. [(first (:position %)) (dec (last (:position %)))] (:piece-kind %)) upper-blocks)))

(defn clear-full-rows
  [view]
  (let [grid-size (:grid-size view)
        size-y (last grid-size)]
    (loop [current-view view
           current-row (dec size-y)]
      (if (>= current-row 0)
        (if (is-row-full? current-view current-row)
          (let [current-piece (:current-piece current-view)]
            (recur
              (GameView. (into
                           (blocks-below-row current-view current-row)
                           (move-upper-blocks-down current-view current-row))
                         grid-size
                         current-piece)
              (dec current-row)))
          (recur current-view (dec current-row)))
        current-view))))