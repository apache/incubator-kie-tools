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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.drools.drl.parser.antlr4.DRL10Lexer;
import org.drools.drl.parser.antlr4.DRL10ParserHelper;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;

/**
 * Parses in-source linter suppression directives under the {@code @drl-lsp-lint}
 * namespace from ANTLR comment tokens and filters out suppressed diagnostics.
 *
 * <p>Supported directives:
 * <ul>
 *   <li>{@code @drl-lsp-lint:disable-file [code, ...]} &mdash; suppresses matching codes (or all codes) for the entire file</li>
 *   <li>{@code @drl-lsp-lint:disable-line [code, ...]} &mdash; suppresses matching codes on the current line</li>
 *   <li>{@code @drl-lsp-lint:disable-next-line [code, ...]} &mdash; suppresses matching codes on the line immediately following (for block comments, the line following closing {@code *&#47;})</li>
 *   <li>{@code @drl-lsp-lint:disable-rule [code, ...]} &mdash; suppresses matching codes for the entire enclosing or following rule/query</li>
 *   <li>{@code @drl-lsp-lint:disable [code, ...]} &mdash; starts a suppression region from this line downwards</li>
 *   <li>{@code @drl-lsp-lint:enable [code, ...]} &mdash; ends a suppression region from this line downwards</li>
 * </ul>
 *
 * <p>Directives apply strictly to {@code drools-lint}, {@code drools-type}, and
 * {@code drools-deprecated} diagnostics. Parser/syntax errors ({@code drools-drl-parser})
 * and compiler diagnostics ({@code drools}) are never suppressed.
 */
public final class DRLLintSuppression {

    private static final Pattern DIRECTIVE_PATTERN = Pattern.compile("(?i)@drl-lsp-lint:([a-zA-Z0-9_-]+)");

    public static final Set<String> VALID_ACTIONS = Set.of(
            "disable-file",
            "disable-next-line",
            "disable-line",
            "disable-rule",
            "disable",
            "enable"
    );

    public static final Set<String> ALL_LINT_CODES = Set.of(
            DRLLintHelper.CODE_MISSING_END,
            DRLLintHelper.CODE_MISSING_SEPARATOR,
            DRLLintHelper.CODE_MISSING_SEMICOLON,
            DRLLintHelper.CODE_UNBALANCED_PARENS,
            DRLLintHelper.CODE_MVEL_PROPERTY_ACCESS,
            DRLLintHelper.CODE_UNKNOWN_TYPE,
            DRLLintHelper.CODE_UNKNOWN_MEMBER
    );

    public static final Set<String> ALL_DEPRECATED_CODES = Set.of(
            DeprecatedConstructs.CODE_HALF_CONSTRAINT,
            DeprecatedConstructs.CODE_PATTERN_CONNECTIVE,
            DeprecatedConstructs.CODE_LHS_CONNECTIVE_ANNOTATION,
            DeprecatedConstructs.CODE_AGENDA_GROUP,
            DeprecatedConstructs.CODE_CUSTOM_OPERATOR
    );

    public static final Set<String> ALL_KNOWN_CODES;
    static {
        Set<String> all = new HashSet<>(ALL_LINT_CODES);
        all.addAll(ALL_DEPRECATED_CODES);
        ALL_KNOWN_CODES = Collections.unmodifiableSet(all);
    }

    private DRLLintSuppression() {
    }

    /**
     * Filters {@code diagnostics} against any {@code @drl-lsp-lint} directives
     * found in {@code text}. Diagnostics with sources other than {@code drools-lint},
     * {@code drools-type}, or {@code drools-deprecated} are never dropped.
     */
    public static List<Diagnostic> filter(List<Diagnostic> diagnostics, String text) {
        if (diagnostics == null || diagnostics.isEmpty() || text == null || text.isEmpty()) {
            return diagnostics;
        }
        SuppressionIndex index = parse(text);
        if (!index.hasAnySuppression()) {
            return diagnostics;
        }
        List<Diagnostic> filtered = new ArrayList<>(diagnostics.size());
        for (Diagnostic d : diagnostics) {
            if (!index.isSuppressed(d)) {
                filtered.add(d);
            }
        }
        return filtered;
    }

