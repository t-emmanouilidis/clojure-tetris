(ns clj-tetris.piece-kind)

(defprotocol PieceKind)

(def i-kind (reify PieceKind))
(def j-kind (reify PieceKind))
(def l-kind (reify PieceKind))
(def o-kind (reify PieceKind))
(def s-kind (reify PieceKind))
(def t-kind (reify PieceKind))
(def z-kind (reify PieceKind))

(defn get-next-random-piece-kind
  []
  (let [idx (rand-int 7)]
    (cond
      (= idx 0) i-kind
      (= idx 1) j-kind
      (= idx 2) l-kind
      (= idx 3) o-kind
      (= idx 4) s-kind
      (= idx 5) t-kind
      :else z-kind)))

(defn orientation
  [piece-kind]
  (cond
    (= piece-kind i-kind) 2
    (= piece-kind j-kind) 4
    (= piece-kind l-kind) 4
    (= piece-kind o-kind) 1
    (= piece-kind s-kind) 2
    (= piece-kind t-kind) 4
    (= piece-kind z-kind) 2))