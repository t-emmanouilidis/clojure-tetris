(ns clj-tetris.block)

(defrecord Block [position piece-kind])

(defn blocks-from-piece [piece]
  (let [piece-position (:position piece)
        pos-x (first piece-position)
        pos-y (last piece-position)]
    (mapv
      (fn [[local-pos-x local-pos-y]]
        (Block.
          [(Math/floor (+ local-pos-x pos-x))
           (Math/floor (+ local-pos-y pos-y))]
          (:kind piece)))
      (:local-points piece))))

(defn move-block-down [block]
  (Block. [(first (:position block)) (dec (last (:position block)))] (:piece-kind block)))

(defn move-blocks-down [blocks] (mapv move-block-down blocks))