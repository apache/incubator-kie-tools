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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.SymbolKind;
import org.eclipse.lsp4j.TypeHierarchyItem;

/**
 * Type hierarchy ({@code textDocument/prepareTypeHierarchy} +
 * {@code typeHierarchy/supertypes} / {@code subtypes}) for DRL types.
 *
 * <ul>
 *   <li><b>Prepare</b> resolves the identifier at the caret to its declaration:
 *       a DRL {@code declare} (current doc or sibling) → its {@code .drl}
 *       declare-site; or a classpath type → its project {@code .java} source
 *       (navigable only — JAR-only types resolve to nothing).</li>
 *   <li><b>Supertypes</b>: a declare's {@code extends} parent (declare → {@code .drl},
 *       Java parent → reflected ancestry, navigable-only); a classpath type's
 *       direct superclass + interfaces via {@link ClassMemberIndex#supertypesOf}.</li>
 *   <li><b>Subtypes</b>: reachable declares (current doc + siblings) whose
 *       {@code extends} names this type. Classpath subtypes aren't enumerable
 *       without a full classpath scan — see the note in {@link #subtypes}.</li>
 * </ul>
 *
 * <p>An item's {@code data} carries the classpath FQCN for classpath items;
 * declare items leave it {@code null} and are re-resolved from their {@code uri}
 * (the declaring {@code .drl}) + {@code name}. All operations are best-effort —
 * any failure yields an empty list.
 */
public final class DRLTypeHierarchyHelper {

    private static final Logger logger = Logger.getLogger(DRLTypeHierarchyHelper.class.getName());

    private DRLTypeHierarchyHelper() {
    }

    /** A declared type together with the URI of the file that declares it. */
    private static final class Declared {
        final String uri;
        final DeclaredType type;

        Declared(String uri, DeclaredType type) {
            this.uri = uri;
            this.type = type;
        }
    }

