(ns clj-tetris.block)

(defrecord Block [position piece-kind])

(defn move-block-down [block]
  (Block. [(first (:position block)) (dec (last (:position block)))] (:piece-kind block)))

(defn move-blocks-down [blocks] (mapv move-block-down blocks))