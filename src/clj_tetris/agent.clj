(ns clj-tetris.agent
  (:require [clj-tetris.view :as view]
            [clj-tetris.core :as tcore]))

(defn evaluate-view [view] (if (:game-over view) -1000 (:cleared-line-count view)))

(def min-utility -1000.0)

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
       (if (> cur-utility next-larger-utility)
         [cur-utility cur-move]
         next-larger-combination))
     [min-utility tcore/drop-down])))

(defn next-move
  []
  (let [next-move (last (find-best-move @tcore/game-view))]
    (println "Next move: " next-move)
    next-move))
