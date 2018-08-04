(ns clj-tetris.block-utils-test
  (:require [clj-tetris.piece]
            [clj-tetris.block-utils :refer :all]
            [clj-tetris.piece-kind :refer :all]
            [clojure.test :refer :all])
  (:import (clj_tetris.piece Block)))

(def block (Block. [1.0 1.0] i-kind))

(deftest should-be-able-to-move-a-block-lower
  (testing "Should be able to move a block in the lower row"
    (is (= (move-block-down block) (Block. [1.0 0.0] i-kind)))))
