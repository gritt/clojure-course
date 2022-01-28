(ns curso.aula5)

; a map is also a collection,
; thus we can use map, reduce and filters

(def todos-pedidos {:mochila  {:quantidade 2 :preco 80}
                    :camiseta {:quantidade 3 :preco 40}})

(println todos-pedidos)

; destruct thing which is a vector
; (defn print-stuff-inside-map [thing]

;  destruct to key and value:
(defn map-printer [[key value]]
      (println "The key is:" key "Value is:" value)
      value)


(println (map map-printer todos-pedidos))

; eg.: calculate cost of all products
(defn preco-por-produto [[_ produto]]
      (* (:quantidade produto) (:preco produto)))

(println (map preco-por-produto todos-pedidos))

; sum all prices together!
; reduce with custom function!
(println (reduce
           #(+ %1 %2)
           (map preco-por-produto todos-pedidos)))

(defn calcula-total [pedidos]
      (reduce
        #(+ %1 %2)
        (map preco-por-produto pedidos)))

(println (calcula-total todos-pedidos))


; do the same stuff threading way!
; THREAD LAST!
; ,,,, <- is the "param" - result of prev fn
(defn calcula-total-v2
      [pedidos]
      (->> pedidos
           (map preco-por-produto,,,)
           (reduce +,,,)))

(println (calcula-total-v2 todos-pedidos))


; accessing the keys instead of destructing in args
; cleaner
(defn product-price [product]
      (* (:quantidade product) (:preco product)))

(defn calcula-total-v3
      [pedidos]
      (->> pedidos
           vals
           (map product-price)
           (reduce +)))

(println (calcula-total-v3 todos-pedidos))


; filter transactions
; which items are free! ?
; add one free first
(def todos-pedidos (assoc todos-pedidos
                          :chaveiro {:quantidade 1 :preco 0}
                          :adesivo {:quantidade 3 :preco 0}))

(println "Updated" todos-pedidos)

(defn product-free?
      [product]
      (<= (product :preco) 0))

; not!
(defn product-paid?
      [product]
      (not (product-free? product)))

; filter free products with lambda
(println (filter (fn [[key item]] (product-free? item)) todos-pedidos))

; filter free products with lambda even fancier
(println (filter #(product-free? (second %)) todos-pedidos))

; filter paid products
(println (filter #(product-paid? (second %)) todos-pedidos))


(println (product-paid? {:preco 10}))
(println (product-paid? {:preco 0}))

(println (product-free? {:preco 10}))
(println (product-free? {:preco 0}))


; ex calcular total de certificados
(def clientes [
               {:nome "Guilherme" :certificados ["Clojure" "Java" "Machine Learning"]}
               {:nome "Paulo" :certificados ["Java" "Ciência da Computação"]}
               {:nome "Daniela" :certificados ["Arquitetura" "Gastronomia"]}])

; para cada cliente, somar :certificados

(defn count-certificates
      [clientes]
      (->> clientes
           (map :certificados)
           (map count)
           (reduce +)))
; 0 - start thread last
; 1 - extract certificates from each client
; 2 - count lists of certificates
; 3 - sum all counts
;

;extract certificates vector from clients map
(println (map :certificados clientes))

; same as:
; map <fn> collection
(defn get-certificate
      [clientes]
      (:certificados clientes))

(println (map get-certificate clientes))