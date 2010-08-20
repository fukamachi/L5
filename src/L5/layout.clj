(ns L5.layout
  (:require [L5.slide :as slide]))

(defmacro context [] `(deref ~'*context*))

(defmacro with-gensyms [names & body]
  `(let ~(vec (mapcat (fn [n] [n `(gensym)]) names))
     ~@body))

(defmacro get-next-padding [y]
  `(vec (cons ~y (-> (context) :padding rest))))

(defmacro with-local-context [params & body]
  `(binding [~'*context* (ref (merge (context) ~params))] ~@body))

(defmacro with-current-y [& body]
  (let [y (gensym)]
    `(let [~y (ref (-> (context) :padding (get 0)))]
       ~@(map (fn [b]
                `(with-local-context {:padding (get-next-padding @~y)}
                   (dosync (ref-set ~y ~b))))
              body))))

(defmacro with [params & body]
  `(with-local-context ~params (with-current-y ~@body)))

(defmacro txt [str]
  `(let [{g# :g font# :font width# :width padding# :padding} (context)]
     (slide/draw-wrapped-text @g# ~str font# width# padding#)))

(defmacro fit [& strs]
  `(let [{g# :g font# :font width# :width height# :height padding# :padding} (context)]
     (slide/draw-fitted-text @g# (vec ~@strs) font# width# height# padding#)))

(defmacro lines [& strs]
  `(let [{g# :g font# :font width# :width padding# :padding} (context)]
     (slide/draw-lines @g# (list ~@strs) font# width# padding#)))

(defmacro center [& strs]
  (with-gensyms [g font width height]
    `(let [{~g :g ~font :font ~width :width ~height :height} (context)]
       (with-current-y
         ~@(map
             (fn [s] `(slide/draw-aligned-text @~g ~s ~font ~width (:padding (context))))
             strs)))))

(defmacro item [& lines]
  `(lines ~@(map #(str "・" %) lines)))

(defmacro enum [& lines]
  `(lines
    ~@(for [[n l] (map list (range 0 (count lines)) lines)]
        `(str ~n ". " ~l))))

(defmacro p [& body]
  `(fn [] (with-current-y ~@body)))

(defmacro with-size [size & body]
  `(with {:font (java.awt.Font. (-> (context) :font .getFontName) 0 ~size)} ~@body))

(defmacro with-padding [y & body]
  `(with {:padding (get-next-padding (+ ((:padding (context)) 0) ~y))} ~@body))

(defmacro title [& str]
  `(with {:font (java.awt.Font. (-> (context) :font .getFontName) 0 50)}
         (fit (list ~@str))))

(defmacro th [& body]
  `(with {:padding [100 100 100 100]} (fit (list ~@body))))
