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

(defn move-right-four-times
  []
  (do
    (tcore/reset-view)
    (tcore/move-right)
    (tcore/move-right)
    (tcore/move-right)
    (tcore/move-right))
  @tcore/game-view)

(defn correct-block-positions?
  [current-view correct-positions]
  (if (some
        #(not (contains? correct-positions %))
        (into #{} (map :position (:all-blocks current-view))))
    false
    true))


(deftest a-test
  (testing "Test if left moves are ignored when the piece hits the left wall after five left moves"
    (is (correct-block-positions? (move-left-five-times) #{[0 0] [0 17] [1 17] [2 17] [1 18]}))))

(deftest right-bound-test
  (testing "Test if right moves are ignored when the piece hits the right wall after four right moves"
    (is (correct-block-positions? (move-right-four-times) #{[0 0] [7 17] [8 17] [9 17] [8 18]}))))

(defn rotate-four-times
  []
  (do
    (tcore/reset-view)
    (tcore/rotate-cw)
    (tcore/rotate-cw)
    (tcore/rotate-cw)
    (tcore/rotate-cw))
  @tcore/game-view)

(deftest check-cw-rotation
  (testing "Test if current-piece is correctly rotated clockwise"
    (is (correct-block-positions? (tcore/rotate-cw) #{[0 0] [5 18] [5 17] [5 16] [6 17]}))))

(deftest check-cw-360-rotation
  (testing "Test if the current piece is equal to the current piece rotated clockwise four times"
    (is (let [initial-piece (:current-piece (do (reset-view) @tcore/game-view))
              rotated-piece (:current-piece (rotate-four-times))]
          (.equals initial-piece rotated-piece)))))