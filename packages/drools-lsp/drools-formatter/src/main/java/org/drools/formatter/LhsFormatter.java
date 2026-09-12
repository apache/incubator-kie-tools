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

import java.util.List;

import org.antlr.v4.runtime.Token;
import org.drools.drl.parser.antlr4.DRL10Lexer;
import org.drools.drl.parser.antlr4.DRL10Parser;

/**
 * Formats a rule's conditions (the when block) and a query's body.
 *
 * <p>This class walks the CST with typed {@code visit*} methods:
 * <ul>
 *   <li>{@code visitLhs} &rarr; {@code visitLhsExpression} dispatches on
 *       expression type (and/or/unary/enclosed).</li>
 *   <li>{@code visitLhsUnary} handles patterns, not/exists, accumulate,
 *       forall, eval, and groupby.</li>
 *   <li>{@code visitPatternBind} renders a pattern on one line if it fits
 *       within {@code options.lineLength()} and its constraint parens hold no line
 *       comment (a {@code //} comment would swallow the rest of a one-line
 *       rendering); otherwise delegates to {@code emitReflowedPattern} which
 *       puts each constraint on its own line — trailing comments attached —
 *       with the closing {@code )} aligned to the opening.</li>
 *   <li>Parentheses are padded — {@code Person( age &gt; 18 )} — whatever the
 *       source did, an empty pair staying {@code ()}. The rule is enforced in
 *       three places because parens reach the output three ways: token-by-token
 *       emission ({@code needsSpaceBetween}), verbatim RHS code
 *       ({@code rhsNeedsSpace}), and string-built signatures
 *       ({@code parenthesized}).</li>
 *   <li>{@code visitAndDef} emits explicit {@code and} keywords between
 *       patterns in accumulate/groupby source via a {@code nextPatternPrefix}
 *       mechanism.</li>
 * </ul>
 */
final class LhsFormatter {
  private final Emitter e;

  // Prefix to prepend to the next pattern (e.g. "and " between patterns)
  private String nextPatternPrefix = "";

  LhsFormatter(Emitter e) {
    this.e = e;
  }

  void visitLhs(DRL10Parser.LhsContext ctx) {
    e.emitHiddenTokensBefore(ctx);
    e.emit(e.indent() + "when");
    e.newline();
    e.depth++;
    for (DRL10Parser.LhsExpressionContext expr : ctx.lhsExpression()) {
      visitLhsExpression(expr);
    }
    e.depth--;
  }

  void visitLhsExpression(DRL10Parser.LhsExpressionContext ctx) {
    e.emitHiddenTokensBefore(ctx);
    if (ctx instanceof DRL10Parser.LhsExpressionEnclosedContext enclosed) {
      e.emit(e.indent() + consumePatternPrefix() + "(");
      e.newline();
      e.depth++;
      visitLhsExpression(enclosed.lhsExpression());
      e.depth--;
      e.emit(e.indent() + ")");
      e.newline();
    } else if (ctx instanceof DRL10Parser.LhsOrContext orCtx) {
      List<DRL10Parser.LhsExpressionContext> exprs = orCtx.lhsExpression();
      for (int i = 0; i < exprs.size(); i++) {
        if (i > 0) {
          e.emit(e.indent() + "or");
          e.newline();
        }
        visitLhsExpression(exprs.get(i));
      }
    } else if (ctx instanceof DRL10Parser.LhsAndContext andCtx) {
      List<DRL10Parser.LhsExpressionContext> exprs = andCtx.lhsExpression();
      for (int i = 0; i < exprs.size(); i++) {
        if (i > 0) {
          nextPatternPrefix = "and ";
        }
        visitLhsExpression(exprs.get(i));
      }
    } else if (ctx instanceof DRL10Parser.LhsUnarySingleContext unary) {
      visitLhsUnary(unary.lhsUnary());
    }
  }

