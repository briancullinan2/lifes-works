; delete the nth symbol in a top level list
(define ndelete
  (lambda (thelist nth)
    ; start the counter at the nth position and count down
    (ndelete_helper thelist nth nth)
  )
)

; this function has an extra attribute just to control counting down
(define ndelete_helper
  (lambda (thelist count nth)
    ; return null when we get to the end of the list
    (if (null? thelist)
        thelist
        ; skip over car when we count down to 1
        (if (= 1 count)
            ; only return the helper of the cdr because the car is being removed
            (ndelete_helper (cdr thelist) nth nth)
            ; combine the car with the helper of the cdr
            (cons (car thelist) (ndelete_helper (cdr thelist) (- count 1) nth))
        )
    )
  )
)

; predefined variables from assignment
(define x '(1 2 3 4 5 6 7 8 9 10))
(define y '(((a) b) (c d (d (f g) h)) i))

; call ndelete and print out the output
(display (ndelete x 2))
