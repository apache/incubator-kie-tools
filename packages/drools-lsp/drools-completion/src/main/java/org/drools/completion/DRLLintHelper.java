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

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.drools.drl.parser.antlr4.DRL10Parser;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextEdit;

/**
 * Heuristic, parser-free lint passes for DRL source files, complementing the
 * ANTLR-based syntax diagnostics from {@link DRLDiagnosticHelper} with
 * friendlier messages anchored at the offending construct (the parser often
 * reports structural mistakes far from their cause, e.g. a missing
 * {@code end} surfaces as a confusing error at end-of-file).
 *
 * <p>Each pass is configurable through a system property, because every
 * heuristic has false-positive potential and teams differ on how loudly they
 * want to be warned:
 *
 * <pre>
 *   drools.lsp.lint.missingEnd         = off | hint | info | warning | error
 *   drools.lsp.lint.missingSeparator   = off | hint | info | warning | error
 *   drools.lsp.lint.missingSemicolon   = off | hint | info | warning | error
 *   drools.lsp.lint.unbalancedParens   = off | hint | info | warning | error
 *   drools.lsp.lint.unknownTypes       = off | hint | info | warning | error
 *   drools.lsp.lint.mvelPropertyAccess = off | hint | info | warning | error
 * </pre>
 *
 * The structural passes default to {@code warning}. The MVEL property-access
 * pass is purely stylistic (both forms are valid DRL), so it defaults to
 * {@code off} and only runs for teams that opt in.
 *
 * <p>The structural passes are stateless heuristics that operate on a sanitized
 * copy of the text in which comments and string-literal contents are blanked
 * out (preserving line/column positions), so quoted parentheses,
 * {@code when}/{@code then} inside comments, and trailing comments cannot
 * confuse them. {@link #lintUnknownTypes} is the exception: it parses the
 * document (once) and resolves types through {@link DRLCompletionHelper#resolveFqcn}
 * — the same resolution hover, completion, and go-to-definition use — so its
 * notion of "known" matches the rest of the language server.
 */
public final class DRLLintHelper {

    private static final String PROP_MISSING_END          = "drools.lsp.lint.missingEnd";
    private static final String PROP_MISSING_SEPARATOR    = "drools.lsp.lint.missingSeparator";
    private static final String PROP_MISSING_SEMICOLON    = "drools.lsp.lint.missingSemicolon";
    private static final String PROP_UNBALANCED_PARENS    = "drools.lsp.lint.unbalancedParens";
    private static final String PROP_UNKNOWN_TYPES        = "drools.lsp.lint.unknownTypes";
    private static final String PROP_MVEL_PROPERTY_ACCESS = "drools.lsp.lint.mvelPropertyAccess";

    private static final int MAX_DIAGNOSTICS_PER_PASS = 20;
    private static final int MAX_UNKNOWN_TYPE_DIAGNOSTICS = 50;

    private static final Pattern THEN_END_BLOCK =
            Pattern.compile("(?is)\\bthen\\b(.*?)\\bend\\b");
    private static final Pattern THEN_NEW_TYPE =
            Pattern.compile("(?is)\\bnew\\s+([A-Z][A-Za-z0-9_$.]*)\\s*\\(");
    private static final Pattern QUALIFIED_REF =
            Pattern.compile("(?<![\\w.$])([A-Z][A-Za-z0-9_$]*(?:\\.[A-Za-z0-9_$]+)+)");

