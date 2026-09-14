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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Central, layered view of the declared types reachable from a DRL document.
 * This is the single place the workspace type lookup lives, so every consumer
 * — hover, completion, go-to-definition (and, later, inlay hints) — shares one
 * notion of "what types exist and where", regardless of which layer resolved a
 * given type.
 *
 * <p>Layers, highest priority first:
 * <ol>
 *   <li>the current document;</li>
 *   <li>open <em>unsaved</em> buffers of the sibling files the active
 *       {@link WorkspaceSiblingResolver} yields — their editor content is
 *       newer than disk, so it shadows the on-disk version;</li>
 *   <li>the remaining sibling files, read from disk.</li>
 * </ol>
 *
 * <p>The earliest layer to provide a given name wins. All on-disk reads go
 * through {@link DRLDeclaredTypeParser}'s mtime-keyed cache, so this is cheap
 * to call per request.
 */
public final class DRLWorkspaceTypeIndex {

    private DRLWorkspaceTypeIndex() {
    }

    /**
     * Builds the {@code name -> DeclaredType} index for {@code text}. Equivalent
     * to {@link #build(List, Path, Map)} with {@code text}'s parsed declares as
     * the current-document layer.
     */
    public static Map<String, DeclaredType> build(String text, Path documentPath,
                                                  Map<Path, String> openFiles) {
        return build(DRLDeclaredTypeParser.parseDeclaredTypes(text), documentPath, openFiles);
    }

    /**
     * Builds the {@code name -> DeclaredType} index from already-parsed
     * current-document types (the overload completion uses, since it has the
     * compilation unit in hand and need not re-parse) plus the sibling layers.
     */
    public static Map<String, DeclaredType> build(List<DeclaredType> currentDocTypes,
                                                  Path documentPath, Map<Path, String> openFiles) {
        Map<String, DeclaredType> byName = new HashMap<>();
        if (currentDocTypes != null) {
            for (DeclaredType t : currentDocTypes) {
                putType(byName, t);
            }
        }
        forEachSiblingType(documentPath, openFiles, (t, uri) -> putType(byName, t));
        return byName;
    }

    /**
     * Builds a {@code typeName -> markdown href} map so doc-comment
     * {@code {@link}} references render as clickable links to the declaration.
     * Each href has the form {@code file:///abs/path#L<line>} (1-based line) —
     * the anchor VSCode hovers understand. Coverage mirrors {@link #build}'s
     * layers; classpath types are intentionally absent (no source to navigate
     * to). Returns an empty map when there is nothing navigable.
     */
    public static Map<String, String> buildLinkTargets(String text, Path documentPath,
                                                        Map<Path, String> openFiles) {
        return buildLinkTargets(DRLDeclaredTypeParser.parseDeclaredTypes(text), text,
                documentPath, openFiles);
    }

    /**
     * As {@link #buildLinkTargets(String, Path, Map)}, reusing the already-parsed
     * current-document declares instead of re-parsing {@code text}.
     */
    public static Map<String, String> buildLinkTargets(List<DeclaredType> currentDocTypes, String text,
                                                        Path documentPath, Map<Path, String> openFiles) {
        Map<String, String> targets = new HashMap<>();
        if (documentPath != null && text != null && !text.isEmpty()) {
            String uri = documentPath.toUri().toString();
            for (DeclaredType t : currentDocTypes) {
                addTarget(targets, t, uri);
            }
        }
        forEachSiblingType(documentPath, openFiles, (t, uri) -> addTarget(targets, t, uri));
        return targets;
    }

