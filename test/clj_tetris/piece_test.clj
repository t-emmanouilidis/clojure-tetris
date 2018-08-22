(ns clj-tetris.piece-test
  (:require [clojure.test :refer :all]
            [clj-tetris.piece :as piece]
            [clj-tetris.piece-kind :as kind]))

(deftest test-piece-moves-correctly-left
  (testing "Should be able to move a piece to the left"
    (is (= (piece/move-piece (piece/create-piece [5.0 5.0] kind/i-kind) [-1.0 -1.0])
           (piece/create-piece [4.0 4.0] kind/i-kind)))))