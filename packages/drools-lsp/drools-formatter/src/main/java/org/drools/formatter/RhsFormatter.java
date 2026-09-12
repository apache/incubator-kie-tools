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

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.Token;
import org.drools.drl.parser.antlr4.DRL10Parser;

/**
 * Formats consequences (then blocks): token-level, since the parser keeps them opaque.
 *
 * <p>The DRL parser treats {@code then} blocks as opaque text with individual
 * tokens on a special RHS channel, so formatting works at the token level
 * rather than the CST level:
 * <ul>
 *   <li>{@code emitConsequenceBody} collects visible RHS tokens into
 *       per-statement lists, splitting only at newlines where paren depth is 0
 *       (so continuation lines within expanded calls stay together).</li>
 *   <li>{@code emitRhsStatement} tries the compact (single-line) form first.
 *       If it exceeds {@code options.lineLength()}, it delegates to
 *       {@code rhsEmitExpanded}.</li>
 *   <li>{@code rhsEmitExpanded} uses {@code rhsMarkExpanded} to decide which
 *       {@code (} groups need expansion: any group whose compact rendering
 *       (including trailing {@code );}) would exceed the line limit is marked.
 *       Marked groups get one argument per line with the closing {@code )}
 *       on its own line at the same indent as the opening.</li>
 * </ul>
 */
final class RhsFormatter {
  private final Emitter e;

  // Extra depth added when a case/default label is emitted without an inline
  // consequence (consequence follows on subsequent lines). Reset to 0 when
  // the next case/default label or closing '}' is encountered.
  private int caseLabelDepthBump = 0;

  RhsFormatter(Emitter e) {
    this.e = e;
  }

  void visitRhs(DRL10Parser.RhsContext ctx) {
    e.emitHiddenTokensBefore(ctx);
    e.emit(e.indent() + "then");
    e.newline();
    e.depth++;

    emitConsequenceBody(ctx.consequenceBody());

    for (DRL10Parser.NamedConsequenceContext nc : ctx.namedConsequence()) {
      e.depth--;
      // named consequence header: "then[name]"
      String ncText = nc.RHS_NAMED_CONSEQUENCE_THEN().getText();
      e.emit(e.indent() + ncText);
      e.newline();
      e.depth++;
      emitConsequenceBody(nc.consequenceBody());
    }

    e.depth--;
  }

  private void emitConsequenceBody(DRL10Parser.ConsequenceBodyContext ctx) {
    if (ctx == null || ctx.getChildCount() == 0) return;

    int startIdx = ctx.getStart().getTokenIndex();
    int stopIdx = ctx.getStop().getTokenIndex();

    // Collect visible RHS tokens into statements. A statement boundary is
    // a newline where paren depth is 0 (balanced). Newlines inside parens
    // are continuation lines and belong to the same statement.
    List<String> stmtTokens = new ArrayList<>();
    boolean pendingBlankLine = false;
    int parenDepth = 0;
    int lastTokenLine = -1;

    for (int i = startIdx; i <= stopIdx; i++) {
      Token t = e.tokens.get(i);
      String text = t.getText();

      if (t.getChannel() == Token.HIDDEN_CHANNEL) {
        if (text.contains("\n") && parenDepth == 0) {
          if (!stmtTokens.isEmpty()) {
            emitRhsStatementInBlock(stmtTokens);
            stmtTokens.clear();
          }
          long nlCount = text.chars().filter(c -> c == '\n').count();
          if (nlCount > 1) pendingBlankLine = true;
        }
        // Newlines inside parens are ignored (tokens collected into same statement)
        continue;
      }

      if (Emitter.isComment(t)) {
        // A comment sharing its source line with the previous token is a
        // trailing comment (e.g. "insert( new Foo() ); // audit note") —
        // flush the statement it trails, then reattach it to that line
        // instead of dropping it onto a fresh one.
        boolean trailsPreviousToken = t.getLine() == lastTokenLine && parenDepth == 0;
        if (!stmtTokens.isEmpty() && parenDepth == 0) {
          emitRhsStatementInBlock(stmtTokens);
          stmtTokens.clear();
        }
        if (trailsPreviousToken) {
          e.appendToLastLine(" " + text.trim());
          e.newline();
        } else {
          if (pendingBlankLine) { e.blankLine(); pendingBlankLine = false; }
          e.emit(e.indent() + text.trim());
          e.newline();
        }
        continue;
      }

      // Track paren depth for statement boundary detection.
      // Only count () — curly braces are block delimiters (switch, if, for)
      // and should not suppress newline-based statement splitting.
      if (text.equals("(")) parenDepth++;
      else if (text.equals(")")) parenDepth = Math.max(0, parenDepth - 1);

      // case/default keywords at top level act as statement boundaries so
      // that multiple cases concatenated on one line are split correctly
      // (e.g. badly-formatted input: "break; case 2: ...").
      if (isCaseLabelStart(text) && parenDepth == 0 && !stmtTokens.isEmpty()) {
        emitRhsStatementInBlock(stmtTokens);
        stmtTokens.clear();
      }

      if (pendingBlankLine) { e.blankLine(); pendingBlankLine = false; }
      stmtTokens.add(text);
      lastTokenLine = t.getLine();
    }
    if (!stmtTokens.isEmpty()) {
      emitRhsStatementInBlock(stmtTokens);
    }

    e.lastEmittedTokenIndex = Math.max(e.lastEmittedTokenIndex, stopIdx);
  }

