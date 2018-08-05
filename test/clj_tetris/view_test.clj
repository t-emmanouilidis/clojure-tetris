(ns clj-tetris.view-test
  (:require [clojure.test :refer :all]
            [clj-tetris.view :as view]
            [clj-tetris.piece-kind :refer :all]
            [clj-tetris.piece])
  (:import (clj_tetris.block Block)))

(def blocks [(Block. [0.0 1.0] t-kind)
             (Block. [1.0 2.0] i-kind)
             (Block. [2.0 0.0] s-kind)])

(deftest should-be-able-to-find-blocks-below-row
  (testing "Should be able to find all blocks below row"
    (is (= (view/blocks-below-row blocks 1) [(Block. [2.0 0.0] s-kind)]))))

(deftest should-be-able-to-find-blocks-above-row
  (testing "Should be able to find all blocks above row"
    (is (= (view/blocks-above-row blocks 1) [(Block. [1.0 2.0] i-kind)]))))

