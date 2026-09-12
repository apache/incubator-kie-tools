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
import java.util.Arrays;
import java.util.List;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.drools.drl.parser.antlr4.DRL10Lexer;
import org.drools.drl.parser.antlr4.DRL10Parser;
import org.drools.drl.parser.antlr4.DRL10ParserHelper;

/**
 * CST-based DRL formatter. Walks the ANTLR4 concrete syntax tree produced by
 * {@link DRL10Parser} and emits formatted output, preserving comments from the
 * hidden channel.
 *
 * <h2>Architecture</h2>
 *
 * State is split across six collaborators. {@code Emitter} is the shared output
 * state — buffer, indentation depth, hidden-token and comment emission,
 * {@code @formatter:off} regions, and the two text extractors ({@code styledText},
 * {@code sourceSlice}) — that every other class writes through one instance of.
 * {@code TokenSpacing} is the pure, stateless token-adjacency spacing rules.
 * {@code StatementFormatter} walks the compilation unit, formats its top-level
 * statements — package, imports, globals, rules, queries, functions, attributes —
 * and owns the public entry point. {@code DeclareFormatter} formats declare
 * blocks: types, traits, enums, entry points and windows. {@code LhsFormatter}
 * formats a rule's conditions (the when block) and a query's body.
 * {@code RhsFormatter} formats consequences (then blocks), token by token since
 * the parser keeps them opaque. The public API ({@code format}) builds an
 * {@code Emitter}, drives a {@code StatementFormatter} over it, and returns the
 * result.
 *
 * <p>{@code formatRange} formats the <em>whole</em> document (for globally
 * consistent output) but returns only the slice of it that corresponds to the
 * requested {@code [startLine, endLine]}, expanded outward to the full
 * top-level statement(s) — package, import, global, declare, rule, query, or
 * function — that the requested range overlaps; {@code RangeResult}'s
 * {@code startLine}/{@code endLine} report that expanded range in the
 * <em>original</em> document's line numbers. Two blank-ish outcomes are
 * deliberately different: a blank/null input returns the input text
 * unchanged (never truncates); a non-blank input whose range overlaps no
 * complete top-level statement returns {@code ""} for {@code text} — a
 * deliberate "nothing to format here" signal that callers (the LSP
 * range-format handler) map to "no edits", not to a truncation.
 *
 * <p>{@code formatRanges} is the multi-range generalization {@code formatRange}
 * delegates to: it formats the document once and returns one slice per maximal
 * contiguous run of touched statements, so disjoint selections do not drag the
 * untouched statements between them into scope. The pre-commit hook uses it via
 * {@code FormatCLI --lines} to hold a commit to the rules it actually edited.
 *
 * <h2>Formatter-control markers</h2>
 *
 * A line comment {@code // @formatter:off} disables formatting; {@code //
 * @formatter:on} re-enables it (an {@code off} with no matching {@code on}
 * runs to end-of-file). Any top-level statement whose line span overlaps a
 * disabled region is emitted verbatim in full ("enclosing-statement freeze")
 * via {@code emitVerbatim}; the rest of the file formats normally. Line
 * endings (per {@code options.lineEndings}) are still normalized inside frozen
 * regions, which keeps {@code format} idempotent.
 */
public class DRLFormatter {

  private DRLFormatter() {}

  // ───────────────────────── public API ─────────────────────────

  /**
   * Formatted text plus the four counts the refusal gates read.
   *
   * <p>{@code syntaxErrors} is the parser's count for the input;
   * {@code outputSyntaxErrors} the count from re-parsing the FORMATTED OUTPUT —
   * nonzero means the formatter itself corrupted valid input.
   *
   * <p>{@code rulesMissingLhs} / {@code outputRulesMissingLhs} are the
   * structural tier: rules the parser did not read as rules at all (see
   * {@link #rulesMissingLhs}). They exist because an error count cannot see that
   * failure mode — the mis-derivation that causes it reports zero errors.
   *
   * <p>The output tier is only computed when the input was non-blank and clean on
   * BOTH input counts; otherwise the output figures stay 0 (not meaningful — we
   * already refuse).
   */
  public record FormatResult(String formatted, int syntaxErrors, int outputSyntaxErrors,
                             int rulesMissingLhs, int outputRulesMissingLhs) {

    /** Whether a caller must refuse to write or emit over this document. */
    public boolean refused() {
      return refusalReason() != null;
    }

    /**
     * Why the document is refused, phrased for a CLI/log line, or {@code null}
     * when it is safe to write. Input defects are reported ahead of output ones:
     * a document we could not read is the user's to fix, an output defect is ours.
     */
    public String refusalReason() {
      if (syntaxErrors > 0) {
        return syntaxErrors + " parse errors";
      }
      if (rulesMissingLhs > 0) {
        return rulesMissingLhs + " rule(s) whose when-block the parser could not read";
      }
      if (outputSyntaxErrors > 0) {
        return "output fails to re-parse - formatter bug";
      }
      if (outputRulesMissingLhs > 0) {
        return "output loses a rule's when-block - formatter bug";
      }
      return null;
    }
  }

