; create a function based on input
(define fn-maker
  (lambda (fn-spec)
    (eval (list 'define (cadar fn-spec) (cons 'lambda (cons (cdadr fn-spec) (cdaddr fn-spec)))))
    ; display verification that the function was created
    (display (cadar fn-spec))
    (display " ;yay the function was made!\n")
  )
)
                                                                                    
(define temp '((name: addtwo) (args: x) (body: (+ x 2)))) 
(fn-maker temp)
(display (addtwo 5))
