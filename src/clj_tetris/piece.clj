(ns clj-tetris.piece
  (:require [clj-tetris.piece-kind :refer :all]))

(defrecord Block [position piece-kind])

(defrecord Piece [position kind local-points])

(defn piece-current-blocks
  [piece]
  (let [piece-posisition (:position piece)
        pos-x (first piece-posisition)
        pos-y (last piece-posisition)]
    (println (str "piece position:[" pos-x ", " pos-y "]"))
    (map
      (fn
        [[local-pos-x local-pos-y]]
        (Block.
          (vector (int (Math/floor (+ local-pos-x pos-x)))
                  (int (Math/floor (+ local-pos-y pos-y))))
          (:kind piece)))
      (:local-points piece))))

(defn movie-piece
  [piece delta]
  (let [piece-position (:position piece)
        old-pos-x (first piece-position)
        old-pos-y (last piece-position)
        new-position (vector (+ old-pos-x (first delta)) (+ old-pos-y (last delta)))]
    (println (str "Old position:" piece-position ". New position:" new-position))
    (Piece. new-position (:kind piece) (:local-points piece))))

(defn create-piece
  [position piece-kind]
  (println (str (type piece-kind)))
  (cond
    (= t-kind piece-kind) (Piece. position piece-kind (vector [-1.0 0.0] [0.0 0.0] [1.0 0.0] [0.0 1.0]))
    :else (throw (IllegalStateException. (str "Tried to create a piece of kind" (type piece-kind))))))