  private static final FormatResult BLANK_OK = new FormatResult("", 0, 0, 0, 0);

  public static FormatResult formatChecked(String text) {
    return formatChecked(text, FormatterOptions.DEFAULTS);
  }

  public static FormatResult formatChecked(String text, FormatterOptions options) {
    if (text == null) {
      return BLANK_OK;
    }
    if (text.isBlank()) {
      // nothing to format; never truncate a whitespace-only file
      return new FormatResult(text, 0, 0, 0, 0);
    }
    String normalized = text.replace("\r\n", "\n").replace("\r", "\n");
    String eol = options.lineEnding(text);
    Emitter e = new Emitter(normalized, options);
    StatementFormatter formatter = new StatementFormatter(e);
    formatter.formatCompilationUnit();
    // --check compares this output against the file's own bytes, so the single
    // terminator is what keeps --check and --write in agreement.
    String body = trimTrailing(e.result());
    String out = (body.isEmpty() ? body : body + "\n").replace("\n", eol);
    int syntaxErrors = e.parser.getNumberOfSyntaxErrors();
    // The input tier reads the very tree the emission was built from, not a fresh
    // parse: the question is what the formatter acted on.
    int rulesMissingLhs = rulesMissingLhs(formatter.compilationUnit, e.tokens);
    ParseHealth output = syntaxErrors == 0 && rulesMissingLhs == 0
        ? parseHealth(out)
        : ParseHealth.NOT_CHECKED;
    return new FormatResult(out, syntaxErrors, output.syntaxErrors(),
        rulesMissingLhs, output.rulesMissingLhs());
  }

  /** Both gate figures for one text, from a single parse. */
  private record ParseHealth(int syntaxErrors, int rulesMissingLhs) {
    static final ParseHealth NOT_CHECKED = new ParseHealth(0, 0);
  }

  /** Re-parses {@code text} (any EOL style) and reports what both gates need. */
  private static ParseHealth parseHealth(String text) {
    String normalized = text.replace("\r\n", "\n").replace("\r", "\n");
    DRL10Parser parser = DRL10ParserHelper.createDrlParser(normalized);
    DRL10Parser.CompilationUnitContext cu = parser.compilationUnit();
    return new ParseHealth(
        parser.getNumberOfSyntaxErrors(),
        rulesMissingLhs(cu, (CommonTokenStream) parser.getTokenStream()));
  }

  /**
   * How many rules the parser did not read as rules: those spanning a
   * {@code when} keyword yet carrying no {@code lhs} child.
   *
   * <p>The grammar is {@code ruledef : ... drlAnnotation* attributes? lhs? rhs ...}
   * with {@code lhs : DRL_WHEN lhsExpression*}. The {@code lhs?} is genuinely
   * optional — {@code rule "R" then ... end} is legal — so absence alone proves
   * nothing. Absence while a {@code when} token sits inside the rule's span does:
   * the keyword was consumed by something that is not an LHS, which in practice
   * means {@code drlAnnotation}'s catch-all {@code chunk} absorbed it along with
   * however much followed.
   *
   * <p>Such a rule is unsafe to format in either direction. On the input side the
   * formatter would re-emit the swallowed span as annotation payload — one flat
   * line, internal whitespace gone. On the output side it means the formatter
   * turned a rule it HAD read into one it can no longer read, which is corruption
   * even though the text still re-parses without error. Neither shows up as a
   * syntax error, so this count is the only thing standing between such a document
   * and a silent overwrite.
   */
  private static int rulesMissingLhs(ParseTree tree, CommonTokenStream tokens) {
    if (tree == null) {
      return 0;
    }
    int found = 0;
    if (tree instanceof DRL10Parser.RuledefContext rule
        && rule.lhs() == null
        && spansWhenKeyword(rule, tokens)) {
      found++;
    }
    for (int i = 0; i < tree.getChildCount(); i++) {
      found += rulesMissingLhs(tree.getChild(i), tokens);
    }
    return found;
  }