    /** Returns {@code true} if {@code source} is eligible for linter suppression. */
    public static boolean isSuppressibleSource(String source) {
        return DRLLintHelper.SOURCE.equals(source)
                || DRLLintHelper.SOURCE_TYPE.equals(source)
                || DeprecatedConstructs.SOURCE.equals(source);
    }

    /**
     * Parses suppression directives from {@code text} by tokenizing comments.
     */
    public static SuppressionIndex parse(String text) {
        return SuppressionIndex.parse(text);
    }

    /**
     * Parses suppression directives from pre-existing {@code tokens} and {@code text}.
     */
    public static SuppressionIndex parse(List<Token> tokens, String text) {
        return SuppressionIndex.parse(tokens, text);
    }

    private static boolean isComment(Token t) {
        if (t == null) {
            return false;
        }
        int type = t.getType();
        if (type == DRL10Lexer.COMMENT
                || type == DRL10Lexer.LINE_COMMENT
                || type == DRL10Lexer.RHS_COMMENT
                || type == DRL10Lexer.RHS_LINE_COMMENT) {
            return true;
        }
        if (t.getChannel() != Token.DEFAULT_CHANNEL) {
            String txt = t.getText();
            return txt != null && (txt.startsWith("//") || txt.startsWith("/*"));
        }
        return false;
    }

    private static int countNewlines(String s, int start, int end) {
        if (s == null) {
            return 0;
        }
        int count = 0;
        int limit = Math.min(end, s.length());
        for (int i = start; i < limit; i++) {
            if (s.charAt(i) == '\n') {
                count++;
            }
        }
        return count;
    }

    private static final class ParsedDirective {
        final String action;
        final Set<String> codes;
        final int directiveLine;
        final int targetNextLine;

        ParsedDirective(String action, Set<String> codes, int directiveLine, int targetNextLine) {
            this.action = action;
            this.codes = codes;
            this.directiveLine = directiveLine;
            this.targetNextLine = targetNextLine;
        }
    }

    /**
     * Tracks regional suppression state as an exclusion set to cleanly support
     * wildcard disable followed by partial re-enablement.
     */
    static final class RegionState {
        boolean allDisabled;
        final Set<String> disabledCodes;
        final Set<String> reEnabledCodes;

        RegionState() {
            this.allDisabled = false;
            this.disabledCodes = new HashSet<>();
            this.reEnabledCodes = new HashSet<>();
        }

        RegionState(boolean allDisabled, Set<String> disabledCodes, Set<String> reEnabledCodes) {
            this.allDisabled = allDisabled;
            this.disabledCodes = new HashSet<>(disabledCodes);
            this.reEnabledCodes = new HashSet<>(reEnabledCodes);
        }

        RegionState copy() {
            return new RegionState(allDisabled, disabledCodes, reEnabledCodes);
        }

        void disable(Set<String> codes) {
            if (codes == null || codes.isEmpty()) {
                return;
            }
            if (codes.contains("*")) {
                allDisabled = true;
                disabledCodes.clear();
                reEnabledCodes.clear();
            } else {
                for (String c : codes) {
                    if (allDisabled) {
                        reEnabledCodes.remove(c);
                    } else {
                        disabledCodes.add(c);
                    }
                }
            }
        }

        void enable(Set<String> codes) {
            if (codes == null || codes.isEmpty()) {
                return;
            }
            if (codes.contains("*")) {
                allDisabled = false;
                disabledCodes.clear();
                reEnabledCodes.clear();
            } else {
                for (String c : codes) {
                    if (allDisabled) {
                        reEnabledCodes.add(c);
                    } else {
                        disabledCodes.remove(c);
                    }
                }
            }
        }

