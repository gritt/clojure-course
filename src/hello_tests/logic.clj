(ns hello-tests.logic
  (:use [clojure pprint])
  (:require [hello-tests.model :as h.model]
            [schema.core :as s]))

(defn fit-queue?
  [hospital department]
  ;(if-let [queue (get hospital department)]

  (when-let [queue (get hospital department)]
    (-> queue
        count
        (< 5)))

  ; will return `nil` if *ANY* step of the threading is nil
  ; (some-> hospital
  ;        department
  ;        count
  ;        (< 5))
  )

(defn add-to-queue [hospital department person]
  (if (fit-queue? hospital department)
    (update hospital department conj person)))

; by default clojure has no types, an argument could be anything
; instead of types, we can use schema! which define rules
; the argument must match
; these schemas are validated in execution time!, not compilation

; function with schema validation .:
; returns h.model/Hospital
; receive arguments of type Hospital and Keyword
(s/defn attend-first-in-queue :- h.model/Hospital
  [hospital :- h.model/Hospital, department :- s/Keyword]
  (update hospital department pop))

(s/defn get-next-in-queue :- h.model/PatientID
  [hospital :- h.model/Hospital, department :- s/Keyword]
  (-> hospital
      (get department)
      (peek)))

(defn arrive-to [hospital department person]
  ; if it doesn't fit return nil (cannot swap!)
  (if (fit-queue? hospital department)
    (update hospital department conj person)
    ;   ,,,

    ; else
    ; we will return an exception
    ; with additional keys to catch by the :error-type
    (throw (ex-info "queue is full" {:error-type :queue-is-full-err
                                     :person     person
                                     :queue      department})))


  ; alternatively (cannot swap!)
  ; we can extract the state update to another small fn
  ; this one will return a map with the state and the status, no exception
  ;(if-let [updated-hospital (add-to-queue hospital, department person)]
  ;  {:hospital updated-hospital :status :success}
  ;  {:hospital hospital :status :error})
  )


; extracted function with post condition
; intrinsic behavior / characteristics of a function,
; (a sum can only return >= than the input)
; (validate input and output contracts..)
(defn same-size? [hospital-in, hospital-out, from, to]
  (= (+ (count (get hospital-in from)) (count (get hospital-in to)))
     (+ (count (get hospital-out from)) (count (get hospital-out to))))
  )

; defining pre-conditions input validation
; in the (transfer function) the Hospital cannot be nil!

; the drawback of using AssertionError:
; in the JVM the AssertionErrors can be disabled in execution time
; so the :pre code would not run :(

; define pos-conditions for output validation
; eg.:
; 1. after transfer, the people count must not change
; 2. after transfer, the order of the queues must not change
(s/defn transfer
  [hospital :- h.model/Hospital, from :- s/Keyword, to :- s/Keyword]
  {
   ; departments must exist inside the hospital
   :pre  [(contains? hospital from)
          (contains? hospital to)]

   ; sum of people in all departments must be the same as before!
   ; good idea? extract these validations to functions!
   :post [(same-size? hospital % from to)]
   }
  (let [person (get-next-in-queue hospital from)]
    (-> hospital
        (attend-first-in-queue from)
        (arrive-to to person)
        )))