  private void visitLhsUnary(DRL10Parser.LhsUnaryContext ctx) {
    e.emitHiddenTokensBefore(ctx);
    // Consume any pending prefix (e.g. "and ") for non-pattern nodes.
    // visitPatternBind consumes it itself; other handlers need it here.
    if (ctx.lhsPatternBind() != null) {
      visitPatternBind(ctx.lhsPatternBind());
      emitTrailingInvocations(ctx);
      return;
    }
    String prefix = consumePatternPrefix();
    if (ctx.lhsExists() != null) {
      visitLhsExists(ctx.lhsExists(), prefix);
    } else if (ctx.lhsNot() != null) {
      visitLhsNot(ctx.lhsNot(), prefix);
    } else if (ctx.lhsEval() != null) {
      String inner = e.styledText(ctx.lhsEval().conditionalOrExpression());
      e.emit(e.indent() + prefix
          + (e.options.parenPadding() ? "eval( " + inner + " )" : "eval(" + inner + ")"));
      e.newline();
    } else if (ctx.lhsForall() != null) {
      visitLhsForall(ctx.lhsForall());
    } else if (ctx.lhsAccumulate() != null) {
      visitLhsAccumulate(ctx.lhsAccumulate());
    } else if (ctx.lhsGroupBy() != null) {
      visitLhsGroupBy(ctx.lhsGroupBy());
    } else if (ctx.lhsExpression() != null) {
      // parenthesized lhsExpression
      e.emit(e.indent() + prefix + "(");
      e.newline();
      e.depth++;
      visitLhsExpression(ctx.lhsExpression());
      e.depth--;
      e.emit(e.indent() + ")");
      e.newline();
    } else if (ctx.conditionalBranch() != null) {
      e.emit(e.indent() + prefix + e.styledText(ctx.conditionalBranch()));
      e.newline();
    }
    // exists/not/eval and a parenthesised group can each carry a trailing
    // invocation too; the pattern-bind path above appends its own.
    emitTrailingInvocations(ctx);
  }

  /**
   * The trailing consequence invocations of an {@code lhsUnary}, rendered for
   * the end of the element's line.
   *
   * <p>The grammar hangs these off {@code lhsUnary} as siblings of the element
   * they qualify — {@code lhsPatternBind consequenceInvocation*},
   * {@code lhsExists namedConsequenceInvocation?} — so visiting only the
   * element drops them. Dropping one is silent: {@code $p : Bar( age > 18 ) if
   * ( $p.age > 65 ) do[senior]} loses its guard while the {@code then[senior]}
   * block survives, leaving a labelled consequence that can never fire, and the
   * output still re-parses, so the output-reparse net cannot see it.
   */
  private String trailingInvocations(DRL10Parser.LhsUnaryContext ctx) {
    StringBuilder sb = new StringBuilder();
    for (DRL10Parser.ConsequenceInvocationContext invocation : ctx.consequenceInvocation()) {
      sb.append(' ').append(e.styledText(invocation));
    }
    if (ctx.namedConsequenceInvocation() != null) {
      sb.append(' ').append(e.styledText(ctx.namedConsequenceInvocation()));
    }
    return sb.toString();
  }

  /**
   * Attach an {@code lhsUnary}'s trailing invocations to the element just
   * emitted. The element may have taken several lines (reflowed, or forced
   * multi-line by a comment), so this appends to the emitted text rather than
   * to a current-line buffer.
   */
  private void emitTrailingInvocations(DRL10Parser.LhsUnaryContext ctx) {
    String trailing = trailingInvocations(ctx);
    if (trailing.isEmpty()) {
      return;
    }
    e.appendToLastLine(trailing);
    e.newline();
  }

