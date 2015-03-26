grammar SQL;
//**************************************************************

ID	: [a-zA-Z][a-zA-Z | 0-9];
CHARR :	['\''][a-z|A-Z]['\''] ;
COMMENT	: '-''-' ~[\r\n]* -> skip ;
TRUE :	('t'|'T')('r'|'R')('u'|'U')('e'|'E');
FALSE :	('f'|'F')('a'|'A')('l'|'L')('s'|'S')('e'|'E');
NUM: [0-9][0-9]* ;
PAJA: 'paja';
//DDL-----------
CREATEDB	: ('c'|'C')('r'|'R')('e'|'E')('a'|'A')('t'|'T')('e'|'E')(' ')('d'|'D')('a'|'A')('t'|'T')('a'|'A')('b'|'B')('a'|'A')('s'|'S')('e'|'E');
ALTERDB		: ('a'|'A')('l'|'L')('t'|'T')('e'|'E')('r'|'R')(' ')('d'|'D')('a'|'A')('t'|'T')('a'|'A')('b'|'B')('a'|'A')('s'|'S')('e'|'E');
RENAMETO	: ('r'|'R')('e'|'E')('n'|'N')('a'|'A')('m'|'M')('e'|'E')(' ')('t'|'T')('o'|'O');
DROPDB		: ('d'|'D')('r'|'R')('o'|'O')('p'|'P')(' ')('d'|'D')('a'|'A')('t'|'T')('a'|'A')('b'|'B')('a'|'A')('s'|'S')('e'|'E');
SHOWDB 	: ('s'|'S')('h'|'H')('o'|'O')('w'|'W')(' ')('d'|'D')('a'|'A')('t'|'T')('a'|'A')('b'|'B')('a'|'A')('s'|'S')('e'|'E')('s'|'S');
USEDB	: ('u'|'U')('s'|'S')('e'|'E')(' ')('d'|'D')('a'|'A')('t'|'T')('a'|'A')('b'|'B')('a'|'A')('s'|'S')('e'|'E');
CREATETABLE	: ('c'|'C')('r'|'R')('e'|'E')('a'|'A')('t'|'T')('e'|'E')(' ')('t'|'T')('a'|'A')('b'|'B')('l'|'L')('e'|'E');
CONSTRAINT	: ('c'|'C')('o'|'O')('n'|'N')('s'|'S')('t'|'T')('r'|'R')('a'|'A')('i'|'I')('n'|'N')('t'|'T');
PRIMARYK	: ('p'|'P')('r'|'R')('i'|'I')('m'|'M')('a'|'A')('r'|'R')('y'|'Y')(' ')('k'|'K')('e'|'E')('y'|'Y');
FOREIGNK	: ('f'|'F')('o'|'O')('r'|'R')('e'|'E')('i'|'I')('g'|'G')('n'|'N')(' ')('k'|'K')('e'|'E')('y'|'Y');
CHECK	: ('c'|'C')('h'|'H')('e'|'E')('c'|'C')('k'|'K');
REFERENCES	: ('r'|'R')('e'|'E')('f'|'F')('e'|'E')('r'|'R')('e'|'E')('n'|'N')('c'|'C')('e'|'E')('s'|'S');

INT	: ('i'|'I')('n'|'N')('t'|'T');
FLOAT	: ('f'|'F')('l'|'L')('o'|'O')('a'|'A')('t'|'T');
DATE	: ('d'|'D')('a'|'A')('t'|'T')('e'|'E');
CHAR	: ('c'|'C')('h'|'H')('a'|'A')('r'|'R');
TYPE	: INT | FLOAT | DATE | CHAR ;

AND	: ('a'|'A')('n'|'N')('d'|'D');
OR	: ('o'|'O')('r'|'R');
NOT	: ('n'|'N')('o'|'O')('t'|'T');
LESSTH	: '<';
GREATTH	: '>';
LESSEQ	: '<=';
GREATEQ	: '>=';
EQ 		: '=';
NOTEQ	: '<>';
ALTERTABLE	: ('a'|'A')('l'|'L')('t'|'T')('e'|'E')('r'|'R')(' ')('t'|'T')('a'|'A')('b'|'B')('l'|'L')('e'|'E');
ADDCOL	: ('a'|'A')('d'|'D')('d'|'D')(' ')('c'|'C')('o'|'O')('l'|'L')('u'|'U')('m'|'M')('n'|'n');
ADDCONST	: ('a'|'A')('d'|'D')('d'|'D')(' ')('c'|'C')('o'|'O')('n'|'N')('s'|'S')('t'|'T')('r'|'R')('a'|'A')('i'|'I')('n'|'N')('t'|'T');
DROPCOL	: ('d'|'D')('r'|'R')('o'|'O')('p'|'P')(' ')('c'|'C')('o'|'O')('l'|'L')('u'|'U')('m'|'M')('n'|'n');
DROPCONST	: ('d'|'D')('r'|'R')('o'|'O')('p'|'P')(' ')('c'|'C')('o'|'O')('n'|'N')('s'|'S')('t'|'T')('r'|'R')('a'|'A')('i'|'I')('n'|'N')('t'|'T');
DROPTABLE	: ('d'|'D')('r'|'R')('o'|'O')('p'|'P')(' ')('t'|'T')('a'|'A')('b'|'B')('l'|'L')('e'|'E');
SHOWTABLE	: ('s'|'S')('h'|'H')('o'|'O')('w'|'W')(' ')('t'|'T')('a'|'A')('b'|'B')('l'|'L')('e'|'E')('s'|'S');
SHOWCOLFROM	: ('s'|'S')('h'|'H')('o'|'O')('w'|'W')(' ')('c'|'C')('o'|'O')('l'|'L')('u'|'U')('m'|'M')('n'|'n')('s'|'S')(' ')('f'|'F')('r'|'R')('o'|'O')('m'|'M');


