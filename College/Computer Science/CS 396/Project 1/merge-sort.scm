; steamroller to flatten array from last years assignment (wrote it myself)
(define steamroller
  (lambda (x)
    (if (null? x)
        x
        (if (list? (car x))
            (if (null? (cdar x))
                (steamroller (cons (caar x) (steamroller (cdr x))))
                (steamroller (cons (caar x) (steamroller (cons (steamroller (cdar x)) (steamroller (cdr x))))))
            )
            (cons (car x) (steamroller (cdr x)))
        )
    )
  )
)

; merge sort function from assignment page
(define mergesort
  (lambda (alist)
    (if (null? (cdr alist)) alist
         (let ((splits (splitter alist)))
           (merge (mergesort (car splits)) (mergesort (cadr splits)))
         )
     )
  )
)

; this calls a splitter helper function
(define splitter
  (lambda (alist)
    (splitter_helper2 '() '() alist)
  )
)

; this splitter does not preserve the arrangment of the atoms, 
; since they are being merge sorted efficiency is more important
(define splitter_helper2
  (lambda (first-half second-half last)
    (if (null? last)
        (cons first-half (cons second-half '()))
        (if (null? (cdr last))
            (cons (cons (car last) first-half) (cons second-half '()))
            (splitter_helper2 (cons (car last) first-half) (cons (cadr last) second-half) (cddr last))
        )
    )
  )
)

(define splitter_helper
  (lambda (alternate alist)
    (if (null? alist)
        alternate
        (if (null? (cdr alist))
            (cons (car alist) alternate)
            (splitter_helper (cons (cadr alist) (cons (car alist) alternate)) (cddr alist))
        )
    )
  )
)

; merge two lists together
(define merge
  (lambda (list1 list2)
    (if (null? list1)
        list2
        (if (null? list2)
            list1
            ; if is a number then do a number comparison
            (if (and (number? (car list1)) (number? (car list2)))
                (if (< (car list1) (car list2))
                    (cons (car list1) (merge (cdr list1) list2))
                    (cons (car list2) (merge list1 (cdr list2)))
                )
                ; do a string comparison instead to sort atoms and strings
                (if (string<? (convert (car list1)) (convert (car list2)))
                    (cons (car list1) (merge (cdr list1) list2))
                    (cons (car list2) (merge list1 (cdr list2)))
                )
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

;; testing data:
(define x2 '(how does he do that cool stuff?))
(define x3 '(I can "go" 4 "about" 123 sodas k?))

(define x1 '(12 23 2 5 64 23 6756 234 2 42 535))
; merge some stuff
(display (mergesort x3))