        boolean isSuppressed(String code) {
            if (code == null) {
                return allDisabled || !disabledCodes.isEmpty();
            }
            String lower = code.toLowerCase(Locale.ROOT);
            if (allDisabled) {
                return !reEnabledCodes.contains(lower);
            }
            return disabledCodes.contains(lower);
        }

        boolean isActive() {
            return allDisabled || !disabledCodes.isEmpty();
        }
    }

    /**
     * Index of parsed suppression directives for fast diagnostic filtering.
     */
    public static final class SuppressionIndex {
        private final Set<String> fileSuppressed = new HashSet<>();
        private final Map<Integer, Set<String>> lineSuppressed = new HashMap<>();
        private final Map<Integer, RegionState> lineRegions = new HashMap<>();
        private final List<Diagnostic> directiveDiagnostics = new ArrayList<>();
        private boolean hasAnySuppression = false;

        public boolean hasAnySuppression() {
            return hasAnySuppression;
        }

        public List<Diagnostic> getDirectiveDiagnostics() {
            return Collections.unmodifiableList(directiveDiagnostics);
        }

        public boolean isSuppressed(String code, int line) {
            if (!hasAnySuppression) {
                return false;
            }
            if (matches(fileSuppressed, code)) {
                return true;
            }
            Set<String> perLine = lineSuppressed.get(line);
            if (matches(perLine, code)) {
                return true;
            }
            RegionState region = lineRegions.get(line);
            if (region != null && region.isSuppressed(code)) {
                return true;
            }
            return false;
        }

        public boolean isSuppressed(Diagnostic d) {
            if (d == null || !hasAnySuppression) {
                return false;
            }
            if (!isSuppressibleSource(d.getSource())) {
                return false;
            }
            String code = null;
            if (d.getCode() != null) {
                if (d.getCode().isLeft()) {
                    code = d.getCode().getLeft();
                } else if (d.getCode().isRight()) {
                    code = String.valueOf(d.getCode().getRight());
                }
            }

            if (d.getRange() != null && d.getRange().getStart() != null) {
                int startLine = d.getRange().getStart().getLine();
                if (isSuppressed(code, startLine)) {
                    return true;
                }
                if (d.getRange().getEnd() != null) {
                    int endLine = d.getRange().getEnd().getLine();
                    if (endLine != startLine && isSuppressed(code, endLine)) {
                        return true;
                    }
                }
                return false;
            }
            return isSuppressed(code, 0);
        }

        private static boolean matches(Set<String> set, String code) {
            if (set == null || set.isEmpty()) {
                return false;
            }
            if (set.contains("*")) {
                return true;
            }
            return code != null && set.contains(code.toLowerCase(Locale.ROOT));
        }

        public static SuppressionIndex parse(String text) {
            if (text == null || text.isEmpty()) {
                return new SuppressionIndex();
            }
            if (!text.toLowerCase(Locale.ROOT).contains("@drl-lsp-lint:")) {
                return new SuppressionIndex();
            }
            CommonTokenStream stream = (CommonTokenStream) DRL10ParserHelper.createDrlParser(text).getTokenStream();
            stream.fill();
            return parse(stream.getTokens(), text);
        }

