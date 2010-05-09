(ns scarecrow.layout)

;; WARNING
; This macro uses local variable *context*.
(defmacro with-local-context [params & body]
  `(-> (merge ~'*context* ~params) ~@body))

(defmacro with-font [font & body]
  `(with-local-context {:font ~font} ~@body))
