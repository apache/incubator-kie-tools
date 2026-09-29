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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.drools.drl.parser.antlr4.DRL10Lexer;
import org.drools.drl.parser.antlr4.DRL10Parser;

/**
 * What a token is doing, read off the parse tree, for the spacing decisions a
 * token pair alone cannot make: {@code /} is division in {@code a / b} and a
 * path separator in {@code /orders[ ... ]}; {@code -} is binary in
 * {@code a - b} and unary in {@code < -5}; {@code [} opens an index in
 * {@code list[0]} and a constraint list in {@code /orders[ amount > 1 ]}.
 */
final class TokenRoles {

  static final TokenRoles NONE = new TokenRoles();

  private final Set<Integer> tightAfter = new HashSet<>();
  private final Set<Integer> tightBefore = new HashSet<>();
  private final Set<Integer> operandStarts = new HashSet<>();
  private final Set<Integer> paddedOpens = new HashSet<>();
  private final Set<Integer> paddedCloses = new HashSet<>();

  static TokenRoles of(ParseTree tree) {
    TokenRoles roles = new TokenRoles();
    roles.collect(tree);
    return roles;
  }

  /**
   * The spacing the roles decide for this pair, or {@code null} when they have
   * no say and the token-pair rules apply.
   */
  Boolean spaceBetween(Token left, Token right, boolean parenPadding) {
    int l = left.getTokenIndex();
    int r = right.getTokenIndex();
    if (paddedOpens.contains(l) || paddedCloses.contains(r)) {
      return parenPadding;
    }
    if (tightAfter.contains(l) || tightBefore.contains(r)) {
      return false;
    }
    if (operandStarts.contains(r)) {
      return left.getType() == DRL10Lexer.LPAREN ? parenPadding : left.getType() != DRL10Lexer.LBRACK;
    }
    return null;
  }

  private void collect(ParseTree node) {
    if (node instanceof DRL10Parser.XpathPrimaryContext path) {
      // The path's first separator starts an operand — "$toy: /crates",
      // "( /crates" — and is spaced by what precedes it; the later ones join
      // segments and are tight on both sides.
      List<DRL10Parser.XpathChunkContext> chunks = path.xpathChunk();
      for (int i = 0; i < chunks.size(); i++) {
        int separator = chunks.get(i).xpathSeparator().getStart().getTokenIndex();
        tightAfter.add(separator);
        if (i == 0) {
          operandStarts.add(separator);
        } else {
          tightBefore.add(separator);
        }
      }
    }
    if (node instanceof DRL10Parser.XpathChunkContext chunk
        && chunk.LBRACK() != null && !isIndex(chunk.xpathExpressionList())) {
      paddedOpens.add(chunk.LBRACK().getSymbol().getTokenIndex());
      paddedCloses.add(chunk.RBRACK().getSymbol().getTokenIndex());
    } else if (node instanceof DRL10Parser.BackReferenceExpressionContext backReference) {
      // (DOT DOT DIV)+ expression: the prefix is one glyph, and an operand
      // where the dot rule would otherwise glue it to the operator before it
      operandStarts.add(backReference.getStart().getTokenIndex());
      for (int i = 0; i < backReference.getChildCount(); i++) {
        if (backReference.getChild(i) instanceof TerminalNode terminal) {
          tightAfter.add(terminal.getSymbol().getTokenIndex());
        }
      }
    } else if (node instanceof DRL10Parser.PatternFilterContext filter) {
      tightAfter.add(filter.COLON().getSymbol().getTokenIndex());
    } else if (node instanceof DRL10Parser.TimeAmountContext amount) {
      for (TerminalNode minus : amount.SUB()) {
        tightAfter.add(minus.getSymbol().getTokenIndex());
      }
    } else {
      Token prefix = unaryPrefix(node);
      if (prefix != null) {
        tightAfter.add(prefix.getTokenIndex());
      }
    }
    for (int i = 0; i < node.getChildCount(); i++) {
      collect(node.getChild(i));
    }
  }

  /** {@code exams[0]}: a lone integer is an index, not a constraint list. */
  private static boolean isIndex(DRL10Parser.XpathExpressionListContext list) {
    return list.getStart().getTokenIndex() == list.getStop().getTokenIndex()
        && list.getStart().getType() == DRL10Lexer.DECIMAL_LITERAL;
  }

  /**
   * The operator of a prefix expression ({@code -5}, {@code !adult},
   * {@code ++i}), in either grammar's expression rule, or {@code null}.
   */
  private static Token unaryPrefix(ParseTree node) {
    boolean expression = node instanceof DRL10Parser.UnaryExpressionContext
        || node instanceof DRL10Parser.UnaryExpressionNotPlusMinusContext
        || node instanceof DRL10Parser.DrlExpressionContext;
    if (!expression || !(node.getChild(0) instanceof TerminalNode first)) {
      return null;
    }
    int type = first.getSymbol().getType();
    boolean prefix = type == DRL10Lexer.ADD || type == DRL10Lexer.SUB
        || type == DRL10Lexer.INC || type == DRL10Lexer.DEC
        || type == DRL10Lexer.TILDE || type == DRL10Lexer.BANG;
    return prefix ? first.getSymbol() : null;
  }
}
