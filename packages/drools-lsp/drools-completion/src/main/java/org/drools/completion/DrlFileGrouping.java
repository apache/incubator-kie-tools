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
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * A workspace's resolved DRL file groups: group name to the files that compile
 * together, and the reverse lookup used on the completion path.
 *
 * <p>Immutable. Rebuilt when the workspace root changes or a config file is
 * edited, never mutated in place.
 */
final class DrlFileGrouping {

    private static final Logger logger = Logger.getLogger(DrlFileGrouping.class.getName());

    static final DrlFileGrouping EMPTY = new DrlFileGrouping(Map.of(), Map.of());

    private final Map<String, WorkspaceSiblingResolver.Group> groupsByName;
    private final Map<Path, String> groupByFile;

    private DrlFileGrouping(Map<String, WorkspaceSiblingResolver.Group> groupsByName,
                            Map<Path, String> groupByFile) {
        this.groupsByName = groupsByName;
        this.groupByFile = groupByFile;
    }

    boolean isEmpty() {
        return groupsByName.isEmpty();
    }

    /** Files in {@code groupName}, in declaration order; empty when unknown. */
    List<Path> filesFor(String groupName) {
        WorkspaceSiblingResolver.Group group = groupsByName.get(groupName);
        return (group == null) ? List.of() : group.files();
    }

    /** The group {@code file} belongs to, or {@code null} when it is ungrouped. */
    String groupFor(Path file) {
        return (file == null) ? null : groupByFile.get(file.toAbsolutePath().normalize());
    }

    Map<String, WorkspaceSiblingResolver.Group> asMap() {
        return groupsByName;
    }

    /**
     * Resolves declarations into concrete file lists.
     *
     * <p>Declarations sharing a name are merged rather than shadowing one
     * another — several config files legitimately contribute to one group, and
     * silently keeping only the first hides half of it. Their patterns and files
     * are unioned and a warning names the group, because a merge is just as
     * often two groups that accidentally share a name.
     *
     * <p>A file claimed by more than one group is indexed to the first that
     * claims it, so lookup stays deterministic; the ambiguity is surfaced to the
     * user through {@link #asMap()} rather than resolved silently here.
     */
    static DrlFileGrouping resolve(List<KieBaseDecl> declarations, List<Path> drlFiles, Path workspaceRoot) {
        if (declarations.isEmpty()) {
            return EMPTY;
        }
        Map<String, KieBaseDecl> merged = mergeByName(declarations);

        PackageIndex packages = new PackageIndex(workspaceRoot);
        boolean anySelectsByPackage = merged.values().stream().anyMatch(KieBaseDecl::selectsByPackage);

        Map<String, WorkspaceSiblingResolver.Group> groupsByName = new LinkedHashMap<>();
        Map<Path, String> groupByFile = new LinkedHashMap<>();

        for (KieBaseDecl decl : merged.values()) {
            Set<Path> files = new LinkedHashSet<>(decl.files());
            if (anySelectsByPackage && decl.selectsByPackage()) {
                for (Path drl : drlFiles) {
                    if (KieBasePackages.matchesWithIncludes(decl, merged, packages.packageOf(drl))) {
                        files.add(drl);
                    }
                }
            }
            if (files.isEmpty()) {
                continue;
            }
            List<Path> ordered = List.copyOf(files);
            groupsByName.put(decl.name(), new WorkspaceSiblingResolver.Group(
                    decl.name(), ordered, decl.origin().kind(), decl.origin().declaredIn()));
            for (Path file : ordered) {
                groupByFile.putIfAbsent(file, decl.name());
            }
        }

        logger.fine(() -> "Resolved " + groupsByName.size() + " DRL file group(s) covering "
                + groupByFile.size() + " file(s)");
        return new DrlFileGrouping(
                Collections.unmodifiableMap(groupsByName),
                Collections.unmodifiableMap(groupByFile));
    }

    private static Map<String, KieBaseDecl> mergeByName(List<KieBaseDecl> declarations) {
        Map<String, KieBaseDecl> merged = new LinkedHashMap<>();
        for (KieBaseDecl decl : declarations) {
            merged.merge(decl.name(), decl, (existing, addition) -> {
                logger.warning("DRL file group '" + existing.name() + "' is declared more than once;"
                        + " the declarations are being merged. Rename one of them if they were"
                        + " meant to be separate groups.");
                return existing.mergedWith(addition);
            });
        }
        return merged;
    }

    /** Lazily resolves each DRL file's package, reading a given file at most once. */
    private static final class PackageIndex {

        private final Map<Path, String> cache = new HashMap<>();
        private final Path workspaceRoot;

        PackageIndex(Path workspaceRoot) {
            this.workspaceRoot = workspaceRoot;
        }

        String packageOf(Path drlFile) {
            return cache.computeIfAbsent(drlFile, this::read);
        }

        private String read(Path drlFile) {
            try {
                String declared = DrlPackageReader.declaredPackage(Files.readString(drlFile));
                if (declared != null) {
                    return declared;
                }
            } catch (Exception e) {
                logger.fine(() -> "Could not read " + drlFile + " for its package declaration: " + e.getMessage());
            }
            return DrlPackageReader.packageFromPath(drlFile, workspaceRoot);
        }
    }

}
