(ns scarecrow.layout
  (:require [scarecrow.slide :as slide]))

(defmacro get-current-padding []
  `(:padding ~'*context*))

(defmacro get-next-padding [y]
  `(vec (cons ~y (-> ~'*context* :padding rest))))

(defmacro with-local-context [params & body]
  `(binding [~'*context* (merge ~'*context* ~params)] ~@body))

(defmacro with-current-y [& body]
  (let [y (gensym)]
    `(let [~y (ref (-> ~'*context* :padding (get 0)))]
       ~@(map (fn [b]
                `(with-local-context {:padding (get-next-padding @~y)}
                   (dosync (ref-set ~y ~b))))
              body))))

(defmacro with [params & body]
  `(with-local-context ~params (with-current-y ~@body)))

(defmacro txt [str]
  `(let [g# @(:g ~'*context*)
         font# (:font ~'*context*)
         width# (:width ~'*context*)
         padding# (:padding ~'*context*)]
     (slide/draw-wrapped-text g# ~str font# width# padding#)))

(defmacro fit [& strs]
  `(let [g# @(:g ~'*context*)
         font# (:font ~'*context*)
         width# (:width ~'*context*)
         height# (:height ~'*context*)
         padding# (:padding ~'*context*)]
     (slide/draw-fitted-text g# (vec ~@strs) font# width# height# padding#)))

(defmacro lines [& lines]
  `(let [g# @(:g ~'*context*)
         font# (:font ~'*context*)
         width# (:width ~'*context*)
         padding# (:padding ~'*context*)]
     (slide/draw-lines g# (list ~@lines) font# width# padding#)))

(defmacro item [& lines]
  `(lines ~@(map #(str "・" %) lines)))

(defmacro enum [& lines]
  `(lines
    ~@(for [[n l] (map list (range 0 (count lines)) lines)]
        `(str ~n ". " ~l))))

(defmacro p [& body]
  `(fn [] (with-current-y ~@body)))

(defmacro with-size [size & body]
  `(with {:font (java.awt.Font. (-> ~'*context* :font .getFontName) 0 ~size)} ~@body))

(defmacro with-padding [y & body]
  `(with {:padding (get-next-padding (+ ((get-current-padding) 0) ~y))} ~@body))

(defmacro title [& str]
  `(with {:font (java.awt.Font. (-> ~'*context* :font .getFontName) 0 50)}
         (fit (list ~@str))))

;; FIXME
(defmacro title-page [& strs]
  `(with {:padding [50 30 100 30]} (fit (list ~@strs))))

;; FIXME
(defmacro with-title [ttl & body]
  `(with {:padding [20 20 420 20]}
     (title ~ttl)
     (with-padding 40 ~@body)))

(defmacro th [& body]
  `(with {:padding [100 100 100 100]} (fit (list ~@body))))
