(ns clj-tetris.piece)

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
          (int (Math/floor (+ local-pos-x pos-x)))
          (int (Math/floor (+ local-pos-y pos-y)))))
      (:local-points piece))))

(defn movie-piece
  [piece delta]
  (let [piece-position (:position piece)
        old-pos-x (first piece-position)
        old-pos-y (last piece-position)
        new-position (vector (+ old-pos-x (first delta)) (+ old-pos-y (last delta)))]
    (Piece. new-position (:kind piece) (:local-points piece))))

(defn create-piece
  [position pieceKind]
  (case pieceKind
    (IKind) (Piece. position pieceKind (vector [-1.0 0.0] [0.0 0.0] [1.0 0.0] [0.0 1.0]))
    "default" (throw (IllegalStateException. (str "Tried to create a piece of kind" (type pieceKind))))))
