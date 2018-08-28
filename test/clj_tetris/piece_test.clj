(ns clj-tetris.piece-test
  (:require [clojure.test :refer :all]
            [clj-tetris.block :refer :all]
            [clj-tetris.piece :as piece]
            [clj-tetris.piece-kind :as kind])
  (:import (clj_tetris.block Block)))

(deftest test-piece-moves-correctly-left
  (testing "Should be able to move a piece to the left"
    (is (= (piece/move-piece (piece/create [5.0 5.0] kind/i-kind) [-1.0 0.0])
           (piece/create [4.0 5.0] kind/i-kind)))))

(deftest test-point-rotates-correctly
  (testing "Should be able to rotate a point"
    (is (= [1 -4] (piece/rotate-point [4.0 1.0])))))

(deftest test-piece-to-blocks-conversion
  (testing "Should be able to get the blocks that make up a piece"
    (is (= [(Block. [4.0 5.0] kind/t-kind)
            (Block. [5.0 5.0] kind/t-kind)
            (Block. [6.0 5.0] kind/t-kind)
            (Block. [5.0 6.0] kind/t-kind)]
           (piece/to-blocks
             (piece/create [5.0 5.0] kind/t-kind))))))