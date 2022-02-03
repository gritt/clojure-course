(ns hello-mutability.simulate-atom-swaps
  (:use [clojure pprint])
  (:require [hello-mutability.logic :as h.logic]
            [hello-mutability.model :as h.model]))

(defn simulate-day []
  (let [hospital (atom (h.model/new-hospital))]

    (h.logic/arrive-to-wait! hospital "1_Tales")
    (h.logic/arrive-to-wait! hospital "2_Tania")
    (h.logic/arrive-to-wait! hospital "3_Tamila")
    (h.logic/arrive-to-wait! hospital "4_Talita")

    (h.logic/transfer-from-to! hospital :wait :lab_one)
    (h.logic/transfer-from-to! hospital :wait :lab_two)

    (h.logic/arrive-to-wait! hospital "5_Tadeu")
    (h.logic/arrive-to-wait! hospital "6_Tenório")

    (h.logic/transfer-from-to! hospital :wait :lab_one)
    (h.logic/transfer-from-to! hospital :wait :lab_one)
    (h.logic/transfer-from-to! hospital :lab_one :lab_two)

    (println "Hospital Review")
    (pprint hospital)
    ))

(simulate-day)

; in this exercise we have one big atom
; with multiple attributes being manipulated
; creating a busy-retry scenario

; eg.: when two people are going to different queues at the same time
; the later will suffer a retry because the atom changed its state
; this is bad and poorly efficient also.

; considering this scenario
; would be better to have split atoms with  smaller scope / responsibility
; when data from one queue is being moved to another
; we only need to "lock" both involved

; !BUT! there is no support for transactions between two atoms
; lets use another structure!
; ->> simulate_refs