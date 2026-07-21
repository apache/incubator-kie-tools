/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
parser grammar DRL10Expressions;

options {
    language = Java;
    tokenVocab = DRL10Lexer;
}

// --------------------------------------------------------
//                      GENERAL RULES
// --------------------------------------------------------
literal
    :	STRING_LITERAL
    |	DRL_STRING_LITERAL
    |	DECIMAL_LITERAL
    |   OCT_LITERAL
    |	DRL_BIG_INTEGER_LITERAL
    |	HEX_LITERAL
    |	FLOAT_LITERAL
    |	DRL_BIG_DECIMAL_LITERAL
    |	BOOL_LITERAL
    |	NULL_LITERAL
    |   TIME_INTERVAL
    |   MUL            // this means "infinity" in Drools
    ;

operator
  : x=TILDE?
    ( op=EQUAL
    | op=NOTEQUAL
    | rop=relationalOp
    )
    ;



relationalOp
  : ( op=LE
    | op=GE
    | op=LT
    | op=GT
    | xop=complexOp
    | not_key nop=neg_operator_key
    | cop=operator_key
    )
    ;

complexOp
    : t=TILDE e=ASSIGN
    ;

typeList
    :	type (COMMA type)*
    ;

type
    : 	tm=typeMatch
    ;

typeMatch
    : primitiveType (LBRACK RBRACK)*
    |	drlIdentifier (typeArguments)? (DOT drlIdentifier (typeArguments)? )* (LBRACK RBRACK)*
    ;

typeArguments
    :	LT typeArgument (COMMA typeArgument)* GT
    ;

typeArgument
    :	type
    |	QUESTION ((extends_key | super_key) type)?
    ;

// matches any identifiers including acceptable java keywords (defined in JavaParser.g4) and drl keywords
drlIdentifier 
    : drlKeywords
    | IDENTIFIER
    // java keywords
    | ABSTRACT
    | ASSERT
    | BOOLEAN
    | BREAK
    | BYTE
    | CASE
    | CATCH
    | CHAR
    | CLASS
    | CONST
    | CONTINUE
    | DEFAULT
    | DO
    | DOUBLE
    | ELSE
    | ENUM
    | EXTENDS
    | FINAL
    | FINALLY
    | FLOAT
    | FOR
    | IF
    | GOTO
    | IMPLEMENTS
    | IMPORT
    | INSTANCEOF
    | INT
    | INTERFACE
    | LONG
    | NATIVE
//    | NEW     // avoid ambiguity with 'new_key creator' and 'drlIdentifier' in 'primary'
    | PACKAGE
    | PRIVATE
    | PROTECTED
    | PUBLIC
    | RETURN
    | SHORT
    | STATIC
    | STRICTFP
    | SUPER
    | SWITCH
    | SYNCHRONIZED
    | THIS
    | THROW
    | THROWS
    | TRANSIENT
    | TRY
    | VOID
    | VOLATILE
    | WHILE
    // Module related keywords
    | MODULE
    | OPEN
    | REQUIRES
    | EXPORTS
    | OPENS
    | TO
    | USES
    | PROVIDES
    | WITH
    | TRANSITIVE
    // other java keywords
    | VAR
    | YIELD
    | RECORD
    | SEALED
    | PERMITS
    | NON_SEALED
    ;