  private void visitLhsExists(DRL10Parser.LhsExistsContext ctx, String prefix) {
    if (ctx.lhsPatternBind() != null) {
      if (emitMultiLineIfCommented(ctx.lhsPatternBind(), prefix + "exists ")) {
        return;
      }
      String patternText = formatPatternBind(ctx.lhsPatternBind());
      String line = e.indent() + prefix + "exists " + patternText;
      if (line.length() <= e.options.lineLength()) {
        e.emit(line);
        e.newline();
      } else {
        emitReflowedPattern(ctx.lhsPatternBind(), prefix + "exists ");
      }
    } else if (ctx.lhsExpression() != null) {
      e.emit(e.indent() + prefix + "exists(");
      e.newline();
      e.depth++;
      visitLhsExpression(ctx.lhsExpression());
      e.depth--;
      e.emit(e.indent() + ")");
      e.newline();
    }
  }

  private void visitLhsNot(DRL10Parser.LhsNotContext ctx, String prefix) {
    if (ctx.lhsPatternBind() != null) {
      if (emitMultiLineIfCommented(ctx.lhsPatternBind(), prefix + "not ")) {
        return;
      }
      String patternText = formatPatternBind(ctx.lhsPatternBind());
      String line = e.indent() + prefix + "not " + patternText;
      if (line.length() <= e.options.lineLength()) {
        e.emit(line);
        e.newline();
      } else {
        emitReflowedPattern(ctx.lhsPatternBind(), prefix + "not ");
      }
    } else if (ctx.lhsExpression() != null) {
      e.emit(e.indent() + prefix + "not(");
      e.newline();
      e.depth++;
      visitLhsExpression(ctx.lhsExpression());
      e.depth--;
      e.emit(e.indent() + ")");
      e.newline();
    }
  }

  private void visitLhsForall(DRL10Parser.LhsForallContext ctx) {
    e.emit(e.indent() + "forall(");
    e.newline();
    e.depth++;
    for (DRL10Parser.LhsPatternBindContext pb : ctx.lhsPatternBind()) {
      visitPatternBind(pb);
    }
    e.depth--;
    e.emit(e.indent() + ")");
    e.newline();
  }

  private void visitLhsAccumulate(DRL10Parser.LhsAccumulateContext ctx) {
    String keyword = ctx.DRL_ACC() != null ? "acc" : "accumulate";
    e.emit(e.indent() + keyword + "(");
    e.newline();
    e.depth++;
    // The grammar accepts "," or ";" after the source pattern, but the DRL
    // language reference documents inline accumulate as
    //   accumulate( <source pattern>; <functions> [;<constraints>] )
    // so a source "," is normalized to the documented ";" form.
    visitAndDef(ctx.lhsAndDef());
    e.appendToLastLine(e.options.normalizeTerminators() ? ";" : sourceSeparator(ctx.lhsAndDef()));
    e.newline();
    // accumulate functions
    List<DRL10Parser.AccumulateFunctionContext> funcs = ctx.accumulateFunction();
    for (int i = 0; i < funcs.size(); i++) {
      e.emit(e.indent() + e.styledText(funcs.get(i)));
      if (i < funcs.size() - 1) e.emit(",");
      e.newline();
    }
    // optional trailing constraints
    if (ctx.constraints() != null) {
      e.appendToLastLine(";");
      e.newline();
      e.emit(e.indent() + e.styledText(ctx.constraints()));
      e.newline();
    }
    e.depth--;
    e.emit(e.indent() + ")");
    e.newline();
  }

  private void visitLhsGroupBy(DRL10Parser.LhsGroupByContext ctx) {
    e.emit(e.indent() + "groupby(");
    e.newline();
    e.depth++;
    // The grammar accepts "," or ";" after the source pattern; the ";" form
    // mirrors the documented accumulate syntax (see visitLhsAccumulate).
    visitAndDef(ctx.lhsAndDef());
    e.appendToLastLine(e.options.normalizeTerminators() ? ";" : sourceSeparator(ctx.lhsAndDef()));
    e.newline();
    e.emit(e.indent() + e.styledText(ctx.groupByKeyBinding()) + ";");
    e.newline();
    List<DRL10Parser.AccumulateFunctionContext> funcs = ctx.accumulateFunction();
    for (int i = 0; i < funcs.size(); i++) {
      e.emit(e.indent() + e.styledText(funcs.get(i)));
      if (i < funcs.size() - 1) e.emit(",");
      e.newline();
    }
    if (ctx.constraints() != null) {
      e.appendToLastLine(";");
      e.newline();
      e.emit(e.indent() + e.styledText(ctx.constraints()));
      e.newline();
    }
    e.depth--;
    e.emit(e.indent() + ")");
    e.newline();
  }

