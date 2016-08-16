(ns clj-tetris.swing
  (:gen-class)
  (:import [javax.swing AbstractAction KeyStroke JPanel JFrame])
  (:import [java.awt Color])
  (:import [java.util Stack]))

(def bluishGray (Color. 48 99 99))
(def bluishSilver (Color. 210 255 255))

(def main-frame (JFrame. "Tetris"))

(def lastKeyStack (Stack.))

(defn onKeyPress [key] (.push lastKeyStack key))

(defn onPaint [graphics]
  (.setColor graphics bluishSilver)
  (if (not (.isEmpty lastKeyStack)) (.drawString graphics (.pop lastKeyStack) 20 20)))

(def main-panel
  (proxy [JPanel] []
    (paint [graphics]
      (do
        (let [panel-width (.getWidth (.getSize this)) panel-height (.getHeight (.getSize this))]
          (.setColor graphics bluishGray)
          (.fillRect graphics 0 0 panel-width panel-height)
          (onPaint graphics))))))

(def tetris-space-action (proxy [AbstractAction] [] (actionPerformed [event] (onKeyPress "SPACE") (.repaint main-panel))))
(def tetris-down-action (proxy [AbstractAction] [] (actionPerformed [event] (onKeyPress "DOWN") (.repaint main-panel))))
(def tetris-up-action (proxy [AbstractAction] [] (actionPerformed [event] (onKeyPress "UP") (.repaint main-panel))))
(def tetris-left-action (proxy [AbstractAction] [] (actionPerformed [event] (onKeyPress "LEFT") (.repaint main-panel))))
(def tetris-right-action (proxy [AbstractAction] [] (actionPerformed [event] (onKeyPress "RIGHT") (.repaint main-panel))))

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

  (.setSize main-panel 200 200)
  (.setFocusable main-panel true)

  (.setSize main-frame 200 200)
  (.setVisible main-frame true))