    /**
     * Resolves the type at {@code position} to a single root {@link TypeHierarchyItem},
     * or an empty list when the caret isn't on a navigable type.
     */
    public static List<TypeHierarchyItem> prepare(String text, Position position, String uri,
                                                  Map<Path, String> openFiles, ClassIndex classIndex,
                                                  Set<Path> buildOutputDirs) {
        try {
            if (text == null || position == null) {
                return Collections.emptyList();
            }
            String word = DRLDefinitionHelper.wordAt(text, position);
            if (word.isEmpty() || !Character.isJavaIdentifierStart(word.charAt(0))) {
                return Collections.emptyList();
            }
            Declared declared = locateDeclare(word, text, uri, openFiles);
            if (declared != null) {
                return Collections.singletonList(declareItem(declared));
            }
            TypeHierarchyItem classpath = classpathItem(
                    DRLDefinitionHelper.resolveFqcn(text, word, classIndex), buildOutputDirs);
            return classpath == null ? Collections.emptyList() : Collections.singletonList(classpath);
        } catch (Exception e) {
            logger.fine(() -> "prepareTypeHierarchy failed: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Direct supertypes of {@code item}. {@code declaringText} is the text of the
     * item's own file (for declares); classpath items ignore it.
     */
    public static List<TypeHierarchyItem> supertypes(TypeHierarchyItem item, String declaringText,
                                                     Map<Path, String> openFiles, ClassIndex classIndex,
                                                     ClassMemberIndex memberIndex, Set<Path> buildOutputDirs) {
        try {
            String fqcn = fqcnData(item);
            if (fqcn != null) {
                // Classpath type: direct superclass + interfaces, navigable-only.
                List<TypeHierarchyItem> out = new ArrayList<>();
                for (String parent : memberIndex.supertypesOf(fqcn)) {
                    TypeHierarchyItem parentItem = classpathItem(parent, buildOutputDirs);
                    if (parentItem != null) {
                        out.add(parentItem);
                    }
                }
                return out;
            }
            // Declare: resolve its single `extends` parent.
            DeclaredType self = findDeclare(declaringText, item.getName());
            if (self == null || self.extendsName == null) {
                return Collections.emptyList();
            }
            Declared parentDeclare = locateDeclare(self.extendsName, declaringText, item.getUri(), openFiles);
            if (parentDeclare != null) {
                return Collections.singletonList(declareItem(parentDeclare));
            }
            TypeHierarchyItem classpathParent = classpathItem(
                    DRLDefinitionHelper.resolveFqcn(declaringText, self.extendsName, classIndex), buildOutputDirs);
            return classpathParent == null ? Collections.emptyList()
                    : Collections.singletonList(classpathParent);
        } catch (Exception e) {
            logger.fine(() -> "typeHierarchy/supertypes failed: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Direct subtypes of {@code item}: reachable declares (the item's file +
     * siblings) whose {@code extends} names it. {@code declaringText} is the
     * item's own file text.
     */
    public static List<TypeHierarchyItem> subtypes(TypeHierarchyItem item, String declaringText,
                                                   Map<Path, String> openFiles) {
        try {
            if (fqcnData(item) != null) {
                // build-out: classpath subtypes would need a full classpath scan
                // (an index of class -> direct subclasses); not enumerable here.
                return Collections.emptyList();
            }
            String name = item.getName();
            List<TypeHierarchyItem> out = new ArrayList<>();
            for (DeclaredType dt : DRLDeclaredTypeParser.parseDeclaredTypes(declaringText)) {
                if (name.equals(dt.extendsName)) {
                    out.add(declareItem(new Declared(item.getUri(), dt)));
                }
            }
            Path docPath = toPath(item.getUri());
            if (docPath != null) {
                DRLWorkspaceTypeIndex.forEachSiblingType(docPath, openFiles, (dt, fileUri) -> {
                    if (name.equals(dt.extendsName)) {
                        out.add(declareItem(new Declared(fileUri, dt)));
                    }
                });
            }
            return out;
        } catch (Exception e) {
            logger.fine(() -> "typeHierarchy/subtypes failed: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    /** Finds {@code name}'s declaration in the current document, then siblings. */
    private static Declared locateDeclare(String name, String text, String uri,
                                          Map<Path, String> openFiles) {
        for (DeclaredType dt : DRLDeclaredTypeParser.parseDeclaredTypes(text)) {
            if (name.equals(dt.name)) {
                return new Declared(uri, dt);
            }
        }
        Path docPath = toPath(uri);
        if (docPath == null) {
            return null;
        }
        Declared[] hit = {null};
        DRLWorkspaceTypeIndex.forEachSiblingType(docPath, openFiles, (dt, fileUri) -> {
            if (hit[0] == null && name.equals(dt.name)) {
                hit[0] = new Declared(fileUri, dt);
            }
        });
        return hit[0];
    }

    private static DeclaredType findDeclare(String text, String name) {
        if (text == null) {
            return null;
        }
        for (DeclaredType dt : DRLDeclaredTypeParser.parseDeclaredTypes(text)) {
            if (name.equals(dt.name)) {
                return dt;
            }
        }
        return null;
    }

    private static TypeHierarchyItem declareItem(Declared d) {
        Range range = nameRange(d.type);
        SymbolKind kind = d.type.isEnum ? SymbolKind.Enum : SymbolKind.Class;
        TypeHierarchyItem item = new TypeHierarchyItem(d.type.name, kind, d.uri, range, range);
        if (d.type.extendsName != null) {
            item.setDetail("extends " + d.type.extendsName);
        }
        return item;  // data left null → re-resolved from uri + name
    }

    /** Builds a navigable classpath item, or {@code null} when no project source resolves. */
    private static TypeHierarchyItem classpathItem(String fqcn, Set<Path> buildOutputDirs) {
        if (fqcn == null) {
            return null;
        }
        JavaSourceLocator.Result res = JavaSourceLocator.locate(fqcn, buildOutputDirs);
        if (res == null) {
            return null;
        }
        TypeHierarchyItem item = new TypeHierarchyItem(simpleName(fqcn), res.kind,
                res.location.getUri(), res.location.getRange(), res.location.getRange());
        item.setData(fqcn);
        item.setDetail(fqcn);
        return item;
    }

    private static Range nameRange(DeclaredType type) {
        return new Range(new Position(type.nameLine, type.nameCol),
                         new Position(type.nameLine, type.nameCol + type.name.length()));
    }

    /**
     * True when {@code item} is a classpath type (an FQCN is stashed in
     * {@code data}). Such items resolve via reflection and ignore the file text
     * and sibling buffers, so callers can skip computing those.
     */
    public static boolean isClasspathItem(TypeHierarchyItem item) {
        return fqcnData(item) != null;
    }

    /** The classpath FQCN stashed in {@code data}, or {@code null} for a declare item. */
    private static String fqcnData(TypeHierarchyItem item) {
        Object data = item.getData();
        if (data == null) {
            return null;
        }
        if (data instanceof String s) {
            return s.isEmpty() ? null : s;
        }
        if (data instanceof com.google.gson.JsonPrimitive primitive) {
            return primitive.getAsString();
        }
        return data.toString();
    }

    private static String simpleName(String fqcn) {
        int dot = fqcn.lastIndexOf('.');
        return dot >= 0 ? fqcn.substring(dot + 1) : fqcn;
    }

    private static Path toPath(String uri) {
        try {
            return Path.of(java.net.URI.create(uri));
        } catch (Exception e) {
            return null;
        }
    }
}
