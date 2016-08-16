(ns clj-tetris.core
  (:require [clj-tetris.piece :as piece :refer :all])
  (:require [clj-tetris.view :as view :refer :all])
  (:import (clj_tetris.view GameView)
           (clj_tetris.piece Block)))

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

(def grid-size [10 20])

(def drop-off-pos
  (let [[size-x size-y] grid-size]
    [(/ size-x 2.0) (- size-y 3.0)]))

(defprotocol StageProtocol (game-view [this]))

(defrecord Stage [] StageProtocol
  (game-view [this]
    (let [current-piece (create-piece drop-off-pos t-kind)]
      (GameView.
        (conj (piece-current-blocks current-piece) (Block. (vector 0.0 0.0) t-kind))
        grid-size
        current-piece))))

(defn game-view
  []
  (GameView.
    (vector
      (Block. [5 5] (t-kind))
      (Block. [6 5] (t-kind))
      (Block. [7 5] (t-kind))
      (Block. [6 6] (t-kind))
      (Block. [0 0] (t-kind)))
    grid-size
    (vector
      (Block. [5 5] (t-kind))
      (Block. [6 5] (t-kind))
      (Block. [7 5] (t-kind))
      (Block. [6 6] (t-kind)))))
