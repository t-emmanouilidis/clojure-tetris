(ns clj-tetris.piece
  (:require [clj-tetris.piece-kind :refer :all]))

(def standard-rotation-theta (/ (- Math/PI) 2.0))

(defrecord Piece [position kind local-points])

(defn move-piece [piece delta]
  (let [piece-position (:position piece)
        old-pos-x (first piece-position)
        old-pos-y (last piece-position)
        new-position [(+ old-pos-x (first delta))
                      (+ old-pos-y (last delta))]]
    (Piece. new-position (:kind piece) (:local-points piece))))

(defn rotate-piece [piece]
  (let [theta standard-rotation-theta
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

(defn create-piece [position piece-kind]
  (cond
    (= i-kind piece-kind) (Piece. position piece-kind [[-1.5 0.0] [-0.5 0.0] [0.5 0.0] [1.5 0.0]])
    (= j-kind piece-kind) (Piece. position piece-kind [[-1.0 0.5] [0.0 0.5] [1.0 0.5] [1.0 -0.5]])
    (= l-kind piece-kind) (Piece. position piece-kind [[-1.0 -0.5] [-1.0 0.5] [0.0 0.5] [1.0 0.5]])
    (= o-kind piece-kind) (Piece. position piece-kind [[-0.5 0.5] [0.5 0.5] [0.5 -0.5] [-0.5 -0.5]])
    (= s-kind piece-kind) (Piece. position piece-kind [[-1.0 -0.5] [0.0 -0.5] [0.0 0.5] [1.0 0.5]])
    (= t-kind piece-kind) (Piece. position piece-kind [[-1.0 0.0] [0.0 0.0] [1.0 0.0] [0.0 1.0]])
    (= z-kind piece-kind) (Piece. position piece-kind [[-1.0 0.5] [0.0 0.5] [0.0 -0.5] [1.0 -0.5]])
    :else (throw (IllegalStateException. (str "Tried to create a piece of kind" (type piece-kind))))))
