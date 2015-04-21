//************************** SQL Grammar **************************
grammar SQL;

fragment Letter 	: ('a'..'z' | 'A'..'Z');
fragment Digit 		:'0'..'9' ;


//************************** Words reserved for DDL **************************

CREATE		: ('c'|'C')('r'|'R')('e'|'E')('a'|'A')('t'|'T')('e'|'E');
DATABASE	: ('d'|'D')('a'|'A')('t'|'T')('a'|'A')('b'|'B')('a'|'A')('s'|'S')('e'|'E');
DATABASES	: ('d'|'D')('a'|'A')('t'|'T')('a'|'A')('b'|'B')('a'|'A')('s'|'S')('e'|'E')('s'|'S');
ALTER		: ('a'|'A')('l'|'L')('t'|'T')('e'|'E')('r'|'R');
RENAME		: ('r'|'R')('e'|'E')('n'|'N')('a'|'A')('m'|'M')('e'|'E');
TO			: ('t'|'T')('o'|'O');
DROP		: ('d'|'D')('r'|'R')('o'|'O')('p'|'P');
SHOW 		: ('s'|'S')('h'|'H')('o'|'O')('w'|'W');
SHOWS 		: ('s'|'S')('h'|'H')('o'|'O')('w'|'W')('s'|'S');
USE			: ('u'|'U')('s'|'S')('e'|'E');
TABLE		: ('t'|'T')('a'|'A')('b'|'B')('l'|'L')('e'|'E');
TABLES		: ('t'|'T')('a'|'A')('b'|'B')('l'|'L')('e'|'E')('s'|'S');
CONSTRAINT	: ('c'|'C')('o'|'O')('n'|'N')('s'|'S')('t'|'T')('r'|'R')('a'|'A')('i'|'I')('n'|'N')('t'|'T');
PRIMARY		: ('p'|'P')('r'|'R')('i'|'I')('m'|'M')('a'|'A')('r'|'R')('y'|'Y');
KEY			: ('k'|'K')('e'|'E')('y'|'Y');
FOREIGN		: ('f'|'F')('o'|'O')('r'|'R')('e'|'E')('i'|'I')('g'|'G')('n'|'N');
CHECK		: ('c'|'C')('h'|'H')('e'|'E')('c'|'C')('k'|'K');
REFERENCES	: ('r'|'R')('e'|'E')('f'|'F')('e'|'E')('r'|'R')('e'|'E')('n'|'N')('c'|'C')('e'|'E')('s'|'S');

INT		: ('i'|'I')('n'|'N')('t'|'T');
FLOAT	: ('f'|'F')('l'|'L')('o'|'O')('a'|'A')('t'|'T');
DATE	: ('d'|'D')('a'|'A')('t'|'T')('e'|'E');
CHAR	: ('c'|'C')('h'|'H')('a'|'A')('r'|'R');
BOOLEAN	: ('b'|'B')('o'|'O')('o'|'O')('l'|'L')('e'|'E')('a'|'A')('n'|'N');
TRUE 	: ('t'|'T')('r'|'R')('u'|'U')('e'|'E');
FALSE 	: ('f'|'F')('a'|'A')('l'|'L')('s'|'S')('e'|'E');

AND		: ('a'|'A')('n'|'N')('d'|'D');
OR		: ('o'|'O')('r'|'R');
NOT		: ('n'|'N')('o'|'O')('t'|'T');
LESSTH	: '<';
GREATTH	: '>';
LESSEQ	: '<=';
GREATEQ	: '>=';
EQ 		: '=';
NOTEQ	: '<>';
ADD				: ('a'|'A')('d'|'D')('d'|'D');
COLUMN			: ('c'|'C')('o'|'O')('l'|'L')('u'|'U')('m'|'M')('n'|'N');
COLUMNS			: ('c'|'C')('o'|'O')('l'|'L')('u'|'U')('m'|'M')('n'|'N')('s'|'S');


//************************** Words reserved for DML **************************

FLOATNUM 	: ('-')?(NUM)'.'(NUM);
DATED 		: '\'' Digit Digit Digit Digit '-' Digit Digit '-' Digit Digit '\'';
CHARS 		: '\''(Letter|Digit)(Letter|Digit|' '|'-'|'_')*'\'';
INSERT 		: ('i'|'I')('n'|'N')('s'|'S')('e'|'E')('r'|'R')('t'|'T');
INTO		: ('i'|'I')('n'|'N')('t'|'T')('o'|'O');
VALUES 		: ('v'|'V')('a'|'A')('l'|'L')('u'|'U')('e'|'E')('s'|'S');
UPDATE 		: ('u'|'U')('p'|'P')('d'|'D')('a'|'A')('t'|'T')('e'|'E');
SET 		: ('s'|'S')('e'|'E')('t'|'T');
WHERE 		: ('w'|'W')('h'|'H')('e'|'E')('r'|'R')('e'|'E');
DELETE 		: ('d'|'D')('e'|'E')('l'|'L')('e'|'E')('t'|'T')('e'|'E');
SELECT 		: ('s'|'S')('e'|'E')('l'|'L')('e'|'E')('c'|'C')('t'|'T');
FROM 		: ('f'|'F')('r'|'R')('o'|'O')('m'|'M');
ORDER 		: ('o'|'O')('r'|'R')('d'|'D')('e'|'E')('r'|'R');
BY			: ('b'|'B')('y'|'Y');
ASC 		: ('a'|'A')('s'|'S')('c'|'C');
DESC 		: ('d'|'D')('e'|'E')('s'|'S')('c'|'C');


