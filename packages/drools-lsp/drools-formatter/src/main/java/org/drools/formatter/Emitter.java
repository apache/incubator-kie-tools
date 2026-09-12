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

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Interval;
import org.drools.drl.parser.antlr4.DRL10Lexer;
import org.drools.drl.parser.antlr4.DRL10Parser;
import org.drools.drl.parser.antlr4.DRL10ParserHelper;

/**
 * Output side of the formatter: the buffer, indentation depth, hidden-token and
 * comment emission, {@code @formatter:off} regions, and the two text extractors
 * ({@code styledText} normalizes spacing, {@code sourceSlice} is byte-faithful).
 * Every area formatter writes through one instance of this.
 *
 * <p>Comments and blank lines from the hidden channel are emitted by
 * {@code emitHiddenTokensBefore} / {@code emitTrailingHiddenTokens}, which
 * walk the token stream between the last emitted index and the current node.
 * Block comments are re-indented; line comments are preserved.
 *
 * <p>Emission is STYLE-NORMALIZING and COMMENT-PRESERVING, not byte-preserving:
 * token content and comments always survive, while spacing and the sanctioned
 * separator/terminator normalizations (statement {@code ;}, accumulate/groupby
 * source separator {@code ,} &rarr; {@code ;}) come from the formatter, not the
 * source.
 * <ul>
 *   <li>{@code styledText} &mdash; the standard emission helper: visible tokens
 *       of a node (or token span) joined per the house spacing rules
 *       ({@code needsSpaceBetween}), block comments kept inline, hidden
 *       whitespace discarded.</li>
 *   <li>{@code sourceSlice} &mdash; exact original text of a node (or token span),
 *       including comments and inter-token whitespace. Reserved for the sites
 *       where verbatim IS the contract: frozen regions/{@code emitVerbatim},
 *       the unparsed-rule fallback, and the multi-line reflow guards that keep
 *       a line comment's newline.</li>
 *   <li>Both advance {@code lastEmittedTokenIndex} so hidden-token emission
 *       never double-prints what a span already contains.</li>
 *   <li>{@code singleLine} &mdash; collapses newline+indent runs to single spaces
 *       for one-line emission contexts.</li>
 * </ul>
 */
final class Emitter {

  final DRL10Parser parser;
  final CommonTokenStream tokens;
  final StringBuilder out = new StringBuilder();
  final FormatterOptions options;
  final TokenSpacing spacing;
  int depth = 0;
  int lastEmittedTokenIndex = -1;
  int consecutiveNewlines = 0;

  // Source-line ranges (1-based, inclusive) frozen by @formatter:off/on
  // markers; top-level statements overlapping these are emitted verbatim.
  // Populated once the token stream is filled, via computeSkipRegions().
  List<int[]> skipRegions = List.of();

  Emitter(String text, FormatterOptions options) {
    parser = DRL10ParserHelper.createDrlParser(text);
    tokens = (CommonTokenStream) parser.getTokenStream();
    this.options = options;
    this.spacing = new TokenSpacing(options);
  }

  String result() {
    return out.toString();
  }

  // ── output helpers ────────────────────────────────────────────────────

  String indent() {
    return options.indentUnit().repeat(Math.max(0, depth));
  }

  String indent(int offset) {
    return options.indentUnit().repeat(Math.max(0, depth + offset));
  }

  void emit(String text) {
    out.append(text);
    if (!text.isEmpty()) {
      consecutiveNewlines = 0;
    }
  }

  void newline() {
    out.append('\n');
    consecutiveNewlines++;
  }

  void blankLine() {
    if (consecutiveNewlines < 2) {
      newline();
    }
  }

  /**
   * Append text to the last line of output (before any trailing newline).
   * Used to attach suffixes like "," to a pattern's closing ")".
   */
  void appendToLastLine(String suffix) {
    // Strip trailing newline, append suffix. Caller must call newline() afterwards.
    int len = out.length();
    while (len > 0 && out.charAt(len - 1) == '\n') {
      len--;
    }
    out.setLength(len);
    out.append(suffix);
    consecutiveNewlines = 0;
  }

