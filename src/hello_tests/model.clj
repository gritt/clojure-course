(ns hello-tests.model
  (:require [schema.core :as s])
  (:import (java.util Queue)))

(def empty-queue clojure.lang.PersistentQueue/EMPTY)

(defn new-hospital [] {:wait    empty-queue
                       :lab_one empty-queue
                       :lab_two empty-queue})


(s/def PatientID s/Str)
(s/def Department (s/queue PatientID))
(s/def Hospital {s/Keyword Department})


;_____________________________________________________________
;https://github.com/plumatic/schema
; Schema is a rich language for describing data shapes,
; we can think as a lighter version of "typing"

; lets validate these schemas??!!

;_____________________________________________________________
; accepted, eg.:

;(s/validate PatientID "some-uuid-str")
;(s/validate Department empty-queue)
;(s/validate Department (conj empty-queue "id-one" "id-two" "id-three"))
;(s/validate Hospital {:x-ray (conj empty-queue "id-one")})

;_____________________________________________________________
; will fail, eg.:

;(s/validate PatientID 10)
;(s/validate Department [])
;(s/validate Department ["id-one" "id-two" "id-three"])
;(s/validate Department [10 20 30])
;(s/validate Hospital [10])
;(s/validate Hospital {:key [10]})
