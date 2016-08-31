(ns clj-tetris.agent-test
  (:require [clojure.test :refer :all]
            [clj-tetris.core :refer :all]
            [clj-tetris.piece-kind :refer :all]
            [clj-tetris.agent :as tagent]
            [clj-tetris.view :as view])
  (:import (clj_tetris.piece Block)))

(deftest test-correct-move-for-clearing-rows
  (testing "Test that the agent correctly selects to drop a piece if a line is to be cleared"
    (is
      (do
        (reset-view [(Block. [0 0] t-kind)
                     (Block. [1 0] t-kind)
                     (Block. [2 0] t-kind)
                     (Block. [3 0] t-kind)
                     (Block. [7 0] t-kind)
                     (Block. [8 0] t-kind)
                     (Block. [9 0] t-kind)]
                    [t-kind o-kind o-kind o-kind o-kind o-kind])
        (= move-down (tagent/next-move))))))

(deftest test-penalty-for-heights
  (testing "Test that moves that cause the columns of the board to have difference in height larger that 1 get penalized"
    (is
      (=
        49.0
        (tagent/evaluate-view
          (reset-view [(Block. [0 0] t-kind)
                       (Block. [0 1] t-kind)
                       (Block. [0 2] t-kind)
                       (Block. [0 3] t-kind)
                       (Block. [0 4] t-kind)
                       (Block. [0 5] t-kind)
                       (Block. [0 6] t-kind)
                       ]
                      [o-kind o-kind o-kind o-kind]))))))

(deftest test-view-evaluation
  (testing "Test that a game-over view takes negative evaluation"
    (is
      (let [game-over-view (assoc (view/create-initial-view [] [1 1] [] [1 1]) :game-over true)
            normal-view (view/create-initial-view [] [1 1] [] [1 1])]
        (and
          (= (tagent/evaluate-view game-over-view) -1000.0)
          (= (tagent/evaluate-view normal-view) -0.1))))))