  /**
   * Wrapper around {@link #emitRhsStatement} that manages brace-depth and
   * case/default label splitting so that switch blocks are formatted as:
   * <pre>
   *   switch( x )
   *   {
   *     case 1:
   *       value = ...; break;
   *     default:
   *       value = ...;
   *   }
   * </pre>
   * Rules:
   * <ul>
   *   <li>Statement starting with {@code }} → decrement depth before emit.</li>
   *   <li>Statement ending with {@code {} → increment depth after emit.</li>
   *   <li>Statement starting with {@code case} or {@code default} → split at
   *       the first top-level {@code :}, emit label at current depth, bump
   *       depth by 1, emit consequence, restore depth.</li>
   * </ul>
   */
  private void emitRhsStatementInBlock(List<String> toks) {
    if (toks.isEmpty()) return;

    String first = toks.get(0);
    String last  = toks.get(toks.size() - 1);

    // Closing brace: undo any case-consequence depth bump, then decrease brace indent.
    if (first.equals("}")) {
      e.depth -= caseLabelDepthBump;
      caseLabelDepthBump = 0;
      e.depth--;
    }

    // case / default label splitting.
    // Handles: "case", "default" (separate tokens) and "default:" (fused token).
    if (isCaseLabelStart(first)) {
      // Undo any previous case-consequence indent before emitting the new label.
      e.depth -= caseLabelDepthBump;
      caseLabelDepthBump = 0;

      // In RHS mode the colon is often fused with the preceding token (e.g. "1:",
      // "default:") because RHS_CHUNK matches all non-delimiter chars greedily.
      // findCaseLabelColon handles both standalone ":" and fused "...:" tokens.
      int colonIdx = findCaseLabelColon(toks);

      if (colonIdx >= 0 && colonIdx < toks.size() - 1) {
        // Consequence is on the same line as the label: emit label, then
        // consequence one indent level deeper, then restore depth.
        emitRhsStatement(new ArrayList<>(toks.subList(0, colonIdx + 1)));
        e.depth++;
        emitRhsStatement(new ArrayList<>(toks.subList(colonIdx + 1, toks.size())));
        e.depth--;
      } else {
        // Label only – consequence comes as subsequent statements.
        // Bump depth so those statements are indented past the label.
        emitRhsStatement(toks);
        e.depth++;
        caseLabelDepthBump++;
      }
      if (last.equals("{")) e.depth++;
      return;
    }

    emitRhsStatement(toks);

    // Opening brace: increase indent after emitting
    if (last.equals("{")) {
      e.depth++;
    }
  }

  /** Returns true if {@code first} is the opening token of a case/default label. */
  private boolean isCaseLabelStart(String first) {
    return "case".equals(first) || "default".equals(first) || "default:".equals(first);
  }

  /**
   * Find the index of the case/default label colon within {@code toks}.
   * In RHS mode the colon is often fused with the preceding token by the
   * {@code RHS_CHUNK} rule (e.g. {@code "1:"}, {@code "default:"}).
   * Matches both a standalone {@code ":"} token and any token that ends
   * with {@code ':'} at paren depth 0.
   * Scanning starts at index 1 (skipping the opening keyword).
   */
  private int findCaseLabelColon(List<String> toks) {
    int pd = 0;
    for (int i = 1; i < toks.size(); i++) {
      String t = toks.get(i);
      if (t.equals("(") || t.equals("{")) pd++;
      else if (t.equals(")") || t.equals("}")) pd = Math.max(0, pd - 1);
      else if (pd == 0 && t.endsWith(":")) return i;
    }
    return -1;
  }

