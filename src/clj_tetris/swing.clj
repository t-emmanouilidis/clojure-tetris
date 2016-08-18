(ns clj-tetris.swing
  (:gen-class)
  (:import [javax.swing AbstractAction KeyStroke JPanel JFrame Timer])
  (:import [java.awt Color Rectangle]
           (java.util TimerTask))
  (:require [clj-tetris.core :as tcore :refer :all]))

(def gray (Color. 48 99 99))
(def silver (Color. 210 255 255))
(def light-gray (Color. 165 185 185))
(def bright-gray (Color. 228 242 242))

(def main-frame (JFrame. "Tetris"))

(def block-size 20)
(def block-margin 3)
(def block-size-plus-margin (+ block-size block-margin))

(defn create-rect
  [[size-x size-y] [pos-x pos-y]]
  (Rectangle. (* pos-x block-size-plus-margin)
              (* (- size-y pos-y) block-size-plus-margin)
              block-size
              block-size))

(defn draw-empty-grid
  [graphics [size-x size-y]]
  (.setColor graphics light-gray)
  (let [panel-block-positions (for [x (range size-x) y (range size-y)] [x y])]
    (loop [remaining-block-positions panel-block-positions]
      (if (not (empty? remaining-block-positions))
        (do
          (let [[x y] (first remaining-block-positions)]
            (.draw graphics (create-rect [size-x size-y] [x y])))
          (recur (rest remaining-block-positions)))))))

(defn draw-blocks
  [graphics blocks size-of-grid]
  (if (not (empty? blocks))
    (do (.fill graphics (create-rect size-of-grid (:position (first blocks))))
        (draw-blocks graphics (rest blocks) size-of-grid))))

(defn draw-all-blocks
  [graphics all-blocks size-of-grid]
  (.setColor graphics bright-gray)
  (draw-blocks graphics all-blocks size-of-grid))

(defn draw-current-piece
  [graphics size-of-grid]
  (.setColor graphics silver)
  (draw-blocks graphics (tcore/current-piece-blocks) size-of-grid))

(defn onPaint
  [graphics]
  (let [view @tcore/game-view
        size-of-grid (:grid-size view)
        all-blocks (:all-blocks view)]
    (.setColor graphics silver)
    (draw-empty-grid graphics size-of-grid)
    (draw-all-blocks graphics all-blocks size-of-grid)
    (draw-current-piece graphics size-of-grid)))

(def main-panel
  (proxy [JPanel] []
    (paint [graphics]
      (let [panel-width (.getWidth (.getSize this))
            panel-height (.getHeight (.getSize this))]
        (.setColor graphics gray)
        (.fillRect graphics 0 0 panel-width panel-height)
        (onPaint graphics)))))

(def tetris-space-action
  (proxy [AbstractAction] []
    (actionPerformed [event]
      (tcore/rotate-cw)
      (.repaint main-panel))))

(def tetris-down-action
  (proxy [AbstractAction] []
    (actionPerformed [event]
      (tcore/move-down)
      (.repaint main-panel))))

(def tetris-up-action
  (proxy [AbstractAction] []
    (actionPerformed [event]
      (.repaint main-panel))))

(def tetris-left-action
  (proxy [AbstractAction] []
    (actionPerformed [event]
      (tcore/move-left)
      (.repaint main-panel))))

(def tetris-right-action
  (proxy [AbstractAction] []
    (actionPerformed [event]
      (tcore/move-right)
      (.repaint main-panel))))

(defn -main
  [& args]

  ;Example key binding
  (.put (.getInputMap main-panel) (KeyStroke/getKeyStroke "SPACE") "tetrisSpaceAction")
  (.put (.getInputMap main-panel) (KeyStroke/getKeyStroke "DOWN") "tetrisDownAction")
  (.put (.getInputMap main-panel) (KeyStroke/getKeyStroke "UP") "tetrisUpAction")
  (.put (.getInputMap main-panel) (KeyStroke/getKeyStroke "LEFT") "tetrisLeftAction")
  (.put (.getInputMap main-panel) (KeyStroke/getKeyStroke "RIGHT") "tetrisRightAction")

  (.put (.getActionMap main-panel) "tetrisSpaceAction" tetris-space-action)
  (.put (.getActionMap main-panel) "tetrisDownAction" tetris-down-action)
  (.put (.getActionMap main-panel) "tetrisUpAction" tetris-up-action)
  (.put (.getActionMap main-panel) "tetrisLeftAction" tetris-left-action)
  (.put (.getActionMap main-panel) "tetrisRightAction" tetris-right-action)

  ;Setup panel properties
  (.setContentPane main-frame main-panel)

  (.setSize main-panel 700 700)
  (.setFocusable main-panel true)

  (.setSize main-frame 700 700)
  (.setVisible main-frame true)

  (.start
    (Timer.
      100
      (proxy [AbstractAction] []
        (actionPerformed [event] (.repaint main-panel)))))

  (.scheduleAtFixedRate
    (java.util.Timer.)
    (proxy [TimerTask] []
      (run [] (move-down)))
    0
    1000))



