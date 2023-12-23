; check a complex list and search for an item in it
(define deep-member?
  (lambda (needle haystack)
    ; if the hackstack is empty we know for sure the answer is false
    (if (null? haystack)
        #f
        (if (list? (car haystack))
            ; check the car list then check the cdr list either one can return true
            (or (deep-member? needle (car haystack)) (deep-member? needle (cdr haystack)))
            ; convert the symbol to a string and compare it
            ;(if (eq? needle (car haystack))
            (if (string=? (convert needle) (convert (car haystack)))
                #t
                ; keep checking the cdr of the list
                (deep-member? needle (cdr haystack))
            )
        )
    )
  )
)

; check a complex list and search for an item in it and replace it
(define deep-replace
  (lambda (replace needle haystack)
    ; if the hackstack is empty we know for sure the answer is false
    (if (null? haystack)
        '()
        (if (list? (car haystack))
            ; check the car list then check the cdr list either one can return true
            (or (deep-replace replace needle (car haystack)) (deep-replace replace needle (cdr haystack)))
            ; convert the symbol to a string and compare it
            ;(if (eq? needle (car haystack))
            (if (string=? (convert needle) (convert (car haystack)))
                (cons replace (deep-replace replace needle (cdr haystack)))
                ; keep checking the cdr of the list
                (cons (car haystack) (deep-replace replace needle (cdr haystack)))
            )
        )
    )
  )
)


;need help converting numbers to string and symbols to strings
(define convert
  (lambda (x)
    ; already a string just return
    (if (string? x)
        x
        ;convert symbol
        (if (symbol? x)
            (symbol->string x)
            ;convert number
            (if (number? x)
                (number->string x)
                ;weird type, just return false
                '"E"
            )
        )
    )
  )
)

; predefined variables from assignment
(define x '(1 2 3 4 5 6 7 8 9 10))
(define y '(((a) b) (c j ("d" (f g) h)) i))

; call deep-member and print out the output
(display (deep-member? 'd y))

(deep-replace 'tree 'root '(this tree has a root))
