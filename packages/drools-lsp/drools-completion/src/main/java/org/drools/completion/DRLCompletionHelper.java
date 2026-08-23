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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vmware.antlr4c3.CodeCompletionCore;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.drools.drl.parser.antlr4.DRL10Lexer;
import org.drools.drl.parser.antlr4.DRL10Parser;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.services.LanguageClient;

import static org.drools.drl.parser.antlr4.DRLParserHelper.computeTokenIndex;
import static org.drools.drl.parser.antlr4.DRLParserHelper.createDrlParser;

public class DRLCompletionHelper {

    private static final Logger logger = Logger.getLogger(DRLCompletionHelper.class.getName());

    // PREFERRED_RULES is used to filter out the rules that consist of unwanted tokens
    // additionally, it can be used to customize getCompletionItems behavior
    private static final Set<Integer> PREFERRED_RULES = Set.of(
            DRL10Parser.RULE_drlIdentifier,
            DRL10Parser.RULE_drlQualifiedName,
            DRL10Parser.RULE_stringId,
            DRL10Parser.RULE_consequenceBody
    );

    // The eight Java primitive-type keyword tokens. Mirrors the grammar's
    // `primitiveType` rule (DRL10Expressions.g4), which shares DRL10Lexer's token
    // vocab — so these ids match the ones c3 reports against DRL10Parser. ANTLR
    // exposes no token-set constant for a rule, so the list is restated here.
    private static final Set<Integer> PRIMITIVE_TYPE_TOKENS = Set.of(
            DRL10Parser.BOOLEAN, DRL10Parser.CHAR, DRL10Parser.BYTE, DRL10Parser.SHORT,
            DRL10Parser.INT, DRL10Parser.LONG, DRL10Parser.FLOAT, DRL10Parser.DOUBLE
    );

    // Inside a pattern constraint, c3 predicts the Java expression starters that
    // can legally open a constraint: the primitive types above plus new/super and
    // the big-decimal/-integer literal tokens. They are valid grammar but useless
    // noise next to the field completions, so they are dropped in constraint
    // position only — primitive types stay useful elsewhere (e.g. after `global`).
    private static final Set<Integer> CONSTRAINT_KEYWORD_NOISE = Stream.concat(
            PRIMITIVE_TYPE_TOKENS.stream(),
            Stream.of(DRL10Parser.NEW, DRL10Parser.SUPER,
                    DRL10Parser.DRL_BIG_DECIMAL_LITERAL, DRL10Parser.DRL_BIG_INTEGER_LITERAL)
    ).collect(Collectors.toUnmodifiableSet());

    // Rule attributes are only legal in a rule/query header. c3 predicts them at
    // a statement boundary too (after `end`, or in an empty document), where they
    // cannot be typed — 14 unusable items next to the 6 that are usable. They are
    // dropped there only; the rule header keeps offering them.
    private static final Set<Integer> RULE_ATTRIBUTE_TOKENS = Set.of(
            DRL10Parser.DRL_ATTRIBUTES, DRL10Parser.DRL_SALIENCE, DRL10Parser.DRL_ENABLED,
            DRL10Parser.DRL_NO_LOOP, DRL10Parser.DRL_AUTO_FOCUS, DRL10Parser.DRL_LOCK_ON_ACTIVE,
            DRL10Parser.DRL_ACTIVATION_GROUP, DRL10Parser.DRL_RULEFLOW_GROUP,
            DRL10Parser.DRL_DATE_EFFECTIVE, DRL10Parser.DRL_DATE_EXPIRES,
            DRL10Parser.DRL_DIALECT, DRL10Parser.DRL_CALENDARS,
            DRL10Parser.DRL_TIMER, DRL10Parser.DRL_DURATION
    );

    private DRLCompletionHelper() {
    }

    public static List<CompletionItem> getCompletionItems(String text, Position caretPosition, LanguageClient client) {
        return getCompletionItems(text, caretPosition, client, ClassIndex.empty());
    }

    public static List<CompletionItem> getCompletionItems(String text, Position caretPosition, LanguageClient client, ClassIndex classIndex) {
        return getCompletionItems(text, caretPosition, client, classIndex, ClassMemberIndex.empty());
    }

