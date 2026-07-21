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
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.Position;

/**
 * Find-references ({@code textDocument/references}) for DRL.
 *
 * <p>Resolves the identifier at the caret to a symbol and collects its uses
 * across the workspace via {@link DRLReferenceScanner}:
 * <ul>
 *   <li><b>Bound variable</b> ({@code $x}) — uses within the enclosing rule
 *       only (rename-safe scoping); current document.</li>
 *   <li><b>Declared type</b> — uses across the current document and its sibling
 *       {@code .drl} files (open buffers shadow disk). The {@code declare} site
 *       is included only when {@code includeDeclaration} is set.</li>
 *   <li><b>Classpath type</b> — DRL uses across files that resolve the simple
 *       name to the <em>same</em> FQCN, so a different same-named type from
 *       another package isn't swept in. The Java source is not scanned, so
 *       these are read-only references (rename is blocked elsewhere).</li>
 * </ul>
 *
 * <p>All operations are best-effort — any failure yields the references found
 * so far (typically empty).
 */
public final class DRLReferencesHelper {

    private DRLReferencesHelper() {
    }

    /**
     * Returns the locations referencing the symbol at {@code position}, or an
     * empty list when the caret is not on a resolvable symbol.
     *
     * @param openFiles          open unsaved sibling buffers keyed by path, so
     *                           cross-file references reflect unsaved edits
     * @param includeDeclaration whether to include a declared type's
     *                           {@code declare} site (LSP {@code ReferenceContext})
     */
    public static List<Location> references(String uri, String text, Position position,
                                            Map<Path, String> openFiles, ClassIndex classIndex,
                                            Set<Path> buildOutputDirs, boolean includeDeclaration) {
        if (text == null || position == null) {
            return new ArrayList<>();
        }
        // Parse the current document once; every step below reuses this parse.
        return collectReferences(uri, ParsedDrl.of(text), position, openFiles, classIndex,
                buildOutputDirs, includeDeclaration);
    }

    /** As {@link #references}, reusing an existing parse (rename shares its parse with this). */
    static List<Location> collectReferences(String uri, ParsedDrl parsed, Position position,
                                            Map<Path, String> openFiles, ClassIndex classIndex,
                                            Set<Path> buildOutputDirs, boolean includeDeclaration) {
        List<Location> out = new ArrayList<>();
        String word = DRLDefinitionHelper.wordAt(parsed.text, position);
        if (word.isEmpty()) {
            return out;
        }
        if (DRLDefinitionHelper.caretInCommentOrString(parsed, position)) {
            return out;
        }
        Path docPath = toPath(uri);

        // Bound variable: rule-scoped, current document only.
        if (word.charAt(0) == '$') {
            for (var range : DRLReferenceScanner.bindingOccurrences(parsed, position, word)) {
                out.add(new Location(uri, range));
            }
            return out;
        }
        if (!Character.isJavaIdentifierStart(word.charAt(0))) {
            return out;
        }

        // Declared type anywhere in the workspace → simple-name match across files.
        Map<String, DeclaredType> typeIndex =
                DRLWorkspaceTypeIndex.build(parsed.declaredTypes(), docPath, openFiles);
        if (typeIndex.containsKey(word)) {
            addTypeRefs(uri, DRLReferenceScanner.typeOccurrences(parsed, word), includeDeclaration, out);
            DRLWorkspaceTypeIndex.forEachSiblingFile(docPath, openFiles, (fileUri, fileText) ->
                    addTypeRefs(fileUri, DRLReferenceScanner.typeOccurrences(fileText, word),
                            includeDeclaration, out));
            return out;
        }

        // Classpath type → DRL uses in files resolving the name to the same FQCN.
        String fqcn = DRLDefinitionHelper.resolveFqcn(parsed, word, classIndex);
        if (fqcn == null) {
            return out;
        }
        addClasspathRefs(uri, parsed, word, fqcn, classIndex, out);
        DRLWorkspaceTypeIndex.forEachSiblingFile(docPath, openFiles,
                (fileUri, fileText) -> addClasspathRefs(fileUri, fileText, word, fqcn, classIndex, out));
        return out;
    }

    private static void addTypeRefs(String uri, List<DRLReferenceScanner.Occurrence> occurrences,
                                    boolean includeDeclaration, List<Location> out) {
        for (DRLReferenceScanner.Occurrence occ : occurrences) {
            if (occ.declaration && !includeDeclaration) {
                continue;
            }
            out.add(new Location(uri, occ.range));
        }
    }

    /** Current-document classpath refs, reusing the shared parse. */
    private static void addClasspathRefs(String uri, ParsedDrl parsed, String simpleName, String fqcn,
                                         ClassIndex classIndex, List<Location> out) {
        if (!parsed.text.contains(simpleName)) {
            return;
        }
        if (!fqcn.equals(DRLDefinitionHelper.resolveFqcn(parsed, simpleName, classIndex))) {
            return;
        }
        for (DRLReferenceScanner.Occurrence occ : DRLReferenceScanner.typeOccurrences(parsed, simpleName)) {
            out.add(new Location(uri, occ.range));
        }
    }

    /** Sibling-file classpath refs: cheap text pre-check, then a single parse of that sibling. */
    private static void addClasspathRefs(String uri, String text, String simpleName, String fqcn,
                                         ClassIndex classIndex, List<Location> out) {
        if (text == null || !text.contains(simpleName)) {
            return;
        }
        addClasspathRefs(uri, ParsedDrl.of(text), simpleName, fqcn, classIndex, out);
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
