/*
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

package org.drools.completion;

import java.util.Set;

import org.antlr.v4.runtime.Token;
import org.drools.drl.parser.antlr4.DRL10Lexer;

public class Tokens {

    public static Set<Integer> IGNORED = Set.of(
            Token.EPSILON, Token.EOF, Token.INVALID_TYPE,

            DRL10Lexer.TIME_INTERVAL, DRL10Lexer.HASH, DRL10Lexer.DRL_UNIFY, DRL10Lexer.NULL_SAFE_DOT, DRL10Lexer.QUESTION_DIV, DRL10Lexer.MISC,

            DRL10Lexer.DECIMAL_LITERAL, DRL10Lexer.HEX_LITERAL,
            DRL10Lexer.OCT_LITERAL, DRL10Lexer.BINARY_LITERAL, DRL10Lexer.FLOAT_LITERAL, DRL10Lexer.HEX_FLOAT_LITERAL,
            DRL10Lexer.BOOL_LITERAL, DRL10Lexer.CHAR_LITERAL, DRL10Lexer.STRING_LITERAL, DRL10Lexer.TEXT_BLOCK,
            DRL10Lexer.NULL_LITERAL, DRL10Lexer.LPAREN, DRL10Lexer.RPAREN, DRL10Lexer.LBRACE, DRL10Lexer.RBRACE, DRL10Lexer.LBRACK,
            DRL10Lexer.RBRACK, DRL10Lexer.SEMI, DRL10Lexer.COMMA, DRL10Lexer.DOT, DRL10Lexer.ASSIGN, DRL10Lexer.GT, DRL10Lexer.LT,
            DRL10Lexer.BANG, DRL10Lexer.TILDE, DRL10Lexer.QUESTION, DRL10Lexer.COLON, DRL10Lexer.EQUAL, DRL10Lexer.LE, DRL10Lexer.GE,
            DRL10Lexer.NOTEQUAL, DRL10Lexer.AND, DRL10Lexer.OR, DRL10Lexer.INC, DRL10Lexer.DEC, DRL10Lexer.ADD, DRL10Lexer.SUB, DRL10Lexer.MUL,
            DRL10Lexer.DIV, DRL10Lexer.BITAND, DRL10Lexer.BITOR, DRL10Lexer.CARET, DRL10Lexer.MOD, DRL10Lexer.ADD_ASSIGN, DRL10Lexer.SUB_ASSIGN,
            DRL10Lexer.MUL_ASSIGN, DRL10Lexer.DIV_ASSIGN, DRL10Lexer.AND_ASSIGN, DRL10Lexer.OR_ASSIGN, DRL10Lexer.XOR_ASSIGN,
            DRL10Lexer.MOD_ASSIGN, DRL10Lexer.LSHIFT_ASSIGN, DRL10Lexer.RSHIFT_ASSIGN, DRL10Lexer.URSHIFT_ASSIGN,
            DRL10Lexer.ARROW, DRL10Lexer.COLONCOLON, DRL10Lexer.AT, DRL10Lexer.ELLIPSIS, DRL10Lexer.WS, DRL10Lexer.COMMENT,
            DRL10Lexer.LINE_COMMENT, DRL10Lexer.IDENTIFIER, DRL10Lexer.TEXT,
            DRL10Lexer.DRL_STRING_LITERAL,

            DRL10Lexer.RHS_COMMENT, DRL10Lexer.RHS_LINE_COMMENT, DRL10Lexer.RHS_STRING_LITERAL, DRL10Lexer.RHS_NAMED_CONSEQUENCE_THEN, DRL10Lexer.RHS_CHUNK
    );
}