  /**
   * Emits a header line and its metadata items (annotations, attributes) per
   * options.headerMetadata(), leaving depth one deeper than on entry — the
   * body's level. Comments between items are emitted before the item they
   * precede; under INLINE that places them on their own lines after the header.
   */
  void emitHeader(String header, List<? extends ParserRuleContext> items) {
    FormatterOptions.HeaderMetadata mode = options.headerMetadata();
    if (mode == FormatterOptions.HeaderMetadata.INLINE) {
      StringBuilder line = new StringBuilder(header);
      for (ParserRuleContext item : items) {
        String text = styledText(item);
        if (line.length() + 1 + text.length() > options.lineLength() && line.length() > header.length()) {
          emit(line.toString());
          newline();
          line = new StringBuilder(indent(1)).append(text);
        } else {
          line.append(' ').append(text);
        }
      }
      emit(line.toString());
      newline();
      depth++;
      for (ParserRuleContext item : items) {
        emitHiddenTokensBefore(item);
      }
      return;
    }
    emit(header);
    newline();
    if (mode == FormatterOptions.HeaderMetadata.INDENTED) {
      depth++;
    }
    for (ParserRuleContext item : items) {
      emitHiddenTokensBefore(item);
      emit(indent() + styledText(item));
      newline();
    }
    if (mode == FormatterOptions.HeaderMetadata.FLUSH) {
      depth++;
    }
  }

  // ── text extraction from parse tree ──────────────────────────────────

  /**
   * Exact original source text of {@code ctx}, straight from the char stream —
   * byte-faithful including comments and inter-token whitespace. Reserved for
   * the sites where verbatim is the contract (frozen regions, unparsed-rule
   * fallback, multi-line reflow guards that must keep a line comment's
   * newline); everywhere else emission goes through {@link #styledText} so
   * spacing comes from the house rules. Unlike the old token-concat helpers,
   * neither path ever drops comments.
   */
  String sourceSlice(ParserRuleContext ctx) {
    if (ctx == null) return "";
    return sourceSlice(ctx.getStart(), ctx.getStop());
  }

  /**
   * Byte-faithful source text spanning {@code start}..{@code stop} (inclusive),
   * advancing {@code lastEmittedTokenIndex} past {@code stop} so hidden-token
   * emission does not re-print anything inside the slice.
   */
  String sourceSlice(Token start, Token stop) {
    if (start == null || stop == null) return "";
    String text = start.getInputStream().getText(
        Interval.of(start.getStartIndex(), stop.getStopIndex()));
    lastEmittedTokenIndex = Math.max(lastEmittedTokenIndex, stop.getTokenIndex());
    return text;
  }

  /** Collapses newline+indent runs to single spaces, for one-line emission contexts. */
  static String singleLine(String s) {
    return s.replaceAll("[ \t]*\r?\n[ \t]*", " ").trim();
  }

  /**
   * Style-normalized text of {@code ctx}: tokens joined per the house spacing
   * rules (needsSpaceBetween), block comments kept inline, hidden whitespace
   * discarded (spacing comes from the rules, not the source). Line comments in
   * the span are the callers' responsibility (reflow guards) — if one reaches
   * here it is emitted followed by nothing; callers guarantee it ends a line.
   * Advances lastEmittedTokenIndex like every emission helper.
   */
  String styledText(ParserRuleContext ctx) {
    if (ctx == null) return "";
    return styledText(ctx.getStart(), ctx.getStop());
  }

  /** Token-span variant of {@link #styledText(ParserRuleContext)} (both inclusive). */
  String styledText(Token start, Token stop) {
    if (start == null || stop == null) return "";
    StringBuilder sb = new StringBuilder();
    Token prev = null;
    boolean afterComment = false;
    for (int i = start.getTokenIndex(); i <= stop.getTokenIndex(); i++) {
      Token t = tokens.get(i);
      if (isComment(t)) {
        // Block comments inline with surrounding spaces; line comments as-is
        // (the callers' reflow guards ensure a line break follows them).
        if (sb.length() > 0) sb.append(' ');
        sb.append(singleLine(t.getText()));
        afterComment = true;
        continue;
      }
      if (t.getChannel() == Token.HIDDEN_CHANNEL) {
        continue;   // pure whitespace: spacing comes from the rules, not the source
      }
      if (afterComment) {
        sb.append(' ');
        afterComment = false;
      } else if (prev != null && spacing.needsSpaceBetween(prev, t)) {
        sb.append(' ');
      }
      sb.append(t.getText());
      prev = t;
    }
    lastEmittedTokenIndex = Math.max(lastEmittedTokenIndex, stop.getTokenIndex());
    return sb.toString();
  }

