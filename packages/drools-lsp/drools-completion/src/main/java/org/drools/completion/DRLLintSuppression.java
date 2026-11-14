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

import org.eclipse.lsp4j.Diagnostic;

/**
 * Parses in-source linter suppression directives under the {@code @drl-lsp-lint}
 * namespace and filters out suppressed diagnostics.
 *
 * <p>Supported directives:
 * <ul>
 *   <li>{@code @drl-lsp-lint:disable-file [code, ...]} &mdash; suppresses matching codes (or all codes) for the entire file</li>
 *   <li>{@code @drl-lsp-lint:disable-line [code, ...]} &mdash; suppresses matching codes on the current line</li>
 *   <li>{@code @drl-lsp-lint:disable-next-line [code, ...]} &mdash; suppresses matching codes on the line immediately following</li>
 *   <li>{@code @drl-lsp-lint:disable-rule [code, ...]} &mdash; suppresses matching codes for the entire enclosing or following rule</li>
 *   <li>{@code @drl-lsp-lint:disable [code, ...]} &mdash; starts a suppression region from this line downwards</li>
 *   <li>{@code @drl-lsp-lint:enable [code, ...]} &mdash; ends a suppression region from this line downwards</li>
 * </ul>
 *
 * <p>Directives apply strictly to {@code drools-lint} and {@code drools-type}
 * diagnostics. Parser/syntax errors ({@code drools-drl-parser}) and compiler
 * diagnostics ({@code drools}) are never suppressed.
 */
public final class DRLLintSuppression {

    private static final Pattern DIRECTIVE_PATTERN = Pattern.compile(
            "(?i)@drl-lsp-lint:(disable-file|disable-next-line|disable-line|disable-rule|disable|enable)\\b");