    public static List<CompletionItem> getCompletionItems(String text, Position caretPosition, LanguageClient client, ClassIndex classIndex, ClassMemberIndex memberIndex) {
        return getCompletionItems(text, caretPosition, client, classIndex, memberIndex, null);
    }

    /**
     * @param documentPath filesystem location of the document, used to find
     *                     sibling DRL files; {@code null} for non-file
     *                     documents (sibling declares are then unavailable)
     */
    public static List<CompletionItem> getCompletionItems(String text, Position caretPosition, LanguageClient client, ClassIndex classIndex, ClassMemberIndex memberIndex, Path documentPath) {
        return getCompletionItems(text, caretPosition, client, classIndex, memberIndex, documentPath, Map.of());
    }

    /**
     * @param openFiles open unsaved sibling buffers keyed by path, so field
     *                  completion on sibling-declared types reflects unsaved
     *                  edits; may be empty
     */
    public static List<CompletionItem> getCompletionItems(String text, Position caretPosition, LanguageClient client, ClassIndex classIndex, ClassMemberIndex memberIndex, Path documentPath, Map<Path, String> openFiles) {
        DRL10Parser drlParser = createDrlParser(text);

        int row = caretPosition == null ? -1 : caretPosition.getLine() + 1; // caret line position is zero based
        int col = caretPosition == null ? -1 : caretPosition.getCharacter();

        DRL10Parser.CompilationUnitContext compilationUnit = drlParser.compilationUnit();
        Integer nodeIndex = computeTokenIndex(drlParser, row, col);
        String prefix = extractPrefix(drlParser, nodeIndex);

        // null when the caret is past the last token; fall back to EOF.
        int caretTokenIndex = nodeIndex != null
                ? nodeIndex
                : drlParser.getInputStream().size() - 1;

        // Right after '(' the matched token is the paren itself, for which c3
        // yields no candidates; look one token ahead for the constraint, but
        // keep the paren's index to resolve the pattern type (its span ends at
        // '(' until the closing ')' is typed).
        // A caret directly after a dot is a member position: the only thing that
        // can follow is a member of the path's type. c3 predicts the constraint
        // operators there instead (it cannot see that the path is incomplete), so
        // this position is answered from the type walk alone, keywords included.
        String[] chain = dottedChainBeforeCaret(text, caretPosition);
        if (chain != null) {
            return memberItemsForChain(chain, text, caretPosition, compilationUnit,
                    caretTokenIndex, classIndex, memberIndex, documentPath, openFiles);
        }

        int candidatesIndex = caretTokenIndex;
        if (caretTokenIndex >= 0 && caretTokenIndex < drlParser.getInputStream().size() - 1
                && drlParser.getInputStream().get(caretTokenIndex).getType() == DRL10Lexer.LPAREN) {
            candidatesIndex = caretTokenIndex + 1;
        }

        return getCompletionItems(drlParser, candidatesIndex, caretTokenIndex, compilationUnit, classIndex, prefix, memberIndex, documentPath, openFiles);
    }

    static List<CompletionItem> getCompletionItems(DRL10Parser drlParser, int nodeIndex) {
        return getCompletionItems(drlParser, nodeIndex, nodeIndex, null, ClassIndex.empty(), "", ClassMemberIndex.empty(), null, Map.of());
    }