  /**
   * Inner text of a parenthesized context (everything inside the outer parens),
   * style-normalized onto a single line.
   */
  String getInnerText(ParserRuleContext ctx) {
    String full = styledText(ctx);
    if (full.startsWith("(") && full.endsWith(")")) {
      return full.substring(1, full.length() - 1).trim();
    }
    return full;
  }

  // ── hidden token (comment) handling ───────────────────────────────────

  /**
   * Emit hidden-channel tokens (comments, whitespace containing blank
   * lines) that appear between the last emitted token and the start of
   * the given parse-tree node.
   */
  void emitHiddenTokensBefore(ParserRuleContext ctx) {
    if (ctx == null || ctx.getStart() == null) return;

    int targetIndex = ctx.getStart().getTokenIndex();
    emitHiddenTokensUpTo(targetIndex);
    lastEmittedTokenIndex = Math.max(lastEmittedTokenIndex, targetIndex);
  }

  void emitHiddenTokensUpTo(int targetIndex) {
    if (lastEmittedTokenIndex < 0) {
      // Check for leading hidden tokens before token 0
      List<Token> leading = tokens.getHiddenTokensToLeft(
          Math.min(targetIndex, tokens.size() - 1), DRL10Lexer.HIDDEN);
      if (leading != null) {
        for (Token ht : leading) {
          if (ht.getTokenIndex() < targetIndex) {
            emitHiddenToken(ht);
          }
        }
      }
      // Also check for comment tokens on the default channel before target
      for (int i = 0; i < targetIndex; i++) {
        Token t = tokens.get(i);
        if (isComment(t) && t.getChannel() != Token.HIDDEN_CHANNEL) {
          emitCommentToken(t);
        }
      }
      return;
    }

    // Scan from last emitted to target, looking for hidden tokens
    for (int i = lastEmittedTokenIndex + 1; i < targetIndex; i++) {
      Token t = tokens.get(i);
      if (t.getChannel() == Token.HIDDEN_CHANNEL) {
        emitHiddenToken(t);
      } else if (isComment(t)) {
        // RHS comments are on default channel
        emitCommentToken(t);
      }
    }
  }

  void emitTrailingHiddenTokens() {
    int totalTokens = tokens.size();
    for (int i = lastEmittedTokenIndex + 1; i < totalTokens; i++) {
      Token t = tokens.get(i);
      if (t.getType() == Token.EOF) break;
      if (t.getChannel() == Token.HIDDEN_CHANNEL) {
        emitHiddenToken(t);
      } else if (isComment(t)) {
        emitCommentToken(t);
      }
    }
  }

  private void emitHiddenToken(Token t) {
    String text = t.getText();
    // Whitespace – only care about blank-line preservation
    if (text.isBlank()) {
      long newlines = text.chars().filter(c -> c == '\n').count();
      if (newlines > 1) {
        // Preserve one blank line separator
        blankLine();
      }
      return;
    }
    // Comment token on hidden channel
    emitCommentToken(t);
  }

  void emitCommentToken(Token t) {
    String text = t.getText().trim();
    if (text.startsWith("/*")) {
      // Block comment: the first line is reindented to the current depth, and
      // what happens to the rest depends on which kind of comment it is.
      //
      // A star-column (Javadoc-shaped) comment has a canonical form, so its "*"
      // rows are anchored one column in from the "/" — a ragged one is squared
      // up, which is the house style being enforced. A banner's columns are the
      // author's drawing, so its rows move by the same delta the first line
      // moved: zero when the comment is not re-indented, which leaves it
      // byte-identical, and otherwise carrying the whole drawing along intact.
      // Anchoring a banner instead put the closing row of a column-0 banner at
      // column 1, out of line with its own opening row.
      //
      // Rows that are neither ("|" borders, prose at column 0) always move by
      // the delta: there is no canonical column to snap them to.
      String[] lines = text.split("\n", -1);
      String firstLineIndent = indent();
      int shift = firstLineIndent.length() - t.getCharPositionInLine();
      boolean banner = isDrawnBanner(text);
      emit(firstLineIndent + lines[0].trim());
      newline();
      for (int i = 1; i < lines.length; i++) {
        String trimmed = lines[i].trim();
        if (!banner && trimmed.startsWith("*")) {
          emit(firstLineIndent + " " + trimmed);
        } else {
          emit(shiftIndent(stripTrailingWhitespace(lines[i]), shift));
        }
        newline();
      }
    } else if (text.startsWith("//")) {
      emit(indent() + text);
      newline();
    }
  }

