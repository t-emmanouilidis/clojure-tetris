(ns clj-tetris.core-test
  (:require [clojure.test :refer :all]
            [clj-tetris.core :refer :all]
            [clj-tetris.piece-kind :refer :all])
  (:import (clj_tetris.piece Block)))

(def nil-block (Block. [0 0] t-kind))
(def initial-blocks (vector nil-block))

(defn correct-block-positions?
  [current-view correct-positions]
  (let [all-block-positions (mapv :position (:all-blocks current-view))]
    (if (or (some
              #(not (contains? correct-positions %))
              (into #{} all-block-positions))
            (not (= (count correct-positions) (count all-block-positions))))
      false
      true)))

(deftest initial-view-test
  (testing "Test that the positions of the blocks for the initial view are the correct ones"
    (is (correct-block-positions? (reset-view initial-blocks) #{[0 0] [4 17] [5 17] [6 17] [5 18]}))))

(deftest left-bound-test
  (testing "Test if left moves are ignored when the piece hits the left wall after five left moves"
    (is (correct-block-positions?
          (do (reset-view initial-blocks) (move-left) (move-left) (move-left) (move-left) (move-left))
          #{[0 0] [0 17] [1 17] [2 17] [1 18]}))))

(deftest right-bound-test
  (testing "Test if right moves are ignored when the piece hits the right wall after four right moves"
    (is (correct-block-positions?
          (do (reset-view (vector nil-block)) (move-right) (move-right) (move-right))
          #{[0 0] [7 17] [8 17] [9 17] [8 18]}))))

(deftest check-cw-rotation
  (testing "Test if current-piece is correctly rotated clockwise"
    (is (correct-block-positions? (do (reset-view initial-blocks) (rotate-cw)) #{[0 0] [5 18] [5 17] [5 16] [6 17]}))))

(deftest check-cw-360-rotation
  (testing "Test if the current piece is equal to the current piece rotated clockwise four times"
    (is (let [initial-piece (:current-piece (reset-view initial-blocks))
              rotated-piece (:current-piece (do (rotate-cw) (rotate-cw) (rotate-cw) (rotate-cw)))]
          (.equals initial-piece rotated-piece)))))

(deftest test-collision-with-blocks
  (testing "Test that the current piece cannot move and overlap other blocks in the board"
    (is
      (correct-block-positions?
        (do
          (reset-view (vector (Block. [3 17] t-kind)))
          (move-left))
        #{[3 17] [4 17] [5 17] [6 17] [5 18]}))))

(deftest test-moving-down
  (testing "Test that the current piece is moving down correctly"
    (is (correct-block-positions?
          (do
            (reset-view [])
            (move-down))
          #{[4 16] [5 16] [6 16] [5 17]}))))

(deftest test-new-piece-spawning
  (testing "Test that a new piece spawns when the current piece reached the maximum depth"
    (is
      (correct-block-positions?
        (do
          (reset-view (vector (Block. [5 14] t-kind)))
          (move-down)
          (move-down)
          (move-down))
        #{[5 14]
          [4 15] [5 15] [6 15] [5 16]
          [4 17] [5 17] [6 17] [5 18]}))))

(defn- create-blocks-for-row
  [size-x row-num]
  (mapv
    #(Block. [% row-num] t-kind)
    (range size-x)))

(deftest test-clearing-full-rows
  (testing "Test that a row that is full gets correctly cleared when we're moving the current piece down"
    (is
      (correct-block-positions?
        (do
          (reset-view (create-blocks-for-row (last grid-size) 0))
          (move-down))
        #{[4 16] [5 16] [6 16] [5 17]}))))