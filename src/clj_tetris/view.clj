(ns clj-tetris.view
  (:require [clj-tetris.piece :as piece])
  (:require [clj-tetris.piece-kind :as piece-kind])
  (:import (clj_tetris.piece Block)))

(defrecord GameView [all-blocks grid-size current-piece next-piece next-piece-kinds game-over cleared-line-count])

(defn remove-piece-from-view
  [view piece]
  (let [grid-size (:grid-size view)
        current-block-positions (into #{} (map :position (piece/piece-current-blocks piece)))
        view-blocks (:all-blocks view)
        blocks-without-current (filter
                                 #(not (contains? current-block-positions (:position %)))
                                 view-blocks)
        cleared-line-count (:cleared-line-count view)]
    (GameView.
      blocks-without-current
      grid-size
      nil
      (:next-piece view)
      (:next-piece-kinds view)
      false
      cleared-line-count)))

(defn add-piece-to-view [view moved-piece]
  (let [grid-size (:grid-size view)
        blocks-with-moved-piece (into (:all-blocks view) (piece/piece-current-blocks moved-piece))
        cleared-line-count (:cleared-line-count view)]
    (GameView.
      blocks-with-moved-piece
      grid-size
      moved-piece
      (:next-piece view)
      (:next-piece-kinds view)
      false
      cleared-line-count)))

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

(defn current-piece-out-of-bounds?
  "There is no need to check for the upper bound since a piece cannot go up"
  [current-piece-block-positions [grid-size-x grid-size-y]]
  (some
    (fn [[pos-x pos-y]] (not (and (>= pos-x 0) (< pos-x grid-size-x) (>= pos-y 0))))
    current-piece-block-positions))

(defn block-position-more-than-once?
  [all-block-positions]
  (some #(> (last %) 1) (frequencies all-block-positions)))

(defn current-piece-in-illegal-state?
  [view]
  (let [all-blocks (:all-blocks view)
        all-block-positions (map :position all-blocks)
        grid-size (:grid-size view)
        current-piece (:current-piece view)
        current-piece-blocks (piece/piece-current-blocks current-piece)
        current-piece-block-positions (map :position current-piece-blocks)]
    (or (current-piece-out-of-bounds? current-piece-block-positions grid-size)
        (block-position-more-than-once? all-block-positions))))

(defn spawn-new-piece
  [current-view drop-off-pos]
  (let [grid-size (:grid-size current-view)
        current-piece (:next-piece current-view)
        current-piece-kind (:kind current-piece)
        current-piece-with-correct-pos (piece/create-piece drop-off-pos current-piece-kind)
        next-piece-kinds (:next-piece-kinds current-view)
        next-piece-kind (first next-piece-kinds)
        next-piece (piece/create-piece [2 1] next-piece-kind)
        current-blocks (:all-blocks current-view)
        current-piece-blocks (piece/piece-current-blocks current-piece-with-correct-pos)
        all-blocks (into current-blocks current-piece-blocks)
        all-block-positions (map :position all-blocks)
        next-next-piece-kinds (rest next-piece-kinds)
        cleared-line-count (:cleared-line-count current-view)]
    (if (block-position-more-than-once? all-block-positions)
      (assoc current-view :game-over true)
      (GameView.
        all-blocks
        grid-size
        current-piece-with-correct-pos
        next-piece
        next-next-piece-kinds
        false
        cleared-line-count))))

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
                next-piece-kinds (:next-piece-kinds current-view)
                cleared-line-count (:cleared-line-count current-view)]
            (recur
              (GameView.
                (into
                  (blocks-below-row current-view current-row)
                  (move-upper-blocks-down current-view current-row))
                grid-size
                current-piece
                next-piece
                next-piece-kinds
                false
                (inc cleared-line-count))
              (dec current-row)))
          (recur current-view (dec current-row)))
        current-view))))

(defn create-initial-view
  [initial-blocks grid-size piece-kinds drop-off-pos]
  (let [initial-piece-kinds (if (empty? piece-kinds)
                              (repeatedly (fn [] (piece-kind/get-next-random-piece-kind)))
                              piece-kinds)
        current-piece (piece/create-piece drop-off-pos (first initial-piece-kinds))
        current-piece-blocks (piece/piece-current-blocks current-piece)
        next-piece-kinds (rest initial-piece-kinds)
        next-next-piece-kinds (rest next-piece-kinds)]
    (GameView.
      (into initial-blocks current-piece-blocks)
      grid-size
      current-piece
      (piece/create-piece [2 1] (first next-piece-kinds))
      next-next-piece-kinds
      false
      0)))

(defn drop-view
  [view]
  (loop [current-view view]
    (let [moved-down-view (move-view-down current-view)]
      (if (current-piece-in-illegal-state? moved-down-view)
        current-view
        (recur moved-down-view)))))