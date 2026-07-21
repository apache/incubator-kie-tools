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

import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Expands Javadoc-style inline tags in a doc-comment body into markdown
 * suitable for an LSP hover {@code MarkupContent}.
 *
 * <p>Supported inline tags:
 * <ul>
 *   <li>{@code {@code text}} → {@code `text`} (inline code)</li>
 *   <li>{@code {@literal text}} → the text with markdown formatting
 *       characters escaped</li>
 *   <li>{@code {@link Name}} / {@code {@link Name Label}} → a markdown link
 *       when {@code Name} resolves via the supplied {@code linkTargets},
 *       otherwise inline code; {@code {@linkplain ...}} is identical except
 *       the unresolved fallback is plain text.</li>
 * </ul>
 *
 * Anything else inside {@code {@...}} is left untouched.
 */
public final class DRLDocFormatter {

    /** {@code {@code text}} — captures the inner text. */
    private static final Pattern INLINE_CODE =
            Pattern.compile("\\{@code\\s+([^}]*)\\}");

    /** {@code {@literal text}} — captures the inner text. */
    private static final Pattern INLINE_LITERAL =
            Pattern.compile("\\{@literal\\s+([^}]*)\\}");

    /**
     * {@code {@link Name}}, {@code {@link Name Label text}}, or
     * {@code {@linkplain ...}}: group(1) is {@code "plain"} for
     * {@code @linkplain}, group(2) the reference (may include
     * {@code #member}), group(3) the optional label.
     */
    private static final Pattern INLINE_LINK = Pattern.compile(
            "\\{@link(plain)?\\s+([^}\\s]+)(?:\\s+([^}]+))?\\}");

    private DRLDocFormatter() {
    }

    /**
     * Formats {@code body}, expanding all recognised inline tags.
     *
     * @param body        the doc body (post-{@code *}-stripping), or null
     * @param linkTargets {@code typeName -> markdown link href}; references
     *                    not in the map render as fallback text (code for
     *                    {@code @link}, plain for {@code @linkplain}). May be
     *                    null or empty.
     */
    public static String format(String body, Map<String, String> linkTargets) {
        if (body == null || body.isEmpty()) {
            return body;
        }
        String out = replaceWith(body, INLINE_CODE, m -> "`" + m.group(1) + "`");
        out = replaceWith(out, INLINE_LITERAL, m -> escapeMarkdown(m.group(1)));
        out = replaceWith(out, INLINE_LINK, m -> renderLink(m, linkTargets));
        return out;
    }

    private static String renderLink(Matcher m, Map<String, String> linkTargets) {
        boolean plain = m.group(1) != null;
        String ref = m.group(2);
        String label = m.group(3);
        if (label == null || label.isBlank()) {
            label = ref;
        }

        // Strip "#member" for the lookup; the full reference stays visible.
        String typeKey = ref;
        int hash = typeKey.indexOf('#');
        if (hash >= 0) {
            typeKey = typeKey.substring(0, hash);
        }

        String href = linkTargets == null ? null : linkTargets.get(typeKey);
        if (href != null && !href.isEmpty()) {
            return "[" + label + "](" + href + ")";
        }
        return plain ? label : ("`" + label + "`");
    }

    /**
     * Escapes characters that would otherwise be interpreted as markdown
     * formatting, matching Javadoc's {@code @literal} semantics.
     */
    private static String escapeMarkdown(String s) {
        if (s == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder(s.length() + 8);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\' || c == '`' || c == '*' || c == '_' || c == '{'
                    || c == '}' || c == '[' || c == ']' || c == '<' || c == '>'
                    || c == '#' || c == '+' || c == '-' || c == '.' || c == '!') {
                sb.append('\\');
            }
            sb.append(c);
        }
        return sb.toString();
    }

    /**
     * Replaces every match of {@code p} in {@code input} with the result of
     * applying {@code mapper} to that match, with full literal-replacement
     * semantics so dollar signs and backslashes in the replacement don't
     * surprise the caller.
     */
    private static String replaceWith(String input, Pattern p,
                                      Function<Matcher, String> mapper) {
        Matcher m = p.matcher(input);
        StringBuilder sb = new StringBuilder(input.length());
        int last = 0;
        while (m.find()) {
            sb.append(input, last, m.start());
            sb.append(mapper.apply(m));
            last = m.end();
        }
        sb.append(input, last, input.length());
        return sb.toString();
    }
}
