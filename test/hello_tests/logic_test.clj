(ns hello-tests.logic-test
  (:require [clojure.test :refer :all]
            [hello-tests.logic :refer :all]
            [hello-tests.model :as h.model]
            [schema.core :as s]))

; import schema validation and enable it
(s/set-fn-validation! true)

(deftest fit-queue?-test
  (testing "should fit queue when has space"
    (is (fit-queue? {:wait []} :wait))
    (is (fit-queue? {:wait [1 2]} :wait)))

  (testing "should not fit queue when is full"
    (is (not (fit-queue? {:wait [1 2 3 4 5]} :wait)))
    (is (not (fit-queue? {:wait [1 2 3 4 5 6]} :wait))))

  (testing "should not fit queue when department does not exist"
    (is (not (fit-queue? {:wait [1 2 3]} :lab_one))))
  )

(deftest arrive-to-test
  (let
    ; we can move commonly used variables...
    [full-hospital {:wait [1 2 3 4 5]}]

    (testing "should accept people when there is space in the queue"
      ; __________________________________________________________
      ; return the updated queue
      ; __________________________________________________________
      (is (= full-hospital
             (arrive-to {:wait [1 2 3 4]}, :wait 5)))
      (is (= {:wait [1 2 5]}
             (arrive-to {:wait [1 2]}, :wait 5)))
      ; __________________________________________________________
      ; return a map with the result and the status:
      ; __________________________________________________________
      ;(is (= {:hospital full-hospital :status :success}
      ;       (arrive-to {:wait [1 2 3 4]}, :wait 5)))
      ;(is (= {:hospital {:wait [1 2 5]} :status :success}
      ;       (arrive-to {:wait [1 2]}, :wait 5)))
      )

    ; ############; how to assert the errors ##################

    ; __________________________________________________________
    ; return nil:
    ; swap! requires a function that changes the atom
    ; and returns the updated state, it cannot return nil
    ; otherwise it will mess with the atom state!
    ; __________________________________________________________
    ;(testing "should return nil when queue is full"
    ;  (is (nil? (arrive-to {:wait [1 2 3 4 6]}, :wait 5))))


    ; __________________________________________________________
    ; return exception:
    ; test if an exception is thrown, !warning!
    ; avoid using generic exception types
    ; __________________________________________________________
    ;(testing "should raise exception when queue is full"
    ;  (is (thrown? clojure.lang.ExceptionInfo
    ;               (arrive-to {:wait [1 2 3 4 6]}, :wait 5)
    ;               )))


    ; __________________________________________________________
    ; return exception with error type:
    ; try catch and identifying the error type
    ; __________________________________________________________
    (testing "should raise exception when queue is full"
      (is (try
            (arrive-to {:wait [1 2 3 4 6]} :wait 5)
            false
            (catch clojure.lang.ExceptionInfo e
              (= :queue-is-full-err (:error-type (ex-data e)))
              )))
      )


    ; __________________________________________________________
    ; return a map with the result and the status:
    ; will mess with the state, swap or similar!
    ; __________________________________________________________
    ;(testing "should return error status when queue is full"
    ;  (is (= {:hospital full-hospital :status :error}
    ;         (arrive-to full-hospital, :wait 6)))
    ;  )
    ))

; be aware that:
; (let [hospital {:wait [] :x-ray [5]}])
; is not actually a *queue*, as we wanted it to be, instead it is a vector!
; the logic associated with the *transfer-to* (peek, pop)
; only work with the behavior we expect in queues!
;
; so we need to ensure that an hospital can only be
; created with the proper "type"
; we can do that with schemas!
(deftest transfer-test
  (testing "should accept people when there is space in the queue"

    (let [hospital {:wait  (conj h.model/empty-queue "id-5")
                    :x-ray h.model/empty-queue}]
      (is (= {:wait  h.model/empty-queue
              :x-ray (conj h.model/empty-queue "id-5")}
             (transfer hospital :wait :x-ray))))


    (let [empty-hospital {:wait  (conj h.model/empty-queue "id-51" "id-5")
                          :x-ray (conj h.model/empty-queue "id-13")}]
      (is (= {:wait  ["id-5"]
              :x-ray ["id-13" "id-51"]}
             (transfer empty-hospital :wait :x-ray)))))


  (testing "should not accept people when queue is full"
    (let [full-hospital {:wait  (conj h.model/empty-queue "id-6")
                         :x-ray (conj h.model/empty-queue "id-1" "id-2" "id-3" "id-4" "id-5")}]
      (is (thrown? clojure.lang.ExceptionInfo
                   (transfer full-hospital :wait :x-ray)))))


  ; the schema ensure we cannot send a nil hospital
  (testing "should not accept when hospital is nil"
    (is (thrown? clojure.lang.ExceptionInfo (transfer nil :a :b))))


  ; we can ensure protect our functions with pre-conditions validations!
  ; eg.:
  ; when (transfer A, B) and keys A or B don't exist
  (testing "should not accept with failing pre conditions"
    (let [hospital {:wait  (conj h.model/empty-queue "id-1")
                    :x-ray (conj h.model/empty-queue "id-2")}]

      ; the drawback of using AssertionError:
      ; in the JVM the AssertionErrors can be disabled in execution time
      (is (thrown? AssertionError (transfer hospital :unknown :wait)))
      (is (thrown? AssertionError (transfer hospital :wait :unknown))))
    ))




