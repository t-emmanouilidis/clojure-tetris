(ns clj-tetris.piece
  (:require [clj-tetris.piece-kind :refer :all]))

;; relative position deltas for each different block kind
(def i-kind-points [[-1.5 0.0] [-0.5 0.0] [0.5 0.0] [1.5 0.0]])
(def j-kind-points [[-1.0 0.5] [0.0 0.5] [1.0 0.5] [1.0 -0.5]])
(def l-kind-points [[-1.0 -0.5] [-1.0 0.5] [0.0 0.5] [1.0 0.5]])
(def o-kind-points [[-0.5 0.5] [0.5 0.5] [0.5 -0.5] [-0.5 -0.5]])
(def s-kind-points [[-1.0 -0.5] [0.0 -0.5] [0.0 0.5] [1.0 0.5]])
(def t-kind-points [[-1.0 0.0] [0.0 0.0] [1.0 0.0] [0.0 1.0]])
(def z-kind-points [[-1.0 0.5] [0.0 0.5] [0.0 -0.5] [1.0 -0.5]])

(def ccw-ninety-degree-rads (/ (- Math/PI) 2.0))

(defrecord Piece [position kind local-points])

(defn move-piece [piece delta]
  "Returns a new piece whose position is the result
  of applying the given delta to the given piece"
  (let [piece-position (:position piece)
        old-pos-x (first piece-position)
        old-pos-y (last piece-position)
        new-position [(+ old-pos-x (first delta))
                      (+ old-pos-y (last delta))]]
    (Piece. new-position (:kind piece) (:local-points piece))))

(defn rotate-piece [piece]
  (let [cos-theta (Math/cos ccw-ninety-degree-rads)
        sin-theta (Math/sin ccw-ninety-degree-rads)
        current-position (:position piece)
        local-points (:local-points piece)
        piece-kind (:kind piece)]
    (Piece. current-position piece-kind
            (mapv
              (fn [[x y]]
                [(- (Math/round (* cos-theta x)) (Math/round (* sin-theta y)))
                 (+ (Math/round (* sin-theta x)) (Math/round (* cos-theta y)))])
              local-points))))

(defn create-piece [position piece-kind]
  (cond
    (= i-kind piece-kind) (Piece. position piece-kind i-kind-points)
    (= j-kind piece-kind) (Piece. position piece-kind j-kind-points)
    (= l-kind piece-kind) (Piece. position piece-kind l-kind-points)
    (= o-kind piece-kind) (Piece. position piece-kind o-kind-points)
    (= s-kind piece-kind) (Piece. position piece-kind s-kind-points)
    (= t-kind piece-kind) (Piece. position piece-kind t-kind-points)
    (= z-kind piece-kind) (Piece. position piece-kind z-kind-points)
    :else (throw (IllegalStateException. (str "Tried to create a piece of kind" (type piece-kind))))))
