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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Indexes {@code .java} source under a workspace's source roots via {@link
 * JavaSourceTypeParser}, so completion/hover/lint can resolve types and
 * members before a compile exists. An instance is an immutable snapshot;
 * callers rebuild via {@link #build} on workspace changes rather than
 * mutating one in place — the same model {@code ClassIndex} uses.
 */
public final class JavaSourceTypeIndex implements JavaMemberSource {

    private static final Logger logger = Logger.getLogger(JavaSourceTypeIndex.class.getName());

    private static final JavaSourceTypeIndex EMPTY =
            new JavaSourceTypeIndex(Set.of(), Map.of(), Map.of(), Map.of());

    private static final class CachedEntry {
        final long modMillis;
        final List<JavaSourceType> types;

        CachedEntry(long modMillis, List<JavaSourceType> types) {
            this.modMillis = modMillis;
            this.types = types;
        }
    }

    /** Per-file cache keyed by normalized absolute path, valid by mtime. Shared across builds. */
    private static final Map<Path, CachedEntry> FILE_CACHE = new ConcurrentHashMap<>();

    /**
     * Drops all cached parse results. Entries are already mtime-validated, so
     * this is about bounding memory in a long-running server rather than
     * correctness; call it when the workspace source roots are rebuilt or on
     * shutdown.
     */
    public static void clearCache() {
        FILE_CACHE.clear();
    }

    private final Set<Path> roots;
    private final Map<String, List<String>> classNames;
    private final Map<String, JavaSourceType> typesByFqcn;
    private final Map<String, Path> fileByFqcn;

    private JavaSourceTypeIndex(Set<Path> roots, Map<String, List<String>> classNames,
                                 Map<String, JavaSourceType> typesByFqcn, Map<String, Path> fileByFqcn) {
        this.roots = roots;
        this.classNames = classNames;
        this.typesByFqcn = typesByFqcn;
        this.fileByFqcn = fileByFqcn;
    }

    /** An index over no source roots; resolves nothing. */
    public static JavaSourceTypeIndex empty() {
        return EMPTY;
    }

    /**
     * Walks each of {@code sourceRoots} for {@code .java} files, parses each
     * (via the mtime cache) into its top-level types, and keeps those whose
     * package passes {@code packageFilters} — a type's package is its FQCN
     * minus the last segment; it passes when {@code packageFilters} is empty,
     * when a filter equals the package exactly, or when a filter ends with
     * {@code *} and the package starts with the prefix before it. A
     * default-package type passes only when {@code packageFilters} is empty.
     * On a duplicate FQCN across roots, the first one seen wins (walk order);
     * later ones are logged at FINE and dropped.
     */
    public static JavaSourceTypeIndex build(Set<Path> sourceRoots, List<String> packageFilters) {
        List<String> filters = packageFilters == null ? List.of() : packageFilters;
        Set<Path> usedRoots = new LinkedHashSet<>();
        Map<String, JavaSourceType> typesByFqcn = new LinkedHashMap<>();
        Map<String, Path> fileByFqcn = new LinkedHashMap<>();

        if (sourceRoots != null) {
            for (Path root : sourceRoots) {
                if (root == null || !Files.isDirectory(root)) {
                    continue;
                }
                usedRoots.add(root);
                indexRoot(root, filters, typesByFqcn, fileByFqcn);
            }
        }

        // The roots are the one fact needed to explain everything else this
        // index reports — including duplicate-FQCN drops, which are expected
        // when two roots legitimately see the same file and alarming otherwise.
        if (!usedRoots.isEmpty()) {
            logger.info("Indexed " + typesByFqcn.size() + " Java source type(s) from "
                    + usedRoots.size() + " root(s): " + usedRoots);
        }

        Map<String, List<String>> classNames = new LinkedHashMap<>();
        for (JavaSourceType type : typesByFqcn.values()) {
            classNames.computeIfAbsent(type.simpleName, k -> new ArrayList<>()).add(type.fqcn);
        }
        Map<String, List<String>> classNamesOut = new LinkedHashMap<>();
        for (Map.Entry<String, List<String>> entry : classNames.entrySet()) {
            classNamesOut.put(entry.getKey(), List.copyOf(entry.getValue()));
        }

        return new JavaSourceTypeIndex(Set.copyOf(usedRoots), Map.copyOf(classNamesOut),
                Map.copyOf(typesByFqcn), Map.copyOf(fileByFqcn));
    }

    /**
     * Indexes files one at a time (rather than collecting the walk to a list
     * first) so a failure partway through the walk — an unreadable
     * subdirectory, say — still leaves everything found before it in {@code
     * typesByFqcn}/{@code fileByFqcn}; only the remainder of this root is
     * lost.
     */
    private static void indexRoot(Path root, List<String> filters,
                                   Map<String, JavaSourceType> typesByFqcn, Map<String, Path> fileByFqcn) {
        try (Stream<Path> walk = Files.walk(root)) {
            walk.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".java"))
                    .forEach(file -> indexFile(file, filters, typesByFqcn, fileByFqcn));
        } catch (IOException | RuntimeException e) {
            logger.fine(() -> "Failed to walk source root " + root + ": " + e.getMessage());
        }
    }

    private static void indexFile(Path file, List<String> filters,
                                   Map<String, JavaSourceType> typesByFqcn, Map<String, Path> fileByFqcn) {
        for (JavaSourceType type : cachedParse(file)) {
            if (!passesFilter(type.fqcn, filters)) {
                continue;
            }
            if (typesByFqcn.containsKey(type.fqcn)) {
                logger.fine(() -> "Duplicate FQCN " + type.fqcn + " from " + file + " ignored (first wins)");
                continue;
            }
            typesByFqcn.put(type.fqcn, type);
            fileByFqcn.put(type.fqcn, file);
        }
    }

    private static boolean passesFilter(String fqcn, List<String> filters) {
        if (filters.isEmpty()) {
            return true;
        }
        int dot = fqcn.lastIndexOf('.');
        String pkg = dot >= 0 ? fqcn.substring(0, dot) : "";
        if (pkg.isEmpty()) {
            return false;
        }
        for (String filter : filters) {
            if (filter.endsWith("*")) {
                if (pkg.startsWith(filter.substring(0, filter.length() - 1))) {
                    return true;
                }
            } else if (pkg.equals(filter)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Parses a file's top-level types, serving a cached result while the
     * file's modification time is unchanged. Missing/unreadable files yield
     * an empty list.
     */
    private static List<JavaSourceType> cachedParse(Path file) {
        if (file == null || !Files.isRegularFile(file)) {
            return List.of();
        }
        try {
            Path key = file.toAbsolutePath().normalize();
            long modMillis = Files.getLastModifiedTime(file).toMillis();
            CachedEntry cached = FILE_CACHE.get(key);
            if (cached != null && cached.modMillis == modMillis) {
                return cached.types;
            }
            List<JavaSourceType> types = JavaSourceTypeParser.parse(Files.readString(file));
            FILE_CACHE.put(key, new CachedEntry(modMillis, types));
            return types;
        } catch (Exception e) {
            logger.fine(() -> "Failed to read/parse " + file + ": " + e.getMessage());
            return List.of();
        }
    }

    /** Simple name → FQCNs, for merging into {@code ClassIndex}'s own name map. */
    public Map<String, List<String>> classNames() {
        return classNames;
    }

    /** The parsed model for {@code fqcn}, or {@code null} when unknown. */
    public JavaSourceType byFqcn(String fqcn) {
        return fqcn == null ? null : typesByFqcn.get(fqcn);
    }

    /** The source file {@code fqcn} was parsed from, or {@code null} when unknown. */
    public Path fileOf(String fqcn) {
        return fqcn == null ? null : fileByFqcn.get(fqcn);
    }

    /** The source roots this index was built from, for logging/tests. */
    public Set<Path> roots() {
        return roots;
    }

    @Override
    public List<Field> membersOf(String fqcn) {
        JavaSourceType type = byFqcn(fqcn);
        return type == null ? List.of() : List.copyOf(membersIncludingInherited(fqcn, type).values());
    }

    @Override
    public Set<String> memberNames(String fqcn) {
        JavaSourceType type = byFqcn(fqcn);
        if (type == null) {
            return null;
        }
        return new LinkedHashSet<>(membersIncludingInherited(fqcn, type).keySet());
    }

    /**
     * Returns {@code type}'s own members followed by those of its ancestors,
     * walked through {@code extendsSimpleName} within this index only, so a
     * source-only subclass shows the same member set the reflection path
     * (which walks {@code getMethods()}/superclass automatically) would show
     * once a build replaces it. Deduped by name via {@code putIfAbsent} — own
     * members and nearer ancestors win over farther ones. Depth-capped at 10
     * and cycle-guarded by a seen-FQCN set, mirroring {@code
     * DRLDeclaredTypeParser#fieldsIncludingInherited}'s shape. Stops (rather
     * than guessing) the moment a parent's simple name can't be resolved to
     * exactly one FQCN in this index.
     */
    private Map<String, Field> membersIncludingInherited(String fqcn, JavaSourceType type) {
        Map<String, Field> members = new LinkedHashMap<>();
        for (Field f : type.members) {
            members.putIfAbsent(f.name, f);
        }
        Set<String> seen = new HashSet<>();
        seen.add(fqcn);
        String currentFqcn = fqcn;
        String parentSimpleName = type.extendsSimpleName;
        int depth = 0;
        while (parentSimpleName != null && depth++ < 10) {
            String parentFqcn = resolveParentFqcn(currentFqcn, parentSimpleName);
            if (parentFqcn == null || !seen.add(parentFqcn)) {
                break;
            }
            JavaSourceType parent = typesByFqcn.get(parentFqcn);
            if (parent == null) {
                break;
            }
            for (Field f : parent.members) {
                members.putIfAbsent(f.name, f);
            }
            currentFqcn = parentFqcn;
            parentSimpleName = parent.extendsSimpleName;
        }
        return members;
    }

    /**
     * Resolves {@code parentSimpleName} (an {@code extends} target) to an
     * FQCN within this index: first the same package as {@code childFqcn}
     * (the common case), else — only when {@code classNames()} maps the
     * simple name to exactly one FQCN — that unique cross-package match.
     * Returns {@code null} (stop the walk) rather than guess among several
     * same-named candidates.
     */
    private String resolveParentFqcn(String childFqcn, String parentSimpleName) {
        int dot = childFqcn.lastIndexOf('.');
        String childPackage = dot >= 0 ? childFqcn.substring(0, dot) : "";
        String samePackageCandidate = childPackage.isEmpty()
                ? parentSimpleName : childPackage + "." + parentSimpleName;
        if (typesByFqcn.containsKey(samePackageCandidate)) {
            return samePackageCandidate;
        }
        List<String> candidates = classNames.get(parentSimpleName);
        return (candidates != null && candidates.size() == 1) ? candidates.get(0) : null;
    }

    /**
     * Returns {@code fqcn}'s direct supertypes — its {@code extends} target
     * followed by its directly-implemented interfaces — resolved to FQCNs via
     * {@link #resolveParentFqcn} (same package first, else a unique
     * cross-package simple-name match within this index). A supertype that
     * can't be resolved that way is omitted rather than guessed at, mirroring
     * {@link #membersIncludingInherited}'s stop-don't-guess discipline.
     */
    @Override
    public List<String> supertypesOf(String fqcn) {
        JavaSourceType type = byFqcn(fqcn);
        if (type == null) {
            return List.of();
        }
        List<String> out = new ArrayList<>();
        if (type.extendsSimpleName != null) {
            String resolved = resolveParentFqcn(fqcn, type.extendsSimpleName);
            if (resolved != null) {
                out.add(resolved);
            }
        }
        for (String interfaceSimpleName : type.interfaceSimpleNames) {
            String resolved = resolveParentFqcn(fqcn, interfaceSimpleName);
            if (resolved != null) {
                out.add(resolved);
            }
        }
        return out;
    }

    @Override
    public List<String> constructorsOf(String fqcn) {
        JavaSourceType type = byFqcn(fqcn);
        return type == null ? List.of() : type.constructors;
    }
}
