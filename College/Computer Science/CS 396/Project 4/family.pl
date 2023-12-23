multn(INT1, INT2, OUT) :- OUT is INT1*INT2.

xreplace(_, _, [], []).
xreplace(NEEDLE, REPLACE, [NEEDLE | INPUT], [REPLACE | OUTPUT]) :- xreplace(NEEDLE, REPLACE, INPUT, OUTPUT).
xreplace(NEEDLE, REPLACE, [X | INPUT], [X | OUTPUT]) :- xreplace(NEEDLE, REPLACE, INPUT, OUTPUT).