    private static final Pattern RULE_KEYWORD =
            Pattern.compile("^\\s*(rule|query)\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern DECLARE_KEYWORD =
            Pattern.compile("^\\s*declare\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern END_AT_START =
            Pattern.compile("^end\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern END_AT_END =
            Pattern.compile("(?:^|\\s)end$", Pattern.CASE_INSENSITIVE);
    private static final Pattern WHEN_KEYWORD =
            Pattern.compile("^\\s*when\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern THEN_KEYWORD =
            Pattern.compile("^\\s*then\\b", Pattern.CASE_INSENSITIVE);
    /** A bare pattern header opening its paren at end of line: {@code Person(}. */
    private static final Pattern PATTERN_HEADER_LINE = Pattern.compile(
            "^\\s*(?:(?:not|exists|forall)\\s+)?\\$?[A-Za-z_][\\w$]*\\s*\\(\\s*$",
            Pattern.CASE_INSENSITIVE);
    /** A line that starts like a field constraint: {@code name == ...}. */
    private static final Pattern CONSTRAINT_START = Pattern.compile(
            "^\\s*\\$?[A-Za-z_][\\w$]*(?:\\.[A-Za-z_][\\w$]*)*\\s*"
                    + "(?:[=!<>]|contains\\b|matches\\b|memberOf\\b|in\\b).*");

    private DRLLintHelper() {
    }

    /**
     * Runs all enabled lint passes over {@code text} and returns their
     * combined diagnostics. Returns an empty list for null/empty input or
     * when every pass is disabled.
     */
    public static List<Diagnostic> lint(String text) {
        if (text == null || text.isEmpty()) {
            return Collections.emptyList();
        }
        String sanitized = sanitize(text);
        List<Diagnostic> out = new ArrayList<>();

        DiagnosticSeverity endSeverity = severityFor(PROP_MISSING_END);
        if (endSeverity != null) {
            out.addAll(lintMissingRuleEnds(sanitized, endSeverity));
        }
        DiagnosticSeverity separatorSeverity = severityFor(PROP_MISSING_SEPARATOR);
        if (separatorSeverity != null) {
            out.addAll(lintMissingConstraintSeparators(sanitized, separatorSeverity));
        }
        DiagnosticSeverity semicolonSeverity = severityFor(PROP_MISSING_SEMICOLON);
        if (semicolonSeverity != null) {
            out.addAll(lintMissingThenSemicolons(sanitized, text, semicolonSeverity));
        }
        DiagnosticSeverity parenSeverity = severityFor(PROP_UNBALANCED_PARENS);
        if (parenSeverity != null) {
            out.addAll(lintUnbalancedParens(sanitized, parenSeverity));
        }
        DiagnosticSeverity mvelSeverity = severityFor(PROP_MVEL_PROPERTY_ACCESS, "off");
        if (mvelSeverity != null) {
            out.addAll(lintMvelPropertyAccess(sanitized, mvelSeverity));
        }
        return out;
    }

    // ── unbalanced parentheses in the LHS ────────────────────────────────

    /**
     * Reports unbalanced parentheses inside rule LHS (when-sections), rule
     * consequences (then-sections), and query bodies — the regions where a
     * missing {@code )} is both easy to type and hard to spot from the
     * parser's far-away recovery error. An unclosed {@code (} is anchored
     * at the paren itself; a stray {@code )} at its own position. Runs on
     * sanitized text, so parens in strings and comments don't count.
     */
    private static List<Diagnostic> lintUnbalancedParens(String sanitized,
                                                         DiagnosticSeverity severity) {
        String[] lines = sanitized.split("\r?\n", -1);
        List<Diagnostic> out = new ArrayList<>();
        List<int[]> openParens = new ArrayList<>(); // {line, col}
        boolean inLhs = false;
        boolean inRhs = false;

        for (int i = 0; i < lines.length && out.size() < MAX_DIAGNOSTICS_PER_PASS; i++) {
            String raw = lines[i];
            String line = raw.trim();

            java.util.regex.Matcher ruleStart = RULE_KEYWORD.matcher(line);
            if (ruleStart.find()) {
                // A new rule/query while parens are still open: flush what the
                // previous section left unclosed, then track the new block
                // (query bodies are pattern regions from the start; rules from `when`).
                reportUnclosed(openParens, lines, out, severity);
                inLhs = "query".equalsIgnoreCase(ruleStart.group(1));
                inRhs = false;
                continue;
            }
            if (WHEN_KEYWORD.matcher(line).find()) {
                inLhs = true;
                inRhs = false;
                openParens.clear();
                continue;
            }
            if (inLhs && THEN_KEYWORD.matcher(line).find()) {
                reportUnclosed(openParens, lines, out, severity);
                inLhs = false;
                inRhs = true;
                continue;
            }
            if (END_AT_START.matcher(line).find() && (inLhs || inRhs)) {
                reportUnclosed(openParens, lines, out, severity);
                inLhs = false;
                inRhs = false;
                continue;
            }
            if (!inLhs && !inRhs) {
                continue;
            }

            for (int col = 0; col < raw.length(); col++) {
                char c = raw.charAt(col);
                if (c == '(') {
                    openParens.add(new int[] {i, col});
                } else if (c == ')') {
                    if (openParens.isEmpty()) {
                        Diagnostic d = new Diagnostic();
                        d.setSeverity(severity);
                        d.setSource("drools-lint");
                        d.setMessage("Unmatched ')' — no corresponding '('");
                        d.setRange(new Range(new Position(i, col),
                                             new Position(i, col + 1)));
                        out.add(d);
                        if (out.size() >= MAX_DIAGNOSTICS_PER_PASS) {
                            return out;
                        }
                    } else {
                        openParens.remove(openParens.size() - 1);
                    }
                }
            }
        }
        reportUnclosed(openParens, lines, out, severity);
        return out;
    }

    private static void reportUnclosed(List<int[]> openParens, String[] lines,
                                       List<Diagnostic> out, DiagnosticSeverity severity) {
        for (int[] open : openParens) {
            if (out.size() >= MAX_DIAGNOSTICS_PER_PASS) {
                break;
            }
            Diagnostic d = new Diagnostic();
            d.setSeverity(severity);
            d.setSource("drools-lint");
            d.setMessage("Unclosed '(' — missing matching ')'");
            d.setRange(new Range(new Position(open[0], open[1]),
                                 new Position(open[0], open[1] + 1)));
            out.add(d);
        }
        openParens.clear();
    }

    // ── MVEL property access in constraints ──────────────────────────────

    /**
     * A no-arg JavaBean accessor call with an explicit instance receiver:
     * {@code $p.getCode()} / {@code address.isActive()}. group(1) is the
     * accessor name without the leading dot.
     */
    private static final Pattern ACCESSOR_CALL = Pattern.compile(
            "\\.(get[A-Z][A-Za-z0-9_]*|is[A-Z][A-Za-z0-9_]*)\\(\\s*\\)");

    /**
     * A no-arg JavaBean accessor call with no explicit receiver — the bare
     * form used inside constraint bodies: {@code getName()} in
     * {@code Person(getName() == "John")}. The negative lookbehind excludes
     * calls already matched by {@link #ACCESSOR_CALL} (preceded by {@code .})
     * and calls that are part of a longer identifier (preceded by a word char).
     * group(1) is the accessor name.
     */
    private static final Pattern BARE_ACCESSOR_CALL = Pattern.compile(
            "(?<![.A-Za-z0-9_$])(get[A-Z][A-Za-z0-9_]*|is[A-Z][A-Za-z0-9_]*)\\(\\s*\\)");

    /** A flagged getter call: line/col range, accessor name, and MVEL property name. */
    static final class AccessorFinding {
        final int line;
        final int startCol;
        final int endCol;
        final String accessor;
        final String property;

        AccessorFinding(int line, int startCol, int endCol, String accessor, String property) {
            this.line = line;
            this.startCol = startCol;
            this.endCol = endCol;
            this.accessor = accessor;
            this.property = property;
        }
    }

    /**
     * Scans the WHEN sections of {@code sanitized} text for JavaBean getter
     * calls eligible for MVEL property-access rewriting. Single source of
     * truth for both the diagnostic pass ({@link #lintMvelPropertyAccess}) and
     * the quick-fix edits ({@link #mvelPropertyAccessEdits}).
     *
     * <p>Covers both the explicit-receiver form ({@code $p.getCode()}) and the
     * bare constraint form ({@code getName() == "x"}). Skips
     * {@code getClass()} and {@code Type.getX()} static calls.
     */
    static List<AccessorFinding> findLhsAccessorFindings(String sanitized) {
        String[] lines = sanitized.split("\r?\n", -1);
        List<AccessorFinding> out = new ArrayList<>();
        boolean inWhen = false;

        for (int i = 0; i < lines.length; i++) {
            String raw = lines[i];
            String trimmed = raw.trim();

            if (RULE_KEYWORD.matcher(trimmed).find()) {
                inWhen = false;
            }
            if (WHEN_KEYWORD.matcher(trimmed).find()) {
                inWhen = true;
                continue;
            }
            if (inWhen && THEN_KEYWORD.matcher(trimmed).find()) {
                inWhen = false;
                continue;
            }
            if (!inWhen) {
                continue;
            }

            java.util.regex.Matcher m = ACCESSOR_CALL.matcher(raw);
            while (m.find()) {
                String accessor = m.group(1);
                if ("getClass".equals(accessor)) {
                    continue;
                }
                if (receiverLooksLikeType(raw, m.start())) {
                    continue;
                }
                out.add(new AccessorFinding(i, m.start(1), m.end(), accessor, toProperty(accessor)));
                if (out.size() >= MAX_DIAGNOSTICS_PER_PASS) {
                    return out;
                }
            }
            java.util.regex.Matcher m2 = BARE_ACCESSOR_CALL.matcher(raw);
            while (m2.find()) {
                String accessor = m2.group(1);
                if ("getClass".equals(accessor)) {
                    continue;
                }
                out.add(new AccessorFinding(i, m2.start(1), m2.end(), accessor, toProperty(accessor)));
                if (out.size() >= MAX_DIAGNOSTICS_PER_PASS) {
                    return out;
                }
            }
        }
        return out;
    }

    /**
     * Flags JavaBean getter calls in LHS constraints and suggests the MVEL
     * property-access form. Covers both the bare form ({@code getName()} inside
     * a pattern constraint) and the explicit-receiver form
     * ({@code address.getCode()}). Scoped to the WHEN section only — the THEN
     * consequence is real Java, where getter calls are correct and the property
     * sugar is unavailable. Skips {@code getClass()} (its property form
     * {@code .class} is a reserved MVEL construct) and {@code Type.getX()}
     * static calls.
     */
    private static List<Diagnostic> lintMvelPropertyAccess(String sanitized,
                                                           DiagnosticSeverity severity) {
        List<Diagnostic> out = new ArrayList<>();
        for (AccessorFinding f : findLhsAccessorFindings(sanitized)) {
            Diagnostic d = new Diagnostic();
            d.setSeverity(severity);
            d.setSource("drools-lint");
            d.setCode("mvel-property-access");
            d.setMessage("Prefer MVEL property access '" + f.property
                    + "' over '" + f.accessor + "()' in constraints");
            d.setRange(new Range(new Position(f.line, f.startCol),
                                 new Position(f.line, f.endCol)));
            out.add(d);
            if (out.size() >= MAX_DIAGNOSTICS_PER_PASS) {
                break;
            }
        }
        return out;
    }

    /**
     * Returns the {@link TextEdit}s that rewrite every flagged LHS getter call
     * to MVEL property access ({@code getCode()} → {@code code}). Drives the
     * code-action quick-fix; uses the same detection as
     * {@link #lintMvelPropertyAccess}.
     */
    public static List<TextEdit> mvelPropertyAccessEdits(String text) {
        if (text == null || text.isEmpty()) {
            return Collections.emptyList();
        }
        String sanitized = sanitize(text);
        List<TextEdit> out = new ArrayList<>();
        for (AccessorFinding f : findLhsAccessorFindings(sanitized)) {
            out.add(new TextEdit(
                    new Range(new Position(f.line, f.startCol), new Position(f.line, f.endCol)),
                    f.property));
        }
        return out;
    }

    private static String toProperty(String accessor) {
        return decapitalize(accessor.startsWith("get") ? accessor.substring(3) : accessor.substring(2));
    }

    /**
     * Returns true when the identifier immediately before the dot at
     * {@code dotIndex} starts with an uppercase letter — a {@code Type.getX()}
     * static call rather than a bean property access on an instance.
     */
    private static boolean receiverLooksLikeType(String line, int dotIndex) {
        int end = dotIndex;
        int start = end;
        while (start > 0) {
            char c = line.charAt(start - 1);
            if (Character.isLetterOrDigit(c) || c == '_' || c == '$') {
                start--;
            } else {
                break;
            }
        }
        return start < end && Character.isUpperCase(line.charAt(start));
    }

    /**
     * JavaBeans {@code Introspector.decapitalize} rule, inlined to avoid a
     * {@code java.desktop} dependency: lowercase the first character unless
     * the first two are both uppercase ({@code URL} stays {@code URL}) —
     * matching how Drools resolves pattern properties back to accessors.
     */
    private static String decapitalize(String name) {
        if (name.isEmpty()) {
            return name;
        }
        if (name.length() > 1 && Character.isUpperCase(name.charAt(0))
                && Character.isUpperCase(name.charAt(1))) {
            return name;
        }
        char[] chars = name.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }

    // ── missing ';' in THEN ──────────────────────────────────────────────

    /** Matches a {@code dialect "mvel"} attribute (checked on original text). */
    private static final Pattern MVEL_DIALECT =
            Pattern.compile("(?i)\\bdialect\\s*\"mvel\"");
    private static final Pattern CONTROL_FLOW_START = Pattern.compile(
            "^(if|for|while|else|try|catch|finally|switch|do)\\b.*",
            Pattern.CASE_INSENSITIVE);

    /**
     * Reports Java statements in the {@code then} consequence that end
     * without a semicolon. Exemptions for the legal semicolon-free cases:
     * MVEL-dialect rules (file- or rule-level attribute), statement bodies
     * inside braces (e.g. {@code modify(...) { ... }}), fluent-chain
     * continuations (next line starts with {@code .}), multi-line call
     * arguments (open paren depth), and control-flow/brace lines.
     *
     * @param sanitized comment/string-blanked text the heuristics run on
     * @param original  unmodified text, needed for dialect detection (the
     *                  sanitizer blanks the {@code "mvel"} literal)
     */
    private static List<Diagnostic> lintMissingThenSemicolons(String sanitized,
                                                              String original,
                                                              DiagnosticSeverity severity) {
        String[] lines = sanitized.split("\r?\n", -1);
        String[] originalLines = original.split("\r?\n", -1);
        List<Diagnostic> out = new ArrayList<>();

        boolean inRuleHeader = false;
        boolean inRhs = false;
        boolean fileMvel = false;
        boolean ruleMvel = false;
        int parenDepth = 0;
        int braceDepth = 0;

        for (int i = 0; i < lines.length; i++) {
            String raw = lines[i];
            String line = raw.trim();
            String originalLine = i < originalLines.length ? originalLines[i] : "";

            // dialect "mvel" before the first rule applies to the whole file.
            if (!inRuleHeader && !inRhs && MVEL_DIALECT.matcher(originalLine).find()) {
                if (!containsRuleBefore(lines, i)) {
                    fileMvel = true;
                } else {
                    ruleMvel = true;
                }
            }

            if (RULE_KEYWORD.matcher(line).find()) {
                inRuleHeader = true;
                inRhs = false;
                ruleMvel = false;
                parenDepth = 0;
                braceDepth = 0;
            }
            if (inRuleHeader && MVEL_DIALECT.matcher(originalLine).find()) {
                ruleMvel = true;
            }
            if (WHEN_KEYWORD.matcher(line).find()) {
                inRuleHeader = false;
            }
            if (THEN_KEYWORD.matcher(line).find()) {
                inRuleHeader = false;
                inRhs = true;
                parenDepth = 0;
                braceDepth = 0;
                continue;
            }
            if (!inRhs) {
                continue;
            }
            if (END_AT_START.matcher(line).find()) {
                inRhs = false;
                continue;
            }
            if (line.isEmpty()) {
                continue;
            }
            if (fileMvel || ruleMvel) {
                continue;
            }

            int braceDelta = countChar(raw, '{') - countChar(raw, '}');
            boolean wasInsideBraces = braceDepth > 0;
            braceDepth = Math.max(0, braceDepth + braceDelta);
            parenDepth = Math.max(0, parenDepth + countChar(raw, '(') - countChar(raw, ')'));

            // Inside a braced body (modify blocks, if/for bodies) the
            // heuristic stands down — modify uses commas legally, and
            // distinguishing it from Java blocks isn't worth false positives.
            if (wasInsideBraces || braceDelta != 0
                    || line.endsWith("{") || line.endsWith("}")) {
                continue;
            }
            // A statement whose call parens are still open continues on the
            // next line.
            if (parenDepth > 0) {
                continue;
            }
            if (CONTROL_FLOW_START.matcher(line).matches()) {
                continue;
            }
            if (line.endsWith(";") || line.endsWith(",")) {
                continue;
            }
            // Fluent chain: the statement continues on a `.method(...)` line.
            String next = nextNonBlankLine(lines, i + 1);
            if (next != null && next.startsWith(".")) {
                continue;
            }

            String lower = line.toLowerCase();
            boolean looksLikeStatement = line.endsWith(")")
                    || lower.startsWith("insert") || lower.startsWith("update")
                    || lower.startsWith("retract") || lower.startsWith("delete")
                    || lower.startsWith("modify") || lower.startsWith("new ")
                    || line.contains("=");
            if (!looksLikeStatement) {
                continue;
            }

            int col = Math.max(0, lastNonWhitespaceCol(raw));
            Diagnostic d = new Diagnostic();
            d.setSeverity(severity);
            d.setSource("drools-lint");
            d.setMessage("Likely missing ';' at the end of this consequence statement");
            d.setRange(new Range(new Position(i, col),
                                 new Position(i, col + 1)));
            out.add(d);
            if (out.size() >= MAX_DIAGNOSTICS_PER_PASS) {
                break;
            }
        }
        return out;
    }

    private static boolean containsRuleBefore(String[] lines, int index) {
        for (int i = 0; i < index && i < lines.length; i++) {
            if (RULE_KEYWORD.matcher(lines[i].trim()).find()) {
                return true;
            }
        }
        return false;
    }

    // ── missing constraint separators ────────────────────────────────────

    /**
     * Reports adjacent constraint lines inside an open pattern paren where
     * the first line ends without a separator ({@code ,}, {@code &&},
     * {@code ||}, {@code and}, {@code or}) — a newline is not a constraint
     * separator in DRL. Operates on sanitized text, so parens inside string
     * literals and comment lines cannot corrupt the depth bookkeeping.
     */
    private static List<Diagnostic> lintMissingConstraintSeparators(String sanitized,
                                                                    DiagnosticSeverity severity) {
        String[] lines = sanitized.split("\r?\n", -1);
        List<Diagnostic> out = new ArrayList<>();

        boolean inWhen = false;
        int parenDepth = 0;

        for (int i = 0; i < lines.length; i++) {
            String raw = lines[i];
            String a = raw.trim();

            if (RULE_KEYWORD.matcher(a).find()) {
                inWhen = false;
                parenDepth = 0;
            }
            if (WHEN_KEYWORD.matcher(a).find()) {
                inWhen = true;
                parenDepth = 0;
                continue;
            }
            if (inWhen && THEN_KEYWORD.matcher(a).find()) {
                inWhen = false;
                parenDepth = 0;
                continue;
            }
            if (!inWhen) {
                continue;
            }

            // Depth bookkeeping happens for every line in the when-section —
            // skipping it for "uninteresting" lines is how comment lines used
            // to silence the pass for the rest of the rule.
            parenDepth += countChar(raw, '(') - countChar(raw, ')');

            if (a.isEmpty() || parenDepth <= 0) {
                continue;
            }
            if (PATTERN_HEADER_LINE.matcher(a).matches()) {
                continue;
            }
            if (!CONSTRAINT_START.matcher(a).matches()) {
                continue;
            }

            String lower = a.toLowerCase();
            boolean endsWithSeparator = a.endsWith(",") || a.endsWith("&&")
                    || a.endsWith("||") || a.endsWith("(")
                    || lower.endsWith(" and") || lower.endsWith(" or");
            if (endsWithSeparator) {
                continue;
            }

            String next = nextNonBlankLine(lines, i + 1);
            if (next == null || !CONSTRAINT_START.matcher(next).matches()) {
                continue;
            }

            int col = Math.max(0, lastNonWhitespaceCol(raw));
            Diagnostic d = new Diagnostic();
            d.setSeverity(severity);
            d.setSource("drools-lint");
            d.setMessage("Likely missing ',' between constraints (newline is not a separator)");
            d.setRange(new Range(new Position(i, col),
                                 new Position(i, col + 1)));
            out.add(d);
            if (out.size() >= MAX_DIAGNOSTICS_PER_PASS) {
                break;
            }
        }
        return out;
    }

    // ── missing 'end' ────────────────────────────────────────────────────

    /**
     * Reports {@code rule}/{@code query} blocks that are never closed with
     * {@code end}. {@code declare} blocks are tracked separately so their
     * {@code end} doesn't close a rule; a declare block left unclosed does
     * not suppress tracking of subsequent rules (a {@code rule} keyword
     * cannot legally appear inside {@code declare}, so it implicitly
     * terminates the tracking of one).
     */
    private static List<Diagnostic> lintMissingRuleEnds(String sanitized,
                                                        DiagnosticSeverity severity) {
        String[] lines = sanitized.split("\r?\n", -1);
        List<Diagnostic> out = new ArrayList<>();
        List<int[]> openRules = new ArrayList<>(); // {line, startCol}
        boolean inDeclare = false;

        for (int i = 0; i < lines.length; i++) {
            String raw = lines[i];
            String trimmed = raw.trim();
            if (trimmed.isEmpty()) {
                continue;
            }

            if (RULE_KEYWORD.matcher(trimmed).find()) {
                inDeclare = false;
                openRules.add(new int[] {i, firstNonWhitespaceCol(raw)});
            } else if (DECLARE_KEYWORD.matcher(trimmed).find()) {
                inDeclare = true;
            }

            boolean closes = END_AT_START.matcher(trimmed).find()
                    || END_AT_END.matcher(trimmed).find();
            if (closes) {
                if (inDeclare) {
                    inDeclare = false;
                } else if (!openRules.isEmpty()) {
                    openRules.remove(openRules.size() - 1);
                }
            }
        }

        for (int[] rs : openRules) {
            String raw = lines[rs[0]];
            int startCol = rs[1];
            int keywordLen = raw.trim().toLowerCase().startsWith("query") ? 5 : 4;
            int endCol = Math.min(raw.length(), startCol + keywordLen);
            if (endCol <= startCol) {
                endCol = Math.min(raw.length(), startCol + 1);
            }
            Diagnostic d = new Diagnostic();
            d.setSeverity(severity);
            d.setSource("drools-lint");
            d.setRange(new Range(new Position(rs[0], startCol),
                                 new Position(rs[0], endCol)));
            d.setMessage("Missing 'end' for rule/query starting at line " + (rs[0] + 1));
            out.add(d);
            if (out.size() >= MAX_DIAGNOSTICS_PER_PASS) {
                break;
            }
        }
        return out;
    }

    // ── unknown types ────────────────────────────────────────────────────

    /**
     * Convenience entry point that parses {@code text} itself, for callers with
     * no compilation unit in hand (e.g. tests). Server requests share the syntax
     * pass's parse through the {@code cu} overload instead.
     */
    public static List<Diagnostic> lintUnknownTypes(String text, Path documentPath,
                                                    Map<Path, String> openFiles, ClassIndex classIndex,
                                                    ClassMemberIndex memberIndex, boolean classpathResolved) {
        if (text == null || text.isEmpty()) {
            return Collections.emptyList();
        }
        return lintUnknownTypes(text, DRLParsers.silent(text).compilationUnit(), documentPath,
                openFiles, classIndex, memberIndex, classpathResolved);
    }

    /**
     * Flags type references that resolve to no known type and, for likely typos
     * (within bounded Levenshtein ≤ 2 of a known name), suggests the right one.
     * Candidates come from three sources:
     * <ul>
     *   <li>pattern object types, read from the parse tree (so nested
     *       {@code exists}/{@code not}/{@code from}/{@code accumulate} patterns
     *       are covered);</li>
     *   <li>upper-case-led qualified references in LHS constraints, e.g.
     *       {@code Pet.Dog.name} — the head type is
     *       resolved, then the first upper-case member segment is verified
     *       against the head's reflected members (enum constants, fields, nested
     *       types); lower-case property segments are not chased;</li>
     *   <li>{@code new T(...)} in the consequence, matched by regex — the RHS is
     *       lexed into opaque chunks the grammar does not structure.</li>
     * </ul>
     * A type is known when it is a declared type or resolves via
     * {@link DRLCompletionHelper#resolveFqcn} (imports, wildcard imports, the
     * class index, or {@code java.lang}) — the same resolution hover, completion
     * and go-to-definition use. The caller supplies the parsed {@code cu}
     * (shared with the syntax pass) and it is reused here for declared-type
     * extraction. Self-gated by {@code drools.lsp.lint.unknownTypes}
     * (default {@code warning}; {@code off} disables).
     *
     * @param cu                the parsed compilation unit (shared with the syntax pass); may be {@code null}
     * @param documentPath      document path for sibling declared-type lookup; may be {@code null}
     * @param openFiles         open unsaved sibling buffers, so cross-file declares count; may be empty
     * @param classIndex        resolved classpath index, for type resolution and suggestions
     * @param memberIndex       member reflection, for verifying classpath qualified-reference members
     * @param classpathResolved whether the project classpath has resolved; when {@code false}, only
     *                          checks derivable from the DRL alone run (declared types and declared-enum
     *                          members), since a non-declared name can't be confirmed unknown without it
     */
    public static List<Diagnostic> lintUnknownTypes(String text, DRL10Parser.CompilationUnitContext cu,
                                                    Path documentPath, Map<Path, String> openFiles,
                                                    ClassIndex classIndex, ClassMemberIndex memberIndex,
                                                    boolean classpathResolved) {
        if (text == null || text.isEmpty() || cu == null) {
            return Collections.emptyList();
        }
        DiagnosticSeverity severity = severityFor(PROP_UNKNOWN_TYPES, "warning");
        if (severity == null) {
            return Collections.emptyList();
        }
        // Strings/comments are blanked so type-like text inside them can't match;
        // the cu and the sanitized text share character positions.
        String sanitized = sanitize(text);

        // Declared types of the document + siblings, kept as full types so a
        // declared enum's constants can be verified for qualified references.
        Map<String, DeclaredType> declared = new HashMap<>();
        for (DeclaredType dt : DRLDeclaredTypeParser.extractFromCompilationUnit(cu)) {
            if (dt.name != null) {
                declared.putIfAbsent(dt.name, dt);
            }
        }
        DRLWorkspaceTypeIndex.forEachSiblingType(documentPath, openFiles, (dt, uri) -> {
            if (dt.name != null) {
                declared.putIfAbsent(dt.name, dt);
            }
        });
        Set<String> known = declared.keySet();
        // Typo suggestions are drawn from declared types plus classpath simple names.
        Set<String> suggestions = new HashSet<>(known);
        suggestions.addAll(classIndex.simpleNames());

        List<Diagnostic> out = new ArrayList<>();
        collectPatternTypes(cu, cu, known, suggestions, classIndex, classpathResolved, severity, out);
        scanQualifiedRefs(cu, sanitized, declared, suggestions, cu, classIndex, memberIndex,
                          classpathResolved, severity, out);

        Matcher then = THEN_END_BLOCK.matcher(sanitized);
        while (then.find() && out.size() < MAX_UNKNOWN_TYPE_DIAGNOSTICS) {
            int base = then.start(1);
            Matcher newType = THEN_NEW_TYPE.matcher(then.group(1));
            while (newType.find() && out.size() < MAX_UNKNOWN_TYPE_DIAGNOSTICS) {
                Range range = rangeOf(sanitized, base + newType.start(1), base + newType.end(1));
                addUnknown(newType.group(1), range, known, suggestions, cu, classIndex,
                           classpathResolved, severity, out);
            }
        }
        return out;
    }

    /** Walks {@code node}, checking each pattern's object type against {@code cu}'s resolution. */
    private static void collectPatternTypes(ParseTree node, DRL10Parser.CompilationUnitContext cu,
                                            Set<String> known, Set<String> suggestions,
                                            ClassIndex classIndex, boolean classpathResolved,
                                            DiagnosticSeverity severity, List<Diagnostic> out) {
        if (out.size() >= MAX_UNKNOWN_TYPE_DIAGNOSTICS) {
            return;
        }
        if (node instanceof DRL10Parser.LhsPatternContext pattern && pattern.objectType != null) {
            Token start = pattern.objectType.getStart();
            Token stop = pattern.objectType.getStop();
            if (start != null && stop != null) {
                Range range = new Range(
                        new Position(start.getLine() - 1, start.getCharPositionInLine()),
                        new Position(stop.getLine() - 1,
                                     stop.getCharPositionInLine() + stop.getText().length()));
                addUnknown(pattern.objectType.getText(), range, known, suggestions, cu, classIndex,
                           classpathResolved, severity, out);
            }
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            collectPatternTypes(node.getChild(i), cu, known, suggestions, classIndex, classpathResolved,
                                severity, out);
        }
    }

    /**
     * Walks {@code node} for {@code when} sections and checks every upper-case-led
     * qualified reference in each (constraints carry these inside expression
     * subtrees, so the chain is located by regex over the section's text and
     * resolution stays with {@link DRLCompletionHelper#resolveFqcn} / the member
     * index).
     */
    private static void scanQualifiedRefs(ParseTree node, String sanitized,
                                          Map<String, DeclaredType> declared, Set<String> suggestions,
                                          DRL10Parser.CompilationUnitContext cu, ClassIndex classIndex,
                                          ClassMemberIndex memberIndex, boolean classpathResolved,
                                          DiagnosticSeverity severity, List<Diagnostic> out) {
        if (out.size() >= MAX_UNKNOWN_TYPE_DIAGNOSTICS) {
            return;
        }
        if (node instanceof DRL10Parser.LhsContext lhs && lhs.getStart() != null && lhs.getStop() != null) {
            int start = lhs.getStart().getStartIndex();
            int stop = lhs.getStop().getStopIndex();
            if (start >= 0 && stop >= start && stop < sanitized.length()) {
                Matcher m = QUALIFIED_REF.matcher(sanitized.substring(start, stop + 1));
                while (m.find() && out.size() < MAX_UNKNOWN_TYPE_DIAGNOSTICS) {
                    checkChain(m.group(1), start + m.start(1), sanitized, declared, suggestions,
                               cu, classIndex, memberIndex, classpathResolved, severity, out);
                }
            }
            return; // the section's whole text is covered; no nested when-sections
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            scanQualifiedRefs(node.getChild(i), sanitized, declared, suggestions, cu, classIndex,
                              memberIndex, classpathResolved, severity, out);
        }
    }

    /**
     * Checks the head type and first upper-case member of a qualified chain like
     * {@code QuestionIds.TargetResponse.groupType}. The head is verified against
     * declared types and {@link DRLCompletionHelper#resolveFqcn}; the member is
     * verified against a declared enum's constants (no classpath needed) or a
     * resolved classpath type's reflected members. A lower-case member (a
     * property) is not chased.
     */
    private static void checkChain(String chain, int chainStart, String sanitized,
                                   Map<String, DeclaredType> declared, Set<String> suggestions,
                                   DRL10Parser.CompilationUnitContext cu, ClassIndex classIndex,
                                   ClassMemberIndex memberIndex, boolean classpathResolved,
                                   DiagnosticSeverity severity, List<Diagnostic> out) {
        int dot = chain.indexOf('.');
        String head = chain.substring(0, dot);
        int memberStart = dot + 1;
        int nextDot = chain.indexOf('.', memberStart);
        String member = nextDot < 0 ? chain.substring(memberStart) : chain.substring(memberStart, nextDot);
        boolean memberIsType = !member.isEmpty() && Character.isUpperCase(member.charAt(0));

        DeclaredType headType = declared.get(head);
        if (headType != null) {
            // Declared head. Only an enum exposes verifiable Type.MEMBER access
            // (its constants); other declares have no static members to check.
            if (headType.isEnum && memberIsType) {
                Set<String> names = declaredMemberNames(headType);
                if (!names.contains(member)) {
                    out.add(memberWarning(member, head, memberRange(sanitized, chainStart, memberStart, member),
                            bestTypoSuggestion(member, names), severity));
                }
            }
            return;
        }

        // Classpath head.
        String headFqcn = DRLCompletionHelper.resolveFqcn(head, head, cu, classIndex);
        if (headFqcn == null) {
            // Can't be sure it's a typo vs an unresolved real classpath type.
            if (classpathResolved) {
                out.add(makeUnknownTypeWarning(head,
                        rangeOf(sanitized, chainStart, chainStart + head.length()),
                        bestTypoSuggestion(head, suggestions), severity));
            }
            return;
        }
        if (!memberIsType) {
            return; // a property (lower-case) — not chased
        }
        Set<String> members = memberIndex.memberNames(headFqcn);
        if (members == null || members.contains(member)) {
            return; // head not loadable (can't verify) or member is valid
        }
        out.add(memberWarning(member, toSimpleTypeName(head),
                memberRange(sanitized, chainStart, memberStart, member),
                bestTypoSuggestion(member, members), severity));
    }

    private static Range memberRange(String sanitized, int chainStart, int memberStart, String member) {
        return rangeOf(sanitized, chainStart + memberStart, chainStart + memberStart + member.length());
    }

    /** Field and enum-constant names declared on {@code dt} (its own members). */
    private static Set<String> declaredMemberNames(DeclaredType dt) {
        Set<String> names = new HashSet<>();
        if (dt.fields != null) {
            for (Field f : dt.fields) {
                if (f != null && f.name != null) {
                    names.add(f.name);
                }
            }
        }
        return names;
    }

    /**
     * Emits an unknown-type warning for {@code candidate} unless it is a declared
     * type or resolves. When the classpath is unresolved a non-resolving name is
     * left alone — it can't be told apart from a real-but-unindexed type.
     */
    private static void addUnknown(String candidate, Range range, Set<String> known,
                                   Set<String> suggestions, DRL10Parser.CompilationUnitContext cu,
                                   ClassIndex classIndex, boolean classpathResolved,
                                   DiagnosticSeverity severity, List<Diagnostic> out) {
        if (candidate == null) {
            return;
        }
        String trimmed = candidate.trim();
        if (trimmed.isEmpty()) {
            return;
        }
        String simple = toSimpleTypeName(trimmed);
        if (known.contains(simple)
                || DRLCompletionHelper.resolveFqcn(trimmed, simple, cu, classIndex) != null) {
            return;
        }
        if (!classpathResolved) {
            return;
        }
        out.add(makeUnknownTypeWarning(trimmed, range, bestTypoSuggestion(simple, suggestions), severity));
    }

    private static Range rangeOf(String text, int startOffset, int endOffset) {
        return new Range(offsetToPosition(text, startOffset), offsetToPosition(text, endOffset));
    }

    private static Diagnostic memberWarning(String member, String container, Range range,
                                            String suggestion, DiagnosticSeverity severity) {
        Diagnostic d = new Diagnostic();
        d.setSeverity(severity);
        d.setSource("drools-type");
        d.setCode("unknown-member");
        d.setRange(range);
        String msg = "'" + member + "' is not a member of '" + container + "'";
        if (suggestion != null && !suggestion.isBlank() && !suggestion.equals(member)) {
            msg += ". Did you mean '" + suggestion + "'?";
            d.setData(suggestion); // drives the "Replace with '<suggestion>'" quick-fix
        } else {
            msg += ".";
        }
        d.setMessage(msg);
        return d;
    }

    private static Diagnostic makeUnknownTypeWarning(String candidate, Range range,
                                                     String suggestion, DiagnosticSeverity severity) {
        Diagnostic d = new Diagnostic();
        d.setSeverity(severity);
        d.setSource("drools-type");
        d.setCode("unknown-type");
        d.setRange(range);

        String msg = "Unknown type '" + candidate + "'";
        if (suggestion != null && !suggestion.isBlank() && !suggestion.equals(candidate)) {
            msg += " (possible typo). Did you mean '" + suggestion + "'?";
            // The quick-fix reads this to offer "Replace with '<suggestion>'".
            d.setData(suggestion);
        } else {
            msg += ".";
        }
        msg += " If this is a Java type, add an import or ensure it is on the resolved classpath.";
        d.setMessage(msg);
        return d;
    }

    /**
     * Closest known simple name to {@code candidateSimple} within edit distance
     * 2 (a likely typo), or {@code null} when nothing is close enough. Exact
     * matches return {@code null} — the caller only reaches here for unknowns.
     */
    static String bestTypoSuggestion(String candidateSimple, Set<String> knownSimpleNames) {
        if (candidateSimple == null || candidateSimple.isBlank()
                || knownSimpleNames == null || knownSimpleNames.isEmpty()) {
            return null;
        }
        String c = candidateSimple.trim();
        if (c.length() < 2) {
            return null;
        }
        String best = null;
        int bestDist = Integer.MAX_VALUE;
        for (String k : knownSimpleNames) {
            if (k == null || k.isBlank()) {
                continue;
            }
            if (k.equals(c)) {
                return null;
            }
            if (Math.abs(k.length() - c.length()) > 2) {
                continue;
            }
            int dist = levenshteinDistanceAtMost(c, k, 2);
            if (dist >= 0 && dist < bestDist) {
                bestDist = dist;
                best = k;
                if (bestDist == 1) {
                    return best;
                }
            }
        }
        return bestDist <= 2 ? best : null;
    }

    /** Levenshtein distance between {@code a} and {@code b}, or -1 once it exceeds {@code max}. */
    private static int levenshteinDistanceAtMost(String a, String b, int max) {
        if (a == null || b == null) {
            return -1;
        }
        int n = a.length();
        int m = b.length();
        if (Math.abs(n - m) > max) {
            return -1;
        }
        if (n == 0) {
            return m <= max ? m : -1;
        }
        if (m == 0) {
            return n <= max ? n : -1;
        }
        if (n > m) {
            String tmp = a;
            a = b;
            b = tmp;
            n = a.length();
            m = b.length();
        }
        int[] prev = new int[n + 1];
        int[] cur = new int[n + 1];
        for (int i = 0; i <= n; i++) {
            prev[i] = i;
        }
        for (int j = 1; j <= m; j++) {
            char bj = b.charAt(j - 1);
            cur[0] = j;
            int rowMin = cur[0];
            for (int i = 1; i <= n; i++) {
                int cost = a.charAt(i - 1) == bj ? 0 : 1;
                cur[i] = Math.min(prev[i] + 1, Math.min(cur[i - 1] + 1, prev[i - 1] + cost));
                if (cur[i] < rowMin) {
                    rowMin = cur[i];
                }
            }
            if (rowMin > max) {
                return -1;
            }
            int[] tmp = prev;
            prev = cur;
            cur = tmp;
        }
        return prev[n] <= max ? prev[n] : -1;
    }

    static String toSimpleTypeName(String candidate) {
        if (candidate == null) {
            return "";
        }
        int idx = Math.max(candidate.lastIndexOf('.'), candidate.lastIndexOf('$'));
        return idx >= 0 && idx < candidate.length() - 1 ? candidate.substring(idx + 1) : candidate;
    }

    /** Converts an absolute character offset into a 0-based line/column position. */
    private static Position offsetToPosition(String text, int offset) {
        int line = 0;
        int col = 0;
        int end = Math.min(offset, text.length());
        for (int i = 0; i < end; i++) {
            if (text.charAt(i) == '\n') {
                line++;
                col = 0;
            } else {
                col++;
            }
        }
        return new Position(line, col);
    }

    // ── shared helpers ───────────────────────────────────────────────────

    private static DiagnosticSeverity severityFor(String property) {
        return severityFor(property, "warning");
    }

    /**
     * Resolves the severity for a pass from its system property: {@code off}
     * returns null (pass disabled), unknown values fall back to
     * {@code defaultValue}.
     */
    private static DiagnosticSeverity severityFor(String property, String defaultValue) {
        String value = System.getProperty(property, defaultValue)
                .trim().toLowerCase();
        switch (value) {
            case "off":
                return null;
            case "hint":
                return DiagnosticSeverity.Hint;
            case "info":
                return DiagnosticSeverity.Information;
            case "error":
                return DiagnosticSeverity.Error;
            case "warning":
            default:
                return DiagnosticSeverity.Warning;
        }
    }

    /**
     * Returns a copy of {@code text} in which line comments ({@code //},
     * {@code #}), block comments and the contents of string literals are
     * replaced by spaces. Newlines are preserved (CR normalised to a space
     * within lines is avoided by keeping CR as-is only when followed by LF),
     * so every remaining character keeps its original line and column.
     */
    static String sanitize(String text) {
        char[] chars = text.toCharArray();
        boolean inString = false;
        boolean inLineComment = false;
        boolean inBlockComment = false;

        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c == '\n' || c == '\r') {
                inLineComment = false;
                inString = false; // DRL string literals do not span lines
                continue;
            }
            if (inLineComment) {
                chars[i] = ' ';
                continue;
            }
            if (inBlockComment) {
                if (c == '*' && i + 1 < chars.length && chars[i + 1] == '/') {
                    chars[i] = ' ';
                    chars[i + 1] = ' ';
                    i++;
                    inBlockComment = false;
                } else {
                    chars[i] = ' ';
                }
                continue;
            }
            if (inString) {
                if (c == '\\' && i + 1 < chars.length) {
                    chars[i] = ' ';
                    chars[i + 1] = ' ';
                    i++;
                } else if (c == '"') {
                    inString = false;
                } else {
                    chars[i] = ' ';
                }
                continue;
            }
            if (c == '"') {
                inString = true;
            } else if (c == '/' && i + 1 < chars.length && chars[i + 1] == '/') {
                chars[i] = ' ';
                chars[i + 1] = ' ';
                i++;
                inLineComment = true;
            } else if (c == '#') {
                chars[i] = ' ';
                inLineComment = true;
            } else if (c == '/' && i + 1 < chars.length && chars[i + 1] == '*') {
                chars[i] = ' ';
                chars[i + 1] = ' ';
                i++;
                inBlockComment = true;
            }
        }
        return new String(chars);
    }

    private static int firstNonWhitespaceCol(String line) {
        for (int i = 0; i < line.length(); i++) {
            if (!Character.isWhitespace(line.charAt(i))) {
                return i;
            }
        }
        return 0;
    }

    private static int lastNonWhitespaceCol(String line) {
        for (int i = line.length() - 1; i >= 0; i--) {
            if (!Character.isWhitespace(line.charAt(i))) {
                return i;
            }
        }
        return 0;
    }

    private static String nextNonBlankLine(String[] lines, int from) {
        for (int i = from; i < lines.length; i++) {
            String trimmed = lines[i].trim();
            if (!trimmed.isEmpty()) {
                return trimmed;
            }
        }
        return null;
    }

    private static int countChar(String s, char c) {
        int n = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == c) {
                n++;
            }
        }
        return n;
    }
}
