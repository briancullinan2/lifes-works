; resets the ttboard variable
(define ttnew-game
  (lambda ()
    (set-helper '((_ _ _) (_ _ _) (_ _ _) ))
  )
)

; displays the ttboard
(define ttstat
  (lambda ()
    (display (car ttboard))
    (display "\n")
    (display (cadr ttboard))
    (display "\n")
    (display (caddr ttboard))
    (display "\n")
    (display ttboard)
    (display "\n")
  )
)

; allows recursive calling on the ttstat
(define ttstat-helper
  (lambda (null)
    (ttstat)
  )
)

; the main action function passes variables to ttplay-helper, and determines what to print out
(define ttplay
  (lambda (symbol row col)
    (let ((error (ttplay-helper symbol row col))
          (winnar (checkwin))
          )
      (if (eq? winnar #f)
          (display "\n")
          (if (eq? winnar 'x)
              (display "The winnar is: x")
              (display "The winnar is: o")
          )
      )
    )
  )
)

; calls the checkposition and set-helper functions
(define ttplay-helper
  (lambda (symbol row col)
    (let ((isvalid (checkposition col row))
          )
      (if (eq? isvalid #t)
          (ttstat-helper (set-helper (nth-replace (nth-replace symbol (nth ttboard row) col) ttboard row)))
          (display "Can't place a piece there! Try again!")
      )
    )
  )
)

; simply sets the new board based on the alist input
(define set-helper
  (lambda (alist)
    (set! ttboard alist)
  )
)



;; Simply checks to make sure a proposed placement position is on the board and not occupied. Returns false if 
;; the proposed position won't work.
(define checkposition
  (lambda (xpos ypos)
    (not (or (invalid? xpos) (invalid? ypos) 
             (not (eq? (nth (nth ttboard ypos) xpos) '_))))
  )
)

;; Just a little helper function to check to see if an x,y position is even on the board.
(define invalid?
  (lambda (pos)
    (if (or (< pos 1) (> pos 3))
        #t
        #f)))

;; Helper function.  Returns nth element in an input list.
(define nth 
  (lambda (alist num)
    (if (eq? num 1)
        (car alist)
        (nth (cdr alist) (- num 1))
    )
  )
)

; replaces the nth (num) in alist element with the new element
(define nth-replace
  (lambda (new alist num)
    (if (eq? num 1)
        (cons new (cdr alist))
        (cons (car alist) (nth-replace new (cdr alist) (- num 1)))
    )
  )
)

; calls checkwin
(define checkwin
  (lambda ()
    (checkwin-helper '())
  )
)

; allows for recursive calling via the null attribute
(define checkwin-helper
  (lambda (null)
    (let ((rows ttboard)
          (cols (make-cols ttboard))
          (diags (make-diags)))
      (or (findwinners rows) (findwinners cols) (findwinners diags)))))

; create colums recursively
(define make-cols
  (lambda (board)
    (if (null? (cdar board))
        (cons (list (caar board) (caadr board) (caaddr board)) '())
        (cons (list (caar board) (caadr board) (caaddr board)) (make-cols (list (cdar board) (cdadr board) (cdaddr board))))
    )
  )
)

; make dialogs stupidly
(define make-diags
  (lambda ()
    (list (list (caar ttboard) (cadadr ttboard) (car (cdr (cdr (caddr ttboard))))) (list (caddar ttboard) (cadadr ttboard) (caaddr ttboard)))
  )
)

; checks if any of the list elements match
(define findwinners
  (lambda (alist)
    (if (null? alist)
        #f
        (or (findwinners (cdr alist)) (all-match (car alist)))
    )
  )
)

; checks if all the elements in a list are the same
; not recursive
(define all-match
  (lambda (alist)
    (if (not (eq? (car alist) '_))
        (if (eq? (car alist) (cadr alist))
            (if (eq? (cadr alist) (caddr alist))
                (car alist)
                #f
                )
            #f
        )
        #f
    )
  )
)

; main global definition of ttboard
(define ttboard '((_ _ _) (_ _ _) (_ _ _) ))

; test functions
(ttnew-game)
(ttstat)
(ttplay 'x 3 3)
(ttplay 'o 2 3)
(ttplay 'x 2 3)
(ttplay 'x 1 1)
(ttplay 'o 33 -45)
(ttplay 'o 3 1)
(ttplay 'x 2 2)
