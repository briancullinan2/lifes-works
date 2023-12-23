;; load-file is a simple function that loads the definitions from a file
;; It recursives calls itself, reading one line at a time; on the recursive return, it cons'es
;; all of the lines together in a giant list, which it returns to caller.
(require (lib "include.ss") )

( define load-file
   ( lambda ( port )
      ( let ( ( nextrec ( read port ) ) )
         ( cond
            ( ( eof-object? nextrec ) '() ) ;; If I've read off the end, return empty list
            ( else
              ( let* ( ( nascent-db ( load-file port ) ) ) ;; Recursive call to finish reading file
                 ;; Now add the line read at this level to growing list
                 ( cons nextrec nascent-db ) ) ) ) ) ) )

; get the specified part of the class definition
(define get-definition
  (lambda (args name)
    (if (null? args)
        '()
        (if (equal? (caar args) name)
            (apply (eval (caar args)) `(,(cdar args)))
            (get-definition (cdr args) name)
        ))))


; merge methods together, used for merging the parent to the child class
(define merge-methods
  (lambda (parent-methods child-methods)
    ;(display parent-methods)
    (if (null? parent-methods)
        child-methods
        (if (null? (car parent-methods))
            (if (null? (cdr parent-methods))
                child-methods
                (merge-methods (cdr parent-methods) child-methods)
                )
            (if (null? (get-method child-methods (caar parent-methods) (cadar parent-methods)))
                ; add the parent method to child methods
                (cons (car parent-methods) (merge-methods (cdr parent-methods) child-methods))
                (merge-methods (cdr parent-methods) child-methods)
                )))))
; merge ivars
(define merge-ivars
  (lambda (parent-ivars child-ivars)
    (if (null? parent-ivars)
        child-ivars
        (if (null? (car parent-ivars))
            (if (null? (cdr parent-ivars))
                child-ivars
                (merge-ivars (cdr parent-ivars) child-ivars)
                )
            (if (null? (get-ivar child-ivars (caar parent-ivars)))
                ; add the parent method to child methods
                (cons (car parent-ivars) (merge-ivars (cdr parent-ivars) child-ivars))
                (merge-ivars (cdr parent-ivars) child-ivars)
                )))))

; merge the second list onto the end of the first list
(define merge
  (lambda (first second)
    ;(display second)
    (if (null? first)
        second
        (cons (car first) (merge (cdr first) second))
        )))



; this is our class creator
(define class
  (lambda args
    ; call all the parts that define a class
    (let ((return-val '())
          (class-name (car args))
          (parent           (get-definition (cdr args) 'parent:))
          (constructor-args (get-definition (cdr args) 'constructor_args:))
          (ivars            (get-definition (cdr args) 'ivars:))
          (methods          (get-definition (cdr args) 'methods:))
          (static           (get-definition (cdr args) 'static:))
          (constructor '())
          (get-methods '())
          (get-ivars   '())
          (this        `(eval (lambda args ,__call)))
          (this-static `(eval (lambda args ,__call-static)))
          )
      
      ; merge the methods if there is a parent
      (if (null? parent)
          '()
          (let () 
            (set! methods (merge-methods (apply (eval (car parent)) '(__get-methods)) methods))
            (set! ivars (merge-ivars (apply (eval (car parent)) '(__get-ivars)) ivars))
            )
      )
      
      ; set the get-methods to return the methods for an instantiated class
      (set! get-methods `(__get-methods 0 ,(eval (lambda () methods))))
      (set! get-ivars `(__get-ivars 0 ,(eval (lambda () ivars))))
      
      ; get the constructor method for creating new instances
      (set! constructor `(__new ,(count constructor-args) ,(cons 'unquote `((lambda ,constructor-args 
                                                                              (eval (letrec ,ivars
                                                                                      (lambda args 
                                                                                        (letrec ,(list
                                                                                              `(*this* ,this)
                                                                                              `(__methods ,(list 'quasiquote methods))
                                                                                              )
                                                                                        (apply *this* args))
                                                                                      ))))))))

      ; add constructor and __get-methods to static methods
      (set! static (list constructor get-methods get-ivars static))
      ;(display constructor)
      
      ; THIS ALLOWS FOR A STATIC SECTION
      (set! return-val (list 'define class-name `(lambda args (letrec ,(list `(*this* ,this-static)
                                                                `(__methods ,(list 'quasiquote static))
                                                                  )
                                                   (apply *this* args))
                                                   )))
      ;(display return-val)
      (newline)
      (eval return-val)
      )))


; this is the dispatcher for static objects
(define __call-static
  ; make this a list so the class creator can import this code into the class
  '(let ( ; get called method from methods
         (method (get-method __methods (car args) (count (cdr args))))
         )
     ; if the get-method returns '() then no method was found
     ;(display args)
     (if (null? method)
          (let () 
            (display "Error: No method with signature:")
            (display args)
            (newline))
          (apply (caddr method) (cdr args))
          )
     ))

; this is our function for distributing function calls
(define __call
  ; make this a list so the class creator can import this code into the class
  '(let (
         (method (get-method __methods (car args) (count (cdr args))))
         )
     ;(display (cdr args))
     (if (null? method)
         (let ()
           (display "Error: No method with signature:")
           (display args)
           (newline))
         (apply (caddr method) (cdr args))
         )
     ;(display args)
     ))


; this is out object factory that creates local objects
(define new
  (lambda args
    ; rebuild the object with a constructor method and replace dispatcher
    (apply (car args) (cons '__new (cdr args)))
    ))


; get an ivar by name
(define get-ivar
  (lambda (ivars name)
    (if (null? ivars)
        '()
        (if (null? (car ivars))
            (if (null? (cdr ivars))
                '()
                (get-ivar (cdr ivars) name)
                )
            (if (equal? (caar ivars) name)
                (car ivars)
                (get-ivar (cdr ivars) name)
                )))))

; recursevly loop through methods and find the one that matches
(define get-method
  ; take a methods list of all the methods, the name of the method being searched for, and the count for how many arguments the method has
  (lambda (methods name count-args)
    ;(display methods)
    (if (null? methods)
        '()
        (if (null? (car methods))
            (if (null? (cdr methods))
                '()
                (get-method (cdr methods) name count-args)
                )
            (if (list? (cadar methods))
                (if (and (equal? (caar methods) name) (equal? (count (cadar methods)) count-args))
                    (car methods)
                    (if (null? (cdr methods))
                        '()
                        (get-method (cadr methods) name count-args)
                        ))
                (if (and (equal? (caar methods) name) (equal? (cadar methods) count-args))
                    (car methods)
                    (if (null? (cdr methods))
                        '()
                        (get-method (cdr methods) name count-args)
                        )))))))


; count the number of top level elements
(define count
  (lambda (list)
    (if (null? list)
        0
        (+ 1 (count (cdr list)))
    )))
  
; this gets the parent class for another class to use
(define parent:
  (lambda (args)
    args
  ))

; this gets the constructor arguments in a list form
(define constructor_args:
  (lambda (args)
    args
  ))

; this is the let list of local variables for an object
(define ivars:
  (lambda (args)
    args
  ))

; returns the list of methods
(define methods:
  (lambda (args)
    (if (null? args)
        '()
        (cons (list (caar args) (count (cadar args)) (list 'unquote `(lambda ,(cadar args) ,(caddar args))) ) (methods: (cdr args)))
    )))


; loads a file of class definitions into a list
(define load-classes
  (lambda (file)
    (load-class (load-file (open-input-file file)))
    (display "")
  )
)

; loads a single class at a time recursively
(define load-class
  (lambda (classes)
    (if (null? classes)
        '()
        (if (equal? (caar classes) 'define)
            (eval (car classes))
            (let ()
              (apply (eval (caar classes)) (cdar classes))
              (display "Defined class:")
              (display (cadar classes))
              (display "\n")
              (load-class (cdr classes))
              )))))
  
#|
(load-classes "CLASS1.TXT")

(define x (new point 3 4))
(define y (new point 2 5))
(x 'display)
(x 'getx)
(x 'getz)
(x 'polar)
(x 'dist y)
(x 'setx 67)
(x 'display)


(load-classes "inherit.txt")

(define foo (new point 12 45))
(define fum (new 3dpoint 5 7 9))
(foo 'show)
(fum 'show)
(foo 'getx)
(foo 'getz)
(fum 'getx)
(fum 'getz)
(foo 'show 'Hello!)
(fum 'show 'Howdy)


(load-classes "car_world.txt")
(define car1 (new xcar 'mercedes 250 null form1))
(define dude (new person 40 'eck))
(define punk (new person 17 'bubba))
(define geez (new person 78 'oldtimer))
(car1 'status)
(car1 'drive)
(car1 'fillup 15)
(car1 'status)
(car1 'drive)
(car1 'new_driver dude)
(car1 'status)
(car1 'drive)
(car1 'status)
(car1 'insurance)
(car1 'new_driver punk)
(car1 'status)
(car1 'drive)
(car1 'status)
(car1 'fillup 20)
(car1 'insurance)
(car1 'new_driver geez)
(car1 'status)
(car1 'drive)
(car1 'status)
(car1 'insurance)


(load-classes "inheritance2.txt")
(define test (new dog 'fluffy 200))
(test 'describe)
|#