(ns clj-tetris.view)

(defrecord GameView [all-blocks grid-size current-piece])

(defn move-view-by
  [view delta])

(defn remove-piece-from-view
  [view piece]
  (let [current-block-positions
        (map
          (fn [block] (:position block))
          (:current-blocks piece))]
    (filter
      (fn [block]
        (not (contains? current-block-positions (:position block))))
      (:all-blocks view))))

(defn add-piece-to-view [view piece] (into (:all-blocks view) (:current-blocks piece)))