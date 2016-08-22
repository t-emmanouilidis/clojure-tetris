(ns clj-tetris.piece-kind)

(defprotocol PieceKind)
(defrecord IKind [] PieceKind)
(defrecord JKind [] PieceKind)
(defrecord LKind [] PieceKind)
(defrecord OKind [] PieceKind)
(defrecord SKind [] PieceKind)
(defrecord TKind [] PieceKind)
(defrecord ZKind [] PieceKind)

(def i-kind (IKind.))
(def j-kind (JKind.))
(def l-kind (LKind.))
(def o-kind (OKind.))
(def s-kind (SKind.))
(def t-kind (TKind.))
(def z-kind (ZKind.))

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
