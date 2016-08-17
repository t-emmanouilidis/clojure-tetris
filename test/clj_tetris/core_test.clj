(ns clj-tetris.core-test
  (:require [clojure.test :refer :all]
            [clj-tetris.core :as tcore :refer :all]))

(defn move-left-five-times
  []
  (do
    (tcore/reset-view)
    (tcore/move-left)
    (tcore/move-left)
    (tcore/move-left)
    (tcore/move-left)
    (tcore/move-left))
  @tcore/game-view)

(defn passed-the-wall
  [current-view]
  (let [correct-positions #{[0 0] [0 17] [1 17] [2 17] [1 18]}]
    (if (some
          #(not (contains? correct-positions %))
          (into #{} (map :position (:all-blocks current-view))))
      true
      false)))


(deftest a-test
  (testing "Test if left moves are ignored when the piece hits the left wall after five left moves"
    (is (not (passed-the-wall (move-left-five-times))))))