ID		: Letter (Letter | Digit|'_')*;
NUM		: ('-')? Digit(Digit)*;
				
Comments: '-''-' ~('\r' | '\n' )*  -> channel(HIDDEN);
WhitespaceDeclaration : [\t\r\n\f ]+ -> skip ;


//************************** Union DDL y DML **************************

start	:	(ddlDeclaration | dmlDeclaration)+ ;


//************************** Grammar for DDL **************************

ddlDeclaration	: 	ddlInstruction ';' ;

ddlInstruction	: 	CREATE DATABASE ID 						#createDB
				| 	ALTER DATABASE ID RENAME TO ID			#alterDB
				| 	DROP DATABASE ID 						#dropDB
				| 	SHOW DATABASES							#showDB
				| 	USE DATABASE ID							#useDB
				| 	CREATE TABLE ID '(' columnsTable (',' CONSTRAINT constraints)? ')'	#createTable
				| 	ALTER TABLE ID RENAME TO ID				#alterTableRename
				| 	ALTER TABLE ID actions					#alterTableAction
				| 	DROP TABLE ID							#dropTable
				| 	SHOW TABLES								#showTables
				| 	SHOW COLUMNS FROM ID					#showColumnsFrom
				;
				
columnsTable	:	(columnTable (',' columnTable)* )? ;

columnTable		:	ID type;

type 	: 	INT						#typeInt
		| 	FLOAT					#typeFloat
		| 	DATE					#typeDate
		| 	CHAR '(' NUM ')'		#typeChar
		|	BOOLEAN					#typeBoolean
		;

constraints		: 	constraintType (',' constraintType)* ;

constraintType	:	ID PRIMARY KEY '(' setIDs ')'									#constraintPrimaryKey
				|	ID FOREIGN KEY '(' setIDs ')' REFERENCES ID '(' setIDs ')'		#constraintForeingKey
				|	ID CHECK '(' expression ')'										#constraintCheck
				;
				
setIDs	:	ID (',' ID)* ;

expression 	:	expression orOperator andExpression
		   	| 	andExpression
		   	;

andExpression	:	andExpression andOperator equalExpression
	    		|	equalExpression
	    		;

equalExpression :	equalExpression equalOperator relationExpression
	   			|	relationExpression
	  			;

relationExpression	:	relationExpression relationOperator unaryExpression
			 		|	unaryExpression
			 		;

unaryExpression	:	varExpression					#variableExpression
				|	value							#valueExpression
			 	|	NOT '(' expression ')'			#notExpression
		 		; 

varExpression	:	ID								#varExpressionID
				|	'(' expression ')'				#varExpressionParentesis
				;
		
orOperator 			:	OR ;
andOperator			:	AND ;
equalOperator		:	EQ | NOTEQ ;
relationOperator	: 	LESSTH | GREATTH | LESSEQ | GREATEQ ;

actions		: action (',' action)* ;

action 		: ADD COLUMN ID type /*CONSTRAINT constraints*/		#actionAddColumn
			| ADD CONSTRAINT constraintType						#actionAddConstraint
			| DROP COLUMN ID									#actionDropColumn
			| DROP CONSTRAINT ID								#actionDropConstrait
			;

value 	: 	integerValue
		| 	floatValue
		| 	dateValue
		| 	charValue
		|	booleanValue
		;

integerValue	:	NUM	;
floatValue		: 	FLOATNUM ;
dateValue		:  	DATED ;
charValue		:  	CHARS ;
booleanValue	:	TRUE | FALSE ;


//************************** Grammar for DML **************************

dmlDeclaration 	:	dmlInstruction ';' ;

dmlInstruction 	:	INSERT INTO ID (insertColumns)? VALUES '(' insertValues ')'			#insert
				| 	UPDATE ID SET assignments (WHERE expression)?						#update
				| 	DELETE FROM ID (WHERE expression)?									#delete
				|	SELECT selectColumns FROM ID (WHERE expression)? (ORDER BY order)?	#select
				;

insertColumns	:	'(' ID (',' ID)* ')' ;

insertValues	:	value (',' value)* ;


assignments	:	asign (',' asign)* ;

asign	:	ID '=' value ;

selectColumns	: '*'
				| setIDs
				;

	
order	:	ID (',' ID)* (ASC | DESC)?;