  /**
   * Whether a block comment is a drawn banner rather than a star-column
   * (Javadoc-shaped) one, decided per comment rather than per row: a banner's
   * closing row belongs in the author's column, where a Javadoc star row
   * belongs one column in from the slash, and per-row tests cannot tell the two
   * apart — a closing delimiter is itself a short run of asterisks.
   *
   * <p>A run of four is the discriminator: above anything Javadoc produces (an
   * opening delimiter is two asterisks, a closing one) and below any drawn
   * rule. A Javadoc
   * comment containing a drawn separator counts as a banner, which is the right
   * answer — the author drew something whose columns matter.
   */
  private static boolean isDrawnBanner(String text) {
    int run = 0;
    for (int i = 0; i < text.length(); i++) {
      run = text.charAt(i) == '*' ? run + 1 : 0;
      if (run >= 4) {
        return true;
      }
    }
    return false;
  }

  /**
   * Move one line's indentation by {@code shift} columns, never past column 0.
   * A line indented with anything other than spaces is left alone: shifting it
   * would mean converting its tabs, which is a change this is not asked to make.
   */
  private static String shiftIndent(String line, int shift) {
    if (line.isBlank()) {
      return "";
    }
    int leading = 0;
    while (leading < line.length() && line.charAt(leading) == ' ') {
      leading++;
    }
    if (leading < line.length() && Character.isWhitespace(line.charAt(leading))) {
      return line;
    }
    return " ".repeat(Math.max(0, leading + shift)) + line.substring(leading);
  }

  // ── formatter-skip-region support (@formatter:off / @formatter:on) ──────

  private enum MarkerKind { OFF, ON, NONE }

  /**
   * Classify a token as a formatter-control marker. Recognises line comments
   * ({@code //}, on either the LHS or RHS channel) whose text — after the
   * leading {@code //} is stripped and trimmed — is exactly
   * {@code @formatter:off} or {@code @formatter:on}. Block comments are not
   * treated as markers.
   */
  private static MarkerKind markerKind(Token t) {
    int type = t.getType();
    if (type != DRL10Lexer.LINE_COMMENT && type != DRL10Lexer.RHS_LINE_COMMENT) {
      return MarkerKind.NONE;
    }
    // LINE_COMMENT / RHS_LINE_COMMENT are guaranteed by the grammar to begin
    // with "//", so strip those two characters directly.
    String body = t.getText().substring(2).trim();
    if (body.equals("@formatter:off")) return MarkerKind.OFF;
    if (body.equals("@formatter:on")) return MarkerKind.ON;
    return MarkerKind.NONE;
  }

  /**
   * Scan the (filled) token stream for {@code @formatter:off}/{@code on}
   * markers and set {@code skipRegions} to the frozen source-line ranges. An
   * {@code off} with no matching {@code on} freezes to end-of-file
   * ({@code Integer.MAX_VALUE}); a stray {@code on}, and a repeated
   * {@code off} while already off, are ignored.
   */
  void computeSkipRegions() {
    List<int[]> regions = new ArrayList<>();
    int regionStart = -1;
    for (Token t : tokens.getTokens()) {
      MarkerKind kind = markerKind(t);
      if (kind == MarkerKind.OFF) {
        if (regionStart < 0) regionStart = t.getLine();
      } else if (kind == MarkerKind.ON) {
        if (regionStart >= 0) {
          regions.add(new int[]{regionStart, t.getLine()});
          regionStart = -1;
        }
      }
    }
    if (regionStart >= 0) {
      regions.add(new int[]{regionStart, Integer.MAX_VALUE});
    }
    skipRegions = regions;
  }

