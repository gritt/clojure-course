(ns hello-clojure.hello-01)

; symbol with value
(def this-is-a-symbol 13)

; prints symbol
(print this-is-a-symbol)

; re-define symbol value, eg:
(def this-is-a-symbol (+ 13 3))

; symbol with vector
(def this-is-a-vector ["Mochila", "Chinelo"])

; count length
(count this-is-a-vector)

; prints index 0 of vector
(this-is-a-vector 0)

; returns new vector with appended value
(conj this-is-a-vector "Camiseta")

; redefine vector with appended value, eg:
(def this-is-a-vector (conj this-is-a-vector "Camiseta"))

; define a function
(defn prints-a-message []
  (println "this is the message")
  )

; define a function with argument
(defn prints-a-message-with-arg [some-arg]
  (println some-arg "this is the message"))


; define function with argument whos a fn
(defn count-vector [vector] 
  (count vector)
  )

(defn sum-to-vector-size [vector-counter-fn] 
  (+ 5 vector-counter-fn)
  )

; higher order function (?)
(sum-to-vector-size (count-vector this-is-a-vector))

; define function with documentation
(defn aplica-desconto
  "Returns the value with 25% discount"
  [valor]
  (* valor (- 1 0.25)))


; discount symbol in global to scope (?)
(def discount 0.25)
(defn aplica-desconto-v2
  "Returns the value with 25% discount"
  [valor]
  (* valor (- 1 discount)))

; discount symbol in internal scope
; syntax tip: close all parentheses in last line indicates the last return
(defn aplica-desconto-v3
  "Returns the value with 25% discount"
  [valor]
  (let [discount 0.25]
    (* valor (- 1 discount))))

; asks type of something
(class 90.0)

; conditionals
(if (> 500 100)
  (println "maior")
  )

; only applies discount when value > 100
(defn discount
      "Returns the value with 25% discount if higher than 100"
      [value]
      (if (> value 100)
        (let [discount 0.25]
             (* value (- 1 discount)))
        ))

; only applies discount when value > 100, otherwise returns value
(defn discount
      "Returns the value with 25% discount if higher than 100"
      [value]
      (if (> value 100)
        (let [discount 0.25]
             (* value (- 1 discount)))
        value
        ))

