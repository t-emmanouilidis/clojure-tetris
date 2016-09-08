(ns clj-tetris.agent
  (:require [clj-tetris.view :as view]
            [clj-tetris.core :as tcore]
            [clj-tetris.piece-kind :refer :all]
            [clj-tetris.piece-kind :as piece-kind]))


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


(defn allowed-number-of-moves
  ([view fn] (allowed-number-of-moves view fn 0))
  ([view fn n]
   (let [resulted-view (apply fn [view])]
     (if (view/current-piece-in-illegal-state? resulted-view)
       n
       (allowed-number-of-moves resulted-view fn (inc n))))))


(defn side-limits
  [view]
  {:left-limit  (allowed-number-of-moves view view/move-view-left)
   :right-limit (allowed-number-of-moves view view/move-view-right)})


(defn orientation-actions
  [current-piece-kind]
  (map
    #(repeat % {:core-move tcore/rotate-cw :view-move view/rotate-view-cw})
    (range (piece-kind/orientation current-piece-kind))))


(defn translation-actions
  [limit core-move view-move]
  (map
    #(repeat % {:core-move core-move :view-move view-move})
    (range 1 (inc limit))))


(defn action-seqs
  [view]
  (let [current-piece (:current-piece view)
        current-piece-kind (:kind current-piece)
        orientation-actions (orientation-actions current-piece-kind)
        side-limits (side-limits view)
        left-limit (:left-limit side-limits)
        right-limit (:right-limit side-limits)
        left-actions (translation-actions left-limit tcore/move-left view/move-view-left)
        right-actions (translation-actions right-limit tcore/move-right view/move-view-right)]
    (conj (for [orientation-action orientation-actions
                move-action (concat left-actions right-actions)]
            (concat orientation-action move-action))
          (list))))


(defn complement-move
  "doing a drop before clearing rows does not actually changes the current piece which is removed in the next step"
  [fns]
  (apply comp (concat [view/clear-full-rows view/drop-view] fns)))


(defn get-utility-per-move
  [view]
  (map
    (fn [action-seq]
      (let [core-moves (map :core-move action-seq)
            view-moves (map :view-move action-seq)
            complemented-move (apply complement-move [view-moves])
            resulted-view (apply complemented-move [view])
            utility (evaluate-view resulted-view)]
        {:core-moves core-moves :utility utility}))
    (action-seqs view)))


(defn find-best-move [view] (:core-moves (apply max-key :utility (get-utility-per-move view))))


(defn next-move [] (find-best-move @tcore/game-view))