ddlDeclaration	: (ddlInstruction ';')+ ;
ddlInstruction	: CREATEDB ID 
				| ALTERDB ID RENAMETO ID
				| DROPDB ID 
				| SHOWDB
				| USEDB ID
				| CREATETABLE ID '(' (columnas)* (constraints)* ')'
				| ALTERTABLE ID RENAMETO ID
				| ALTERTABLE ID (action)*
				| DROPTABLE ID
				| SHOWTABLE
				| SHOWCOLFROM ID
				;
columnas	: columna | (columna ',')+ columna;
columna		: ID TYPE ;
constraints	: constraint | (constraint ',')+ constraint ;
constraint	: CONSTRAINT c;
c	: ('PK_')ID PRIMARYK (ids)* 
	| ('FK_')ID FOREIGNK (ids)* REFERENCES ID/*de una tabla*/ (ids)* 
	| ('CH_')ID CHECK '(' expression/*exp booleana*/ ')'
	;

ids	: ID | (ID ',')+ ID ; 
action 		: ADDCOL ID TYPE (constraints)*
			| ADDCONST c
			| DROPCOL ID
			| DROPCONST ID/*nombre*/'_'ID/*constrin*/
			;

expression : andExpr	#simpleExpression
		   | expression or_op andExpr	#doubleExpression
		   ;

andExpr : eqExpr	#simpleAndExpression
	   | andExpr and_op eqExpr	#doubleAndExpression 
	   ;

eqExpr : relationExpr	#simpleEqExpression 
	  | eqExpr eq_op relationExpr	#doubleEqExpression
	  ;

relationExpr : addExpr	#simpleRelationExpression
			| relationExpr rel_op addExpr	#doubleRelationExpression 
			;
addExpr: multExpr	#simpleAddExpression
	   | addExpr add_op multExpr	#doubleAddExpression
	   ;

multExpr: unaryExpr	#simpleMultExpression
		| multExpr mult_op unaryExpr	#doubleMultExpression 
		;

unaryExpr:  '('(INT|CHARR)')'  value #castedUnary
		 | '-' value	#negativeUnary
		 | '!' value 	#negationUnary
		 | value  		#simpleUnary
		 ; 

value	: ID	
		| literal 	
		;
		
literal	: int_literal	
		| char_literal	
		| bool_literal 	
		;

int_literal	: NUM ;

char_literal	: CHARR ;

bool_literal	: TRUE | FALSE ;
		
or_op :	('o'|'O')('r'|'R');
and_op :	('a'|'A')('n'|'N')('d'|'D');
eq_op :	'=' | '<>';
rel_op	: '<' | '>' | '<=' | '>=' ;	
add_op	: '+' | '-' ;
mult_op	: '*' | '/' | '%' ;

//DDL-----------

//DML-----------
//faltan estas producciones
INTEGER :	[0-9]+;
FLOATNUM :	[0-9]+'.'[0-9]+;
DATED :	'\''[0-9][0-9][0-9][0-9]'-'[0-9][0-9]'-'[0-9][0-9]'\'';
CHARACTER :	'\''[a-zA-Z]*'\'';
INSERT : 	('i'|'I')('n'|'N')('s'|'S')('e'|'E')('r'|'R')('t'|'T');
VALUES :	('v'|'V')('a'|'A')('l'|'L')('u'|'U')('e'|'E')('s'|'S');
UPDATE :	('u'|'U')('p'|'P')('d'|'D')('a'|'A')('t'|'T')('e'|'E');
SET :		('s'|'S')('e'|'E')('t'|'T');
WHERE :		('w'|'W')('h'|'H')('e'|'E')('r'|'R')('e'|'E');
DELETE :	('d'|'D')('e'|'E')('l'|'L')('e'|'E')('t'|'T')('e'|'E');
SELECT :	('s'|'S')('e'|'E')('l'|'L')('e'|'E')('c'|'C')('t'|'T');
FROM :		('f'|'F')('r'|'R')('o'|'O')('m'|'M');
ORDERBY :	('o'|'O')('r'|'R')('d'|'D')('e'|'E')('r'|'R')(' ')('b'|'B')('y'|'Y');
ASC : 		('a'|'A')('s'|'S')('c'|'C');
DESC :		('d'|'D')('e'|'E')('s'|'S')('c'|'C');

dmlDeclaration :	(dmlInstruction)+ ;

dmlInstruction  :	inserts
				| 	UPDATE ID SET asignaciones WHERE condition ';'
				| 	DELETE ID WHERE condition ';'
				|	SELECT sel FROM ID WHERE conditions ORDERBY ords ';'
				;

inserts : 	(INSERT ID '(' (columnas)* ')' VALUES '(' (valores)* ')'';')+;
valores :	(valor | (valor ',')+ valor);
valor :	(INTEGER | FLOATNUM | DATED | CHARACTER);

asignaciones :	asignacion | (asignacion ',')+ asignacion;
asignacion :	ID '=' valor ;

conditions 	:	condition | (condition (AND | OR)) condition; 
condition :		ID (eq_op | rel_op) valor	;

sel : '*' 
	| columnas  
	;
ords : 	ord | (ord ',')+ ord ;
ord :	expression (ASC | DESC)? ;


//DML-----------

DIGIT	: [0-9];
LOWERLETTER	: [a-z];

UPPERLETTER	: [A-Z];

//gramatica terminada