  /** The source's separator after an accumulate/groupby source pattern: "," or ";". */
  private String sourceSeparator(DRL10Parser.LhsAndDefContext source) {
    for (int i = source.getStop().getTokenIndex() + 1; i < e.tokens.size(); i++) {
      Token t = e.tokens.get(i);
      if (t.getChannel() == Token.HIDDEN_CHANNEL || Emitter.isComment(t)) {
        continue;
      }
      return t.getType() == DRL10Lexer.COMMA ? "," : ";";
    }
    return ";";
  }

  private void visitAndDef(DRL10Parser.LhsAndDefContext ctx) {
    List<DRL10Parser.LhsUnaryContext> unaries = ctx.lhsUnary();
    boolean hasExplicitAnd = !ctx.DRL_AND().isEmpty();
    for (int i = 0; i < unaries.size(); i++) {
      if (i > 0 && hasExplicitAnd) {
        nextPatternPrefix = "and ";
      }
      visitLhsUnary(unaries.get(i));
    }
    // If it's a parenthesized andDef, recurse
    if (ctx.lhsAndDef() != null) {
      visitAndDef(ctx.lhsAndDef());
    }
  }

  void visitPatternBind(DRL10Parser.LhsPatternBindContext ctx) {
    e.emitHiddenTokensBefore(ctx);
    String prefix = consumePatternPrefix();
    if (emitMultiLineIfCommented(ctx, prefix)) {
      return;
    }
    String text = formatPatternBind(ctx);
    String indented = e.indent() + prefix + text;
    if (indented.length() > e.options.lineLength()) {
      emitReflowedPattern(ctx, prefix);
    } else {
      e.emit(indented);
      e.newline();
    }
  }

  private String consumePatternPrefix() {
    String p = nextPatternPrefix;
    nextPatternPrefix = "";
    return p;
  }

  /**
   * A line comment inside a pattern's constraint parens cannot survive
   * one-line emission — the {@code //} would swallow the rest of the line and
   * corrupt the output. If one is present, emit the pattern multi-line
   * instead: reflowed for a single pattern, verbatim for {@code or}-connected
   * patterns (which the reflow path cannot represent). Block comments are
   * safe inline and do not force the multi-line path. Returns true if it
   * emitted the pattern.
   */
  private boolean emitMultiLineIfCommented(DRL10Parser.LhsPatternBindContext bindCtx, String prefix) {
    boolean hasLineComment = false;
    for (DRL10Parser.LhsPatternContext pat : bindCtx.lhsPattern()) {
      if (pat.LPAREN() != null && pat.RPAREN() != null
          && spanContainsLineComment(pat.LPAREN().getSymbol().getTokenIndex() + 1,
              pat.RPAREN().getSymbol().getTokenIndex() - 1)) {
        hasLineComment = true;
        break;
      }
    }
    if (!hasLineComment) {
      return false;
    }
    if (bindCtx.lhsPattern().size() > 1) {
      // or-connected patterns have no reflowed form; keep the original text.
      e.emit(e.indent() + prefix + e.sourceSlice(bindCtx));
      e.newline();
    } else {
      emitReflowedPattern(bindCtx, prefix);
    }
    return true;
  }

