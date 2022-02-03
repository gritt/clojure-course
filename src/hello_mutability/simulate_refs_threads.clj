(ns hello-mutability.simulate-refs-threads
  (:use [clojure pprint])
  (:require [hello-mutability.model :as h.model]))

; future executes in a parallel thread, in the future
; eg.:
;(println (future 15))
;(println (future (Thread/sleep (rand 5000))))

(defn fit-queue? [queue]
  (-> queue
      count
      (< 5)))

(defn arrive [queue person]
  (if (fit-queue? queue)
    (conj queue person)
    (throw (ex-info "Queue is full!" {:trying-to-add person}))))


(defn arrive-to-wait! [hospital person]
  (let [queue (get hospital :wait)]
    (alter queue arrive person)))

; we can use *future* instead of (.Start (.Thread (fn []...)))
; future executes a block of code only *when the result is needed*
; future executes in paralel
; future auto-retry
(defn async-arrive-to-wait! [hospital person]
  (future
    (Thread/sleep (rand 5000))
    ; do inside sync transaction
    (dosync
      (println "running sync code" person)
      (arrive-to-wait! hospital person))))


(defn simulate-day-async []
  (let [hospital {:wait    (ref h.model/empty-queue)
                  :lab_one (ref h.model/empty-queue)
                  :lab_two (ref h.model/empty-queue)}

        ; capture the future results and put in a map!
        futures (mapv #(async-arrive-to-wait! hospital %) (range 10))]

    ; exec async tasks, ignore results
    ;(dotimes [person 10]
    ;  (async-arrive-to-wait! hospital person))

    ; when a feature completes it has a result value
    ; either is a *value* or an *exception*
    ; for the *exceptions*, they're captured, you can access them later

    ; eg.:
    ; watch the results every second, 8 times
    (future
      (dotimes [n 4]
        (Thread/sleep 2000)
        (pprint hospital)
        (pprint futures)))
    ))

(simulate-day-async)