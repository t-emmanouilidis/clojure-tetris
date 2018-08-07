(ns clj-tetris.view-test
  (:require [clojure.test :refer :all]
            [clj-tetris.view :as view]
            [clj-tetris.piece-kind :refer :all]
            [clj-tetris.piece])
  (:import (clj_tetris.block Block)))

(def test-blocks [(Block. [0.0 1.0] t-kind)
                  (Block. [1.0 1.0] t-kind)
                  (Block. [1.0 2.0] i-kind)
                  (Block. [2.0 0.0] s-kind)
                  (Block. [2.0 1.0] s-kind)])

(deftest should-be-able-to-find-blocks-below-row
  (testing "Should be able to find all blocks below row"
    (is (= (view/blocks-below-row test-blocks 1) [(Block. [2.0 0.0] s-kind)]))))

(deftest should-be-able-to-find-blocks-above-row
  (testing "Should be able to find all blocks above row"
    (is (= (view/blocks-above-row test-blocks 1) [(Block. [1.0 2.0] i-kind)]))))

(deftest test-full-row
  (testing "Should be able to check if a row is full or not"
    (is (view/is-row-full? test-blocks 3 1.0))))

(deftest test-out-of-x-bounds
  (testing "Should be able to check if a block is out of x axis bounds"
    (is (view/position-out-of-bounds? [3.0 0.0] 3))))

(deftest test-out-of-y-bounds
  (testing "Should be able to check if a block is out of y axis bounds"
    (is (view/position-out-of-bounds? [0.0 -1.0] 3))))

(deftest test-positions-overlap
  (testing "Should be able to check if two positions overlap"
    (is (view/positions-overlap? [[1.0 1.0] [1.0 1.0]]))))

(deftest test-positions-do-not-overlap
  (testing "Should be able to check that two positions do not overlap"
    (is (not (view/positions-overlap? [[1.0 0.0] [1.0 1.0]])))))