    private static final Pattern RULE_KEYWORD =
            Pattern.compile("^\\s*(rule|query)\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern DECLARE_KEYWORD =
            Pattern.compile("^\\s*declare\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern END_AT_START =
            Pattern.compile("^end\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern END_AT_END =
            Pattern.compile("(?:^|\\s)end$", Pattern.CASE_INSENSITIVE);

    public static final Set<String> ALL_LINT_CODES = Set.of(
            DRLLintHelper.CODE_MISSING_END,
            DRLLintHelper.CODE_MISSING_SEPARATOR,
            DRLLintHelper.CODE_MISSING_SEMICOLON,
            DRLLintHelper.CODE_UNBALANCED_PARENS,
            DRLLintHelper.CODE_MVEL_PROPERTY_ACCESS,
            DRLLintHelper.CODE_UNKNOWN_TYPE,
            DRLLintHelper.CODE_UNKNOWN_MEMBER
    );

    private DRLLintSuppression() {
    }

    /**
     * Filters {@code diagnostics} against any {@code @drl-lsp-lint} directives
     * found in {@code text}. Diagnostics with sources other than {@code drools-lint}
     * or {@code drools-type} are never dropped.
     */
    public static List<Diagnostic> filter(List<Diagnostic> diagnostics, String text) {
        if (diagnostics == null || diagnostics.isEmpty() || text == null || text.isEmpty()) {
            return diagnostics;
        }
        SuppressionIndex index = SuppressionIndex.parse(text);
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
        return "drools-lint".equals(source) || "drools-type".equals(source);
    }

    /**
     * Replaces string literal contents with spaces so directives inside quotes
     * (e.g. {@code String s = "// @drl-lsp-lint:disable"}) are not treated as directives.
     * Newlines are preserved to keep original line numbers intact.
     */
    static String blankStrings(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        char[] chars = text.toCharArray();
        boolean inDouble = false;
        boolean inSingle = false;
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c == '\n' || c == '\r') {
                inDouble = false;
                inSingle = false;
                continue;
            }
            if (inDouble) {
                if (c == '\\' && i + 1 < chars.length) {
                    chars[i] = ' ';
                    chars[i + 1] = ' ';
                    i++;
                } else if (c == '"') {
                    inDouble = false;
                } else {
                    chars[i] = ' ';
                }
                continue;
            }
            if (inSingle) {
                if (c == '\\' && i + 1 < chars.length) {
                    chars[i] = ' ';
                    chars[i + 1] = ' ';
                    i++;
                } else if (c == '\'') {
                    inSingle = false;
                } else {
                    chars[i] = ' ';
                }
                continue;
            }
            if (c == '"') {
                inDouble = true;
            } else if (c == '\'') {
                inSingle = true;
            }
        }
        return new String(chars);
    }

    /**
     * Index of parsed suppression directives for fast diagnostic filtering.
     */
    public static final class SuppressionIndex {
        private final Set<String> fileSuppressed = new HashSet<>();
        private final Map<Integer, Set<String>> lineSuppressed = new HashMap<>();
        private boolean hasAnySuppression = false;

        public boolean hasAnySuppression() {
            return hasAnySuppression;
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

            if (matches(fileSuppressed, code)) {
                return true;
            }

            if (d.getRange() != null && d.getRange().getStart() != null) {
                int startLine = d.getRange().getStart().getLine();
                if (matches(lineSuppressed.get(startLine), code)) {
                    return true;
                }
                if (d.getRange().getEnd() != null) {
                    int endLine = d.getRange().getEnd().getLine();
                    if (endLine != startLine && matches(lineSuppressed.get(endLine), code)) {
                        return true;
                    }
                }
            }
            return false;
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
            SuppressionIndex index = new SuppressionIndex();
            if (text == null || text.isEmpty()) {
                return index;
            }

            // Quick check to bypass regex and line parsing if no directive namespace is present
            if (!text.toLowerCase(Locale.ROOT).contains("@drl-lsp-lint:")) {
                return index;
            }

            String codeText = DRLLintHelper.sanitize(text);
            String[] codeLines = codeText.split("\r?\n", -1);
            List<int[]> rules = findRuleBoundaries(codeLines);

            String stringBlanked = blankStrings(text);
            String[] lines = stringBlanked.split("\r?\n", -1);

            Set<String> activeRegion = new HashSet<>();

            for (int i = 0; i < lines.length; i++) {
                String line = lines[i];
                Matcher m = DIRECTIVE_PATTERN.matcher(line);

                while (m.find()) {
                    index.hasAnySuppression = true;
                    String action = m.group(1).toLowerCase(Locale.ROOT);
                    int argStart = m.end();

                    int nextDirective = findNextDirective(line, argStart);
                    String rawArgs = nextDirective >= 0
                            ? line.substring(argStart, nextDirective)
                            : line.substring(argStart);

                    Set<String> codes = parseCodes(rawArgs);

                    switch (action) {
                        case "disable-file" -> index.fileSuppressed.addAll(codes);
                        case "disable-line" -> index.lineSuppressed
                                .computeIfAbsent(i, k -> new HashSet<>())
                                .addAll(codes);
                        case "disable-next-line" -> {
                            if (i + 1 < lines.length) {
                                index.lineSuppressed
                                        .computeIfAbsent(i + 1, k -> new HashSet<>())
                                        .addAll(codes);
                            }
                        }
                        case "disable-rule" -> {
                            int[] targetRule = findTargetRule(rules, i);
                            if (targetRule != null) {
                                for (int r = targetRule[0]; r <= targetRule[1] && r < lines.length; r++) {
                                    index.lineSuppressed
                                            .computeIfAbsent(r, k -> new HashSet<>())
                                            .addAll(codes);
                                }
                            }
                        }
                        case "disable" -> {
                            if (codes.contains("*")) {
                                activeRegion.clear();
                                activeRegion.add("*");
                            } else {
                                activeRegion.addAll(codes);
                            }
                        }
                        case "enable" -> {
                            if (codes.contains("*")) {
                                activeRegion.clear();
                            } else {
                                if (activeRegion.contains("*")) {
                                    activeRegion.remove("*");
                                    activeRegion.addAll(ALL_LINT_CODES);
                                }
                                activeRegion.removeAll(codes);
                            }
                        }
                    }
                }

                if (!activeRegion.isEmpty()) {
                    index.lineSuppressed
                            .computeIfAbsent(i, k -> new HashSet<>())
                            .addAll(activeRegion);
                }
            }

            return index;
        }

        private static int findNextDirective(String line, int fromIndex) {
            Matcher m = DIRECTIVE_PATTERN.matcher(line);
            if (m.find(fromIndex)) {
                return m.start();
            }
            return -1;
        }

        private static Set<String> parseCodes(String rawArgs) {
            if (rawArgs == null) {
                return Set.of("*");
            }
            int blockCommentEnd = rawArgs.indexOf("*/");
            if (blockCommentEnd >= 0) {
                rawArgs = rawArgs.substring(0, blockCommentEnd);
            }
            int commentSep = rawArgs.indexOf("--");
            if (commentSep >= 0) {
                rawArgs = rawArgs.substring(0, commentSep);
            }
            String trimmed = rawArgs.trim();
            if (trimmed.isEmpty()) {
                return Set.of("*");
            }
            Set<String> set = new HashSet<>();
            String[] tokens = trimmed.split("[,\\s]+");
            for (String t : tokens) {
                String token = t.trim().toLowerCase(Locale.ROOT);
                if (!token.isEmpty()) {
                    set.add(token);
                }
            }
            return set.isEmpty() ? Set.of("*") : set;
        }

        private static List<int[]> findRuleBoundaries(String[] codeLines) {
            List<int[]> rules = new ArrayList<>();
            boolean inDeclare = false;
            int currentRuleStart = -1;

            for (int i = 0; i < codeLines.length; i++) {
                String trimmed = codeLines[i].trim();
                if (trimmed.isEmpty()) {
                    continue;
                }
                if (RULE_KEYWORD.matcher(trimmed).find()) {
                    inDeclare = false;
                    currentRuleStart = i;
                } else if (DECLARE_KEYWORD.matcher(trimmed).find()) {
                    inDeclare = true;
                }

                boolean closes = END_AT_START.matcher(trimmed).find()
                        || END_AT_END.matcher(trimmed).find();
                if (closes) {
                    if (inDeclare) {
                        inDeclare = false;
                    } else if (currentRuleStart >= 0) {
                        rules.add(new int[] {currentRuleStart, i});
                        currentRuleStart = -1;
                    }
                }
            }
            if (currentRuleStart >= 0) {
                rules.add(new int[] {currentRuleStart, codeLines.length - 1});
            }
            return rules;
        }

        private static int[] findTargetRule(List<int[]> rules, int directiveLine) {
            for (int idx = 0; idx < rules.size(); idx++) {
                int[] r = rules.get(idx);
                if (directiveLine >= r[0] && directiveLine <= r[1]) {
                    return r;
                }
                if (directiveLine < r[0]) {
                    int prevEnd = idx > 0 ? rules.get(idx - 1)[1] : -1;
                    if (directiveLine > prevEnd) {
                        return r;
                    }
                }
            }
            return null;
        }
    }
}