// matches any drl keywords
drlKeywords 
    : builtInOperator
    | DRL_UNIT
    | DRL_FUNCTION
    | DRL_GLOBAL
    | DRL_DECLARE
    | DRL_TRAIT
    | DRL_TYPE
    | DRL_RULE
    | DRL_QUERY
    | DRL_WHEN
    | DRL_THEN
    | DRL_END
    | DRL_AND
    | DRL_OR
    | DRL_EXISTS
    | DRL_NOT
    | DRL_IN
    | DRL_FROM
    | DRL_COLLECT
    | DRL_ACCUMULATE
    | DRL_ACC
    | DRL_INIT
    | DRL_ACTION
    | DRL_REVERSE
    | DRL_RESULT
    | DRL_ENTRY_POINT
    | DRL_EVAL
    | DRL_FORALL
    | DRL_OVER
    | DRL_ATTRIBUTES
    | DRL_SALIENCE
    | DRL_ENABLED
    | DRL_NO_LOOP
    | DRL_AUTO_FOCUS
    | DRL_LOCK_ON_ACTIVE
    | DRL_ACTIVATION_GROUP
    | DRL_RULEFLOW_GROUP
    | DRL_DATE_EFFECTIVE
    | DRL_DATE_EXPIRES
    | DRL_DIALECT
    | DRL_CALENDARS
    | DRL_TIMER
    | DRL_DURATION
    | DRL_WINDOW
    ;

builtInOperator 
    : DRL_CONTAINS
    | DRL_EXCLUDES
    | DRL_MATCHES
    | DRL_MEMBEROF
    | DRL_SOUNDSLIKE
    | DRL_AFTER
    | DRL_BEFORE
    | DRL_COINCIDES
    | DRL_DURING
    | DRL_FINISHED_BY
    | DRL_FINISHES
    | DRL_INCLUDES
    | DRL_MEETS
    | DRL_MET_BY
    | DRL_OVERLAPPED_BY
    | DRL_OVERLAPS
    | DRL_STARTED_BY
    | DRL_STARTS
    | DRL_STR
    ;

// --------------------------------------------------------
//                      EXPRESSIONS
// --------------------------------------------------------
// the following dymmy rule is to force the AT symbol to be
// included in the follow set of the expression on the DFAs
dummy
    :	expression ( AT | SEMI | EOF | IDENTIFIER | RPAREN ) ;

dummy2
    :  relationalExpression EOF;

// top level entry point for arbitrary expression parsing
expression 
    :	left=conditionalExpression
        (op=assignmentOperator right=expression)?
    ;

conditionalExpression 
    :   left=conditionalOrExpression
        ternaryExpression?
    ;

ternaryExpression
    :	QUESTION ts=expression COLON fs=expression
    ;

fullAnnotation 
  : AT name=drlIdentifier  ( DOT x=drlIdentifier  )*
    annotationArgs
  ;

annotationArgs
  : LPAREN
    (
       annotationElementValuePairs
       | value=annotationValue
    )?
    RPAREN
  ;

annotationElementValuePairs 
  : annotationElementValuePair ( COMMA annotationElementValuePair )*
  ;

annotationElementValuePair 
  : key=drlIdentifier ASSIGN val=annotationValue
  ;

annotationValue
  : exp=expression
    | annos=annotationArray
    | anno=fullAnnotation
  ;

annotationArray
  :  LBRACE ( anno=annotationValue
                ( COMMA anno=annotationValue  )* )?
     RBRACE
  ;



conditionalOrExpression 
  : left=conditionalAndExpression
  ( OR
        args=fullAnnotation? right=conditionalAndExpression
  )*
  ;

conditionalAndExpression 
  : left=inclusiveOrExpression
  ( AND
        args=fullAnnotation? right=inclusiveOrExpression
  )*
  ;

inclusiveOrExpression 
  : left=exclusiveOrExpression
  ( BITOR right=exclusiveOrExpression
  )*
  ;

exclusiveOrExpression 
  : left=andExpression
  ( CARET right=andExpression
  )*
  ;

andExpression 
  : left=equalityExpression
  ( BITAND right=equalityExpression
  )*
  ;

equalityExpression 
  : left=instanceOfExpression
  ( ( op=EQUAL | op=NOTEQUAL )
    right=instanceOfExpression
  )*
  ;

instanceOfExpression 
  : left=inExpression
  ( op=instanceof_key
    right=type
  )?
  ;

