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
import java.util.List;
import java.util.Map;

import org.drools.drl.parser.antlr4.DRL10Parser;
import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.MarkupContent;
import org.eclipse.lsp4j.MarkupKind;
import org.eclipse.lsp4j.Position;

/**
 * Hover content for DRL documents: type structure for pattern type names and
 * type information for fields inside constraints.
 *
 * <p>Type names resolve through {@link DRLWorkspaceTypeIndex} — the same
 * layered view (current document, open unsaved siblings, on-disk siblings)
 * that completion and go-to-definition use — then classpath types through
 * imports and the class index. Declared types render as their declare block
 * with the doc comment above it, classpath types as their member list.
 */
public final class DRLHoverHelper {

    private DRLHoverHelper() {
    }

    /**
     * Equivalent to {@link #hover(String, Position, ClassIndex, ClassMemberIndex, Path, Map)}
     * with no open sibling buffers — sibling declares resolve from disk only.
     */
    public static Hover hover(String text, Position position, ClassIndex classIndex,
                              ClassMemberIndex memberIndex, Path documentPath) {
        return hover(text, position, classIndex, memberIndex, documentPath, Map.of());
    }

    /**
     * Returns hover content for the identifier at {@code position}, or
     * {@code null} when there is nothing useful to show.
     *
     * @param documentPath filesystem location of the document, used to find
     *                     sibling DRL files; {@code null} for non-file
     *                     documents
     * @param openFiles    open unsaved sibling buffers keyed by path, so
     *                     cross-file resolution reflects unsaved edits; may be
     *                     empty
     */
    public static Hover hover(String text, Position position, ClassIndex classIndex,
                              ClassMemberIndex memberIndex, Path documentPath,
                              Map<Path, String> openFiles) {
        if (text == null || position == null) {
            return null;
        }
        String word = DRLDefinitionHelper.wordAt(text, position);
        if (word.isEmpty() || !Character.isJavaIdentifierStart(word.charAt(0))) {
            return null;
        }

        // Dotted member chain ($o.total, Status.ACTIVE, $a.b.c): handled by a
        // dedicated segment walk. Single-segment words take the steps below.
        Chain chain = chainAt(text, position);

        // Parse the current document once; every step below reuses this parse.
        ParsedDrl parsed = ParsedDrl.of(text);
        List<DeclaredType> currentDocTypes = parsed.declaredTypes();
        Map<String, DeclaredType> typeIndex =
                DRLWorkspaceTypeIndex.build(currentDocTypes, documentPath, openFiles);

        if (chain.segments.length >= 2) {
            return hoverChain(chain, text, position, parsed, currentDocTypes, typeIndex,
                              classIndex, memberIndex, documentPath, openFiles);
        }

        // 1. The word is itself a DRL-declared type.
        DeclaredType declared = typeIndex.get(word);
        if (declared != null) {
            return markdown(renderDeclaredHover(
                    declared, typeIndex, currentDocTypes, text, documentPath, openFiles));
        }

        // 1b. Documented function/query/global — the doc-comment parser maps
        //     names for all four declaration kinds.
        Hover doc = docHover(word, currentDocTypes, text, documentPath, openFiles);
        if (doc != null) {
            return doc;
        }

        DRL10Parser.CompilationUnitContext compilationUnit = parsed.compilationUnit;
        Integer nodeIndex = parsed.tokenIndexAt(position);

        // 2. Bound variable: resolve $var to its type via the shared binding
        //    engine (pattern, field, nested-path, JDK-accessor, accumulate),
        //    scoped to the rule under the caret so a binding name reused across
        //    rules resolves to the right one, then hover that type.
        if (word.startsWith("$")) {
            int offset = positionToOffset(text, position);
            String boundType =
                    LhsBindingResolver.resolveAt(text, offset, typeIndex).get(word.substring(1));
            if (boundType != null) {
                DeclaredType boundDeclared = typeIndex.get(boundType);
                if (boundDeclared != null) {
                    return markdown(renderDeclaredHover(
                            boundDeclared, typeIndex, currentDocTypes, text, documentPath, openFiles));
                }
                String boundFqcn = DRLCompletionHelper.resolveFqcn(
                        boundType, boundType, compilationUnit, classIndex);
                if (boundFqcn != null) {
                    return markdown(renderJavaType(boundType, boundFqcn, memberIndex.membersOf(boundFqcn)));
                }
            }
        }

        // 3. Field of the pattern enclosing the caret.
        if (nodeIndex != null) {
            String patternType = DRLCompletionHelper.findEnclosingPatternTypeName(
                    compilationUnit, nodeIndex);
            if (patternType != null && !patternType.equals(word)) {
                Field field = findField(patternType, word, typeIndex,
                                        compilationUnit, classIndex, memberIndex);
                if (field != null) {
                    return markdown(renderField(field, simpleName(patternType)));
                }
            }
        }

        // 4. Classpath type (or java.lang built-in). Show the hover even with no
        //    members — knowing the FQN (e.g. java.lang.Object) is still useful.
        String fqcn = DRLCompletionHelper.resolveFqcn(word, word, compilationUnit, classIndex);
        if (fqcn != null) {
            return markdown(renderJavaType(word, fqcn, memberIndex.membersOf(fqcn)));
        }
        return null;
    }