  /**
   * Whether a {@code when} KEYWORD falls inside {@code rule}'s token span. Asks the
   * token stream rather than the text so that "when" inside a rule name, a string
   * literal, or an RHS chunk cannot be mistaken for a when-block.
   */
  private static boolean spansWhenKeyword(ParserRuleContext rule, CommonTokenStream tokens) {
    if (tokens == null || rule.getStart() == null || rule.getStop() == null) {
      return false;
    }
    for (int i = rule.getStart().getTokenIndex(); i <= rule.getStop().getTokenIndex(); i++) {
      if (tokens.get(i).getType() == DRL10Lexer.DRL_WHEN) {
        return true;
      }
    }
    return false;
  }

  public static String format(String text) {
    return format(text, FormatterOptions.DEFAULTS);
  }

  public static String format(String text, FormatterOptions options) {
    return formatChecked(text, options).formatted();
  }

  /**
   * Result of a range-format operation. {@code startLine} and {@code endLine}
   * are 0-based, inclusive, and reflect the statement-expanded range in the
   * <em>original</em> document that was actually formatted. {@code text} is
   * the formatted replacement (the ending {@code options.lineEnding} chose,
   * trailing newline included) — except the blank-input echo path, which
   * returns the caller's text verbatim (see {@link #formatRange}'s null/blank
   * guard).
   */
  public record RangeResult(int startLine, int endLine, String text) {}

  public static RangeResult formatRange(String text, int startLine, int endLine) {
    return formatRange(text, startLine, endLine, FormatterOptions.DEFAULTS);
  }

  public static RangeResult formatRange(String text, int startLine, int endLine, FormatterOptions options) {
    if (text == null || text.isBlank()) {
      // Nothing to format; never truncate a whitespace-only file (mirrors
      // formatChecked's blank guard). This is NOT the "no-edit" signal below
      // — the caller gets its own text back, not "".
      return new RangeResult(startLine, endLine, text == null ? "" : text);
    }
    List<RangeResult> runs = formatRanges(text, List.of(new int[]{startLine, endLine}), options);
    if (runs.isEmpty()) {
      // Selection doesn't cover any complete statement; nothing to format.
      return new RangeResult(startLine, endLine, "");
    }
    // A single input range always intersects a contiguous span of statements,
    // so formatRanges collapses it to exactly one run.
    return runs.get(0);
  }

  /**
   * Formats the whole document once and returns the formatted text of each
   * <em>maximal contiguous run</em> of top-level statements intersecting
   * {@code ranges}. Each {@code RangeResult} reports the run's span in the
   * <em>original</em> document (0-based, inclusive) and carries the formatted
   * replacement for exactly those lines (the ending {@code options.lineEnding}
   * chose, trailing newline included).
   *
   * <p>Runs are returned in document order and never overlap, so callers may
   * splice them back bottom-up without recomputing line numbers. Statements
   * between two touched ones are <em>not</em> pulled in — a caller touching the
   * first and last rule of a file gets two results, not one file-sized one.
   *
   * <p>{@code ranges} are 0-based inclusive {@code [start, end]} pairs and need
   * not be sorted or disjoint. Returns an empty list when there is nothing to
   * format: blank/null input, no ranges, or no range overlapping a complete
   * statement — the same "no edits" signal {@link #formatRange} maps to
   * {@code ""}. Unlike {@link #formatChecked} this does <em>not</em> screen for
   * parse errors; callers that write or gate on the result must run
   * {@code formatChecked} themselves.
   *
   * <p>A slice reaches past its last statement when the original statement's
   * last line trailed a comment ({@code end // done}): the replaced range covers
   * the line that comment was written on, so the slice must carry it where
   * formatting moved it. A following statement's own leading comment is not
   * taken.
   */
  public static List<RangeResult> formatRanges(String text, List<int[]> ranges) {
    return formatRanges(text, ranges, FormatterOptions.DEFAULTS);
  }

