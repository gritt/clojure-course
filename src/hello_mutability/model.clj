(ns hello-mutability.model)

; import queue structure definition, javaish style..
; define symbol as ref to type
(def empty-queue clojure.lang.PersistentQueue/EMPTY)

; returns map with all hospital queues
(defn new-hospital [] {:wait    empty-queue
                       :lab_one empty-queue
                       :lab_two empty-queue
                       :pay     empty-queue})