  /** True if any token in {@code [startIdx, stopIdx]} is a line comment. */
  private boolean spanContainsLineComment(int startIdx, int stopIdx) {
    for (int i = startIdx; i <= stopIdx; i++) {
      int type = e.tokens.get(i).getType();
      if (type == DRL10Lexer.LINE_COMMENT || type == DRL10Lexer.RHS_LINE_COMMENT) {
        return true;
      }
    }
    return false;
  }

  /**
   * Comments strictly between token indices {@code afterIdx} and
   * {@code boundaryIdx} (both exclusive), rendered inline with a leading
   * space, and consumed (advancing {@code lastEmittedTokenIndex}) so the
   * hidden-token walk cannot re-emit them. The caller must end the line
   * right after — a line comment swallows the rest of its line.
   */
  private String inlineComments(int afterIdx, int boundaryIdx) {
    StringBuilder sb = new StringBuilder();
    for (int i = afterIdx + 1; i < boundaryIdx; i++) {
      Token t = e.tokens.get(i);
      if (Emitter.isComment(t)) {
        sb.append(' ').append(Emitter.singleLine(t.getText()));
        e.lastEmittedTokenIndex = Math.max(e.lastEmittedTokenIndex, i);
      }
    }
    return sb.toString();
  }

  /**
   * Emit each comment strictly between the token indices (both exclusive) on
   * its own line at the current indent, consuming them.
   */
  private void emitOwnLineComments(int afterIdx, int boundaryIdx) {
    for (int i = afterIdx + 1; i < boundaryIdx; i++) {
      Token t = e.tokens.get(i);
      if (Emitter.isComment(t)) {
        e.emitCommentToken(t);
        e.lastEmittedTokenIndex = Math.max(e.lastEmittedTokenIndex, i);
      }
    }
  }

  private String bindingColon() {
    return e.options.bindingColonSpace() ? " : " : ": ";
  }

  private String formatPatternBind(DRL10Parser.LhsPatternBindContext ctx) {
    StringBuilder sb = new StringBuilder();
    if (ctx.label() != null) {
      sb.append(ctx.label().drlIdentifier().getText()).append(bindingColon());
    } else if (ctx.unif() != null) {
      sb.append(ctx.unif().drlIdentifier().getText()).append(" := ");
    }

    List<DRL10Parser.LhsPatternContext> patterns = ctx.lhsPattern();
    for (int i = 0; i < patterns.size(); i++) {
      if (i > 0) sb.append(" or ");
      sb.append(formatPattern(patterns.get(i)));
    }
    return sb.toString();
  }

  private String formatPattern(DRL10Parser.LhsPatternContext ctx) {
    if (ctx.xpathPrimary() != null) {
      return e.styledText(ctx.xpathPrimary());
    }
    StringBuilder sb = new StringBuilder();
    if (ctx.QUESTION() != null) {
      sb.append("?");
    }
    sb.append(ctx.objectType.getText());

    // Padding is enforced, not echoed from the source: the token-spacing rules
    // pad every other paren, so a pattern whose source was written tight comes
    // out padded like the rest. An empty pair stays "()".
    boolean padded = e.options.parenPadding() && (ctx.positionalConstraints() != null || ctx.constraints() != null);
    sb.append(padded ? "( " : "(");
    if (ctx.positionalConstraints() != null) {
      sb.append(formatConstraintList(ctx.positionalConstraints().constraint()));
      // The trailing ";" is grammar-MANDATORY whenever positionalConstraints
      // parses (positionalConstraints : constraint (COMMA constraint)* SEMI) —
      // dropping it silently reinterprets positional args as named
      // constraints on reparse. Emit it unconditionally; only the spacing
      // depends on whether a named-constraints section follows.
      sb.append(";");
      if (ctx.constraints() != null) {
        sb.append(" ");
      }
    }
    if (ctx.constraints() != null) {
      sb.append(formatConstraintList(ctx.constraints().constraint()));
    }
    sb.append(padded ? " )" : ")");

    for (DRL10Parser.DrlAnnotationContext ann : ctx.drlAnnotation()) {
      sb.append(" ").append(e.styledText(ann));
    }

    if (ctx.DRL_OVER() != null && ctx.patternFilter() != null) {
      sb.append(" over ").append(e.styledText(ctx.patternFilter()));
    }
    if (ctx.DRL_FROM() != null && ctx.patternSource() != null) {
      sb.append(" from ").append(formatPatternSource(ctx.patternSource()));
    }
    return sb.toString();
  }