  /**
   * Emit a single RHS statement (list of visible tokens).
   * If it fits on one line at the current indent, emit compact.
   * Otherwise, fully expand: break after every "(" that contains commas,
   * put each argument on its own line, close ")" on its own line aligned
   * with the opening.
   */
  private void emitRhsStatement(List<String> toks) {
    if (toks.isEmpty()) return;

    // Build compact single-line version
    String compact = rhsBuildCompact(toks);
    String fullLine = e.indent() + compact;

    if (fullLine.length() <= e.options.lineLength()) {
      e.emit(fullLine);
      e.newline();
      return;
    }

    // Fully expand the statement
    rhsEmitExpanded(toks);
  }

  /**
   * Build a compact single-line representation of RHS tokens with proper spacing.
   */
  private String rhsBuildCompact(List<String> toks) {
    StringBuilder sb = new StringBuilder();
    String prev = "";
    for (String tok : toks) {
      if (!prev.isEmpty() && e.spacing.rhsNeedsSpace(prev, tok)) {
        sb.append(' ');
      }
      sb.append(tok);
      prev = tok;
    }
    return sb.toString();
  }

  /**
   * Emit an expanded RHS statement with one argument per line.
   * For every "(" whose content (rendered compact) would exceed the line
   * limit, break after "(", put each comma-separated arg on its own line,
   * and put ")" on its own line aligned with the "(". This applies
   * recursively: inner parens that also overflow get the same treatment.
   */
  private void rhsEmitExpanded(List<String> toks) {
    // Determine which paren groups need expansion.
    // A paren at index i is "expanded" if rendering its content compact
    // on the same line as the prefix before it would exceed options.lineLength().
    boolean[] expanded = rhsMarkExpanded(toks);

    int rhsDepth = 0;
    StringBuilder currentLine = new StringBuilder();
    String prev = "";

    for (int ti = 0; ti < toks.size(); ti++) {
      String tok = toks.get(ti);
      boolean isOpen = tok.equals("(") || tok.equals("{");
      boolean isClose = tok.equals(")") || tok.equals("}");
      boolean isComma = tok.equals(",");

      if (isClose) {
        rhsDepth = Math.max(0, rhsDepth - 1);
        // If the matching open was expanded, put ")" on its own line
        int matchIdx = rhsFindMatchingOpen(toks, ti);
        if (matchIdx >= 0 && expanded[matchIdx]) {
          // Flush any pending content
          String text = currentLine.toString().stripTrailing();
          if (!text.isEmpty()) {
            e.emit(e.indent() + e.options.indentUnit().repeat(rhsDepth + 1) + text);
            e.newline();
            currentLine.setLength(0);
          }
          // ")" at the same depth as the "("
          currentLine.append(tok);
        } else {
          if (!prev.isEmpty() && currentLine.length() > 0 && e.spacing.rhsNeedsSpace(prev, tok)) {
            currentLine.append(' ');
          }
          currentLine.append(tok);
        }
      } else if (isOpen) {
        if (!prev.isEmpty() && e.spacing.rhsNeedsSpace(prev, tok)) {
          currentLine.append(' ');
        }
        currentLine.append(tok);
        if (expanded[ti]) {
          // Break after "(": emit current line, continue on next
          e.emit(e.indent() + e.options.indentUnit().repeat(rhsDepth) + currentLine.toString().stripTrailing());
          e.newline();
          currentLine.setLength(0);
        }
        rhsDepth++;
      } else if (isComma) {
        currentLine.append(tok);
        // If the enclosing paren group is expanded, break after ","
        int enclosingOpen = rhsFindEnclosingOpen(toks, ti);
        if (enclosingOpen >= 0 && expanded[enclosingOpen]) {
          e.emit(e.indent() + e.options.indentUnit().repeat(rhsDepth) + currentLine.toString().stripTrailing());
          e.newline();
          currentLine.setLength(0);
        }
      } else {
        if (!prev.isEmpty() && currentLine.length() > 0 && e.spacing.rhsNeedsSpace(prev, tok)) {
          currentLine.append(' ');
        }
        currentLine.append(tok);
      }
      prev = tok;
    }
    // Flush remainder
    String remaining = currentLine.toString().stripTrailing();
    if (!remaining.isEmpty()) {
      e.emit(e.indent() + e.options.indentUnit().repeat(rhsDepth) + remaining);
      e.newline();
    }
  }

