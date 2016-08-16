(ns clj-tetris.swing
  (:gen-class)
  (:import [javax.swing AbstractAction KeyStroke JPanel JFrame])
  (:import [java.awt Dimension Color Graphics2D])
  (:import [java.awt.event KeyEvent]))

(def bluishGray (Color. 48 99 99))
(def bluishSilver (Color. 210 255 255))

(def space-key (KeyStroke/getKeyStroke "SPACE"))
(def down-key (KeyStroke/getKeyStroke "DOWN"))
(def up-key (KeyStroke/getKeyStroke "UP"))
(def left-key (KeyStroke/getKeyStroke "LEFT"))
(def right-key (KeyStroke/getKeyStroke "RIGHT"))

(def main-frame (JFrame. "Tetris"))

(defn onKeyPress [key]
  (case key
    space-key "SPACE"
    down-key "DOWN"
    up-key "UP"
    left-key "LEFT"
    right-key "RIGHT"
    "default" nil))

(defn onPaint [graphics])


(def main-panel
  (proxy [JPanel] []
    (paint [graphics]
      (do
        (let [panel-width (.getWidth (.getSize this)) panel-height (.getHeight (.getSize this))]
          (.setColor graphics bluishGray)
          (.fillRect graphics 0 0 panel-width panel-height)
          (onPaint graphics))))))

(def key-action (proxy [AbstractAction] [] (actionPerformed [event] (.repaint main-panel))))

(defn -main
  [& args]

  (.add main-frame main-panel)

  ;Example key binding
  (.put (.getInputMap main-panel) (KeyStroke/getKeyStroke "F2") "customAction")
  (.put (.getActionMap main-panel) "customAction" key-action)

  ;Setup panel properties
  (.setPreferredSize main-panel (Dimension. 700 400))
  (.setFocusable main-panel true)

  (.setVisible main-frame true)
  )