inExpression 
  : left=relationalExpression
    (not_key in=in_key LPAREN
        e1=expression
      (COMMA e2=expression
      )* RPAREN
    | in=in_key LPAREN
        e1=expression
      (COMMA e2=expression
      )* RPAREN
    )?
  ;

relationalExpression 
  : left=shiftExpression
  ( right=singleRestriction
  )*
  ;

singleRestriction 
  :  op=operator
     ( sa=squareArguments value=shiftExpression
       | value=shiftExpression
     )
  ;

shiftExpression 
  : left=additiveExpression
    ( shiftOp additiveExpression )*
  ;

shiftOp
    :	( LT LT
        | GT GT GT
        | GT GT  )
    ;

additiveExpression 
    :   left=multiplicativeExpression
        ( (ADD | SUB) multiplicativeExpression )*
    ;

multiplicativeExpression 
    :   left=unaryExpression
      ( ( MUL | DIV | MOD ) unaryExpression )*
    ;

unaryExpression 
    :   ADD ue=unaryExpression
    |	SUB ue=unaryExpression
    |   INC primary
    |   DEC primary
    |   left=unaryExpressionNotPlusMinus
    ;

unaryExpressionNotPlusMinus 
    :   TILDE unaryExpression
    | 	BANG ue=unaryExpression
    |   castExpression
    |   backReferenceExpression
    |
        ( ( (var=drlIdentifier COLON
                 ))
        | ( (var=drlIdentifier DRL_UNIFY
                 ))
        )?
        ( left2=xpathPrimary
          | left1=primary
        )
        (selector)*
        ((INC|DEC))?
    ;

castExpression
    :  LPAREN primitiveType RPAREN expr=unaryExpression
    |  LPAREN type RPAREN unaryExpressionNotPlusMinus
    ;

backReferenceExpression
    :  (DOT DOT DIV)+ unaryExpressionNotPlusMinus
    ;

primitiveType
    :   boolean_key
    |	char_key
    |	byte_key
    |	short_key
    |	int_key
    |	long_key
    |	float_key
    |	double_key
    ;

xpathSeparator
    :   DIV
    |	QUESTION_DIV
    ;

xpathPrimary 
    : xpathChunk ( xpathChunk)*
    ;

xpathChunk 
    : xpathSeparator drlIdentifier (DOT drlIdentifier)* (HASH drlIdentifier)? (LBRACK xpathExpressionList RBRACK)?
    ;

xpathExpressionList 

  :   f=expression
      (COMMA s=expression )*
  ;

primary 
    :	expr=parExpression
    |   nonWildcardTypeArguments (explicitGenericInvocationSuffix | this_key arguments)
    |   literal
    |   super_key superSuffix
    |   new_key creator
    |   primitiveType (LBRACK RBRACK)* DOT class_key
    //|   void_key DOT class_key
    |   inlineMapExpression
    |   inlineListExpression
    |   i1=drlIdentifier
        (
            ( d=DOT i2=drlIdentifier  )
            |
            ( d=(DOT|NULL_SAFE_DOT) LPAREN
                                    expression (COMMA  expression)*
                                    RPAREN
            )
            |
            ( h=HASH i2=drlIdentifier  )
            |
            ( n=NULL_SAFE_DOT i2=drlIdentifier  )
        )* (identifierSuffix)?
    ;

inlineListExpression
    :   LBRACK expressionList? RBRACK
    ;

inlineMapExpression
    :	LBRACK mapExpressionList RBRACK
    ;

mapExpressionList
    :	mapEntry (COMMA mapEntry)*
    ;

mapEntry
    :	expression COLON expression
    ;

parExpression 
    :	LPAREN expr=expression RPAREN
    ;

identifierSuffix
    :	(LBRACK
                                     RBRACK  )+
                                     DOT  class_key
    |	(LBRACK
                          expression
                          RBRACK  )+ // can also be matched by selector, but do here
    |   arguments
    |   DOT class_key
    ;

