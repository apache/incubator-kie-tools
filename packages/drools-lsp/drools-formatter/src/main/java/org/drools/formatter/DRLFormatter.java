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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
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
   * Formatted text plus the counts the refusal gates read.
   *
   * <p>{@code syntaxErrors} is the parser's count for the input;
   * {@code outputSyntaxErrors} the count from re-parsing the FORMATTED OUTPUT —
   * nonzero means the formatter itself corrupted valid input.
   *
   * <p>{@code rulesMissingLhs} / {@code outputRulesMissingLhs} are the
   * structural tier: rules the parser did not read as rules at all (see
   * {@link #rulesMissingLhs}), with {@code missingLhsLine} the 1-based input
   * line of the first. They exist because an error count cannot see that
   * failure mode — the mis-derivation that causes it reports zero errors.
   *
   * <p>{@code misreadStatements} / {@code outputMisreadStatements} extend that
   * tier to statement keywords: {@code rule} or {@code query} tokens the parser
   * read as neither the start of a statement nor an identifier (see
   * {@link #misreadStatements}), with {@code misreadStatementLine} the 1-based
   * input line of the first.
   *
   * <p>{@code contentChangedLine} / {@code outputGainsContent} are the content
   * tier: the output's tokens compared with the input's, whitespace and the
   * separators emission may normalise aside (see {@link #contentLoss}). A clause
   * the formatter forgot to emit passes every other output gate — what is left
   * is shorter but valid — and this is the gate that sees it.
   *
   * <p>{@code commentsLost} is the comment tier: how many comment tokens the
   * re-parsed output holds fewer than the input (negative when it holds more),
   * with {@code lostCommentLine} the 1-based input line of the first comment no
   * longer found there, or 0. Some comment positions are not carried through
   * formatting (see {@link #commentLoss}); refusing is what keeps that from
   * being silent.
   *
   * <p>{@code syntaxErrorLine} is the 1-based input line of the first parse error.
   * {@code outputDefectLine} is the 1-based INPUT line of the rule or query whose
   * formatted output the output tier found defective, located by name since the
   * user never sees the output: the rule to fence off with the skip markers, or
   * to send with a report.
   *
   * <p>The output tier is only computed when the input was non-blank and clean on
   * every input count; otherwise the output figures stay 0 (not meaningful — we
   * already refuse).
   */
  public record FormatResult(String formatted, int syntaxErrors, int syntaxErrorLine,
                             int outputSyntaxErrors,
                             int rulesMissingLhs, int missingLhsLine, int outputRulesMissingLhs,
                             int misreadStatements, int misreadStatementLine,
                             int outputMisreadStatements, int outputDefectLine,
                             int contentChangedLine, boolean outputGainsContent,
                             int commentsLost, int lostCommentLine) {

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
        return syntaxErrors + " parse errors"
            + (syntaxErrorLine > 0 ? ", the first at line " + syntaxErrorLine : "");
      }
      if (rulesMissingLhs > 0) {
        return rulesMissingLhs + " rule(s) whose when-block the parser could not read"
            + (missingLhsLine > 0 ? ", the first at line " + missingLhsLine : "");
      }
      if (misreadStatements > 0) {
        return "the rule or query at line " + misreadStatementLine + " was not read as one";
      }
      if (outputSyntaxErrors > 0) {
        return "output fails to re-parse" + insideRule() + " - formatter bug";
      }
      if (outputRulesMissingLhs > 0) {
        return (outputDefectLine > 0
            ? "output loses the when-block of the rule at line " + outputDefectLine
            : "output loses a rule's when-block") + " - formatter bug";
      }
      if (outputMisreadStatements > 0) {
        return "output loses a rule or query" + insideRule() + " - formatter bug";
      }
      if (contentChangedLine > 0) {
        return "the content at line " + contentChangedLine + " would be changed";
      }
      if (outputGainsContent) {
        return "output gains content - formatter bug";
      }
      if (commentsLost > 0) {
        return lostCommentLine > 0
            ? "the comment at line " + lostCommentLine + " would be lost"
            : commentsLost + " comment(s) would be lost";
      }
      if (commentsLost < 0) {
        return "output gains comments - formatter bug";
      }
      return null;
    }

    private String insideRule() {
      return outputDefectLine > 0 ? " inside the rule at line " + outputDefectLine : "";
    }
  }

  private static final FormatResult BLANK_OK =
      new FormatResult("", 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, false, 0, 0);

  public static FormatResult formatChecked(String text) {
    return formatChecked(text, FormatterOptions.DEFAULTS);
  }

  public static FormatResult formatChecked(String text, FormatterOptions options) {
    if (text == null) {
      return BLANK_OK;
    }
    if (text.isBlank()) {
      // nothing to format; never truncate a whitespace-only file
      return new FormatResult(text, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, false, 0, 0);
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
    MissingLhs missing = rulesMissingLhs(formatter.compilationUnit, e.tokens);
    Misread misread = misreadStatements(formatter.compilationUnit);
    ParseHealth output = syntaxErrors == 0 && missing.count() == 0 && misread.count() == 0
        ? parseHealth(out)
        : ParseHealth.NOT_CHECKED;
    ContentLoss content = output.tokens() == null
        ? ContentLoss.NONE
        : contentLoss(e.tokens, output.tokens());
    CommentLoss comments = output.tokens() == null
        ? CommentLoss.NONE
        : commentLoss(e.tokens, output.tokens());
    return new FormatResult(out, syntaxErrors, e.syntaxErrors.firstLine, output.syntaxErrors(),
        missing.count(), missing.firstLine(), output.rulesMissingLhs(),
        misread.count(), misread.firstLine(), output.misreadStatements(),
        inputLineOf(formatter.compilationUnit, output.defectStatement()),
        content.changedLine(), content.gained(),
        comments.count(), comments.firstLine());
  }

  /**
   * The gate figures for one text, from a single parse, plus that parse's tokens
   * and the name of the rule or query holding the first defect found, or null.
   */
  private record ParseHealth(int syntaxErrors, int rulesMissingLhs, int misreadStatements,
                             String defectStatement, CommonTokenStream tokens) {
    static final ParseHealth NOT_CHECKED = new ParseHealth(0, 0, 0, null, null);
  }

  /** Re-parses {@code text} (any EOL style) and reports what the output gates need. */
  private static ParseHealth parseHealth(String text) {
    String normalized = text.replace("\r\n", "\n").replace("\r", "\n");
    DRL10Parser parser = DRL10ParserHelper.createDrlParser(normalized);
    SyntaxErrors errors = new SyntaxErrors();
    parser.addErrorListener(errors);
    DRL10Parser.CompilationUnitContext cu = parser.compilationUnit();
    CommonTokenStream tokens = (CommonTokenStream) parser.getTokenStream();
    MissingLhs missing = rulesMissingLhs(cu, tokens);
    Misread misread = misreadStatements(cu);
    ParserRuleContext defect = errors.count > 0 ? statementAtLine(cu, errors.firstLine)
        : missing.count() > 0 ? missing.first()
        : misread.count() > 0 ? enclosingStatement(misread.first())
        : null;
    return new ParseHealth(parser.getNumberOfSyntaxErrors(), missing.count(), misread.count(),
        statementName(defect), tokens);
  }

  /** The input line of the rule or query whose formatted {@code output} is defective, or 0. */
  static int outputDefectLine(String input, String output) {
    DRL10Parser parser = DRL10ParserHelper.createDrlParser(input);
    return inputLineOf(parser.compilationUnit(), parseHealth(output).defectStatement());
  }

  private static String statementName(ParserRuleContext statement) {
    if (statement instanceof DRL10Parser.RuledefContext rule && rule.name != null) {
      return rule.name.getText();
    }
    if (statement instanceof DRL10Parser.QuerydefContext query && query.name != null) {
      return query.name.getText();
    }
    return null;
  }

  private static ParserRuleContext namedStatement(DRL10Parser.DrlStatementdefContext statement) {
    return statement.ruledef() != null ? statement.ruledef() : statement.querydef();
  }

  private static ParserRuleContext statementAtLine(DRL10Parser.CompilationUnitContext cu, int line) {
    for (DRL10Parser.DrlStatementdefContext statement : cu.drlStatementdef()) {
      ParserRuleContext named = namedStatement(statement);
      if (named != null && named.getStop() != null
          && named.getStart().getLine() <= line && line <= named.getStop().getLine()) {
        return named;
      }
    }
    return null;
  }

  private static ParserRuleContext enclosingStatement(ParseTree node) {
    for (ParseTree p = node; p != null; p = p.getParent()) {
      if (p instanceof DRL10Parser.RuledefContext || p instanceof DRL10Parser.QuerydefContext) {
        return (ParserRuleContext) p;
      }
    }
    return null;
  }

  private static int inputLineOf(DRL10Parser.CompilationUnitContext cu, String name) {
    if (cu == null || name == null) {
      return 0;
    }
    for (DRL10Parser.DrlStatementdefContext statement : cu.drlStatementdef()) {
      ParserRuleContext named = namedStatement(statement);
      if (named != null && name.equals(statementName(named))) {
        return named.getStart().getLine();
      }
    }
    return 0;
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
  private record MissingLhs(int count, DRL10Parser.RuledefContext first) {
    int firstLine() {
      return first == null ? 0 : first.getStart().getLine();
    }
  }

  private static MissingLhs rulesMissingLhs(ParseTree tree, CommonTokenStream tokens) {
    List<DRL10Parser.RuledefContext> rules = new ArrayList<>();
    collectRulesMissingLhs(tree, tokens, rules);
    return new MissingLhs(rules.size(), rules.isEmpty() ? null : rules.get(0));
  }

  private static void collectRulesMissingLhs(ParseTree tree, CommonTokenStream tokens,
                                             List<DRL10Parser.RuledefContext> into) {
    if (tree == null) {
      return;
    }
    if (tree instanceof DRL10Parser.RuledefContext rule
        && rule.lhs() == null
        && spansWhenKeyword(rule, tokens)) {
      into.add(rule);
    }
    for (int i = 0; i < tree.getChildCount(); i++) {
      collectRulesMissingLhs(tree.getChild(i), tokens, into);
    }
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

  private record Misread(int count, TerminalNode first) {
    int firstLine() {
      return first == null ? 0 : first.getSymbol().getLine();
    }
  }

  /**
   * How many {@code rule}/{@code query} keyword tokens the parser read as neither
   * the start of a statement nor an identifier ({@code drlKeywords} lets both
   * keywords name a field), and the 1-based line of the first. Such a token sits
   * inside some other construct's payload — an annotation's chunk that ran on
   * past its own rule, which happens without a syntax error when a rule holds a
   * construct the grammar does not know. The when-block guard misses it whenever
   * the payload stops at a later rule's {@code when}, since the swallowing rule
   * then has a when-block after all.
   */
  private static Misread misreadStatements(ParseTree tree) {
    List<TerminalNode> misread = new ArrayList<>();
    collectMisreadKeywords(tree, misread);
    return new Misread(misread.size(), misread.isEmpty() ? null : misread.get(0));
  }

  private static void collectMisreadKeywords(ParseTree tree, List<TerminalNode> into) {
    if (tree == null) {
      return;
    }
    if (tree instanceof TerminalNode terminal) {
      int type = terminal.getSymbol().getType();
      if (type != DRL10Lexer.DRL_RULE && type != DRL10Lexer.DRL_QUERY) {
        return;
      }
      ParseTree parent = terminal.getParent();
      boolean startsStatement = type == DRL10Lexer.DRL_RULE
          ? parent instanceof DRL10Parser.RuledefContext
          : parent instanceof DRL10Parser.QuerydefContext;
      if (!startsStatement && !(parent instanceof DRL10Parser.DrlKeywordsContext)) {
        into.add(terminal);
      }
      return;
    }
    for (int i = 0; i < tree.getChildCount(); i++) {
      collectMisreadKeywords(tree.getChild(i), into);
    }
  }

  private record CommentLoss(int count, int firstLine) {
    static final CommentLoss NONE = new CommentLoss(0, 0);
  }

  /**
   * How many comment tokens {@code output} holds fewer than {@code input}, and
   * the 1-based line of the first input comment whose text is absent from the
   * output. Only the count decides a refusal; the text points at the culprit.
   * Texts are compared line by line after trimming, the one change emission
   * makes to a comment it keeps (re-indenting a block comment's rows, dropping
   * trailing whitespace).
   */
  private static CommentLoss commentLoss(CommonTokenStream input, CommonTokenStream output) {
    Map<String, Integer> remaining = new HashMap<>();
    int outputCount = 0;
    for (Token comment : comments(output)) {
      remaining.merge(normalizeComment(comment.getText()), 1, Integer::sum);
      outputCount++;
    }
    int firstLostLine = 0;
    List<Token> inputComments = comments(input);
    for (Token comment : inputComments) {
      String text = normalizeComment(comment.getText());
      int left = remaining.getOrDefault(text, 0);
      if (left == 0) {
        if (firstLostLine == 0) {
          firstLostLine = comment.getLine();
        }
      } else {
        remaining.put(text, left - 1);
      }
    }
    return new CommentLoss(inputComments.size() - outputCount, firstLostLine);
  }

  private static List<Token> comments(CommonTokenStream tokens) {
    tokens.fill();
    List<Token> comments = new ArrayList<>();
    for (int i = 0; i < tokens.size(); i++) {
      if (Emitter.isComment(tokens.get(i))) {
        comments.add(tokens.get(i));
      }
    }
    return comments;
  }

  /**
   * {@code changedLine} is the 1-based input line of the first content the
   * output does not reproduce, or 0; {@code gained} whether the output carries
   * content past the end of the input's.
   */
  record ContentLoss(int changedLine, boolean gained) {
    static final ContentLoss NONE = new ContentLoss(0, false);
  }

  static ContentLoss contentLoss(String input, String output) {
    return contentLoss(
        (CommonTokenStream) DRL10ParserHelper.createDrlParser(input).getTokenStream(),
        (CommonTokenStream) DRL10ParserHelper.createDrlParser(output).getTokenStream());
  }

  /**
   * Compares what the two token streams say, character by character, once
   * everything emission is allowed to change is removed: whitespace (spacing
   * comes from the rules, and the RHS lexer splits its chunks on it), the
   * {@code ;} and {@code ,} separators it normalises, and comments, which
   * {@link #commentLoss} judges with their own line numbers.
   */
  private static ContentLoss contentLoss(CommonTokenStream input, CommonTokenStream output) {
    Content in = Content.of(input);
    Content out = Content.of(output);
    int shared = Math.min(in.text.length(), out.text.length());
    for (int i = 0; i < shared; i++) {
      if (in.text.charAt(i) != out.text.charAt(i)) {
        return new ContentLoss(in.lines[i], false);
      }
    }
    if (in.text.length() > shared) {
      return new ContentLoss(in.lines[shared], false);
    }
    return new ContentLoss(0, out.text.length() > shared);
  }

  /** The compared characters of a token stream, each with the line it came from. */
  private record Content(String text, int[] lines) {
    static Content of(CommonTokenStream tokens) {
      tokens.fill();
      StringBuilder text = new StringBuilder();
      List<Integer> lines = new ArrayList<>();
      for (int i = 0; i < tokens.size(); i++) {
        Token t = tokens.get(i);
        if (t.getType() == Token.EOF || t.getChannel() != Token.DEFAULT_CHANNEL || Emitter.isComment(t)) {
          continue;
        }
        for (char c : t.getText().toCharArray()) {
          if (!Character.isWhitespace(c) && c != ';' && c != ',') {
            text.append(c);
            lines.add(t.getLine());
          }
        }
      }
      return new Content(text.toString(), lines.stream().mapToInt(Integer::intValue).toArray());
    }
  }

  private static String normalizeComment(String text) {
    StringBuilder normalized = new StringBuilder();
    for (String line : text.split("\n", -1)) {
      normalized.append(line.trim()).append('\n');
    }
    return normalized.toString();
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