  public static List<RangeResult> formatRanges(String text, List<int[]> ranges, FormatterOptions options) {
    if (text == null || text.isBlank() || ranges == null || ranges.isEmpty()) {
      return List.of();
    }
    String normalized = text.replace("\r\n", "\n").replace("\r", "\n");
    if (!normalized.endsWith("\n")) {
      normalized = normalized + "\n";
    }

    // Mark which top-level statements in the original any range intersects.
    DocumentScan original = scan(normalized);
    int[][] origStmts = original.statements;
    boolean[] touched = new boolean[origStmts.length];
    boolean anyTouched = false;
    for (int[] range : ranges) {
      int rangeStart = Math.min(range[0], range[1]);
      int rangeEnd   = Math.max(range[0], range[1]);
      for (int i = 0; i < origStmts.length; i++) {
        if (origStmts[i][0] <= rangeEnd && origStmts[i][1] >= rangeStart) {
          touched[i] = true;
          anyTouched = true;
        }
      }
    }
    if (!anyTouched) {
      return List.of();
    }

    // Format the whole document (ensures correct, globally-consistent output).
    Emitter e = new Emitter(normalized, options);
    StatementFormatter formatter = new StatementFormatter(e);
    formatter.formatCompilationUnit();
    String formatted = trimTrailing(e.result()); // LF only
    if (!formatted.endsWith("\n")) {
      formatted = formatted + "\n";
    }

    // Find the same statements in the formatted output by re-parsing it.
    DocumentScan output = scan(formatted);
    int[][] fmtStmts = output.statements;
    if (fmtStmts.length != origStmts.length) {
      // Statement count mismatch (shouldn't happen) – fall back to whole-doc.
      return List.of(new RangeResult(0, lineCount(normalized) - 1,
          formatted.replace("\n", options.lineEnding(text))));
    }

    String[] fmtLines = formatted.split("\n", -1);
    List<RangeResult> runs = new ArrayList<>();
    int i = 0;
    while (i < origStmts.length) {
      if (!touched[i]) {
        i++;
        continue;
      }
      int firstIdx = i;
      while (i < origStmts.length && touched[i]) {
        i++;
      }
      int lastIdx = i - 1;

      int fmtStart = fmtStmts[firstIdx][0];
      int fmtEnd   = Math.min(fmtStmts[lastIdx][1], fmtLines.length - 1);
      for (int carried = original.trailingCommentLines[lastIdx];
          carried > 0
              && fmtEnd + 1 < fmtLines.length
              && !fmtLines[fmtEnd + 1].isBlank()
              && output.isCommentOnly(fmtEnd + 1);
          carried--) {
        fmtEnd++;
      }
      String slice =
          String.join("\n", Arrays.copyOfRange(fmtLines, fmtStart, fmtEnd + 1)) + "\n";
      runs.add(new RangeResult(origStmts[firstIdx][0], origStmts[lastIdx][1],
          slice.replace("\n", options.lineEnding(text))));
    }
    return runs;
  }

  /**
   * What range formatting needs from a document, all of it 0-based and all of it
   * from one parse: the {@code [startLine, endLine]} of each top-level statement
   * (package, unit, import, global, declare, rule, query, function), the lines
   * of comment each statement's last line trails, and the comment-only lines.
   */
  private static final class DocumentScan {
    final int[][] statements;
    final int[] trailingCommentLines;
    private final boolean[] commentOnlyLine;

    DocumentScan(int[][] statements, int[] trailingCommentLines, boolean[] commentOnlyLine) {
      this.statements = statements;
      this.trailingCommentLines = trailingCommentLines;
      this.commentOnlyLine = commentOnlyLine;
    }

    boolean isCommentOnly(int line) {
      return line >= 0 && line < commentOnlyLine.length && commentOnlyLine[line];
    }
  }

  private static DocumentScan scan(String text) {
    String normalized = text.replace("\r\n", "\n").replace("\r", "\n");
    DRL10Parser parser = DRL10ParserHelper.createDrlParser(normalized);
    CommonTokenStream tokens = (CommonTokenStream) parser.getTokenStream();
    tokens.fill();
    DRL10Parser.CompilationUnitContext cu = parser.compilationUnit();

    List<ParserRuleContext> stmts = new ArrayList<>();
    if (cu.packagedef() != null) {
      stmts.add(cu.packagedef());
    }
    if (cu.unitdef() != null) {
      stmts.add(cu.unitdef());
    }
    stmts.addAll(cu.drlStatementdef());

    int[][] lines = new int[stmts.size()][];
    int[] trailing = new int[stmts.size()];
    for (int i = 0; i < stmts.size(); i++) {
      ParserRuleContext s = stmts.get(i);
      TrailingComments after = trailingComments(tokens, s.getStop());
      lines[i] = new int[]{s.getStart().getLine() - 1, after.endLine()};
      trailing[i] = after.lines();
    }
    return new DocumentScan(lines, trailing,
        commentOnlyLines(tokens, lineCount(normalized)));
  }

  /**
   * The comments after {@code stop} on its own line: the lines they occupy once
   * the formatter has moved them below the statement, and the last line they
   * occupy in this text — a block comment may run on past {@code stop}'s line,
   * and the range replaced has to reach its end or the splice leaves the tail
   * behind.
   */
  private record TrailingComments(int lines, int endLine) {}

