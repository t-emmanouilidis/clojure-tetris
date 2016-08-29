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

(def possible-moves [[tcore/drop-down (possible-move view/drop-view)]
                     [tcore/move-left (possible-move view/move-view-left)]
                     [tcore/move-right (possible-move view/move-view-right)]
                     [tcore/rotate-cw (possible-move view/rotate-view-cw)]
                     [tcore/drop-down (possible-move view/drop-view)]])

(defn find-best-move
  ([view] (find-best-move view possible-moves))
  ([view remaining-moves]
   (if (not (empty? remaining-moves))
     (let [cur-combined-move (first remaining-moves)
           cur-move (first cur-combined-move)
           move-to-apply (last cur-combined-move)
           cur-utility (evaluate-view (move-to-apply view))
           next-moves (rest remaining-moves)
           next-larger-combination (find-best-move view next-moves)
           next-larger-utility (first next-larger-combination)]
       (println (str "Current utility: " cur-combined-move ". Next utility: " next-larger-combination))
       (if (> cur-utility next-larger-utility)
         [cur-utility cur-move]
         next-larger-combination))
     [min-utility tcore/drop-down])))



(defn next-move
  []
  (let [next-move (find-best-move @tcore/game-view)
        utility (first next-move)
        move (last next-move)]
    (println (str "Move: " move ". Utility of next-move: " utility))
    move))