    /**
     * The dotted member chain around the caret ({@code $o.total},
     * {@code Status.ACTIVE.level}): the dot-separated segments (an empty entry
     * marks a stray dot, e.g. {@code a..b} mid-edit) and the index of the
     * segment the caret sits in.
     */
    private static final class Chain {

        final String[] segments;
        final int hoveredIndex;

        Chain(String[] segments, int hoveredIndex) {
            this.segments = segments;
            this.hoveredIndex = hoveredIndex;
        }
    }

    /**
     * Extracts the dotted chain around the caret by expanding over identifier
     * characters and {@code .} on the caret's line. Only called after the word
     * guard in {@link #hover}, so the line index is valid and at least one
     * segment is non-empty. Kept separate from
     * {@link DRLDefinitionHelper#wordAt}, whose single-identifier expansion is
     * shared with rename and go-to-definition.
     */
    private static Chain chainAt(String text, Position position) {
        String[] lines = text.split("\r?\n", -1);
        String line = lines[position.getLine()];
        int col = Math.min(Math.max(position.getCharacter(), 0), line.length());

        int start = col;
        while (start > 0 && isChainChar(line.charAt(start - 1))) {
            start--;
        }
        int end = col;
        while (end < line.length() && isChainChar(line.charAt(end))) {
            end++;
        }
        String[] segments = line.substring(start, end).split("\\.", -1);
        return new Chain(segments, hoveredSegment(segments, col - start));
    }

    private static boolean isChainChar(char c) {
        return DRLDefinitionHelper.isIdentifierChar(c) || c == '.';
    }

    /** Index of the segment containing the caret, {@code rel} chars into the chain. */
    private static int hoveredSegment(String[] segments, int rel) {
        int segmentStart = 0;
        for (int i = 0; i < segments.length; i++) {
            int segmentEnd = segmentStart + segments[i].length();
            if (rel <= segmentEnd) {
                return i;
            }
            segmentStart = segmentEnd + 1; // skip the '.'
        }
        return segments.length - 1;
    }

