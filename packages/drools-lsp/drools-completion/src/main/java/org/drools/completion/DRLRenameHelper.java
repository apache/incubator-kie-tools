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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextEdit;
import org.eclipse.lsp4j.WorkspaceEdit;

/**
 * Rename ({@code textDocument/prepareRename} + {@code textDocument/rename}) for
 * DRL, built on {@link DRLReferencesHelper}: every reference becomes a
 * {@link TextEdit}, grouped into a {@link WorkspaceEdit} by file.
 *
 * <p>Scope policy:
 * <ul>
 *   <li><b>Declared types</b> — renamed across all workspace DRL files
 *       (declaration site included).</li>
 *   <li><b>Bound variables</b> ({@code $x}) — renamed within the enclosing rule
 *       only. The new name is normalized to keep its {@code $} prefix.</li>
 *   <li><b>Classpath types</b> — <em>not</em> renameable: this server must not
 *       rewrite Java sources or the references the rest of the project has to
 *       them. {@link #prepare} returns {@code null} so the client refuses.</li>
 * </ul>
 *
 * <p>A new name that is not a legal identifier is rejected (rename yields
 * {@code null}).
 */
public final class DRLRenameHelper {

    private DRLRenameHelper() {
    }

    /** The editable span of a renameable symbol and its current text. */
    public static final class Prepared {
        public final Range range;
        public final String placeholder;

        Prepared(Range range, String placeholder) {
            this.range = range;
            this.placeholder = placeholder;
        }
    }

    /**
     * Returns the editable range + current name when the caret is on a
     * renameable symbol (a declared type or a bound variable), or {@code null}
     * otherwise (a classpath type, or nothing renameable).
     */
    public static Prepared prepare(String uri, String text, Position position,
                                   Map<Path, String> openFiles, ClassIndex classIndex,
                                   Set<Path> buildOutputDirs) {
        if (text == null || position == null) {
            return null;
        }
        String word = DRLDefinitionHelper.wordAt(text, position);
        if (word.isEmpty()) {
            return null;
        }
        ParsedDrl parsed = ParsedDrl.of(text);
        if (DRLDefinitionHelper.caretInCommentOrString(parsed, position)
                || !isRenameable(word, parsed, uri, openFiles)) {
            return null;
        }
        Range range = DRLDefinitionHelper.wordRangeAt(text, position);
        return range == null ? null : new Prepared(range, word);
    }

    /**
     * Builds the rename edit for the symbol at the caret, or {@code null} when
     * it is not renameable or {@code newName} is not a legal identifier.
     */
    public static WorkspaceEdit rename(String uri, String text, Position position, String newName,
                                       Map<Path, String> openFiles, ClassIndex classIndex,
                                       Set<Path> buildOutputDirs) {
        if (text == null || position == null || newName == null) {
            return null;
        }
        String word = DRLDefinitionHelper.wordAt(text, position);
        if (word.isEmpty()) {
            return null;
        }
        ParsedDrl parsed = ParsedDrl.of(text);

        String replacement;
        if (word.charAt(0) == '$') {
            // Bound variable: always renameable; keep the '$' prefix.
            String bare = newName.startsWith("$") ? newName.substring(1) : newName;
            if (!isIdentifier(bare)) {
                return null;
            }
            replacement = "$" + bare;
        } else {
            // Type: renameable only when declared in the workspace (not classpath).
            if (!isDeclaredType(word, parsed, uri, openFiles) || !isIdentifier(newName)) {
                return null;
            }
            replacement = newName;
        }

        List<Location> refs = DRLReferencesHelper.collectReferences(uri, parsed, position, openFiles,
                classIndex, buildOutputDirs, true);
        if (refs.isEmpty()) {
            return null;
        }
        Map<String, List<TextEdit>> changes = new LinkedHashMap<>();
        for (Location loc : refs) {
            changes.computeIfAbsent(loc.getUri(), k -> new ArrayList<>())
                   .add(new TextEdit(loc.getRange(), replacement));
        }
        return new WorkspaceEdit(changes);
    }

    private static boolean isRenameable(String word, ParsedDrl parsed, String uri,
                                        Map<Path, String> openFiles) {
        if (word.charAt(0) == '$') {
            return true; // bound variable
        }
        if (!Character.isJavaIdentifierStart(word.charAt(0))) {
            return false;
        }
        // Declared types are renameable; classpath types resolve but are not.
        return isDeclaredType(word, parsed, uri, openFiles);
    }

    private static boolean isDeclaredType(String word, ParsedDrl parsed, String uri,
                                          Map<Path, String> openFiles) {
        return DRLWorkspaceTypeIndex.build(parsed.declaredTypes(), toPath(uri), openFiles)
                .containsKey(word);
    }

    private static boolean isIdentifier(String s) {
        if (s == null || s.isEmpty() || !Character.isJavaIdentifierStart(s.charAt(0))) {
            return false;
        }
        for (int i = 1; i < s.length(); i++) {
            if (!Character.isJavaIdentifierPart(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /** Converts a document URI to a filesystem path, or null for non-file URIs. */
    private static Path toPath(String uri) {
        try {
            return Path.of(java.net.URI.create(uri));
        } catch (Exception e) {
            return null;
        }
    }
}
