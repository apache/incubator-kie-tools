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

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.drools.drl.parser.antlr4.DRL10Parser;
import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.MarkupContent;
import org.eclipse.lsp4j.MarkupKind;
import org.eclipse.lsp4j.Position;

/**
 * Hover content for DRL documents: declared and classpath types, fields inside
 * constraints, bound variables, dotted member chains ({@code $o.total},
 * {@code Status.ACTIVE}) rendered at the hovered segment, qualified enum
 * constants, accumulate-function names, and doc comments for documented
 * {@code function}/{@code query}/{@code global} declarations.
 *
 * <p>Type names resolve through {@link DRLWorkspaceTypeIndex} — the same
 * layered view (current document, open unsaved siblings, on-disk siblings)
 * that completion and go-to-definition use — then classpath types through
 * imports and the class index. Declared types render as their declare block
 * with the doc comment above it, classpath types as their member list.
 * Accumulate functions are detected structurally from the parse tree; their
 * result types come from {@link AccumulateFunctionTypes}.
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

        DRL10Parser.CompilationUnitContext compilationUnit = parsed.compilationUnit;
        Integer nodeIndex = parsed.tokenIndexAt(position);

        // 1b. Accumulate function name ($c : count()): detected purely from
        //     the parse tree — the caret's terminal sits under the function's
        //     own identifier. Structural, so it outranks the name-based doc
        //     step below: at this position the identifier can only be an
        //     accumulate function, even when a DRL function shares its name.
        if (nodeIndex != null) {
            Hover accumulate = accumulateFunctionHover(word, compilationUnit, nodeIndex);
            if (accumulate != null) {
                return accumulate;
            }
        }

        // 1c. Documented function/query/global — the doc-comment parser maps
        //     names for all four declaration kinds.
        Hover doc = docHover(word, currentDocTypes, text, documentPath, openFiles);
        if (doc != null) {
            return doc;
        }

        // 3. Bound variable: resolve $var to its type via the shared binding
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
                    return markdown(renderJavaType(boundType, boundFqcn, memberIndex.membersOf(boundFqcn),
                            memberIndex.constructorsOf(boundFqcn)));
                }
                // Nothing to describe beyond the name: a primitive has no class
                // to load and no members, and the type is the useful part anyway.
                return markdown(fencedHeader(word + " : " + boundType).stripTrailing());
            }
        }

        // 4. Field of the pattern enclosing the caret.
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

        // 5. Classpath type (or java.lang built-in). Show the hover even with no
        //    members — knowing the FQN (e.g. java.lang.Object) is still useful.
        String fqcn = DRLCompletionHelper.resolveFqcn(word, word, compilationUnit, classIndex);
        if (fqcn != null) {
            return markdown(renderJavaType(word, fqcn, memberIndex.membersOf(fqcn),
                    memberIndex.constructorsOf(fqcn)));
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
        int start = 0;

        // A chain can open with a fully-qualified type name, whose leading
        // segments are package names that resolve to nothing on their own
        // (`com` in `com.example.model.Order`). Consume the longest dotted
        // prefix that names a known type first, so the walk below starts from
        // the type rather than failing on the package.
        int fqcnEnd = fqcnPrefixEnd(chain.segments, classIndex);
        if (fqcnEnd >= 1) {
            String fqcn = joinSegments(chain.segments, fqcnEnd);
            if (chain.hoveredIndex <= fqcnEnd) {
                // Anywhere inside the qualified name describes that type.
                String simple = chain.segments[fqcnEnd];
                return markdown(renderJavaType(simple, fqcn, memberIndex.membersOf(fqcn),
                        memberIndex.constructorsOf(fqcn)));
            }
            runningType = fqcn;
            start = fqcnEnd + 1;
        }

        for (int i = start; i <= chain.hoveredIndex; i++) {
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
                            return markdown(renderJavaType(segment, fqcn, memberIndex.membersOf(fqcn),
                                    memberIndex.constructorsOf(fqcn)));
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

    /**
     * The index of the last segment of the longest leading run of
     * {@code segments} that names a type on the classpath, or {@code -1} when
     * no prefix of two or more segments does. Longest-first so
     * {@code com.example.Order.status} prefers the type {@code com.example.Order}
     * over a shorter accidental match.
     */
    private static int fqcnPrefixEnd(String[] segments, ClassIndex classIndex) {
        for (int end = segments.length - 1; end >= 1; end--) {
            String simple = segments[end];
            if (simple.isEmpty()) {
                continue;
            }
            String candidate = joinSegments(segments, end);
            if (classIndex.getMatching(simple).contains(candidate)) {
                return end;
            }
        }
        return -1;
    }

    /** {@code segments[0..end]} rejoined with dots. */
    private static String joinSegments(String[] segments, int end) {
        StringBuilder sb = new StringBuilder(segments[0]);
        for (int i = 1; i <= end; i++) {
            sb.append('.').append(segments[i]);
        }
        return sb.toString();
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
            return header + renderJavaType(typeName, fqcn, memberIndex.membersOf(fqcn),
                    memberIndex.constructorsOf(fqcn));
        }
        return header.stripTrailing();
    }

    /**
     * Hover for an accumulate function name ({@code count} in
     * {@code $c : count()}), or {@code null} when the word at
     * {@code tokenIndex} is not one. Detection is purely structural: the
     * terminal at that token index must BE (part of) the function identifier
     * of an {@link DRL10Parser.AccumulateFunctionContext} — an argument whose
     * text merely equals the function name does not qualify — and the name
     * must be a known accumulate function.
     */
    private static Hover accumulateFunctionHover(String word,
                                                 DRL10Parser.CompilationUnitContext compilationUnit,
                                                 int tokenIndex) {
        TerminalNode terminal = terminalAtTokenIndex(compilationUnit, tokenIndex);
        if (terminal == null) {
            return null;
        }
        DRL10Parser.AccumulateFunctionContext function = enclosingAccumulateFunction(terminal);
        if (function == null || function.drlIdentifier() == null
                || !word.equals(function.drlIdentifier().getText())
                || !isUnder(terminal, function.drlIdentifier())) {
            return null;
        }
        String resultType = AccumulateFunctionTypes.get().get(word);
        if (resultType == null) {
            return null;
        }
        return markdown(fencedHeader(word + " : " + resultType) + "_accumulate function_");
    }

    /**
     * The terminal node whose token sits at {@code tokenIndex} in the parse
     * tree rooted at {@code node}, or {@code null} when none does.
     */
    private static TerminalNode terminalAtTokenIndex(ParseTree node, int tokenIndex) {
        if (node instanceof TerminalNode terminal) {
            return terminal.getSymbol().getTokenIndex() == tokenIndex ? terminal : null;
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            TerminalNode found = terminalAtTokenIndex(node.getChild(i), tokenIndex);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    /** The nearest {@code accumulateFunction} ancestor of {@code node}, or {@code null}. */
    private static DRL10Parser.AccumulateFunctionContext enclosingAccumulateFunction(ParseTree node) {
        for (ParseTree current = node; current != null; current = current.getParent()) {
            if (current instanceof DRL10Parser.AccumulateFunctionContext function) {
                return function;
            }
        }
        return null;
    }

    /** True when {@code node} is {@code ancestor} itself or sits beneath it. */
    private static boolean isUnder(ParseTree node, ParseTree ancestor) {
        for (ParseTree current = node; current != null; current = current.getParent()) {
            if (current == ancestor) {
                return true;
            }
        }
        return false;
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

    private static String renderJavaType(String simpleName, String fqcn, List<Field> members,
                                         List<String> constructors) {
        StringBuilder sb = new StringBuilder();
        sb.append("**").append(simpleName).append("** — `").append(fqcn).append("`\n");
        appendMemberSection(sb, "Constants", members, Field.Origin.ENUM_CONSTANT);
        appendMemberSection(sb, "Fields", members, Field.Origin.FIELD);
        appendMemberSection(sb, "Getters", members, Field.Origin.GETTER);
        if (!constructors.isEmpty()) {
            sb.append("\n\n**Constructors**");
            for (String signature : constructors) {
                sb.append("\n- `").append(signature).append('`');
            }
        }
        return sb.toString();
    }

    /** Appends {@code title}'s section for {@code members} of the given {@code origin}, or nothing when none match. */
    private static void appendMemberSection(StringBuilder sb, String title, List<Field> members,
                                            Field.Origin origin) {
        boolean headerWritten = false;
        for (Field member : members) {
            if (member.origin != origin) {
                continue;
            }
            if (!headerWritten) {
                sb.append("\n\n**").append(title).append("**");
                headerWritten = true;
            }
            if (origin == Field.Origin.ENUM_CONSTANT) {
                sb.append("\n- ").append(member.name);
                if (member.args != null) {
                    sb.append(" (").append(member.args).append(')');
                }
            } else {
                sb.append("\n- ").append(member.name).append(" : ").append(member.type);
            }
        }
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
