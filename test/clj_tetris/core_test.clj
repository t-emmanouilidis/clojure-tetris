(ns clj-tetris.core-test
  (:require [clojure.test :refer :all]
            [clj-tetris.core :refer :all]))

(defn correct-block-positions?
  [current-view correct-positions]
  (if (some
        #(not (contains? correct-positions %))
        (into #{} (map :position (:all-blocks current-view))))
    false
    true))

(deftest initial-view-test
  (testing "Test that the positions of the blocks for the initial view are the correct ones"
    (is (correct-block-positions? (reset-view) #{[0 0] [4 17] [5 17] [6 17] [5 18]}))))

(deftest left-bound-test
  (testing "Test if left moves are ignored when the piece hits the left wall after five left moves"
    (is (correct-block-positions?
          (do (reset-view) (move-left) (move-left) (move-left) (move-left) (move-left))
          #{[0 0] [0 17] [1 17] [2 17] [1 18]}))))

(deftest right-bound-test
  (testing "Test if right moves are ignored when the piece hits the right wall after four right moves"
    (is (correct-block-positions?
          (do (reset-view) (move-right) (move-right) (move-right))
          #{[0 0] [7 17] [8 17] [9 17] [8 18]}))))

(deftest check-cw-rotation
  (testing "Test if current-piece is correctly rotated clockwise"
    (is (correct-block-positions? (do (reset-view) (rotate-cw)) #{[0 0] [5 18] [5 17] [5 16] [6 17]}))))

(deftest check-cw-360-rotation
  (testing "Test if the current piece is equal to the current piece rotated clockwise four times"
    (is (let [initial-piece (:current-piece (reset-view))
              rotated-piece (:current-piece (do (rotate-cw) (rotate-cw) (rotate-cw) (rotate-cw)))]
          (.equals initial-piece rotated-piece)))))