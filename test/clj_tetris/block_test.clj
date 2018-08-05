(ns clj-tetris.block-test
  (:require [clj-tetris.piece :as piece]
            [clj-tetris.block :refer :all]
            [clj-tetris.piece-kind :refer :all]
            [clojure.test :refer :all])
  (:import (clj_tetris.block Block)))

(def test-piece (piece/create-piece [4.0 4.0] t-kind))

(def test-piece-blocks
  [(Block. [3.0 4.0] t-kind)
   (Block. [4.0 4.0] t-kind)
   (Block. [5.0 4.0] t-kind)
   (Block. [4.0 5.0] t-kind)])

(def block (Block. [1.0 1.0] i-kind))

(deftest should-be-able-to-move-a-block-lower
  (testing "Should be able to move a block in the lower row"
    (is (= (move-block-down block) (Block. [1.0 0.0] i-kind)))))

(deftest should-be-able-to-get-blocks-for-piece
  (testing "Should be able to get the blocks that a piece consists of"
    (is (= (let [test-blocks (blocks-from-piece test-piece)]
             (do (println test-blocks) test-blocks)) test-piece-blocks))))