    static List<CompletionItem> getCompletionItems(DRL10Parser drlParser, int nodeIndex, int patternTokenIndex, DRL10Parser.CompilationUnitContext compilationUnit, ClassIndex classIndex, String prefix, ClassMemberIndex memberIndex, Path documentPath, Map<Path, String> openFiles) {
        CodeCompletionCore core = new CodeCompletionCore(drlParser, PREFERRED_RULES, Tokens.IGNORED);
        CodeCompletionCore.CandidatesCollection candidates = core.collectCandidates(nodeIndex, null);

        if (candidates.rules.containsKey(DRL10Parser.RULE_consequenceBody)) {
            // in RHS consequence, parser cannot suggest DRL_RHS_END because of island mode approach, so we add it manually
            candidates.tokens.put(DRL10Lexer.DRL_RHS_END, List.of());
        }

        boolean constraintPosition = compilationUnit != null && isConstraintPosition(candidates);
        boolean statementBoundary = isStatementBoundary(candidates);

        List<CompletionItem> items = candidates.tokens.keySet().stream().filter(Objects::nonNull)
                .filter(token -> !(constraintPosition && CONSTRAINT_KEYWORD_NOISE.contains(token)))
                .filter(token -> !(statementBoundary && RULE_ATTRIBUTE_TOKENS.contains(token)))
                .map(integer -> drlParser.getVocabulary().getDisplayName(integer).replace("'", ""))
                .map(String::toLowerCase)
                .map(k -> createCompletionItem(k, CompletionItemKind.Keyword))
                .collect(Collectors.toList());

        if (compilationUnit != null && classIndex.size() > 0 && isPatternPosition(candidates)) {
            items.addAll(getClassCompletionItems(compilationUnit, classIndex, prefix));
        }

        if (constraintPosition) {
            items.addAll(getFieldCompletionItems(compilationUnit, patternTokenIndex, classIndex, memberIndex, documentPath, openFiles));
        }

        return items;
    }

    private static boolean isConstraintPosition(CodeCompletionCore.CandidatesCollection candidates) {
        List<Integer> path = candidates.rules.get(DRL10Parser.RULE_drlIdentifier);
        return path != null && path.contains(DRL10Parser.RULE_constraint);
    }

    /**
     * Whether the caret sits where a whole top-level statement can start, which
     * c3 reports by predicting the statement keywords together. A rule header
     * predicts the attributes without them, so the two positions stay distinct.
     */
    private static boolean isStatementBoundary(CodeCompletionCore.CandidatesCollection candidates) {
        return candidates.tokens.containsKey(DRL10Parser.IMPORT)
                && candidates.tokens.containsKey(DRL10Parser.DRL_RULE);
    }

    /**
     * Completion items for the fields of the pattern enclosing the caret:
     * fields of a DRL-declared type (current document first, then sibling
     * files from the active {@link WorkspaceSiblingResolver}), or bean
     * properties/fields of a classpath type resolved through imports and the
     * class index.
     */
    private static List<CompletionItem> getFieldCompletionItems(DRL10Parser.CompilationUnitContext compilationUnit,
                                                                int patternTokenIndex, ClassIndex classIndex,
                                                                ClassMemberIndex memberIndex, Path documentPath,
                                                                Map<Path, String> openFiles) {
        String patternType = findEnclosingPatternTypeName(compilationUnit, patternTokenIndex);
        if (patternType == null || patternType.isEmpty()) {
            return List.of();
        }
        String simpleName = patternType.substring(patternType.lastIndexOf('.') + 1);

        // DRL-declared types win over classpath types; fields include the
        // ones inherited through the extends chain. Resolution goes through the
        // shared workspace index so sibling (and open-buffer) declares resolve.
        List<DeclaredType> currentDocTypes =
                DRLDeclaredTypeParser.extractFromCompilationUnit(compilationUnit);
        Map<String, DeclaredType> typeIndex =
                DRLWorkspaceTypeIndex.build(currentDocTypes, documentPath, openFiles);
        DeclaredType declared = typeIndex.get(simpleName);
        if (declared != null) {
            return fieldItems(DRLDeclaredTypeParser.fieldsIncludingInherited(declared, typeIndex));
        }

        String fqcn = resolveFqcn(patternType, simpleName, compilationUnit, classIndex);
        if (fqcn == null) {
            return List.of();
        }
        return fieldItems(memberIndex.membersOf(fqcn));
    }

