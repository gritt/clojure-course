(ns hello-clojure.hello-02)

(defn value-is-eligible?
      "Returns whether the gross-value is eligible for discount"
      [gross-value]
      (> gross-value 100))

(defn is-eligible?
      "Allows very abstracted stuff to happen"
      [policy-checker, value]
      (policy-checker value)
      )

(fn [gross-value max-value] (> gross-value max-value))

(defn calculate-discount
      "Returns value with discount if eligible"
      [is-eligible? gross-value]
      (if (is-eligible? gross-value)
        (let [discount-tax (/ 10 100)
              discount-value (* gross-value discount-tax)]
             (- gross-value discount-value))
        gross-value
        ))

(println (value-is-eligible? 200))

; eg.: anonymous function
(println (is-eligible?
           (fn [gross-value] (> gross-value 100))
           200))

; fn can be replaced with #, together with the arguments declaration

; eg.: anonymous function with unnamed number args %1, %2...
(println (is-eligible?
           #(> %1 100)
           200))

; eg.: anonymous function with single unnamed arg %
(println (is-eligible?
           #(> % 100)
           200))

(println "Value with discount is:"
         (calculate-discount value-is-eligible? 200))


