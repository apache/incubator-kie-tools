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
import java.util.Set;
import java.util.logging.Logger;

import org.antlr.v4.runtime.Token;
import org.drools.drl.parser.antlr4.DRL10Lexer;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;

/**
 * Go-to-definition for type names in DRL documents.
 *
 * <p>Resolution order — DRL first, then Java:
 * <ol>
 *   <li>{@code declare} blocks in the current document;</li>
 *   <li>{@code declare} blocks in sibling DRL files from the active
 *       {@link WorkspaceSiblingResolver} (same-directory by default;
 *       non-file documents have no siblings);</li>
 *   <li>project Java sources located by Maven convention: when the resolved
 *       classpath contains {@code <module>/target/classes/pkg/Type.class},
 *       the definition is {@code <module>/src/main/java/pkg/Type.java} if
 *       that file exists. JAR classes have no navigable source.</li>
 * </ol>
 */
public final class DRLDefinitionHelper {

    private static final Logger logger =
            Logger.getLogger(DRLDefinitionHelper.class.getName());

    private DRLDefinitionHelper() {
    }

    /**
     * Returns the definition locations for the identifier at
     * {@code position}, or an empty list when it doesn't name a resolvable
     * type.
     *
     * @param buildOutputDirs build-output directories from the resolved
     *                        classpath, used for the Maven source-mapping
     */
    public static List<Location> findDefinitions(String uri, String text, Position position,
                                                 ClassIndex classIndex, Set<Path> buildOutputDirs) {
        return findDefinitions(uri, text, position, classIndex, buildOutputDirs, Map.of());
    }

    /**
     * @param openFiles open unsaved sibling buffers keyed by path, so a
     *                  definition in an unsaved sibling resolves to its current
     *                  (edited) location; may be empty
     */
    public static List<Location> findDefinitions(String uri, String text, Position position,
                                                 ClassIndex classIndex, Set<Path> buildOutputDirs,
                                                 Map<Path, String> openFiles) {
        if (text == null || position == null) {
            return List.of();
        }
        String word = wordAt(text, position);
        if (word.isEmpty() || !Character.isJavaIdentifierStart(word.charAt(0))) {
            return List.of();
        }
        // Parse the current document once; the steps below reuse this parse.
        ParsedDrl parsed = ParsedDrl.of(text);
        if (caretInCommentOrString(parsed, position)) {
            return List.of();
        }

        // 1. declare blocks in this document.
        for (DeclaredType declared : parsed.declaredTypes()) {
            if (word.equals(declared.name)) {
                return List.of(new Location(uri, nameRange(declared, word)));
            }
        }

        // 2. declare blocks in sibling DRL files (open buffers shadow disk).
        Location sibling = findSiblingDefinition(word, toPath(uri), openFiles);
        if (sibling != null) {
            return List.of(sibling);
        }

        // 3. Java sources by Maven convention.
        String fqcn = resolveFqcn(parsed, word, classIndex);
        if (fqcn == null) {
            return List.of();
        }
        JavaSourceLocator.Result javaSource = JavaSourceLocator.locate(fqcn, buildOutputDirs);
        return javaSource == null ? List.of() : List.of(javaSource.location);
    }

    /**
     * Finds {@code word}'s declaration among sibling files via the shared
     * {@link DRLWorkspaceTypeIndex} walk (open unsaved buffers shadow disk),
     * returning its {@link Location} or {@code null}. First match wins.
     */
    private static Location findSiblingDefinition(String word, Path documentPath,
                                                  Map<Path, String> openFiles) {
        if (documentPath == null) {
            return null;
        }
        Location[] hit = {null};
        DRLWorkspaceTypeIndex.forEachSiblingType(documentPath, openFiles, (declared, fileUri) -> {
            if (hit[0] == null && word.equals(declared.name)) {
                hit[0] = new Location(fileUri, nameRange(declared, word));
            }
        });
        return hit[0];
    }

    private static Range nameRange(DeclaredType declared, String word) {
        return new Range(new Position(declared.nameLine, declared.nameCol),
                         new Position(declared.nameLine, declared.nameCol + word.length()));
    }

    /** Converts a document URI to a filesystem path, or null for non-file URIs. */
    private static Path toPath(String uri) {
        try {
            return Path.of(java.net.URI.create(uri));
        } catch (Exception e) {
            return null;
        }
    }

