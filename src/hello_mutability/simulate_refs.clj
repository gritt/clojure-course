(ns hello-mutability.simulate-refs
  (:use [clojure pprint])
  (:require [hello-mutability.model :as h.model]))

(defn arrive [queue person]
  (conj queue person))

; unlike atoms when using `ref`
; the changes must happen inside a transaction block (dosync)
; a transaction block can exec multiple tasks
; (dosync (do-multiple-things))
(defn arrive-to-wait! [hospital person]
  ; get the wait queue from hospital map (ref)
  (let [queue (get hospital :wait)]

    ; ref-set receives:
    ; - a reference, which is the `queue`
    ; - the function, which is `arrive`
    ; - the function arguments: (queue, person)
    ; --- the `arrive` pure function receives a `queue`, not a *ref* to a queue
    ; --- so we need to deref `queue` first!

    ; ref-set wil SET queue to be the result of the function called
    ;(ref-set queue (arrive (deref queue) person))
    (ref-set queue (arrive @queue person))))

; both `ref-set` and `alter` guarantee that
; if the ref value changes while the transaction is running, it wil retry!
; but with alter handles the deref for us
(defn arrive-to-wait-alter! [hospital person]
  (let [queue (get hospital :wait)]
    (alter queue arrive person)))

(defn simulate-day []
    ; create a map with multiple references instead of one single atom
    (let [hospital {:wait (ref h.model/empty-queue)
                    :lab_one (ref h.model/empty-queue)
                    :lab_two (ref h.model/empty-queue)}]

      ; start a transaction block
      ; to be able to work with references
      (dosync
        (arrive-to-wait! hospital "1_Person")
        (arrive-to-wait-alter! hospital "2_Person"))

      (pprint hospital)))

(simulate-day)