  private static TrailingComments trailingComments(CommonTokenStream tokens, Token stop) {
    int endLine = stop.getLine() - 1;
    int lines = 0;
    for (int i = stop.getTokenIndex() + 1; i < tokens.size(); i++) {
      Token t = tokens.get(i);
      if (t.getType() == Token.EOF || t.getLine() != stop.getLine()) {
        break;
      }
      String text = Emitter.stripTrailingWhitespace(t.getText());
      if (Emitter.isComment(t)) {
        int newlines = Emitter.countNewlines(text);
        lines += 1 + newlines;
        endLine = Math.max(endLine, t.getLine() - 1 + newlines);
      } else if (!text.isBlank()) {
        break;
      }
    }
    return new TrailingComments(lines, endLine);
  }

  private static boolean[] commentOnlyLines(CommonTokenStream tokens, int lineCount) {
    boolean[] comment = new boolean[lineCount];
    boolean[] code = new boolean[lineCount];
    for (Token t : tokens.getTokens()) {
      if (t.getType() == Token.EOF) {
        continue;
      }
      String text = Emitter.stripTrailingWhitespace(t.getText());
      boolean isComment = Emitter.isComment(t);
      if (!isComment && text.isBlank()) {
        continue;
      }
      int first = Math.max(0, t.getLine() - 1);
      int last = Math.min(first + Emitter.countNewlines(text), lineCount - 1);
      for (int line = first; line <= last; line++) {
        if (isComment) {
          comment[line] = true;
        } else {
          code[line] = true;
        }
      }
    }
    boolean[] commentOnly = new boolean[lineCount];
    for (int i = 0; i < lineCount; i++) {
      commentOnly[i] = comment[i] && !code[i];
    }
    return commentOnly;
  }

  private static int lineCount(String text) {
    int count = 1;
    for (int i = 0; i < text.length(); i++) {
      if (text.charAt(i) == '\n') count++;
    }
    return count;
  }

  // ───────────────────────── static utility methods ─────────────────────────

  static int findBreakableOpenParen(String text) {
    boolean inString = false;
    char quote = 0;
    int depth = 0;

    for (int i = 0; i < text.length(); i++) {
      char c = text.charAt(i);
      if (inString) {
        if (c == quote) inString = false;
        else if (c == '\\' && i + 1 < text.length()) i++;
        continue;
      }
      if (c == '\'' || c == '"') {
        inString = true;
        quote = c;
      } else if (c == '(') {
        if (depth == 0) {
          int close = findMatchingClose(text, i);
          if (close > i + 1) {
            String inner = text.substring(i + 1, close).trim();
            if (!inner.isEmpty()) return i;
          }
          if (close == -1) {
            String after = text.substring(i + 1).trim();
            if (!after.isEmpty()) return i;
          }
          if (close > 0) { i = close; continue; }
        }
        depth++;
      } else if (c == ')') {
        depth--;
      }
    }
    return -1;
  }

  static int findMatchingClose(String text, int openPos) {
    int depth = 0;
    boolean inString = false;
    char quote = 0;
    for (int i = openPos; i < text.length(); i++) {
      char c = text.charAt(i);
      if (inString) {
        if (c == quote) inString = false;
        else if (c == '\\' && i + 1 < text.length()) i++;
        continue;
      }
      if (c == '\'' || c == '"') { inString = true; quote = c; }
      else if (c == '(') depth++;
      else if (c == ')') { depth--; if (depth == 0) return i; }
    }
    return -1;
  }

  static List<String> splitTopLevelCommas(String text) {
    List<String> args = new ArrayList<>();
    int depth = 0;
    boolean inString = false;
    char quote = 0;
    int start = 0;

    for (int i = 0; i < text.length(); i++) {
      char c = text.charAt(i);
      if (inString) {
        if (c == quote) inString = false;
        else if (c == '\\' && i + 1 < text.length()) i++;
        continue;
      }
      if (c == '\'' || c == '"') { inString = true; quote = c; }
      else if (c == '(' || c == '{' || c == '[') depth++;
      else if (c == ')' || c == '}' || c == ']') depth--;
      else if (c == ',' && depth == 0) {
        args.add(text.substring(start, i));
        start = i + 1;
      }
    }
    args.add(text.substring(start));
    return args;
  }

  private static String trimTrailing(String text) {
    int end = text.length();
    while (end > 0 && text.charAt(end - 1) == '\n') {
      end--;
    }
    if (end < text.length()) {
      return text.substring(0, end);
    }
    return text;
  }
}
