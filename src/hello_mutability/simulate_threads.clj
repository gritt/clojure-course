(ns hello-mutability.simulate-threads
  (:use [clojure pprint])
  (:require [hello-mutability.logic :as h.logic]
            [hello-mutability.model :as h.model]))

; mutability exercise
; people arrive at the hospital and wait in different queues until they go to the lab for a treatment.

; move values from one queue to another
; explore different structures: vector, list, collections, queues
; peek | conj | pop
; differ in behavior according to the structure
;
; we need a queue:
; -> first entry in -> first entry out
; -> new entries go to the end of the queue

(defn atom-test []

  ; atom is like a shell to the actual reference (?)
  (let [atom-hospital (atom {:wait h.model/empty-queue})]
    (pprint atom-hospital)

    ; to read the contents we can deref the atom
    ; @ is a shortcut
    (pprint (deref atom-hospital))
    ;(pprint @atom-hospital)

    ; to update the contents, we need swap the atom, (without deref)
    ; call the fn "assoc" inside the atom with param
    (swap! atom-hospital assoc :lab_one h.model/empty-queue)
    (swap! atom-hospital assoc :lab_two h.model/empty-queue)
    (swap! atom-hospital assoc :pay h.model/empty-queue)
    (pprint @atom-hospital)

    ; a normal immutable update in a map, eg:
    ; (update @test-hospital :lab_one conj "1_José" )
    ; would return the map with the new value
    ; but would not change the state

    ; the swap fn will do the update
    ; clojure can do it concurrently
    ; any function can be called with swap!
    ; fn that change the state must !!bang!! to make it explicit
    (swap! atom-hospital update :lab_one conj "1_José")
    (pprint @atom-hospital)))

;(atom-test)


; ================ # ================ # ================ # ================


(defn simulate-day-with-concurrency
  []
  ; define hospital as atom
  ; internal scope
  (let [hospital (atom (h.model/new-hospital))]
    ; starts a new Thread
    (.start (Thread. (fn [] (h.logic/arrive-to-wait! hospital "1_Tales"))))
    (.start (Thread. (fn [] (h.logic/arrive-to-wait! hospital "2_Tania"))))
    (.start (Thread. (fn [] (h.logic/arrive-to-wait! hospital "3_Tamila"))))
    (.start (Thread. (fn [] (h.logic/arrive-to-wait! hospital "4_Talita"))))
    (.start (Thread. (fn [] (h.logic/arrive-to-wait! hospital "5_Tadeu"))))
    (.start (Thread. (fn [] (h.logic/arrive-to-wait! hospital "6_Tenório"))))

    (.start (Thread. (fn []
                       (Thread/sleep 3000)
                       (println "Hospital Review")
                       (pprint hospital))))))

;(simulate-day-with-concurrency)


; ================ # ================ # ================ # ================


(defn simulate-day-with-concurrency-v2
  []
  (let [
        hospital (atom (h.model/new-hospital))
        people ["1_Tales", "2_Tania", "3_Tamila", "4_Talita", "5_Tadeu", "6_Tenório",]]

    ; map only execs when it needs the results - lazy loading
    ; mapv forces the execution
    (mapv #(.start (Thread. (fn [] (h.logic/arrive-to-wait! hospital %)))) people)
    ; a lambda function that starts a thread and runs the logic (arrive-to-wait)
    ; whose only argument (%) is each value of the people vector being iterated by mapv

    (.start (Thread. (fn []
                       (Thread/sleep 3000)
                       (println "Hospital Review")
                       (pprint hospital))))))

;(simulate-day-with-concurrency-v2)


; ================ # ================ # ================ # ================


(defn starts-thread
  [hospital person]
  (.start (Thread. (fn [] (h.logic/arrive-to-wait! hospital person)))))

; curry function? mid term?
; because of the arity of args of the mapv (single)
; this functions receives and wrap the first arg
; and return a fn that receives the second arg
; then it calls the final function which needs the two args
(defn wrap-prepare-starts-thread
  [hospital]
  (fn [person] (starts-thread hospital person)))

(defn simulate-day-with-concurrency-v3
  []
  (let [
        hospital (atom (h.model/new-hospital))
        people ["1_Tales", "2_Tania", "3_Tamila", "4_Talita", "5_Tadeu", "6_Tenório",]]

    ; wrap-prepare-starts-thread returns a function that receives one arg
    (mapv (wrap-prepare-starts-thread hospital) people)

    (.start (Thread. (fn []
                       (Thread/sleep 3000)
                       (println "Hospital Review")
                       (pprint hospital))))))

;(simulate-day-with-concurrency-v3)


; ================ # ================ # ================ # ================

; a function that contains like two versions
; will match according to the params arity
(defn starts-thread-with-params
  ; 1 - matches when call with single arg
  ([hospital]
   (fn [person] (starts-thread-with-params hospital person)))
  ; 2 - matches when call with two args
  ([hospital person]
   (.start (Thread. (fn [] (h.logic/arrive-to-wait! hospital person))))))

(defn simulate-day-with-concurrency-v4
  []
  (let [
        hospital (atom (h.model/new-hospital))
        people ["1_Tales", "2_Tania", "3_Tamila", "4_Talita", "5_Tadeu", "6_Tenório",]]

    (mapv (starts-thread-with-params hospital) people)

    (.start (Thread. (fn []
                       (Thread/sleep 3000)
                       (println "Hospital Review")
                       (pprint hospital))))))

;(simulate-day-with-concurrency-v4)


; ================ # ================ # ================ # ================


; wrapped in a partial function
(defn starts-thread-final
  ([hospital person]
   (.start (Thread. (fn [] (h.logic/arrive-to-wait! hospital person))))))

(defn simulate-day-with-concurrency-v5
  []
  (let [
        hospital (atom (h.model/new-hospital))
        people ["1_Tales", "2_Tania", "3_Tamila", "4_Talita", "5_Tadeu", "6_Tenório",]

        ; partials - creates a function from a "part" of the original function
        ; it's common in function programming not to have all args available
        ; so with this we prepare with the args that we have
        starts-thread-partial (partial starts-thread-final hospital)]

    (mapv starts-thread-partial people)

    (.start (Thread. (fn []
                       (Thread/sleep 3000)
                       (println "Hospital Review")
                       (pprint hospital))))))

;(simulate-day-with-concurrency-v5)


; ================ # ================ # ================ # ================

(defn simulate-day-with-concurrency-v6
  []
  (let [
        hospital (atom (h.model/new-hospital))
        people ["1_Tales", "2_Tania", "3_Tamila", "4_Talita", "5_Tadeu", "6_Tenório"]]

    ; to loop through elements of maps, vector, lists
    (doseq [person people]
      (starts-thread-final hospital person))

    ; to execute things N times
    ; like a (range 6), where each number is %person
    ;(dotimes [person 6]
    ;  (starts-thread-final hospital person))

    (.start (Thread. (fn []
                       (Thread/sleep 3000)
                       (println "Hospital Review")
                       (pprint hospital))))))

(simulate-day-with-concurrency-v6)