    /** Resolves {@code word} to an FQCN from {@code text}'s imports + class index. Shared with type hierarchy. */
    static String resolveFqcn(String text, String word, ClassIndex classIndex) {
        try {
            return resolveFqcn(ParsedDrl.of(text), word, classIndex);
        } catch (Exception e) {
            logger.fine(() -> "FQCN resolution failed for " + word + ": " + e.getMessage());
            return null;
        }
    }

    /** As {@link #resolveFqcn(String, String, ClassIndex)}, reusing an existing parse of the document. */
    static String resolveFqcn(ParsedDrl parsed, String word, ClassIndex classIndex) {
        try {
            return DRLCompletionHelper.resolveFqcn(word, word, parsed.compilationUnit, classIndex);
        } catch (Exception e) {
            logger.fine(() -> "FQCN resolution failed for " + word + ": " + e.getMessage());
            return null;
        }
    }

    /** Expands the identifier ({@code [A-Za-z0-9_$]+}) around the caret. */
    static String wordAt(String text, Position position) {
        String[] lines = text.split("\r?\n", -1);
        if (position.getLine() < 0 || position.getLine() >= lines.length) {
            return "";
        }
        String line = lines[position.getLine()];
        int col = Math.min(Math.max(position.getCharacter(), 0), line.length());

        int start = col;
        while (start > 0 && isIdentifierChar(line.charAt(start - 1))) {
            start--;
        }
        int end = col;
        while (end < line.length() && isIdentifierChar(line.charAt(end))) {
            end++;
        }
        return start < end ? line.substring(start, end) : "";
    }

    /**
     * The range of the identifier ({@code [A-Za-z0-9_$]+}) around the caret, or
     * {@code null} when the caret is not on one. Same expansion as
     * {@link #wordAt}; used by rename to mark the editable span. Shared with
     * {@link DRLRenameHelper}.
     */
    static Range wordRangeAt(String text, Position position) {
        if (text == null || position == null) {
            return null;
        }
        String[] lines = text.split("\r?\n", -1);
        if (position.getLine() < 0 || position.getLine() >= lines.length) {
            return null;
        }
        String line = lines[position.getLine()];
        int col = Math.min(Math.max(position.getCharacter(), 0), line.length());

        int start = col;
        while (start > 0 && isIdentifierChar(line.charAt(start - 1))) {
            start--;
        }
        int end = col;
        while (end < line.length() && isIdentifierChar(line.charAt(end))) {
            end++;
        }
        if (start >= end) {
            return null;
        }
        return new Range(new Position(position.getLine(), start), new Position(position.getLine(), end));
    }

    private static boolean isIdentifierChar(char c) {
        return Character.isLetterOrDigit(c) || c == '_' || c == '$';
    }

    /**
     * True when the caret sits inside a comment or string-literal token, where an
     * identifier-looking word is not an actual symbol occurrence. Shared by
     * go-to-definition, find-references, and rename so they don't act on a name
     * that merely appears in prose. Reuses the document's already-built token
     * stream — no extra parse or lex. Best-effort: a caret that doesn't resolve
     * to a token is treated as code, returning {@code false}.
     */
    static boolean caretInCommentOrString(ParsedDrl parsed, Position position) {
        if (parsed == null || position == null) {
            return false;
        }
        Token token = parsed.tokenAt(position);
        return token != null && isCommentOrString(token.getType());
    }

    /** Lexer token types carrying comment or string-literal text, on both the LHS and the RHS consequence. */
    private static boolean isCommentOrString(int tokenType) {
        switch (tokenType) {
            case DRL10Lexer.COMMENT:
            case DRL10Lexer.LINE_COMMENT:
            case DRL10Lexer.STRING_LITERAL:
            case DRL10Lexer.DRL_STRING_LITERAL:
            case DRL10Lexer.TEXT_BLOCK:
            case DRL10Lexer.CHAR_LITERAL:
            case DRL10Lexer.RHS_COMMENT:
            case DRL10Lexer.RHS_LINE_COMMENT:
            case DRL10Lexer.RHS_STRING_LITERAL:
                return true;
            default:
                return false;
        }
    }
}
