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

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.drools.drl.parser.antlr4.DRL10Parser;

/**
 * Extracts {@link DeclaredType}s ({@code declare} blocks, including declared
 * enums) from DRL source using the ANTLR parser, so completion can offer the
 * fields of types that exist only in DRL and never on the compiled classpath.
 */
public final class DRLDeclaredTypeParser {

    private static final Logger logger =
            Logger.getLogger(DRLDeclaredTypeParser.class.getName());

    private DRLDeclaredTypeParser() {
    }

    private static final class CachedEntry {
        final long modMillis;
        final List<DeclaredType> types;

        CachedEntry(long modMillis, List<DeclaredType> types) {
            this.modMillis = modMillis;
            this.types = types;
        }
    }

    /** Per-file cache keyed by normalized absolute path, valid by mtime. */
    private static final Map<Path, CachedEntry> FILE_CACHE = new ConcurrentHashMap<>();

    /**
     * Drops all cached parse results. Entries are already mtime-validated, so
     * this is about bounding memory in a long-running server rather than
     * correctness; call it when the workspace classpath is rebuilt or on
     * shutdown.
     */
    public static void clearCache() {
        FILE_CACHE.clear();
    }

    /**
     * Parses declared types from a file, serving a cached result while the
     * file's modification time is unchanged. Missing/unreadable files yield
     * an empty list.
     */
    public static List<DeclaredType> parseDeclaredTypesCached(Path file) {
        if (file == null || !Files.isRegularFile(file)) {
            return Collections.emptyList();
        }
        try {
            Path key = file.toAbsolutePath().normalize();
            long modMillis = Files.getLastModifiedTime(file).toMillis();
            CachedEntry cached = FILE_CACHE.get(key);
            if (cached != null && cached.modMillis == modMillis) {
                return cached.types;
            }
            List<DeclaredType> types =
                    Collections.unmodifiableList(parseDeclaredTypes(Files.readString(file)));
            FILE_CACHE.put(key, new CachedEntry(modMillis, types));
            return types;
        } catch (Exception e) {
            logger.fine(() -> "Failed to read/parse " + file + ": " + e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Parses all {@code declare} blocks in {@code text}. Parser errors are
     * ignored so partial files still yield partial results.
     */
    static List<DeclaredType> parseDeclaredTypes(String text) {
        try {
            DRL10Parser.CompilationUnitContext cu = DRLParsers.silent(text).compilationUnit();
            return cu == null ? new ArrayList<>() : extractFromCompilationUnit(cu);
        } catch (Exception e) {
            logger.fine(() -> "Failed to parse DRL for declared types: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Returns the type's own fields followed by the fields inherited through
     * its {@code extends} chain, resolving parents through {@code index} (a
     * {@code name -> DeclaredType} map, typically from
     * {@link DRLWorkspaceTypeIndex#build}). Cycle- and depth-guarded.
     */
    static List<Field> fieldsIncludingInherited(DeclaredType type,
                                                Map<String, DeclaredType> index) {
        List<Field> out = new ArrayList<>(type.fields);
        Set<String> seen = new HashSet<>();
        seen.add(type.name);
        String parentName = type.extendsName;
        int depth = 0;
        while (parentName != null && depth++ < 10 && seen.add(parentName)) {
            DeclaredType parent = index.get(parentName);
            if (parent == null) {
                break;
            }
            out.addAll(parent.fields);
            parentName = parent.extendsName;
        }
        return out;
    }

    /**
     * Extracts declared types from an already-parsed compilation unit. Use
     * this overload when the caller already produced the parse tree (e.g.
     * during completion) to avoid a redundant second parse.
     */
    static List<DeclaredType> extractFromCompilationUnit(DRL10Parser.CompilationUnitContext cu) {
        List<DeclaredType> types = new ArrayList<>();
        if (cu == null) {
            return types;
        }
        for (DRL10Parser.DrlStatementdefContext stmt : cu.drlStatementdef()) {
            DRL10Parser.DeclaredefContext decl = stmt.declaredef();
            if (decl == null) {
                continue;
            }
            try {
                if (decl.typeDeclaration() != null) {
                    DeclaredType dt = extractTypeDeclaration(decl.typeDeclaration());
                    if (dt != null) {
                        types.add(dt);
                    }
                } else if (decl.enumDeclaration() != null) {
                    DeclaredType dt = extractEnumDeclaration(decl.enumDeclaration());
                    if (dt != null) {
                        types.add(dt);
                    }
                }
                // entryPointDeclaration and windowDeclaration are not class types.
            } catch (Exception e) {
                logger.fine(() -> "Skipping malformed declare block: " + e.getMessage());
            }
        }
        return types;
    }

    private static DeclaredType extractTypeDeclaration(DRL10Parser.TypeDeclarationContext ctx) {
        if (ctx == null || ctx.name == null) {
            return null;
        }
        String name = ctx.name.getText();
        int nameLine = ctx.name.getStart() != null ? ctx.name.getStart().getLine() - 1 : 0;
        int nameCol = ctx.name.getStart() != null ? ctx.name.getStart().getCharPositionInLine() : 0;
        List<Field> fields = extractFields(ctx.field());
        String extendsName = null;
        if (ctx.superTypes != null && !ctx.superTypes.isEmpty()) {
            String raw = ctx.superTypes.get(0).getText();
            int dot = raw == null ? -1 : raw.lastIndexOf('.');
            extendsName = (raw != null && dot >= 0) ? raw.substring(dot + 1) : raw;
        }
        return new DeclaredType(name, fields, false, nameLine, nameCol, extendsName);
    }

    private static DeclaredType extractEnumDeclaration(DRL10Parser.EnumDeclarationContext ctx) {
        if (ctx == null || ctx.name == null) {
            return null;
        }
        String name = ctx.name.getText();
        int nameLine = ctx.name.getStart() != null ? ctx.name.getStart().getLine() - 1 : 0;
        int nameCol = ctx.name.getStart() != null ? ctx.name.getStart().getCharPositionInLine() : 0;
        List<Field> fields = new ArrayList<>();
        // Enum constants carry the enum type name as their type.
        if (ctx.enumeratives() != null) {
            for (DRL10Parser.EnumerativeContext enumerative : ctx.enumeratives().enumerative()) {
                if (enumerative.drlIdentifier() != null) {
                    fields.add(new Field(enumerative.drlIdentifier().getText(), name,
                                         extractEnumArgs(enumerative)));
                }
            }
        }
        fields.addAll(extractFields(ctx.field()));
        return new DeclaredType(name, fields, true, nameLine, nameCol);
    }

    /**
     * Returns the comma-separated constructor arguments of an enum constant
     * ({@code LOW(1, "x")} → {@code 1, "x"}), or {@code null} when the
     * constant has no argument list.
     */
    private static String extractEnumArgs(DRL10Parser.EnumerativeContext ctx) {
        if (ctx == null || ctx.expression() == null || ctx.expression().isEmpty()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ctx.expression().size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(ctx.expression(i).getText());
        }
        return sb.toString();
    }

    private static List<Field> extractFields(List<DRL10Parser.FieldContext> fieldCtxs) {
        List<Field> fields = new ArrayList<>();
        if (fieldCtxs == null) {
            return fields;
        }
        for (DRL10Parser.FieldContext field : fieldCtxs) {
            try {
                if (field.label() == null || field.type() == null) {
                    continue;
                }
                // label().getText() returns "name:" — strip the trailing colon.
                String rawLabel = field.label().getText();
                String fieldName = rawLabel.endsWith(":")
                        ? rawLabel.substring(0, rawLabel.length() - 1).trim()
                        : rawLabel.trim();
                String fieldType = field.type().getText();
                if (!fieldName.isEmpty() && !fieldType.isEmpty()) {
                    fields.add(new Field(fieldName, fieldType));
                }
            } catch (Exception e) {
                logger.fine(() -> "Skipping malformed field: " + e.getMessage());
            }
        }
        return fields;
    }
}
