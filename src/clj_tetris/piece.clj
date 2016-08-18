(ns clj-tetris.piece
  (:require [clj-tetris.piece-kind :refer :all]))

(defrecord Block [position piece-kind])

(defrecord Piece [position kind local-points])

(defn piece-current-blocks
  [piece]
  (let [piece-posisition (:position piece)
        pos-x (first piece-posisition)
        pos-y (last piece-posisition)]
    (map
      (fn
        [[local-pos-x local-pos-y]]
        (Block.
          (vector (int (Math/floor (+ local-pos-x pos-x)))
                  (int (Math/floor (+ local-pos-y pos-y))))
          (:kind piece)))
      (:local-points piece))))

(defn move-piece
  [piece delta]
  (let [piece-position (:position piece)
        old-pos-x (first piece-position)
        old-pos-y (last piece-position)
        new-position (vector (+ old-pos-x (first delta)) (+ old-pos-y (last delta)))]
    (Piece. new-position (:kind piece) (:local-points piece))))


(defn rotate-piece
  [piece]
  (let [theta (/ (- Math/PI) 2.0)
        cos-theta (Math/cos theta)
        sin-theta (Math/sin theta)
        current-position (:position piece)
        local-points (:local-points piece)
        piece-kind (:kind piece)]
    (Piece. current-position piece-kind
            (mapv
              (comp
                (fn [[x y]]
                  [(* (Math/round (* (double x) 2.0)) 0.5)
                   (* (Math/round (* (double y) 2.0)) 0.5)])
                (fn [[x y]]
                  [(- (* cos-theta x) (* sin-theta y))
                   (+ (* sin-theta x) (* cos-theta y))]))
              local-points))))


(defn create-piece
  [position piece-kind]
  (println (str (type piece-kind)))
  (cond
    (= t-kind piece-kind) (Piece. position piece-kind [[-1.0 0.0] [0.0 0.0] [1.0 0.0] [0.0 1.0]])
    :else (throw (IllegalStateException. (str "Tried to create a piece of kind" (type piece-kind))))))