    /**
     * Resolves a dotted chain segment by segment up to the hovered one,
     * tracking the running type, and renders only the hovered segment.
     *
     * <p>Segment 0 resolves as a {@code $binding} (rule-scoped), a declared
     * type, a classpath type, or a field of the enclosing pattern — and, when
     * it is itself the hovered segment, falls back to a documented
     * function/query/global by that name (the chain head is a plain identifier
     * reference, e.g. {@code results} in {@code results.add(..)}). Later
     * segments are enum constants or fields of the running type; a constant is
     * an instance of its enum, so member access on it continues from the enum
     * type itself.
     *
     * <p>Any segment before the hovered one that does not resolve yields no
     * hover at all: falling back to the single-word steps with one segment of
     * the chain would mis-render it (e.g. a chain tail as a field of the
     * enclosing pattern).
     */
    private static Hover hoverChain(Chain chain, String text, Position position,
                                    ParsedDrl parsed, List<DeclaredType> currentDocTypes,
                                    Map<String, DeclaredType> typeIndex,
                                    ClassIndex classIndex, ClassMemberIndex memberIndex,
                                    Path documentPath, Map<Path, String> openFiles) {
        String runningType = null;
        for (int i = 0; i <= chain.hoveredIndex; i++) {
            String segment = chain.segments[i];
            boolean hovered = i == chain.hoveredIndex;
            if (segment.isEmpty()) {
                return null;
            }

            if (i == 0) {
                if (segment.charAt(0) == '$') {
                    int offset = positionToOffset(text, position);
                    String boundType = LhsBindingResolver.resolveAt(text, offset, typeIndex)
                            .get(segment.substring(1));
                    if (boundType == null) {
                        return null;
                    }
                    if (hovered) {
                        return markdown(renderBinding(segment, boundType, parsed, typeIndex,
                                currentDocTypes, text, classIndex, memberIndex,
                                documentPath, openFiles));
                    }
                    runningType = boundType;
                } else if (typeIndex.get(segment) != null) {
                    if (hovered) {
                        return markdown(renderDeclaredHover(typeIndex.get(segment), typeIndex,
                                currentDocTypes, text, documentPath, openFiles));
                    }
                    runningType = segment;
                } else {
                    String fqcn = DRLCompletionHelper.resolveFqcn(
                            segment, segment, parsed.compilationUnit, classIndex);
                    if (fqcn != null) {
                        if (hovered) {
                            return markdown(renderJavaType(
                                    segment, fqcn, memberIndex.membersOf(fqcn)));
                        }
                        runningType = fqcn;
                    } else {
                        Integer nodeIndex = parsed.tokenIndexAt(position);
                        String patternType = nodeIndex == null ? null
                                : DRLCompletionHelper.findEnclosingPatternTypeName(
                                        parsed.compilationUnit, nodeIndex);
                        Field field = patternType == null || patternType.equals(segment) ? null
                                : findField(patternType, segment, typeIndex,
                                            parsed.compilationUnit, classIndex, memberIndex);
                        if (field != null) {
                            if (hovered) {
                                return markdown(renderField(field, simpleName(patternType)));
                            }
                            runningType = field.type;
                        } else if (hovered) {
                            return docHover(segment, currentDocTypes, text,
                                            documentPath, openFiles);
                        } else {
                            return null;
                        }
                    }
                }
            } else {
                DeclaredType declared = typeIndex.get(simpleName(runningType));
                if (declared != null && declared.isEnum && isEnumConstant(declared, segment)) {
                    if (hovered) {
                        return markdown(fencedHeader(declared.name + "." + segment)
                                + renderDeclaredHover(declared, typeIndex, currentDocTypes,
                                                      text, documentPath, openFiles));
                    }
                    // Running type stays the enum: the constant is an instance of it.
                } else {
                    Field field = findField(runningType, segment, typeIndex,
                                            parsed.compilationUnit, classIndex, memberIndex);
                    if (field == null) {
                        return null;
                    }
                    if (hovered) {
                        return markdown(renderField(field, simpleName(runningType)));
                    }
                    runningType = field.type;
                }
            }
        }
        return null;
    }

    /** True when {@code name} is a constant of {@code enumType} (a field typed as the enum). */
    private static boolean isEnumConstant(DeclaredType enumType, String name) {
        return enumType.fields.stream()
                .anyMatch(field -> name.equals(field.name) && enumType.name.equals(field.type));
    }

    /** Header {@code $x : Type} followed by the bound type's details. */
    private static String renderBinding(String binding, String typeName, ParsedDrl parsed,
                                        Map<String, DeclaredType> typeIndex,
                                        List<DeclaredType> currentDocTypes, String text,
                                        ClassIndex classIndex, ClassMemberIndex memberIndex,
                                        Path documentPath, Map<Path, String> openFiles) {
        String header = fencedHeader(binding + " : " + typeName);
        DeclaredType declared = typeIndex.get(typeName);
        if (declared != null) {
            return header + renderDeclaredHover(declared, typeIndex, currentDocTypes, text,
                                                documentPath, openFiles);
        }
        String fqcn = DRLCompletionHelper.resolveFqcn(
                typeName, typeName, parsed.compilationUnit, classIndex);
        if (fqcn != null) {
            return header + renderJavaType(typeName, fqcn, memberIndex.membersOf(fqcn));
        }
        return header.stripTrailing();
    }

    private static String fencedHeader(String content) {
        return "```\n" + content + "\n```\n\n";
    }

    private static String renderField(Field field, String owner) {
        return "**" + field.name + "** : `" + field.type + "`\n\nField of `" + owner + "`";
    }

    private static String simpleName(String typeName) {
        return typeName.substring(typeName.lastIndexOf('.') + 1);
    }

    /**
     * Hover for a documented function, query, or global named {@code name}, or
     * {@code null} when it has no doc comment. Shared by the single-word flow
     * and the chain-head fallback.
     */
    private static Hover docHover(String name, List<DeclaredType> currentDocTypes, String text,
                                  Path documentPath, Map<Path, String> openFiles) {
        String doc = DRLDocCommentParser.docFor(text, name);
        if (doc == null) {
            return null;
        }
        Map<String, String> linkTargets = DRLWorkspaceTypeIndex.buildLinkTargets(
                currentDocTypes, text, documentPath, openFiles);
        return markdown(DRLDocFormatter.format(doc, linkTargets));
    }

