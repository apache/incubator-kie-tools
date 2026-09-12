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

package org.drools.formatter;

import org.antlr.v4.runtime.Token;
import org.drools.drl.parser.antlr4.DRL10Lexer;

/** Token-adjacency spacing rules, parameterized by {@link FormatterOptions}. */
final class TokenSpacing {

  private final FormatterOptions options;

  TokenSpacing(FormatterOptions options) {
    this.options = options;
  }

  /**
   * Heuristic for whether a space is needed between two adjacent tokens.
   */
  boolean needsSpaceBetween(Token left, Token right) {
    int lt = left.getType();
    int rt = right.getType();

    // Padded parens are house style — the same rule rhsNeedsSpace applies on
    // the RHS and visitFunction applies to signatures. An empty pair stays "()".
    if (lt == DRL10Lexer.LPAREN) return options.parenPadding() && rt != DRL10Lexer.RPAREN;
    if (rt == DRL10Lexer.RPAREN) return options.parenPadding() && lt != DRL10Lexer.LPAREN;

    // Brackets and braces stay tight: list[0], do[label], @ann{...}
    if (lt == DRL10Lexer.LBRACK || lt == DRL10Lexer.LBRACE) return false;
    if (rt == DRL10Lexer.RBRACK || rt == DRL10Lexer.RBRACE) return false;

    // No space before comma or semicolon
    if (rt == DRL10Lexer.COMMA || rt == DRL10Lexer.SEMI) return false;
    // Space after comma
    if (lt == DRL10Lexer.COMMA) return true;
    // No space around dot
    if (lt == DRL10Lexer.DOT || rt == DRL10Lexer.DOT) return false;
    // No space around null-safe dot
    if (lt == DRL10Lexer.NULL_SAFE_DOT || rt == DRL10Lexer.NULL_SAFE_DOT) return false;
    // No space around hash
    if (lt == DRL10Lexer.HASH || rt == DRL10Lexer.HASH) return false;

    // Space around operators
    if (isOperator(lt) || isOperator(rt)) return true;

    // Space between keywords/identifiers
    if (isWordToken(left) && isWordToken(right)) return true;
    // Space between a word and a string literal (attribute values and rule
    // names: dialect "java", ruleflow-group "G", rule "Name" extends "Base")
    if ((isWordToken(left) && rt == DRL10Lexer.DRL_STRING_LITERAL)
        || (lt == DRL10Lexer.DRL_STRING_LITERAL && isWordToken(right))) return true;
    // Space between word and opening paren only for keywords, not method calls
    if (isKeyword(lt) && rt == DRL10Lexer.LPAREN) return true;
    // Space after a closing paren before a word: ") do[x]", ") over", ") from".
    // A following dot is already excluded above, keeping chains like foo().bar()
    // tight.
    if (lt == DRL10Lexer.RPAREN && isWordToken(right)) return true;

    // Space after colon in labels
    if (lt == DRL10Lexer.COLON) return true;

    return false;
  }

  /**
   * Determine if a space is needed between two adjacent RHS tokens.
   * Applies Java-like spacing: no space inside parens, before commas/semicolons,
   * or around dots; space after commas and between words.
   */
  boolean rhsNeedsSpace(String left, String right) {
    if (left.isEmpty() || right.isEmpty()) return false;

    char lc = left.charAt(left.length() - 1);
    char rc = right.charAt(0);

    if (lc == ',') return true;
    if (lc == '(' && rc == ')') return false;
    if (lc == '(' || rc == ')') return options.parenPadding();
    if (rc == ',' || rc == ';') return false;
    if (lc == '.' || rc == '.') return false;
    // No space between identifier and opening paren (method calls)
    if (rc == '(' && Character.isLetterOrDigit(lc)) return false;

    // Space between all other token pairs
    return true;
  }

  boolean isOperator(int type) {
    return type == DRL10Lexer.ASSIGN || type == DRL10Lexer.EQUAL
        || type == DRL10Lexer.NOTEQUAL || type == DRL10Lexer.LE
        || type == DRL10Lexer.GE || type == DRL10Lexer.GT
        || type == DRL10Lexer.LT || type == DRL10Lexer.AND
        || type == DRL10Lexer.OR || type == DRL10Lexer.ADD
        || type == DRL10Lexer.SUB || type == DRL10Lexer.MUL
        || type == DRL10Lexer.DIV || type == DRL10Lexer.BANG
        || type == DRL10Lexer.DRL_AND || type == DRL10Lexer.DRL_OR
        || type == DRL10Lexer.DRL_IN || type == DRL10Lexer.DRL_NOT;
  }

  boolean isKeyword(int type) {
    return type == DRL10Lexer.DRL_IN || type == DRL10Lexer.DRL_FROM
        || type == DRL10Lexer.DRL_NOT || type == DRL10Lexer.DRL_EXISTS
        || type == DRL10Lexer.DRL_FORALL || type == DRL10Lexer.DRL_ACCUMULATE
        || type == DRL10Lexer.DRL_COLLECT || type == DRL10Lexer.DRL_EVAL
        || type == DRL10Lexer.IF || type == DRL10Lexer.DRL_OVER;
  }

  boolean isWordToken(Token t) {
    String text = t.getText();
    if (text.isEmpty()) return false;
    char c = text.charAt(0);
    return Character.isLetterOrDigit(c) || c == '_' || c == '$';
  }
}