  /**
   * Style-normalized, single-line text of a constraint list — one styled span
   * from the first constraint's start to the last constraint's stop, so the
   * separating commas and any inter-constraint block comments are kept (line
   * comments never reach this path: emitMultiLineIfCommented diverts them to
   * the multi-line reflow first).
   */
  private String formatConstraintList(List<DRL10Parser.ConstraintContext> constraints) {
    if (constraints.isEmpty()) return "";
    return e.styledText(
        constraints.get(0).getStart(),
        constraints.get(constraints.size() - 1).getStop());
  }

  private String formatPatternSource(DRL10Parser.PatternSourceContext ctx) {
    if (ctx.fromAccumulate() != null) {
      return e.styledText(ctx.fromAccumulate());
    } else if (ctx.fromCollect() != null) {
      return e.styledText(ctx.fromCollect());
    } else if (ctx.fromEntryPoint() != null) {
      return "entry-point " + ctx.fromEntryPoint().stringId().getText();
    } else if (ctx.fromWindow() != null) {
      return "window " + ctx.fromWindow().drlIdentifier().getText();
    } else if (ctx.fromExpression() != null) {
      return e.styledText(ctx.fromExpression());
    }
    return e.styledText(ctx);
  }

  /**
   * Emit a long pattern with constraints broken across lines.
   */
  private void emitReflowedPattern(DRL10Parser.LhsPatternBindContext bindCtx, String linePrefix) {
    StringBuilder prefix = new StringBuilder(linePrefix);
    if (bindCtx.label() != null) {
      prefix.append(bindCtx.label().drlIdentifier().getText()).append(bindingColon());
    } else if (bindCtx.unif() != null) {
      prefix.append(bindCtx.unif().drlIdentifier().getText()).append(" := ");
    }

    DRL10Parser.LhsPatternContext pat = bindCtx.lhsPattern(0);
    if (pat == null || pat.objectType == null) {
      // Fallback: emit the original source verbatim (multi-line context).
      // linePrefix (not prefix): any label/unif is already inside the slice.
      e.emit(e.indent() + linePrefix + e.sourceSlice(bindCtx));
      e.newline();
      return;
    }

    String typeName = (pat.QUESTION() != null ? "?" : "") + pat.objectType.getText();
    e.emit(e.indent() + prefix + typeName + "(");
    e.newline();
    e.depth++;

    int rparenIdx = pat.RPAREN() != null
        ? pat.RPAREN().getSymbol().getTokenIndex()
        : pat.getStop().getTokenIndex();

    // Comments between "(" and the first constraint get their own lines.
    int firstContentIdx = rparenIdx;
    if (pat.positionalConstraints() != null) {
      firstContentIdx = pat.positionalConstraints().getStart().getTokenIndex();
    } else if (pat.constraints() != null) {
      firstContentIdx = pat.constraints().getStart().getTokenIndex();
    }
    if (pat.LPAREN() != null) {
      emitOwnLineComments(pat.LPAREN().getSymbol().getTokenIndex(), firstContentIdx);
    }

    // Positional constraints. "," separates positional entries from each
    // other; the trailing ";" on the last entry is grammar-MANDATORY
    // (positionalConstraints : constraint (COMMA constraint)* SEMI) and must
    // be emitted whether or not a named-constraints section follows —
    // dropping it silently reinterprets positional args as named constraints.
    if (pat.positionalConstraints() != null) {
      List<DRL10Parser.ConstraintContext> pcs = pat.positionalConstraints().constraint();
      for (int i = 0; i < pcs.size(); i++) {
        boolean isLast = (i == pcs.size() - 1);
        String separator = isLast ? ";" : ",";
        int boundary = !isLast
            ? pcs.get(i + 1).getStart().getTokenIndex()
            : (pat.constraints() != null ? pat.constraints().getStart().getTokenIndex() : rparenIdx);
        String trailing = inlineComments(pcs.get(i).getStop().getTokenIndex(), boundary);
        e.emit(e.indent() + e.styledText(pcs.get(i)) + separator + trailing);
        e.newline();
      }
    }

    // Regular constraints
    if (pat.constraints() != null) {
      List<DRL10Parser.ConstraintContext> cs = pat.constraints().constraint();
      for (int i = 0; i < cs.size(); i++) {
        DRL10Parser.ConstraintContext c = cs.get(i);
        String comma = (i < cs.size() - 1) ? "," : "";
        int boundary = (i + 1 < cs.size()) ? cs.get(i + 1).getStart().getTokenIndex() : rparenIdx;
        String trailing = inlineComments(c.getStop().getTokenIndex(), boundary);
        if (spanContainsLineComment(c.getStart().getTokenIndex(), c.getStop().getTokenIndex())) {
          // A line comment inside the constraint itself must keep its newline:
          // the raw slice preserves the original line structure.
          e.emit(e.indent() + e.sourceSlice(c) + comma + trailing);
          e.newline();
          continue;
        }
        String constraintText = e.styledText(c);
        String line = e.indent() + constraintText + comma + trailing;
        // If individual constraint is still too long, try to reflow it
        if (line.length() > e.options.lineLength()) {
          emitReflowedConstraint(constraintText, comma + trailing);
        } else {
          e.emit(line);
          e.newline();
        }
      }
    }
    e.depth--;
    // Close paren plus any suffix (over, from, annotations)
    StringBuilder suffix = new StringBuilder(")");
    for (DRL10Parser.DrlAnnotationContext ann : pat.drlAnnotation()) {
      suffix.append(" ").append(e.styledText(ann));
    }
    if (pat.DRL_OVER() != null && pat.patternFilter() != null) {
      suffix.append(" over ").append(e.styledText(pat.patternFilter()));
    }
    if (pat.DRL_FROM() != null && pat.patternSource() != null) {
      suffix.append(" from ").append(formatPatternSource(pat.patternSource()));
    }
    e.emit(e.indent() + suffix);
    e.newline();
  }