creator
    :	nonWildcardTypeArguments? createdName
        (arrayCreatorRest | classCreatorRestExpr)
    ;

createdName
    :	drlIdentifier typeArgumentsOrDiamond?
        ( DOT drlIdentifier typeArgumentsOrDiamond? )*
        |	primitiveType
    ;

innerCreator
    :	drlIdentifier nonWildcardTypeArgumentsOrDiamond? classCreatorRestExpr
    ;

arrayCreatorRest
    :   LBRACK
    (   RBRACK (LBRACK RBRACK)* arrayInitializer
        |   expression RBRACK ( LBRACK expression RBRACK)* (LBRACK RBRACK)*
        )
    ;

variableInitializer
    :	arrayInitializer
        |   expression
    ;

arrayInitializer
    :	LBRACE (variableInitializer (COMMA variableInitializer)* (COMMA)? )? RBRACE
    ;

classCreatorRestExpr // do not overwrite JavaParser.g4 classCreatorRest
    :	arguments //classBody?		//sotty:  restored classBody to allow for inline, anonymous classes
    ;

explicitGenericInvocation
    :	nonWildcardTypeArguments arguments
    ;

nonWildcardTypeArguments
    :	LT typeList GT
    ;

typeArgumentsOrDiamond
    : LT GT
    | typeArguments
    ;

nonWildcardTypeArgumentsOrDiamond
    : LT GT
    | nonWildcardTypeArguments
    ;

explicitGenericInvocationSuffix
    :	super_key superSuffix
    |   	drlIdentifier arguments
    ;

selector
    :   DOT  super_key superSuffix
    |   DOT  new_key (nonWildcardTypeArguments)? innerCreator
    |   DOT
                  id=drlIdentifier
                  (arguments)?
    |   NULL_SAFE_DOT
                  id=drlIdentifier
                  (arguments)?
    //|   DOT this_key
    |   LBRACK
                       expression
                       RBRACK
    ;

superSuffix
    :	arguments
    |   	DOT drlIdentifier (arguments)?
    ;

squareArguments
    : LBRACK (el=expressionList )? RBRACK
    ;

arguments
    :	LPAREN
        expressionList?
        RPAREN
    ;

expressionList 

  :   f=expression
      (COMMA s=expression )*
  ;

assignmentOperator
  :   ASSIGN
  |   ADD_ASSIGN
  |   SUB_ASSIGN
  |   MUL_ASSIGN
  |   DIV_ASSIGN
  |   AND_ASSIGN
  |   OR_ASSIGN
  |   XOR_ASSIGN
  |   MOD_ASSIGN
  |   LT LT ASSIGN
  |   GT GT GT ASSIGN
  |   GT GT ASSIGN
    ;

// --------------------------------------------------------
//                      KEYWORDS
// --------------------------------------------------------
extends_key
    : id=EXTENDS
    ;

super_key
    : id=SUPER
    ;

instanceof_key
    : id=INSTANCEOF
    ;

boolean_key
    : id=BOOLEAN
    ;

char_key
    : id=CHAR
    ;

byte_key
    : id=BYTE
    ;

short_key
    : id=SHORT
    ;

int_key
    : id=INT
    ;

float_key
    : id=FLOAT
    ;

long_key
    : id=LONG
    ;

double_key
    : id=DOUBLE
    ;

void_key
    : id=VOID
    ;

this_key
    : id=THIS
    ;

class_key
    : id=CLASS
    ;

new_key
    : id=NEW
    ;

not_key
    : id=DRL_NOT
    ;

in_key
    : id=DRL_IN
    ;

operator_key
  :      DRL_CUSTOM_OPERATOR_PREFIX  id=IDENTIFIER
  |      op=builtInOperator
  ;

neg_operator_key
  :      DRL_CUSTOM_OPERATOR_PREFIX  id=IDENTIFIER
  |      op=builtInOperator
  ;