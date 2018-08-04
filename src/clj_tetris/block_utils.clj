(ns clj-tetris.block-utils
  (:require [clj-tetris.piece])
  (:import (clj_tetris.piece Block)))

(defn move-block-down [block]
  (Block. [(first (:position block)) (dec (last (:position block)))] (:piece-kind block)))

(defn move-blocks-down [blocks] (mapv move-block-down blocks))