  private boolean overlapsSkipRegion(int startLine, int stopLine) {
    for (int[] r : skipRegions) {
      if (r[0] <= stopLine && r[1] >= startLine) return true;
    }
    return false;
  }

  /**
   * Emit one top-level construct: verbatim if its line span overlaps a frozen
   * region (enclosing-statement freeze), otherwise via its normal typed visit.
   */
  void visitTopLevel(ParserRuleContext ctx, Runnable normalVisit) {
    if (ctx.getStart() != null && ctx.getStop() != null
        && overlapsSkipRegion(ctx.getStart().getLine(), ctx.getStop().getLine())) {
      // emitHiddenTokensBefore flushes the @formatter:off marker (a hidden
      // comment just before ctx). The matching @formatter:on sits between
      // this construct and the next, so it is emitted by the next construct's
      // emitHiddenTokensBefore (or emitTrailingHiddenTokens at end-of-file).
      // The verbatim path emits no markers itself.
      emitHiddenTokensBefore(ctx);
      emitVerbatim(ctx.getStart().getTokenIndex(), ctx.getStop().getTokenIndex());
    } else {
      normalVisit.run();
    }
  }

  static boolean isComment(Token t) {
    int type = t.getType();
    return type == DRL10Lexer.COMMENT
        || type == DRL10Lexer.LINE_COMMENT
        || type == DRL10Lexer.RHS_COMMENT
        || type == DRL10Lexer.RHS_LINE_COMMENT;
  }

  /**
   * Emit the original source text spanning the given token indices exactly
   * as written: no per-line trailing-whitespace stripping, no blank-line
   * collapsing. The concatenated token text already carries its original
   * newlines and spacing, so it is appended to {@code out} verbatim. Frozen
   * regions (and other verbatim spans, such as function bodies) therefore
   * come out byte-exact, modulo the one global policy applied to the whole
   * document at {@code DRLFormatter#format}'s return: LF is converted to
   * whatever {@code options.lineEnding} chose.
   *
   * <p>{@code [startIdx, stopIdx]} never includes the hidden whitespace
   * <em>after</em> the span's last visible token — that whitespace belongs
   * to whatever follows (e.g. a {@code // @formatter:on} marker comment,
   * consumed later by {@code emitHiddenTokensBefore}), and single newlines
   * there are intentionally swallowed rather than replayed, on the
   * assumption that the preceding emitter already terminated its own line
   * (every non-verbatim {@code visit*} method ends with an explicit
   * {@code newline()} call after its last token, e.g. {@code emit("end");
   * newline();}). This method honours that same convention: if the
   * verbatim text doesn't already end in a newline, one is appended so the
   * next emission starts on its own line, exactly as it would after any
   * ordinary (non-frozen) statement.
   *
   * <p>Shared by the parser fallback ({@code StatementFormatter#emitOriginalRule}),
   * the {@code @formatter:off} freeze ({@link #visitTopLevel}), and the
   * function-body emission in {@code StatementFormatter#visitFunction}.
   */
  void emitVerbatim(int startIdx, int stopIdx) {
    StringBuilder text = new StringBuilder();
    for (int i = startIdx; i <= stopIdx; i++) {
      text.append(tokens.get(i).getText());
    }
    if (text.length() == 0 || text.charAt(text.length() - 1) != '\n') {
      text.append('\n');
    }
    out.append(text);
    // Keep consecutiveNewlines accurate for surrounding blank-line logic
    // (e.g. blankLine() calls made by the caller after this verbatim span)
    // without re-deriving it by re-scanning the whole output buffer.
    int trailingNewlines = 0;
    while (trailingNewlines < text.length()
        && text.charAt(text.length() - 1 - trailingNewlines) == '\n') {
      trailingNewlines++;
    }
    consecutiveNewlines = (trailingNewlines == text.length())
        ? consecutiveNewlines + trailingNewlines
        : trailingNewlines;
    lastEmittedTokenIndex = Math.max(lastEmittedTokenIndex, stopIdx);
  }

  static String stripTrailingWhitespace(String s) {
    int end = s.length();
    while (end > 0 && Character.isWhitespace(s.charAt(end - 1))) {
      end--;
    }
    return s.substring(0, end);
  }

  static int countNewlines(String s) {
    return (int) s.chars().filter(c -> c == '\n').count();
  }
}
