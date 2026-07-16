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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extracts Javadoc-style {@code /** ... *}{@code /} block comments that
 * immediately precede top-level Drools declarations and maps each comment to
 * the declaration's name.
 *
 * <p>Supported declarations:
 * <ul>
 *   <li>{@code declare X}  (and {@code declare X extends Y})</li>
 *   <li>{@code function Type name(...)}</li>
 *   <li>{@code query "Name"} or {@code query name}</li>
 *   <li>{@code global Type name}</li>
 * </ul>
 *
 * <p>Doc bodies have their leading {@code *} markers and surrounding
 * whitespace stripped, then trimmed. The result is suitable for rendering as
 * markdown in a hover tooltip.
 *
 * <p>Doc comments NOT immediately preceding a supported declaration are
 * ignored. Whitespace and line-comments are allowed between the doc block
 * and the declaration, but other block comments or code are not.
 */
public final class DRLDocCommentParser {

    /**
     * Matches a single Javadoc-style block: opens with exactly two stars
     * ({@code /} then {@code *}{@code *}) followed by a non-star character.
     * Lazy across the body so we stop at the first {@code *}{@code /}; the
     * "is it followed by a declaration?" check is done separately in
     * {@link #parseDocs(String)}.
     */
    private static final Pattern DOC_BLOCK =
            Pattern.compile("(?s)/\\*\\*(?!\\*)(.*?)\\*/");

    /**
     * Anchored at the start of input — matches whitespace/{@code //} comments
     * followed by one of the supported declaration headers.
     *
     * <p>Capture groups:
     * <ol>
     *   <li>declare name</li>
     *   <li>function name</li>
     *   <li>query name (quoted)</li>
     *   <li>query name (bare)</li>
     *   <li>global name</li>
     * </ol>
     */
    private static final Pattern DECL_AFTER_DOC = Pattern.compile(
            "(?s)\\A(?:\\s*//[^\\n]*\\n|\\s)*"
            + "(?:"
            +   "declare\\s+(?:enum\\s+)?(\\w+)"
            +   "|function\\s+\\S+\\s+(\\w+)\\s*\\("
            +   "|query\\s+(?:\"([^\"]+)\"|(\\w+))"
            +   "|global\\s+\\S+\\s+(\\w+)"
            + ")");

    private DRLDocCommentParser() {
    }

    /**
     * Parses {@code text} and returns a {@code declarationName -> docBody}
     * map. Returns an empty map if {@code text} is {@code null} or empty, or
     * contains no doc-attached declarations.
     *
     * <p>A doc block is only associated with a declaration when the
     * declaration appears immediately after the {@code *}{@code /} (allowing
     * only whitespace and {@code //} line comments between).
     */
    public static Map<String, String> parseDocs(String text) {
        if (text == null || text.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, String> docs = new HashMap<>();
        Matcher docMatcher = DOC_BLOCK.matcher(text);
        while (docMatcher.find()) {
            String body = stripDocStars(docMatcher.group(1));
            if (body.isEmpty()) {
                continue;
            }
            Matcher declMatcher = DECL_AFTER_DOC.matcher(text.substring(docMatcher.end()));
            if (!declMatcher.find()) {
                continue;
            }
            String name = firstNonNull(declMatcher.group(1), declMatcher.group(2),
                                       declMatcher.group(3), declMatcher.group(4),
                                       declMatcher.group(5));
            if (name != null && !name.isEmpty()) {
                docs.putIfAbsent(name, body);
            }
        }
        return docs;
    }

    /**
     * Returns the doc body for a named declaration in {@code text}, or
     * {@code null} if there isn't one. Convenience wrapper around
     * {@link #parseDocs(String)} for callers that only need one lookup.
     */
    public static String docFor(String text, String name) {
        if (text == null || name == null || name.isEmpty()) {
            return null;
        }
        return parseDocs(text).get(name);
    }

    /**
     * Strips the leading {@code *} on each line of a doc body, trims trailing
     * whitespace, and collapses to a tidy markdown-ready string.
     */
    static String stripDocStars(String raw) {
        if (raw == null) {
            return "";
        }
        String[] lines = raw.split("\\r?\\n", -1);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            // Strip a single leading "*" with optional surrounding whitespace.
            String stripped = lines[i].replaceFirst("^\\s*\\*\\s?", "");
            // Right-trim only; preserve leading indentation that survived the * strip.
            int end = stripped.length();
            while (end > 0 && Character.isWhitespace(stripped.charAt(end - 1))) {
                end--;
            }
            sb.append(stripped, 0, end);
            if (i < lines.length - 1) {
                sb.append('\n');
            }
        }
        return sb.toString().strip();
    }

    private static String firstNonNull(String... candidates) {
        for (String c : candidates) {
            if (c != null) {
                return c;
            }
        }
        return null;
    }
}