        public static SuppressionIndex parse(List<Token> tokens, String text) {
            SuppressionIndex index = new SuppressionIndex();
            if (text == null || text.isEmpty()) {
                return index;
            }
            if (!text.toLowerCase(Locale.ROOT).contains("@drl-lsp-lint:")) {
                return index;
            }
            if (tokens == null || tokens.isEmpty()) {
                CommonTokenStream stream = (CommonTokenStream) DRL10ParserHelper.createDrlParser(text).getTokenStream();
                stream.fill();
                tokens = stream.getTokens();
            }

            String sanitized = DRLLintHelper.sanitize(text);
            List<DRLLintHelper.RuleBlock> rules = DRLLintHelper.scanRuleBlocks(sanitized);
            int lineCount = text.split("\r?\n", -1).length;

            List<ParsedDirective> directives = new ArrayList<>();

            for (Token t : tokens) {
                if (!isComment(t)) {
                    continue;
                }
                String commentText = t.getText();
                if (commentText == null || !commentText.toLowerCase(Locale.ROOT).contains("@drl-lsp-lint:")) {
                    continue;
                }

                int tokenStartLine = t.getLine() - 1;
                int tokenStartCol = t.getCharPositionInLine();
                int tokenEndLine = tokenStartLine + countNewlines(commentText, 0, commentText.length());

                Matcher m = DIRECTIVE_PATTERN.matcher(commentText);
                while (m.find()) {
                    String action = m.group(1).toLowerCase(Locale.ROOT);
                    int prefixNewlines = countNewlines(commentText, 0, m.start());
                    int directiveLine = tokenStartLine + prefixNewlines;
                    int directiveCol;
                    if (prefixNewlines == 0) {
                        directiveCol = tokenStartCol + m.start();
                    } else {
                        int lastNl = commentText.lastIndexOf('\n', m.start() - 1);
                        directiveCol = m.start() - lastNl - 1;
                    }

                    if (!VALID_ACTIONS.contains(action)) {
                        Diagnostic diag = new Diagnostic();
                        diag.setSeverity(DiagnosticSeverity.Warning);
                        diag.setSource(DRLLintHelper.SOURCE);
                        diag.setMessage("Unknown directive action '@drl-lsp-lint:" + action + "'.");
                        diag.setRange(new Range(new Position(directiveLine, directiveCol),
                                new Position(directiveLine, directiveCol + m.group().length())));
                        index.directiveDiagnostics.add(diag);
                        continue;
                    }

                    int argStart = m.end();
                    Matcher nextM = DIRECTIVE_PATTERN.matcher(commentText);
                    int argEnd = commentText.length();
                    if (nextM.find(argStart)) {
                        argEnd = nextM.start();
                    }

                    String slice = commentText.substring(argStart, argEnd);
                    int endLimit = slice.length();
                    int idxBlock = slice.indexOf("*/");
                    if (idxBlock >= 0 && idxBlock < endLimit) {
                        endLimit = idxBlock;
                    }
                    int idxNl = slice.indexOf('\n');
                    if (idxNl >= 0 && idxNl < endLimit) {
                        endLimit = idxNl;
                    }
                    int idxCr = slice.indexOf('\r');
                    if (idxCr >= 0 && idxCr < endLimit) {
                        endLimit = idxCr;
                    }
                    slice = slice.substring(0, endLimit);

                    int idxDash = slice.indexOf("--");
                    String codePart = (idxDash >= 0) ? slice.substring(0, idxDash) : slice;

                    Matcher codeMatcher = Pattern.compile("[a-zA-Z0-9_-]+").matcher(codePart);
                    Set<String> validCodes = new HashSet<>();
                    boolean foundAnyCode = false;
                    while (codeMatcher.find()) {
                        foundAnyCode = true;
                        String code = codeMatcher.group().toLowerCase(Locale.ROOT);
                        if (ALL_KNOWN_CODES.contains(code)) {
                            validCodes.add(code);
                        } else {
                            int codeOffset = argStart + codeMatcher.start();
                            int codeLineOffset = countNewlines(commentText, 0, codeOffset);
                            int codeLine = tokenStartLine + codeLineOffset;
                            int codeCol;
                            if (codeLineOffset == 0) {
                                codeCol = tokenStartCol + codeOffset;
                            } else {
                                int lastNl = commentText.lastIndexOf('\n', codeOffset - 1);
                                codeCol = codeOffset - lastNl - 1;
                            }
                            Diagnostic diag = new Diagnostic();
                            diag.setSeverity(DiagnosticSeverity.Warning);
                            diag.setSource(DRLLintHelper.SOURCE);
                            diag.setMessage("Unknown linter code '" + code + "' in @drl-lsp-lint directive.");
                            diag.setRange(new Range(new Position(codeLine, codeCol),
                                    new Position(codeLine, codeCol + code.length())));
                            index.directiveDiagnostics.add(diag);
                        }
                    }

                    Set<String> codes = foundAnyCode ? validCodes : Set.of("*");
                    int targetNextLine = tokenEndLine + 1;
                    directives.add(new ParsedDirective(action, codes, directiveLine, targetNextLine));
                }
            }

            directives.sort(java.util.Comparator.comparingInt(d -> d.directiveLine));

            Map<Integer, List<ParsedDirective>> directivesByLine = new HashMap<>();
            for (ParsedDirective d : directives) {
                directivesByLine.computeIfAbsent(d.directiveLine, k -> new ArrayList<>()).add(d);
                switch (d.action) {
                    case "disable-file" -> {
                        if (!d.codes.isEmpty()) {
                            index.fileSuppressed.addAll(d.codes);
                            index.hasAnySuppression = true;
                        }
                    }
                    case "disable-line" -> {
                        if (!d.codes.isEmpty()) {
                            index.lineSuppressed.computeIfAbsent(d.directiveLine, k -> new HashSet<>()).addAll(d.codes);
                            index.hasAnySuppression = true;
                        }
                    }
                    case "disable-next-line" -> {
                        if (!d.codes.isEmpty() && d.targetNextLine < lineCount) {
                            index.lineSuppressed.computeIfAbsent(d.targetNextLine, k -> new HashSet<>()).addAll(d.codes);
                            index.hasAnySuppression = true;
                        }
                    }
                    case "disable-rule" -> {
                        if (!d.codes.isEmpty()) {
                            DRLLintHelper.RuleBlock targetRule = findTargetRule(rules, d.directiveLine);
                            if (targetRule != null) {
                                int start = targetRule.startLine;
                                int end = targetRule.isClosed()
                                        ? targetRule.endLine
                                        : (findNextRuleStart(rules, targetRule) - 1);
                                if (end < start || end >= lineCount) {
                                    end = lineCount - 1;
                                }
                                for (int r = start; r <= end && r < lineCount; r++) {
                                    index.lineSuppressed.computeIfAbsent(r, k -> new HashSet<>()).addAll(d.codes);
                                }
                                index.hasAnySuppression = true;
                            }
                        }
                    }
                }
            }

            RegionState currentRegion = new RegionState();
            for (int line = 0; line < lineCount; line++) {
                List<ParsedDirective> lineDirs = directivesByLine.get(line);
                if (lineDirs != null) {
                    for (ParsedDirective d : lineDirs) {
                        if ("disable".equals(d.action) && !d.codes.isEmpty()) {
                            currentRegion.disable(d.codes);
                        } else if ("enable".equals(d.action) && !d.codes.isEmpty()) {
                            currentRegion.enable(d.codes);
                        }
                    }
                }
                if (currentRegion.isActive()) {
                    index.lineRegions.put(line, currentRegion.copy());
                    index.hasAnySuppression = true;
                }
            }

            return index;
        }

        private static DRLLintHelper.RuleBlock findTargetRule(List<DRLLintHelper.RuleBlock> rules, int directiveLine) {
            for (int i = 0; i < rules.size(); i++) {
                DRLLintHelper.RuleBlock r = rules.get(i);
                int rEnd = r.isClosed() ? r.endLine : (findNextRuleStart(rules, r) - 1);
                if (directiveLine >= r.startLine && directiveLine <= rEnd) {
                    return r;
                }
                if (directiveLine < r.startLine) {
                    int prevEnd = i > 0
                            ? (rules.get(i - 1).isClosed() ? rules.get(i - 1).endLine : findNextRuleStart(rules, rules.get(i - 1)) - 1)
                            : -1;
                    if (directiveLine > prevEnd) {
                        return r;
                    }
                }
            }
            return null;
        }

        private static int findNextRuleStart(List<DRLLintHelper.RuleBlock> rules, DRLLintHelper.RuleBlock current) {
            int idx = rules.indexOf(current);
            if (idx >= 0 && idx + 1 < rules.size()) {
                return rules.get(idx + 1).startLine;
            }
            return Integer.MAX_VALUE;
        }
    }
}