    /**
     * The dot-separated segments preceding a caret that sits immediately after a
     * dot ({@code $ref.order.|} → {@code [$ref, order]}), or {@code null} when the
     * caret follows something else. A numeric head is a decimal literal being
     * typed, not a path.
     */
    private static String[] dottedChainBeforeCaret(String text, Position caret) {
        if (text == null || caret == null) {
            return null;
        }
        String[] lines = text.split("\n", -1);
        int row = caret.getLine();
        if (row < 0 || row >= lines.length) {
            return null;
        }
        String line = lines[row];
        int col = Math.min(caret.getCharacter(), line.length());
        if (col == 0 || line.charAt(col - 1) != '.') {
            return null;
        }
        int start = col - 1;
        while (start > 0 && isChainChar(line.charAt(start - 1))) {
            start--;
        }
        String path = line.substring(start, col - 1);
        if (path.isEmpty() || path.endsWith(".") || Character.isDigit(path.charAt(0))) {
            return null;
        }
        return path.split("\\.", -1);
    }

    private static boolean isChainChar(char c) {
        return Character.isLetterOrDigit(c) || c == '_' || c == '$' || c == '.';
    }

    /**
     * Member items for a dotted path the caret sits at the end of. The head is a
     * binding ({@code $p.}), a field of the enclosing pattern's type ({@code ref.}
     * inside {@code Fact(...)}), or a type name ({@code Status.}); the remaining
     * segments are fields. Every hop goes through the same walker the bindings and
     * hover use, so all three agree on what a path resolves to.
     */
    private static List<CompletionItem> memberItemsForChain(String[] chain, String text, Position caret,
                                                            DRL10Parser.CompilationUnitContext compilationUnit,
                                                            int caretTokenIndex, ClassIndex classIndex,
                                                            ClassMemberIndex memberIndex, Path documentPath,
                                                            Map<Path, String> openFiles) {
        if (compilationUnit == null) {
            return List.of();
        }
        Map<String, DeclaredType> typeIndex = DRLWorkspaceTypeIndex.build(
                DRLDeclaredTypeParser.extractFromCompilationUnit(compilationUnit), documentPath, openFiles);

        String head = chain[0];
        String rootType;
        int firstFieldSegment = 1;
        if (head.startsWith("$")) {
            rootType = LhsBindingResolver.resolveAt(text, DRLHoverHelper.positionToOffset(text, caret), typeIndex)
                    .get(head.substring(1));
        } else if (!head.isEmpty() && Character.isUpperCase(head.charAt(0))) {
            rootType = head;
        } else {
            // A bare lower-case head is a field of the pattern the caret is in.
            rootType = enclosingPatternTypeFromText(text, DRLHoverHelper.positionToOffset(text, caret));
            firstFieldSegment = 0;
        }
        if (rootType == null || rootType.isEmpty()) {
            return List.of();
        }

        String resolved = rootType.substring(rootType.lastIndexOf('.') + 1);
        if (firstFieldSegment < chain.length) {
            String path = String.join(".", Arrays.copyOfRange(chain, firstFieldSegment, chain.length));
            resolved = LhsBindingResolver.resolvePath(
                    LhsBindingResolver.typeOrClasspath(resolved, typeIndex), path, typeIndex);
            if (resolved == null) {
                return List.of();
            }
        }
        return memberItemsOfType(resolved, typeIndex, compilationUnit, classIndex, memberIndex);
    }

    /**
     * The type name of the pattern whose parentheses are still open at
     * {@code offset}, read from the text rather than the parse tree: a caret in
     * mid-edit leaves the constraint incomplete, and error recovery then parses
     * the incomplete path itself as a pattern head, which
     * {@link #findEnclosingPatternTypeName} would return in preference to the
     * real pattern. Non-pattern parens ({@code accumulate(}, {@code eval(}, a
     * method call) are stepped over — a DRL pattern type's simple name always
     * begins upper case, which is also how {@code LhsBindingResolver} finds
     * pattern heads. {@code null} when no pattern encloses the offset.
     */
    private static String enclosingPatternTypeFromText(String text, int offset) {
        int closed = 0;
        for (int i = offset - 1; i >= 0; i--) {
            char c = text.charAt(i);
            if (c == ')') {
                closed++;
            } else if (c == ';' || c == '}') {
                return null;
            } else if (c == '(') {
                if (closed > 0) {
                    closed--;
                    continue;
                }
                int end = i;
                while (end > 0 && Character.isWhitespace(text.charAt(end - 1))) {
                    end--;
                }
                int start = end;
                while (start > 0 && isChainChar(text.charAt(start - 1))) {
                    start--;
                }
                String word = text.substring(start, end);
                String simple = word.substring(word.lastIndexOf('.') + 1);
                if (!simple.isEmpty() && Character.isUpperCase(simple.charAt(0))) {
                    return word;
                }
            }
        }
        return null;
    }