    /**
     * Renders the full declared-type hover: doc comment (with {@code {@link}}
     * references resolved to the declarations they name), the declare block,
     * and inherited fields.
     */
    private static String renderDeclaredHover(DeclaredType declared,
                                              Map<String, DeclaredType> typeIndex,
                                              List<DeclaredType> currentDocTypes, String text,
                                              Path documentPath, Map<Path, String> openFiles) {
        List<Field> allFields = DRLDeclaredTypeParser.fieldsIncludingInherited(declared, typeIndex);
        String doc = DRLWorkspaceTypeIndex.docFor(
                declared.name, currentDocTypes, text, documentPath, openFiles);
        Map<String, String> linkTargets =
                DRLWorkspaceTypeIndex.buildLinkTargets(currentDocTypes, text, documentPath, openFiles);
        return renderDeclared(declared, allFields, doc, linkTargets);
    }

    private static Field findField(String patternType, String fieldName,
                                   Map<String, DeclaredType> typeIndex,
                                   DRL10Parser.CompilationUnitContext compilationUnit,
                                   ClassIndex classIndex, ClassMemberIndex memberIndex) {
        String simpleName = patternType.substring(patternType.lastIndexOf('.') + 1);
        DeclaredType declared = typeIndex.get(simpleName);
        List<Field> fields;
        if (declared != null) {
            fields = DRLDeclaredTypeParser.fieldsIncludingInherited(declared, typeIndex);
        } else {
            String fqcn = DRLCompletionHelper.resolveFqcn(patternType, simpleName,
                                                          compilationUnit, classIndex);
            fields = fqcn == null ? List.of() : memberIndex.membersOf(fqcn);
        }
        for (Field field : fields) {
            if (fieldName.equals(field.name)) {
                return field;
            }
        }
        return null;
    }

    /**
     * @param allFields the type's own fields followed by inherited ones, as
     *                  produced by
     *                  {@link DRLDeclaredTypeParser#fieldsIncludingInherited};
     *                  the inherited tail is rendered as a separate section
     * @param doc         the type's doc comment from {@link DRLWorkspaceTypeIndex#docFor},
     *                    rendered as a leading prose section; {@code null} when undocumented
     * @param linkTargets {@code typeName -> href} for resolving {@code {@link}} references,
     *                    from {@link DRLWorkspaceTypeIndex#buildLinkTargets}; may be empty
     */
    private static String renderDeclared(DeclaredType dt, List<Field> allFields, String doc,
                                         Map<String, String> linkTargets) {
        StringBuilder sb = new StringBuilder();
        if (doc != null) {
            sb.append(DRLDocFormatter.format(doc, linkTargets)).append("\n\n");
        }
        sb.append("```\n");
        sb.append("declare ").append(dt.isEnum ? "enum " : "").append(dt.name);
        if (dt.extendsName != null) {
            sb.append(" extends ").append(dt.extendsName);
        }
        sb.append('\n');
        for (Field field : dt.fields) {
            if (dt.isEnum && dt.name.equals(field.type)) {
                sb.append("  ").append(field.name);
                if (field.args != null) {
                    sb.append('(').append(field.args).append(')');
                }
                sb.append('\n');
            } else {
                sb.append("  ").append(field.name).append(" : ").append(field.type).append('\n');
            }
        }
        sb.append("end\n```");

        List<Field> inherited = allFields.subList(dt.fields.size(), allFields.size());
        if (!inherited.isEmpty()) {
            sb.append("\n\n_Inherited:_");
            for (Field field : inherited) {
                sb.append("\n- ").append(field.name).append(" : ").append(field.type);
            }
        }
        return sb.toString();
    }

    private static String renderJavaType(String simpleName, String fqcn, List<Field> members) {
        StringBuilder sb = new StringBuilder();
        sb.append("**").append(simpleName).append("** — `").append(fqcn).append("`\n");
        for (Field member : members) {
            sb.append("\n- ").append(member.name).append(" : ").append(member.type);
        }
        return sb.toString();
    }

    private static Hover markdown(String content) {
        return new Hover(new MarkupContent(MarkupKind.MARKDOWN, content));
    }

    /**
     * Converts an LSP {@link Position} (zero-based line + UTF-16 character) to a
     * character offset into {@code text}. DRL files are ASCII in practice, so
     * the character offset matches code units. Clamped to {@code text.length()}.
     */
    private static int positionToOffset(String text, Position position) {
        int line = position.getLine();
        int offset = 0;
        int currentLine = 0;
        while (currentLine < line && offset < text.length()) {
            if (text.charAt(offset) == '\n') {
                currentLine++;
            }
            offset++;
        }
        return Math.min(offset + position.getCharacter(), text.length());
    }
}
