<?php

// language spec
// The objective of my language is to be as shorthanded as possible.

Program			::=		[ Command-List ]
				|		[ Command-List
Command-List	::=		Command EOL Command-List
				|		Command EOL
Command			::=		Assignment
				|		Declaration
				|		For-Loop
				|		While-Loop
				|		If-Statement
				|		Function-Def
				|		Function-Call
				|		Return-Call
				|		p Expression  // internal print functionality
				|		Identifier ( Expression )
Assignment		::=		Identifier = Expression
Expression		::=		Literal
				|		V-Name
				|		Operator Expression
				|		( Expression )
				|		Logical Expression
Function-Def	::=		f F-Name(Param-Def)[Command-List]
Function-Call	::=		F-Name Param-Call
Return-Call		::=		re Expression  // returns to line after call just like in assembly
F-Name			::=		Identifier
Param-Def		::=		Identifier
				|		Identifier, Param-Def
Param-Call		::=		Expression
				|		Expression, Param-Call
For-Loop		::=		fo(Assignment,Expression,Assignment)[Command-List]
While-Loop		::=		w(Expression)[Command-List]
If-Statement	::=		if(Expression)[Command-List]
				|		if(Expression)[Command-List]el[Command-List]
				|		if(Expression)[Command-List]el If-Statement
V-Name			::=		Identifier
				::=		Identifier[Integer]  // list reference
Declaration		::=		v Assignment  // global var
				|		c Assignment  // constant
				|		l Assignment  // local var
Type			::=		Identifier
Operator		::=		+ | - | * | / | ^
Logical			::=		< | > | == | <= | >= | || | && | !=
Identifier		::=		Letter | Identifier Letter | Identifier Digit
Literal			::=		i Integer-Literal
				|		r Real-Literal
				|		h Hex-Literal
				|		b Binary-Literal
Integer-Literal	::=		Digit | Integer-Literal Digit
Real-Literal	::=		Integer-Literal . Integer-Literal
Hex-Literal		::=		[A-Z0-9]
Binary-Literal	::=		1
				|		0
				|		1 Binary-Literal
				|		0 Binary-Literal
Comment			::=		# Letter EOL
						


?>