    /**
     * Returns the doc comment for declared type {@code name}, read from the
     * file that declares it (current document, then open buffers, then disk),
     * or {@code null} when the type is absent or undocumented. Buffer-aware so
     * an unsaved edit to a sibling's doc is reflected. Extraction is delegated
     * to {@link DRLDocCommentParser}.
     */
    public static String docFor(String name, String text, Path documentPath,
                                Map<Path, String> openFiles) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        return docFor(name, DRLDeclaredTypeParser.parseDeclaredTypes(text), text, documentPath, openFiles);
    }

    /**
     * As {@link #docFor(String, String, Path, Map)}, reusing the already-parsed
     * current-document declares for the layer-1 check instead of re-parsing.
     */
    public static String docFor(String name, List<DeclaredType> currentDocTypes, String text,
                                Path documentPath, Map<Path, String> openFiles) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        // Layer 1: current document.
        if (containsName(currentDocTypes, name)) {
            return DRLDocCommentParser.docFor(text, name);
        }
        String[] declaring = {null};
        forEachSibling(documentPath, openFiles,
                (sibling, bufferText) -> {
                    if (declaring[0] == null && declaresType(bufferText, name)) {
                        declaring[0] = bufferText;
                    }
                },
                sibling -> {
                    if (declaring[0] == null
                            && containsName(DRLDeclaredTypeParser.parseDeclaredTypesCached(sibling), name)) {
                        declaring[0] = readFileSilently(sibling);
                    }
                });
        return declaring[0] == null ? null : DRLDocCommentParser.docFor(declaring[0], name);
    }

    /**
     * Visits every declared type reachable through the sibling layers (open
     * unsaved buffers first, then on-disk siblings not shadowed by a buffer),
     * passing each type and its file URI to {@code sink}. The current document
     * is <em>not</em> included — callers handle that layer themselves.
     */
    static void forEachSiblingType(Path documentPath, Map<Path, String> openFiles,
                                   BiConsumer<DeclaredType, String> sink) {
        forEachSiblingInfo(documentPath, openFiles, (info, uri) -> {
            for (DeclaredType t : info.types) {
                sink.accept(t, uri);
            }
        });
    }

    /**
     * Visits each sibling once — open unsaved buffers first, then on-disk
     * siblings not shadowed by a buffer — passing its package, imports and
     * declared types together with its file URI to {@code sink}, so a consumer
     * needing more than one of them (the unknown-type lint wants both declares
     * and same-package imports) parses each sibling a single time. The current
     * document is <em>not</em> included.
     */
    static void forEachSiblingInfo(Path documentPath, Map<Path, String> openFiles,
                                   BiConsumer<DRLDeclaredTypeParser.FileInfo, String> sink) {
        forEachSibling(documentPath, openFiles,
                (sibling, bufferText) -> sink.accept(
                        DRLDeclaredTypeParser.parseFileInfo(bufferText), sibling.toUri().toString()),
                sibling -> sink.accept(
                        DRLDeclaredTypeParser.cachedFileInfo(sibling), sibling.toUri().toString()));
    }

    /**
     * Visits each sibling {@code .drl} file reachable through the workspace
     * layers — open unsaved buffers first (their editor text shadows disk),
     * then on-disk siblings not shadowed by a buffer — passing the file's URI
     * and its <em>current</em> text to {@code sink}. The current document is
     * <em>not</em> included; callers scan that themselves. Used by
     * find-references / rename, which need each sibling's raw text rather than
     * its parsed declares.
     */
    static void forEachSiblingFile(Path documentPath, Map<Path, String> openFiles,
                                   BiConsumer<String, String> sink) {
        forEachSibling(documentPath, openFiles,
                (sibling, bufferText) -> sink.accept(sibling.toUri().toString(), bufferText),
                sibling -> {
                    String content = readFileSilently(sibling);
                    if (content != null) {
                        sink.accept(sibling.toUri().toString(), content);
                    }
                });
    }

    /**
     * Visits each sibling the active {@link WorkspaceSiblingResolver} yields for
     * {@code documentPath} exactly once, by normalized absolute path: those with
     * an open buffer go to {@code openBuffer} with their editor text, in
     * resolver order; the rest then go to {@code onDisk}. Membership is the
     * resolver's alone — an open buffer outside its grouping is not a sibling.
     */
    private static void forEachSibling(Path documentPath, Map<Path, String> openFiles,
                                       BiConsumer<Path, String> openBuffer, Consumer<Path> onDisk) {
        if (documentPath == null) {
            return;
        }
        Map<Path, String> buffers = new HashMap<>();
        if (openFiles != null) {
            for (Map.Entry<Path, String> e : openFiles.entrySet()) {
                if (e.getKey() != null && e.getValue() != null) {
                    buffers.put(e.getKey().toAbsolutePath().normalize(), e.getValue());
                }
            }
        }
        List<Path> siblings = new ArrayList<>();
        for (Path sibling : WorkspaceSiblingResolvers.active().resolveSiblings(documentPath)) {
            siblings.add(sibling.toAbsolutePath().normalize());
        }
        for (Path sibling : siblings) {
            String bufferText = buffers.get(sibling);
            if (bufferText != null) {
                openBuffer.accept(sibling, bufferText);
            }
        }
        for (Path sibling : siblings) {
            if (!buffers.containsKey(sibling)) {
                onDisk.accept(sibling);
            }
        }
    }

    private static void putType(Map<String, DeclaredType> byName, DeclaredType t) {
        if (t != null && t.name != null) {
            byName.putIfAbsent(t.name, t);
        }
    }

    private static void addTarget(Map<String, String> targets, DeclaredType t, String fileUri) {
        if (t != null && t.name != null && fileUri != null) {
            // VSCode hover markdown supports file:///...#L<n> for line anchors.
            targets.putIfAbsent(t.name, fileUri + "#L" + (t.nameLine + 1));
        }
    }

    private static boolean declaresType(String text, String name) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        return containsName(DRLDeclaredTypeParser.parseDeclaredTypes(text), name);
    }

    private static boolean containsName(List<DeclaredType> types, String name) {
        if (types != null) {
            for (DeclaredType t : types) {
                if (name.equals(t.name)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static String readFileSilently(Path file) {
        try {
            return Files.readString(file);
        } catch (Exception e) {
            return null;
        }
    }
}
