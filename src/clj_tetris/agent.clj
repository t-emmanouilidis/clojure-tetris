(ns clj-tetris.agent
  (:require [clj-tetris.view :as view]
            [clj-tetris.core :as tcore]
            [clj-tetris.piece-kind :refer :all]
            [clj-tetris.piece-kind :as piece-kind]))

; set the minimum utility
(def min-utility -1000.0)

(defn get-max-height-from-positions
  "Finds the maximum height from a collection of
  positions e.g. [[1.0 0.0][2.0 2.2]] for a specific X on axis"
  [positions]
  (let [heights (mapv #(inc (last %)) positions)]
    (if (empty? heights) 0 (apply max heights))))

(defn group-blocks-by-x-axis
  "Groups block positions by their position on the X axis"
  [blocks]
  (group-by #(first %) (map :position blocks)))

(defn get-heights
  "Returns a collection of the maximum heights for each position
  on the X axis of each of the given for each one of the given blocks"
  [blocks grid-size-x]
  (let [position-groups (group-blocks-by-x-axis blocks)]
    (mapv #(get-max-height-from-positions (get position-groups (double %))) (range grid-size-x))))

(defn get-gap-penalty
  "1) we get the heights of each column in the grid,
  2) we multiply each height with itself,
  3) we sum all the squares"
  [blocks grid-size-x]
  (double (apply + (map #(* % %) (get-heights blocks grid-size-x)))))

(defn evaluate-view [view]
  (double
    (if (:game-over view)
      min-utility
      (- (:cleared-line-count view) (/ (get-gap-penalty (:all-blocks view) (first (:grid-size view))) 10.0)))))


(defn allowed-number-of-moves
  "Returns the number of times the given
  function can be applied to the view until
  the view is no longer in a legal state"
  ([view fn] (allowed-number-of-moves view fn 0))
  ([view fn n]
   (let [resulted-view (apply fn [view])]
     (if (view/current-piece-in-illegal-state? resulted-view)
       n
       (allowed-number-of-moves resulted-view fn (inc n))))))


(defn side-limits
  "Returns a dictionary of the number of how many times
  the current piece can be moved to left or to the right
  without bringing the view to an illegal state"
  [view]
  [(allowed-number-of-moves view view/move-view-left)
   (allowed-number-of-moves view view/move-view-right)])


(defn orientation-actions
  "Returns a collection of the possible action sequences
  regarding a piece's orientation"
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
  "Returns the list of all possible combinations of
  a legal move left/right action, orientation and drop actions
  that can be performed in the view"
  [view]
  (let [current-piece (:current-piece view)
        current-piece-kind (:kind current-piece)
        orientation-actions (orientation-actions current-piece-kind)
        [left-limit right-limit] (side-limits view)
        left-actions (translation-actions left-limit tcore/move-left view/move-view-left)
        right-actions (translation-actions right-limit tcore/move-right view/move-view-right)]
    (conj (for [orientation-action orientation-actions
                move-action (concat left-actions right-actions)]
            (concat
              [{:core-move tcore/drop-down :view-move view/drop-view}]
              orientation-action
              move-action))
          (list))))


(defn complement-move
  "doing a drop before clearing rows does not actually change
  the current piece which is removed in the next step"
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