  /**
   * Mark which opening parens need expansion. Process from innermost out:
   * a "(" needs expansion if placing its compact content on one line
   * would exceed options.lineLength(). Once an inner group is expanded,
   * re-check outer groups since the expansion changes line structure.
   */
  private boolean[] rhsMarkExpanded(List<String> toks) {
    boolean[] expanded = new boolean[toks.size()];

    // Simple approach: build the full compact line for the entire statement.
    // If it exceeds the limit, find the outermost "(" that has commas or
    // nested content and mark it for expansion. Repeat until all lines fit.
    //
    // Simpler: just check each "(" — if the compact text from the line start
    // (considering already-expanded groups) through the matching ")" exceeds
    // the limit, expand it.
    boolean changed = true;
    while (changed) {
      changed = false;
      for (int i = 0; i < toks.size(); i++) {
        if (!toks.get(i).equals("(") && !toks.get(i).equals("{")) continue;
        if (expanded[i]) continue;

        int matchClose = rhsFindMatchingClose(toks, i);
        if (matchClose < 0) continue;

        // Simulate rendering to find the line length at this "("
        int lineLen = simulateLineLength(toks, expanded, i, matchClose);

        if (lineLen > e.options.lineLength()) {
          expanded[i] = true;
          changed = true;
        }
      }
    }
    return expanded;
  }

  /**
   * Simulate rendering to compute the line length that would result
   * from the "(" at openIdx through its matching ")".
   * Accounts for already-expanded groups.
   */
  private int simulateLineLength(List<String> toks, boolean[] expanded, int openIdx, int closeIdx) {
    // Find where the current line starts (after the last expansion break)
    int lineStart = 0;
    for (int j = openIdx - 1; j >= 0; j--) {
      if ((toks.get(j).equals("(") || toks.get(j).equals("{")) && expanded[j]) {
        lineStart = j + 1;
        break;
      }
      if (toks.get(j).equals(",")) {
        int enc = rhsFindEnclosingOpen(toks, j);
        if (enc >= 0 && expanded[enc]) {
          lineStart = j + 1;
          break;
        }
      }
    }

    // Compute the depth (indent level) at lineStart
    int lineDepth = 0;
    for (int j = 0; j < lineStart; j++) {
      if (toks.get(j).equals("(") || toks.get(j).equals("{")) lineDepth++;
      if (toks.get(j).equals(")") || toks.get(j).equals("}")) lineDepth = Math.max(0, lineDepth - 1);
    }

    // Build the compact text from lineStart through closeIdx,
    // plus any trailing tokens that would remain on the same line
    // (e.g. ");", "));", etc.)
    int lineEnd = closeIdx;
    for (int j = closeIdx + 1; j < toks.size(); j++) {
      String t = toks.get(j);
      if (t.equals(")") || t.equals("}") || t.equals(";")) {
        lineEnd = j;
      } else {
        break;
      }
    }

    StringBuilder line = new StringBuilder();
    String prev = "";
    for (int j = lineStart; j <= lineEnd; j++) {
      String t = toks.get(j);
      if (!prev.isEmpty() && e.spacing.rhsNeedsSpace(prev, t)) {
        line.append(' ');
      }
      line.append(t);
      prev = t;
    }

    return e.indent().length() + e.options.indentUnit().repeat(lineDepth).length() + line.length();
  }

  /**
   * Find the matching closing paren/brace for the open at openIdx.
   */
  private int rhsFindMatchingClose(List<String> toks, int openIdx) {
    String openChar = toks.get(openIdx);
    String closeChar = openChar.equals("(") ? ")" : "}";
    int depth = 0;
    for (int i = openIdx; i < toks.size(); i++) {
      if (toks.get(i).equals(openChar)) depth++;
      else if (toks.get(i).equals(closeChar)) {
        depth--;
        if (depth == 0) return i;
      }
    }
    return -1;
  }

  /**
   * Find the matching opening paren/brace for the close at closeIdx.
   */
  private int rhsFindMatchingOpen(List<String> toks, int closeIdx) {
    String closeChar = toks.get(closeIdx);
    String openChar = closeChar.equals(")") ? "(" : "{";
    int depth = 0;
    for (int i = closeIdx; i >= 0; i--) {
      if (toks.get(i).equals(closeChar)) depth++;
      else if (toks.get(i).equals(openChar)) {
        depth--;
        if (depth == 0) return i;
      }
    }
    return -1;
  }

  /**
   * Find the enclosing (immediately surrounding) open paren for position idx.
   */
  private int rhsFindEnclosingOpen(List<String> toks, int idx) {
    int depth = 0;
    for (int i = idx - 1; i >= 0; i--) {
      String t = toks.get(i);
      if (t.equals(")") || t.equals("}")) depth++;
      else if (t.equals("(") || t.equals("{")) {
        if (depth == 0) return i;
        depth--;
      }
    }
    return -1;
  }
}