  /**
   * Reflow a single long constraint that contains nested parenthesized
   * expressions (e.g. {@code ref.group in (A, B, C)}).
   */
  private void emitReflowedConstraint(String text, String trailingComma) {
    int parenPos = DRLFormatter.findBreakableOpenParen(text);
    if (parenPos < 0) {
      e.emit(e.indent() + text + trailingComma);
      e.newline();
      return;
    }

    int closePos = DRLFormatter.findMatchingClose(text, parenPos);
    if (closePos < 0) {
      e.emit(e.indent() + text + trailingComma);
      e.newline();
      return;
    }

    String before = text.substring(0, parenPos + 1);
    String inner = text.substring(parenPos + 1, closePos).trim();
    String after = text.substring(closePos + 1).trim();

    if (inner.isEmpty()) {
      e.emit(e.indent() + text + trailingComma);
      e.newline();
      return;
    }

    List<String> args = DRLFormatter.splitTopLevelCommas(inner);

    e.emit(e.indent() + before);
    e.newline();
    e.depth++;
    for (int i = 0; i < args.size(); i++) {
      String comma = (i < args.size() - 1) ? "," : "";
      e.emit(e.indent() + args.get(i).trim() + comma);
      e.newline();
    }
    e.depth--;
    String closeLine = ")" + (after.isEmpty() ? "" : " " + after) + trailingComma;
    e.emit(e.indent() + closeLine);
    e.newline();
  }
}
