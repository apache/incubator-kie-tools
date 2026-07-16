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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Resolves LHS variable bindings in DRL rule {@code when} sections to their
 * (simple) type names. Binding shapes handled:
 *
 * <ul>
 *   <li>Outer pattern bindings — {@code $p : Person(...)} → {@code p -> Person}.</li>
 *   <li>Field bindings — {@code TypeName($n : fieldName)} → {@code n -> } the
 *       field's declared type, looked up (with inheritance) in the supplied
 *       type index.</li>
 *   <li>Nested-path bindings — {@code $name : Person.name} → the tail field's type.</li>
 *   <li>Accumulate-result bindings — {@code $count : count()} → the function's
 *       result type, from {@link AccumulateFunctionTypes}.</li>
 * </ul>
 *
 * <p>{@link #collect} scans one {@code when} section and returns both the
 * name → type map and, for each <em>implicit</em> binding, a {@link Decl} with
 * the offset where an inlay hint belongs. The whole-text {@link #resolve}
 * methods merge {@code collect(...).types} across rules — used by completion
 * and hover; the inlay-hint helper consumes the full {@link Bindings}. All
 * therefore resolve bindings the same way. Resolution is pure regex over the
 * text, so it adds no per-keystroke cost beyond the on-demand calls.
 */
public final class LhsBindingResolver {

    private static final Pattern RULE_WHEN = Pattern.compile(
            "(?s)\\brule\\b.*?\\bwhen\\b(.*?)\\bthen\\b");
    private static final Pattern RULE_BLOCK = Pattern.compile(
            "(?s)\\brule\\b.*?\\bwhen\\b(.*?)\\bthen\\b.*?\\bend\\b");

    /**
     * Pattern head: an optional {@code $name :} binding prefix, then the pattern
     * type, then {@code (}. group(1) is the binding name (or {@code null}); group(2)
     * the type, which may be fully qualified ({@code org.example.Person}) — the
     * leading package segments are stripped by {@link #simpleName}. The final
     * (simple-name) segment must start uppercase, which keeps this from matching
     * lowercase field accessors/method calls; DRL patterns always have parens, so
     * the trailing {@code (} is required.
     */
    private static final Pattern PATTERN_START = Pattern.compile(
            "(?:\\$(\\w+)\\s*:\\s*)?\\b((?:\\w+\\.)*[A-Z]\\w*)\\s*\\(");

    private static final Pattern FIELD_BINDING = Pattern.compile(
            "\\$(\\w+)\\s*:\\s*([a-z]\\w*)(?![\\w.(])");

    /**
     * Field binding to a nested path, e.g. {@code $name : Person.name}. group(1)
     * is the binding name; group(2) the dotted path, resolved by walking the
     * type index in {@link #resolvePath}.
     */
    private static final Pattern NESTED_FIELD_BINDING = Pattern.compile(
            "\\$(\\w+)\\s*:\\s*([a-z]\\w*(?:\\.[a-z]\\w*)+)");

    /**
     * Accumulate result binding: {@code $count : count()}. group(1) is the binding
     * name, group(2) the function name (resolved only when it's a known accumulate
     * function in the injected map).
     */
    private static final Pattern ACCUM_RESULT_BINDING = Pattern.compile(
            "\\$(\\w+)\\s*:\\s*(\\w+)\\s*\\(");

    /**
     * Narrow fallback for field bindings on JDK types not in the declared index —
     * the well-known {@code Number} accessor methods used as Drools properties
     * (e.g. {@code $count : intValue} on a {@code Number(...)} pattern).
     */
    private static final Map<String, String> JDK_ACCESSOR_TYPES = Map.of(
            "intValue", "int",
            "longValue", "long",
            "doubleValue", "double",
            "floatValue", "float",
            "shortValue", "short",
            "byteValue", "byte");

    private LhsBindingResolver() {
    }

    /**
     * Bindings collected from a single {@code when} section: the name → simple
     * type map (every binding, including outer-pattern ones) plus, for each
     * <em>implicit</em> binding (field / nested-path / JDK-accessor / accumulate
     * result), a {@link Decl} marking where an inlay hint should sit. Outer
     * pattern bindings ({@code $p : Patient(...)}) appear in {@link #types} but
     * get no {@code Decl} — their type is already explicit at the site.
     */
    public static final class Bindings {
        public final Map<String, String> types;
        public final List<Decl> declarations;

        Bindings(Map<String, String> types, List<Decl> declarations) {
            this.types = types;
            this.declarations = declarations;
        }
    }

    /**
     * An implicit binding declaration: its name (without {@code $}), resolved
     * simple type, and {@code hintOffset} — the character offset, relative to the
     * analyzed {@code when} section, where a {@code ": Type"} hint belongs (right
     * after the field/path name, or after an accumulate function's close paren).
     */
    public static final class Decl {
        public final String name;
        public final String type;
        public final int hintOffset;

        Decl(String name, String type, int hintOffset) {
            this.name = name;
            this.type = type;
            this.hintOffset = hintOffset;
        }
    }

    /**
     * Returns a map of binding name (without the leading {@code $}) to the simple
     * type name it resolves to, across all rule {@code when} sections in
     * {@code text}. Field bindings are resolved via {@code typesByName}.
     */
    public static Map<String, String> resolve(String text,
                                              Map<String, DeclaredType> typesByName) {
        return resolve(text, typesByName, AccumulateFunctionTypes.get());
    }

    /**
     * As {@link #resolve(String, Map)}, plus accumulate result bindings.
     */
    public static Map<String, String> resolve(String text,
                                              Map<String, DeclaredType> typesByName,
                                              Map<String, String> accumResultTypes) {
        Map<String, String> bindings = new HashMap<>();
        if (text == null || text.isEmpty()) {
            return bindings;
        }
        Matcher rule = RULE_WHEN.matcher(text);
        while (rule.find()) {
            bindings.putAll(collect(rule.group(1), typesByName, accumResultTypes).types);
        }
        return bindings;
    }

    /**
     * As {@link #resolve(String, Map)}, but scoped to the single rule block
     * enclosing {@code offset} (a character offset into {@code text}). Callers
     * with a caret position — hover, go-to-definition — should prefer this:
     * {@link #resolve} merges bindings across <em>all</em> rules last-write-wins,
     * so a binding name reused across rules (e.g. {@code $p}) would otherwise
     * resolve to whichever rule appears last in the file rather than the one
     * under the caret. Falls back to whole-text {@link #resolve} when
     * {@code offset} is outside every rule block.
     */
    public static Map<String, String> resolveAt(String text, int offset,
                                                Map<String, DeclaredType> typesByName) {
        return resolveAt(text, offset, typesByName, AccumulateFunctionTypes.get());
    }

    /**
     * As {@link #resolveAt(String, int, Map)}, plus accumulate result bindings.
     */
    public static Map<String, String> resolveAt(String text, int offset,
                                                Map<String, DeclaredType> typesByName,
                                                Map<String, String> accumResultTypes) {
        if (text == null || text.isEmpty()) {
            return new HashMap<>();
        }
        Matcher block = RULE_BLOCK.matcher(text);
        while (block.find()) {
            if (offset >= block.start() && offset < block.end()) {
                return collect(block.group(1), typesByName, accumResultTypes).types;
            }
        }
        return resolve(text, typesByName, accumResultTypes);
    }

    /**
     * Collects the bindings declared in a single {@code when} section, with
     * positions. The whole-text {@link #resolve} methods merge
     * {@code collect(...).types} across rules; the inlay-hint helper uses the
     * full {@link Bindings} (including {@link Decl} offsets), so completion,
     * hover and inlay all resolve bindings through this one method.
     *
     * <p>Within a section, intra-section key collisions resolve last-write-wins
     * in scan order (outer patterns, then field/nested bindings per pattern, then
     * accumulate results).
     */
    public static Bindings collect(String whenSection,
                                   Map<String, DeclaredType> typesByName,
                                   Map<String, String> accumResultTypes) {
        Map<String, String> bindings = new HashMap<>();
        List<Decl> declarations = new ArrayList<>();
        if (whenSection == null || whenSection.isEmpty()) {
            return new Bindings(bindings, declarations);
        }

        Matcher m = PATTERN_START.matcher(whenSection);
        int searchFrom = 0;
        while (m.find(searchFrom)) {
            String outerName = m.group(1);
            String typeName = simpleName(m.group(2));
            int openParen = m.end() - 1;

            if (outerName != null) {
                bindings.put(outerName, typeName);  // outer pattern: type explicit, no Decl
            }

            int closeParen = findMatchingParen(whenSection, openParen);
            if (closeParen < 0) {
                searchFrom = openParen + 1;
                continue;
            }

            int bodyStart = openParen + 1;
            String body = whenSection.substring(bodyStart, closeParen);
            DeclaredType declared = typesByName.get(typeName);

            Matcher fb = FIELD_BINDING.matcher(body);
            while (fb.find()) {
                String field = fb.group(2);
                String fieldType = (declared != null)
                        ? lookupFieldType(declared, field, typesByName)
                        : null;
                if (fieldType == null) {
                    // Pattern type isn't a declared DRL type (e.g. a JDK type like
                    // Number), or the field isn't declared — try the narrow JDK map.
                    fieldType = JDK_ACCESSOR_TYPES.get(field);
                }
                if (fieldType != null) {
                    bindings.put(fb.group(1), fieldType);
                    declarations.add(new Decl(fb.group(1), fieldType, bodyStart + fb.end(2)));
                }
            }

            if (declared != null) {
                Matcher nfb = NESTED_FIELD_BINDING.matcher(body);
                while (nfb.find()) {
                    String pathType = resolvePath(declared, nfb.group(2), typesByName);
                    if (pathType != null) {
                        bindings.put(nfb.group(1), pathType);
                        declarations.add(new Decl(nfb.group(1), pathType, bodyStart + nfb.end(2)));
                    }
                }
            }

            searchFrom = openParen + 1;
        }

        if (accumResultTypes != null && !accumResultTypes.isEmpty()) {
            Matcher af = ACCUM_RESULT_BINDING.matcher(whenSection);
            while (af.find()) {
                String resultType = accumResultTypes.get(af.group(2));
                if (resultType != null) {
                    bindings.put(af.group(1), resultType);
                    int open = af.end() - 1;
                    int close = findMatchingParen(whenSection, open);
                    int hintOffset = (close >= 0) ? close + 1 : af.end();
                    declarations.add(new Decl(af.group(1), resultType, hintOffset));
                }
            }
        }

        return new Bindings(bindings, declarations);
    }

    /**
     * Looks up the type of {@code fieldName} on {@code type}, walking the
     * {@link DeclaredType#extendsName} chain if not found directly.
     */
    private static String lookupFieldType(DeclaredType type, String fieldName,
                                          Map<String, DeclaredType> typesByName) {
        final int maxDepth = 10;
        DeclaredType current = type;
        for (int depth = 0; depth < maxDepth && current != null; depth++) {
            if (current.fields != null) {
                for (Field f : current.fields) {
                    if (f != null && fieldName.equals(f.name)) {
                        return simpleName(f.type);
                    }
                }
            }
            if (current.extendsName == null) {
                return null;
            }
            current = typesByName.get(current.extendsName);
        }
        return null;
    }

    /**
     * Walks a dotted field path ({@code ref.dynIndex}) from {@code type},
     * resolving each segment's field type via {@link #lookupFieldType} and
     * advancing through {@code typesByName}. Returns the final segment's (simple)
     * type name, or {@code null} if any segment can't be resolved.
     */
    private static String resolvePath(DeclaredType type, String path,
                                      Map<String, DeclaredType> typesByName) {
        DeclaredType current = type;
        String resolved = null;
        for (String seg : path.split("\\.")) {
            if (current == null) {
                return null;
            }
            resolved = lookupFieldType(current, seg, typesByName);
            if (resolved == null) {
                return null;
            }
            current = typesByName.get(resolved);
        }
        return resolved;
    }

    private static String simpleName(String type) {
        if (type == null) {
            return null;
        }
        int dot = type.lastIndexOf('.');
        return dot >= 0 ? type.substring(dot + 1) : type;
    }

    private static int findMatchingParen(String text, int openIdx) {
        int depth = 1;
        int i = openIdx + 1;
        while (i < text.length() && depth > 0) {
            char c = text.charAt(i);
            if (c == '(') {
                depth++;
            } else if (c == ')') {
                depth--;
            }
            i++;
        }
        return depth == 0 ? i - 1 : -1;
    }
}
