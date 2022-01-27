(ns hello-04)

; hashmap / map
; no guarantee about the order of keys
(def estoque {"mochila"  10
              "camiseta" 5})

(println estoque)

; count also works with maps
(println (count estoque))

; get the keys
(println (keys estoque))

; get the values
(println (vals estoque))

; use keyword
; :mochila
;
; the right way
(def estoque {:mochila  10
              :camiseta 5})
(println estoque)

; return new map with new added key value
(println (assoc estoque :caneta 15))

; return new map with updated key value (built-in or custom fn)..
(println (update estoque :mochila inc))
(println (update estoque :mochila dec))

; anonymous lambda fn
(println (update estoque :mochila #(- % 3)))

; return new map with removed key value
(println (dissoc estoque :mochila))


; nested maps!
(def transaction {
                  :mochila  {:amount 5 :price 10}
                  :camiseta {:amount 3 :price 15}
                  })

(println transaction)


; return new map with added key
(println (assoc transaction :caneta {:amount 4 :price 2}))

; re-define transaction
(def transaction (assoc transaction :caneta {:amount 4 :price 2}))
(println transaction)

; access key of map directly
(println (transaction :mochila))

; directly will fail if key does not exist
; (println (transaction :estojo)


; access key using get a key
(println (get transaction :mochila))
; get allows a fallback if a key does not exist, otherwise return nil
(println (get transaction :estojo {:test "fallback" :desc "fallback"}))


; access key of map, alternative way
; the keys implements a function and can be called
; call the fn (:mochila) inside transaction
(println (:mochila transaction))
; if the key does not exist a fallback can be used, otherwise will get nil
(println (:estojo transaction {:fallback "test"}))


; access even more sub-nested key, transaction>mochila>amount
; call the fn reverse way
(println (:amount (:mochila transaction)))

; eg.: how to update nested stuff
; use update-in
(println (update-in transaction [:mochila :amount] #(+ % 1000)))

; threading first!
; get the result of fn
; use as argument for next call
; chained, or OOP style
; more readable, default way to use
(println (-> transaction
             :mochila
             :amount))

; going even further (print at the end)
(-> transaction
    :mochila
    :amount
    println)