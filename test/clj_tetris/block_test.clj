(ns clj-tetris.block-test
  (:require [clj-tetris.piece :as piece]
            [clj-tetris.block :refer :all]
            [clj-tetris.piece-kind :refer :all]
            [clojure.test :refer :all])
  (:import (clj_tetris.block Block)))

(def block (Block. [1.0 1.0] i-kind))

(deftest should-be-able-to-move-a-block-lower
  (testing "Should be able to move a block in the lower row"
    (is (= (move-block-down block) (Block. [1.0 0.0] i-kind)))))

