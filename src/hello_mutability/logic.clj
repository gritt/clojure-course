(ns hello-mutability.logic)

; extract :department key from :hospital map
; -> count queue size
; -> size < 5
(defn fit-queue? [hospital department]
  (-> hospital
      (get department)
      count
      (< 5)))

; add person to queue if fits
; without (sleep), clojure will have less concurrency issues to deal with
(defn arrive
  [hospital department person]
  (if (fit-queue? hospital department)
    (update hospital department conj person)
    ; how to raise exceptions
    ; a map with args can be provided for context
    (throw (ex-info "Queue is full!" {:trying-to-add person}))))

;same as .:
;(peek (get hospital department))
(defn get-next-in-queue
  [hospital department]
  (-> hospital
      (get department)
      (peek)))

(defn attend-first-in-queue
  [hospital department]
  (update hospital department pop))


(defn attend-first-in-queue-v1
  [hospital department]
  {:person-attended (update hospital department peek)
   :hospital-updated (update hospital department pop)})


(defn attend-first-in-queue-v2
  [hospital department]
  (let [queue (get hospital department)
        ; juxt creates a set of functions that are exec together
        peek-pop (juxt peek pop)
        ; when it executes, we can store the two results
        [person-attended updated-queue] (peek-pop queue)]

    {:person-attended person-attended
     :updated-queue   updated-queue}))


  (defn transfer
    [hospital from-department to-department]
    (let [person (get-next-in-queue hospital from-department)]
      (-> hospital
          (attend-first-in-queue from-department)
          (arrive to-department person)
          )))


  ; add person to queue if fits
  ; sleep and logging to demonstrate concurrency handling
  ; clojure handles locking automatically
  ; if two threads exec concurrently - and both add to the map

  ; when the *last* thread finishes
  ; the *swap* will realize the atom has been changed! value is outdated!
  ; so it will retry swap execution automatically

  ; the auto-retry is also another good reason to have idempotent functions
  ; executing it multiple times must not change the output or have side effects

  ; be aware of a busy-retry scenario if too many concurrent tasks are executing
  ; make sure a (swap) executes only the strict necessary logic that must be retried
  (defn arrive-verbose
    [hospital department person]
    (println "✚ start" person)
    (if (fit-queue? hospital department)
      ; multiple operation inside condition require (do ... )
      (do
        ; warning! because of the rand
        ; this would *not* be considered a pure function
        ; as it generates diff results for every execution
        ; and changes the state of rand
        (Thread/sleep (* (rand) 100))

        (println "⚙︎ updating" person)
        (update hospital department conj person))

      (throw (ex-info "Queue is full!" {:trying-to-add person}))))


  ; swap
  ; 1. first arg is the atom
  ; 2. second arg is the fn
  ; 3. when it calls the fn it auto @deref the atom
  (defn arrive-to-wait! [hospital person]
    ;(swap! hospital arrive-verbose :wait person)
    (swap! hospital arrive :wait person)
    (println "✓ added" person))

  (defn transfer-from-to!
    [hospital from-department to-department]
    (swap! hospital transfer from-department to-department))
