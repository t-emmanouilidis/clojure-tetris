(ns clj-tetris.agent
  (:require [clj-tetris.view :as view]
            [clj-tetris.core :as tcore]
            [clj-tetris.piece-kind :refer :all]))


(def min-utility -1000.0)


(defn get-max-by-group-for-x
  [groups x]
  (let [x-positions (mapv #(last %) (get groups x))]
    (if (empty? x-positions)
      0
      (apply max x-positions))))


(defn get-gaps
  [view]
  (let [[size-x size-y] (:grid-size view)
        blocks-wo-current (tcore/blocks-without-current view)
        grouped (group-by #(first %) (map :position blocks-wo-current))]
    (for [x (range (- size-x 1))
          :let [y (+ x 1)]
          :let [max-x (get-max-by-group-for-x grouped x)]
          :let [max-y (get-max-by-group-for-x grouped y)]
          :let [gap (Math/abs (int (- max-x max-y)))]
          :when (> gap 1)]
      gap)))


(defn get-gap-penalty
  [view]
  (double (apply + (mapv #(* % %) (get-gaps view)))))


(defn evaluate-view [view]
  (double
    (if (:game-over view)
      min-utility
      (- (:cleared-line-count view) (get-gap-penalty view)))))


(defn possible-move
  [fn]
  (comp view/clear-full-rows fn))


(def possible-moves [[tcore/drop-down (possible-move view/move-view-down)]
                     [tcore/move-right (possible-move view/move-view-right)]
                     [tcore/drop-down (possible-move view/drop-view)]
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
