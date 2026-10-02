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

package org.drools.lsp.server;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.drools.completion.DRLDocumentSymbolHelper;
import org.drools.completion.DRLHoverHelper;
import org.drools.completion.DRLInlayHintHelper;
import org.drools.completion.LhsBindingResolver;
import org.eclipse.lsp4j.DocumentSymbol;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.SymbolKind;

/**
 * Turns an engine message into an editor range. The engine's own line and
 * column often point at the enclosing pattern or are missing altogether (0 for
 * expression-analysis failures), while the message text names the offending
 * identifier or expression, so the text is searched first.
 */
final class BuildMessageRanges {

    static final int LINE_WINDOW = 20;

    private static final Pattern QUOTED_TOKEN = Pattern.compile("'([^']+)'");
    private static final List<Pattern> UNQUOTED_EXPRESSION_PATTERNS = List.of(
            Pattern.compile("(?i)Unable to Analy[sz]e Expression\\s+(.+?):\\s"),
            Pattern.compile("\\[Near\\s*:\\s*\\{\\.\\.\\.(.+?)\\.\\.\\.+\\}\\]", Pattern.DOTALL));
    private static final Pattern RULE_NAME = Pattern.compile("Rule name='([^']+)'");

    private BuildMessageRanges() {
    }

    static Range rangeFor(String message, int line, int column, String text) {
        if (text == null) {
            text = "";
        }
        Range smart = trySmartRange(message, line, text);
        if (smart != null) {
            return smart;
        }
        if (line <= 0) {
            Range header = ruleHeaderRange(message, text);
            return header != null ? header : new Range(new Position(0, 0), new Position(0, 1));
        }
        int l = Math.max(0, line - 1);
        int c = Math.max(0, column - 1);
        int maxLine = Math.max(0, countLines(text) - 1);
        if (l > maxLine) {
            l = maxLine;
            c = 0;
        }
        int maxCol = lineLength(text, l);
        if (c > maxCol) {
            c = maxCol;
        }
        return new Range(new Position(l, c), new Position(l, Math.min(c + 1, maxCol)));
    }

    static Range trySmartRange(String message, int line, String text) {
        if (message == null || text == null || text.isEmpty()) {
            return null;
        }
        List<String> tokens = extractSearchTokens(message);
        if (tokens.isEmpty()) {
            return null;
        }
        boolean hasReportedLine = line > 0;
        int reportedLine = Math.max(0, line - 1);
        int searchStart = hasReportedLine ? lineStartOffset(text, reportedLine) : 0;
        int searchEnd = hasReportedLine ? lineStartOffset(text, reportedLine + LINE_WINDOW + 1) : text.length();
        String masked = LhsBindingResolver.maskCommentsAndStrings(text);

        for (String token : tokens) {
            int hit = findTokenInWindow(text, masked, token, searchStart, searchEnd);
            String matched = token;
            if (hit < 0 && token.startsWith("$") && token.length() > 1) {
                matched = token.substring(1);
                hit = findTokenInWindow(text, masked, matched, searchStart, searchEnd);
            }
            if (hit < 0) {
                continue;
            }
            int tokenEnd = hit + matched.length();
            int endOffset = isInsidePattern(masked, hit) ? Math.max(tokenEnd, endOfClause(masked, tokenEnd)) : tokenEnd;
            return new Range(DRLInlayHintHelper.offsetToPosition(text, hit),
                    DRLInlayHintHelper.offsetToPosition(text, endOffset));
        }
        return null;
    }

    static Range ruleHeaderRange(String message, String text) {
        if (message == null || text == null) {
            return null;
        }
        Matcher name = RULE_NAME.matcher(message);
        if (!name.find()) {
            return null;
        }
        for (DocumentSymbol symbol : DRLDocumentSymbolHelper.symbols(text)) {
            if (symbol.getKind() == SymbolKind.Method && name.group(1).equals(symbol.getName())) {
                return headerOf(symbol);
            }
        }
        return null;
    }

    static Range firstRuleRange(String text) {
        for (DocumentSymbol symbol : DRLDocumentSymbolHelper.symbols(text)) {
            if (symbol.getKind() == SymbolKind.Method) {
                return headerOf(symbol);
            }
        }
        return new Range(new Position(0, 0), new Position(0, 1));
    }

    private static Range headerOf(DocumentSymbol symbol) {
        return new Range(symbol.getRange().getStart(), symbol.getSelectionRange().getEnd());
    }

    /** Tokens worth searching the document for, longest first; the rule's own name is left to the header fallback. */
    static List<String> extractSearchTokens(String message) {
        List<String> out = new ArrayList<>();
        Matcher named = RULE_NAME.matcher(message);
        String ruleName = named.find() ? named.group(1) : null;
        Matcher quoted = QUOTED_TOKEN.matcher(message);
        while (quoted.find()) {
            String g = quoted.group(1);
            if (g != null && !g.isBlank() && !g.equals(ruleName)) {
                out.add(g);
            }
        }
        for (Pattern pattern : UNQUOTED_EXPRESSION_PATTERNS) {
            Matcher matcher = pattern.matcher(message);
            while (matcher.find()) {
                String candidate = matcher.group(1);
                if (candidate == null) {
                    continue;
                }
                String trimmed = candidate.trim();
                if (!trimmed.isEmpty() && !out.contains(trimmed)) {
                    out.add(trimmed);
                }
            }
        }
        if (out.size() > 1) {
            out.sort((a, b) -> Integer.compare(b.length(), a.length()));
        }
        return out;
    }

    private static boolean isInsidePattern(String text, int offset) {
        int depth = 0;
        for (int i = offset - 1; i >= 0; i--) {
            char c = text.charAt(i);
            if (c == ')') {
                depth++;
            } else if (c == '(') {
                if (depth == 0) {
                    return true;
                }
                depth--;
            }
        }
        return false;
    }

    /**
     * The first occurrence of {@code token} that starts in code. The original text is searched,
     * because the token may contain a string literal that the masked text blanks out.
     */
    private static int findTokenInWindow(String text, String masked, String token, int searchStart, int searchEnd) {
        int end = Math.min(searchEnd, text.length());
        int from = searchStart;
        while (from < end) {
            int hit = text.indexOf(token, from);
            if (hit < 0 || hit >= end) {
                return -1;
            }
            if (masked.charAt(hit) == text.charAt(hit)) {
                return hit;
            }
            from = hit + 1;
        }
        return -1;
    }

    private static int endOfClause(String text, int startOffset) {
        int depth = 0;
        int n = text.length();
        int i = startOffset;
        while (i < n) {
            char c = text.charAt(i);
            if (c == '(') {
                depth++;
            } else if (c == ')') {
                if (depth == 0) {
                    break;
                }
                depth--;
            } else if ((c == ',' || c == '\n' || c == '\r') && depth == 0) {
                break;
            }
            i++;
        }
        while (i > startOffset && Character.isWhitespace(text.charAt(i - 1))) {
            i--;
        }
        return i;
    }

    private static int lineStartOffset(String text, int line) {
        return DRLHoverHelper.positionToOffset(text, new Position(line, 0));
    }

    private static int countLines(String text) {
        return DRLInlayHintHelper.offsetToPosition(text, text.length()).getLine() + 1;
    }

    private static int lineLength(String text, int lineIdx) {
        int start = lineStartOffset(text, lineIdx);
        int end = text.indexOf('\n', start);
        if (end < 0) {
            end = text.length();
        }
        if (end > start && text.charAt(end - 1) == '\r') {
            end--;
        }
        return Math.max(0, end - start);
    }
}
