(ns clj-tetris.agent
  (:require [clj-tetris.view :as view]
            [clj-tetris.core :as tcore]
            [clj-tetris.piece-kind :refer :all]))


(def min-utility -1000.0)


(defn get-max-by-group-for-x
  [groups x]
  (let [x-positions (mapv #(inc (last %)) (get groups x))]
    (if (empty? x-positions)
      0
      (apply max x-positions))))


(defn get-heights
  [view]
  (let [[size-x size-y] (:grid-size view)
        grouped (group-by #(first %) (map :position (:all-blocks view)))]
    (for [x (range size-x)]
      (get-max-by-group-for-x grouped x))))


(defn get-gap-penalty
  [view]
  (double (apply + (mapv #(* % %) (get-heights view)))))


(defn evaluate-view [view]
  (double
    (if (:game-over view)
      min-utility
      (- (:cleared-line-count view) (/ (get-gap-penalty view) 10.0)))))


(defn possible-move
  "doing a drop before clearing rows does not actually changes the current piece which is removed in the next step"
  [fn]
  (comp view/clear-full-rows view/drop-view fn))


(def possible-moves
  [[tcore/move-down (possible-move view/move-view-down)]
   [tcore/move-right (possible-move view/move-view-right)]
   [tcore/rotate-cw (possible-move view/rotate-view-cw)]
   [tcore/move-left (possible-move view/move-view-left)]])


(defn get-utility-per-move
  [view]
  (map
    (fn [possible-move]
      (let [core-move (first possible-move)
            move-to-apply (last possible-move)
            resulted-view (apply move-to-apply [view])
            utility (evaluate-view resulted-view)]
        {:core-move core-move :utility utility}))
    possible-moves))


(defn find-best-move [view] (:core-move (apply max-key :utility (get-utility-per-move view))))


(defn next-move
  []
  (find-best-move @tcore/game-view))