    /** Field items for a type name: DRL declares win, then the classpath. */
    private static List<CompletionItem> memberItemsOfType(String typeName, Map<String, DeclaredType> typeIndex,
                                                          DRL10Parser.CompilationUnitContext compilationUnit,
                                                          ClassIndex classIndex, ClassMemberIndex memberIndex) {
        String simple = typeName.substring(typeName.lastIndexOf('.') + 1);
        DeclaredType declared = typeIndex.get(simple);
        if (declared != null) {
            return fieldItems(DRLDeclaredTypeParser.fieldsIncludingInherited(declared, typeIndex));
        }
        String fqcn = resolveFqcn(typeName, simple, compilationUnit, classIndex);
        if (fqcn == null) {
            return List.of();
        }
        return fieldItems(memberIndex.membersOf(fqcn));
    }

    private static List<CompletionItem> fieldItems(List<Field> fields) {
        List<CompletionItem> items = new ArrayList<>(fields.size());
        for (Field field : fields) {
            CompletionItem item = new CompletionItem();
            item.setLabel(field.name);
            item.setInsertText(field.name);
            item.setDetail(field.type);
            item.setKind(CompletionItemKind.Field);
            items.add(item);
        }
        return items;
    }

    /**
     * Resolves a pattern's type name to a fully qualified class name.
     *
     * <p>Resolution order:
     * <ol>
     *   <li>Already qualified — returned as-is.</li>
     *   <li>Exact (non-wildcard) import match.</li>
     *   <li>Wildcard import match verified through the class index.</li>
     *   <li>Class index match for the simple name (skipped when ambiguous).</li>
     *   <li>{@code java.lang.*} — implicitly available in DRL without an import,
     *       resolved via the platform class loader.</li>
     * </ol>
     *
     * Shared with {@link DRLDefinitionHelper}.
     */
    static String resolveFqcn(String patternType, String simpleName,
                              DRL10Parser.CompilationUnitContext compilationUnit,
                              ClassIndex classIndex) {
        if (patternType.indexOf('.') >= 0) {
            return patternType;
        }
        Set<String> imports = extractImports(compilationUnit);
        // 1. Exact import.
        for (String imported : imports) {
            if (imported.endsWith("." + simpleName)) {
                return imported;
            }
        }
        // 2. Wildcard import — verify the package actually provides the type via
        //    the class index.
        for (String imported : imports) {
            if (imported.endsWith(".*")) {
                String pkg = imported.substring(0, imported.length() - 1); // keep the dot
                for (String fqcn : classIndex.getMatching(simpleName)) {
                    if (fqcn.startsWith(pkg)
                            && (fqcn.endsWith("." + simpleName) || fqcn.equals(simpleName))) {
                        return fqcn;
                    }
                }
            }
        }
        // 3. Class index (any package). An unqualified name with two classpath
        //    classes sharing a simple name is ambiguous, so it is skipped.
        Set<String> matches = new HashSet<>();
        for (String fqcn : classIndex.getMatching(simpleName)) {
            if (fqcn.endsWith("." + simpleName) || fqcn.equals(simpleName)) {
                matches.add(fqcn);
            }
        }
        if (matches.size() == 1) {
            return matches.iterator().next();
        }
        if (matches.size() > 1) {
            logger.log(Level.FINE, () -> "Ambiguous simple name '" + simpleName
                    + "' matches " + matches + "; skipping field completion");
            return null;
        }
        // 4. java.lang.* — implicitly available in Drools without an import, just
        //    as in Java source. Loadable via the platform class loader even when
        //    absent from the project Maven classpath.
        try {
            String javaLangFqcn = "java.lang." + simpleName;
            Class.forName(javaLangFqcn, false, ClassLoader.getPlatformClassLoader());
            return javaLangFqcn;
        } catch (ClassNotFoundException ignored) {
            // Not a java.lang type.
        }
        return null;
    }

