(ns hello-03)

; vector of numbers
(def prices [30 100 400 1000 50000])

; access index 0
(println (prices 0))

; get index 2
(println (get prices 2))
; get index 6
(println (get prices 6))

; pre-defined default value when index N does not exist
(println (get prices 6 "default-value"))

; return new vector with added value
(println (conj prices 20000))

; immutable
(println prices)

; like +=, increase +1
(println (inc 1))

; access index 1 of prices and increase value
; returns new vector
(println (update prices 1 inc))

; access index 1 of prices and decrease value
; returns new vector
(println (update prices 1 dec))

; access index 1 of prices and runs function
; returns new vector

(defn inc3
      "increases 3"
      [val]
      (+ val 3))

(println (update prices 1 inc3))

; same as:
; anonymous function that +3
;#(+ %1 3)
(println (update prices 1 #(+ % 3)))


(defn calculate-discount
      "Returns value with discount if eligible"
      [gross-value]
      (if (> gross-value 100)
        (let [discount-tax (/ 10 100)
              discount-value (* gross-value discount-tax)]
             (- gross-value discount-value))
        gross-value
        ))
(println (calculate-discount 200))

; map -> apply fn to each element
; calculate discount for each price in vector
(println "new map with result:" (map calculate-discount prices))
; original prices still immutable
(println prices)



; filter even elements of vector [0 to 9]
(println (range 10))
(println (filter even? (range 10)))



(defn value-is-eligible?
      "Returns whether the gross-value is eligible for discount"
      [gross-value]
      (> gross-value 100))

; filter prices eligible eligible for discount
(println (filter value-is-eligible? prices))


; apply discount only for eligible prices
; return new map with discount applied
(println (map calculate-discount
              (filter value-is-eligible? prices)))


; reduce a vector to a single element,
; eg: sum all prices of vector
(println (reduce + prices))

; reduce will get index 0 and 1, and call fn
; after that, will get the index 2 and the result of the previous operation
(defn custom-sum
      [val-1 val2]
      (+ val-1 val2))

(println (reduce custom-sum prices))

; uses value "0" + index 0, instead of index 0 + index 1
(println (reduce custom-sum 0 prices))

; returns 0 when vector is empty
(println (reduce custom-sum 0 []))

