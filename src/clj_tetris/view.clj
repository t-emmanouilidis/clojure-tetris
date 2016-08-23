(ns clj-tetris.view
  (:require [clj-tetris.piece :as piece])
  (:require [clj-tetris.piece-kind :as piece-kind])
  (:import (clj_tetris.piece Block)))

(defrecord GameView [all-blocks grid-size current-piece next-piece next-piece-kinds])

(defn remove-piece-from-view
  [view piece]
  (let [grid-size (:grid-size view)
        current-block-positions (into #{} (map :position (piece/piece-current-blocks piece)))
        view-blocks (:all-blocks view)
        blocks-without-current (filter
                                 #(not (contains? current-block-positions (:position %)))
                                 view-blocks)]
    (GameView.
      blocks-without-current
      grid-size
      nil
      (:next-piece view)
      (:next-piece-kinds view))))

(defn add-piece-to-view [view moved-piece]
  (let [grid-size (:grid-size view)
        blocks-with-moved-piece (into (:all-blocks view) (piece/piece-current-blocks moved-piece))]
    (GameView.
      blocks-with-moved-piece
      grid-size
      moved-piece
      (:next-piece view)
      (:next-piece-kinds view))))

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
  (let [grid-size (:grid-size current-view)
        current-piece (:next-piece current-view)
        next-piece-kinds (:next-piece-kinds current-view)
        next-piece-kind (first next-piece-kinds)
        next-piece (piece/create-piece drop-off-pos next-piece-kind)
        current-blocks (:all-blocks current-view)]
    (GameView.
      (into current-blocks (piece/piece-current-blocks current-piece))
      grid-size
      current-piece
      next-piece
      (rest next-piece-kinds))))

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
          (let [current-piece (:current-piece current-view)
                next-piece (:next-piece current-view)
                next-piece-kinds (:next-piece-kinds current-view)]
            (recur
              (GameView.
                (into
                  (blocks-below-row current-view current-row)
                  (move-upper-blocks-down current-view current-row))
                grid-size
                current-piece
                next-piece
                next-piece-kinds)
              (dec current-row)))
          (recur current-view (dec current-row)))
        current-view))))

(defn create-initial-view
  [initial-blocks grid-size initial-next-piece-kinds drop-off-pos]
  (let [piece-kinds (lazy-seq (if (empty? initial-next-piece-kinds)
                                (repeatedly piece-kind/get-next-random-piece-kind)
                                initial-next-piece-kinds))
        current-piece-kind (first piece-kinds)
        next-piece-kind (first (rest piece-kinds))
        next-piece-kinds (rest (rest piece-kinds))
        current-piece (piece/create-piece drop-off-pos current-piece-kind)
        current-piece-blocks (piece/piece-current-blocks current-piece)]
    (GameView.
      (into initial-blocks current-piece-blocks)
      grid-size
      current-piece
      (piece/create-piece drop-off-pos next-piece-kind)
      next-piece-kinds)))