    /**
     * Returns the type name of the deepest {@code lhsPattern} whose token
     * span contains {@code tokenIndex}, or {@code null} when the caret is
     * not inside a pattern. Shared with {@link DRLHoverHelper}.
     */
    static String findEnclosingPatternTypeName(ParseTree node, int tokenIndex) {
        if (!(node instanceof ParserRuleContext ctx)) {
            return null;
        }
        Token start = ctx.getStart();
        Token stop = ctx.getStop();
        if (start == null || start.getTokenIndex() > tokenIndex
                || (stop != null && stop.getTokenIndex() < tokenIndex)) {
            return null;
        }
        String best = null;
        if (ctx instanceof DRL10Parser.LhsPatternContext pattern && pattern.objectType != null) {
            best = pattern.objectType.getText();
        }
        for (int i = 0; i < ctx.getChildCount(); i++) {
            String deeper = findEnclosingPatternTypeName(ctx.getChild(i), tokenIndex);
            if (deeper != null) {
                best = deeper;
            }
        }
        return best;
    }

    private static boolean isPatternPosition(CodeCompletionCore.CandidatesCollection candidates) {
        List<Integer> path = candidates.rules.get(DRL10Parser.RULE_drlQualifiedName);
        if (path == null) {
            return false;
        }
        return path.contains(DRL10Parser.RULE_lhsPattern)
            || path.contains(DRL10Parser.RULE_lhsPatternBind);
    }

    private static List<CompletionItem> getClassCompletionItems(DRL10Parser.CompilationUnitContext compilationUnit, ClassIndex classIndex, String prefix) {
        Set<String> importedFqcns = extractImports(compilationUnit);
        List<String> matchingFqcns = classIndex.getMatching(prefix);
        List<CompletionItem> items = new ArrayList<>();

        for (String fqcn : matchingFqcns) {
            String simpleName = fqcn.substring(fqcn.lastIndexOf('.') + 1);
            CompletionItem item = new CompletionItem();
            item.setLabel(simpleName);
            item.setDetail(fqcn);
            item.setKind(CompletionItemKind.Class);
            item.setInsertText(simpleName);

            if (importedFqcns.contains(fqcn)) {
                item.setSortText("0_" + simpleName + "_" + fqcn);
            } else {
                item.setSortText("1_" + simpleName + "_" + fqcn);
            }

            items.add(item);
        }

        return items;
    }

    private static Set<String> extractImports(DRL10Parser.CompilationUnitContext compilationUnit) {
        Set<String> imports = new HashSet<>();
        for (DRL10Parser.DrlStatementdefContext stmt : compilationUnit.drlStatementdef()) {
            if (stmt.importdef() instanceof DRL10Parser.ImportStandardDefContext importDef) {
                if (importDef.DRL_FUNCTION() == null && importDef.STATIC() == null) {
                    imports.add(importDef.drlQualifiedName().getText());
                }
            }
        }
        return imports;
    }

    private static String extractPrefix(DRL10Parser drlParser, Integer nodeIndex) {
        if (nodeIndex == null || nodeIndex < 0 || nodeIndex >= drlParser.getInputStream().size()) {
            return "";
        }
        Token token = drlParser.getInputStream().get(nodeIndex);
        String text = token.getText();
        if (text != null && !text.isEmpty() && Character.isJavaIdentifierStart(text.charAt(0))) {
            return text;
        }
        return "";
    }

    static CompletionItem createCompletionItem(String label, CompletionItemKind itemKind) {
        CompletionItem completionItem;
        completionItem = new CompletionItem();
        if (label.startsWith("drl_rhs_")) {
            // when Lexer uses "DRL_RHS_" keywords in multiple modes with type(),
            // drlParser.getVocabulary().getDisplayName() returns the keyword name as-is (symbolicNames), so remove the prefix.
            label = label.substring("drl_rhs_".length());
        }
        completionItem.setInsertText(label);
        completionItem.setLabel(label);
        completionItem.setKind(itemKind);
        return completionItem;
    }
}
