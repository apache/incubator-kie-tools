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
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A single bounded walk of the workspace collecting everything the grouping
 * layer needs: DRL files, {@code kmodule.xml} descriptors, and grouping config
 * files.
 *
 * <p>One walk rather than one per source — this runs on the server's
 * initialization path, and a workspace walk is the most expensive thing the
 * grouping layer does. Directories that never contain hand-written rules
 * ({@code node_modules}, {@code .git}, build output) are pruned rather than
 * filtered, so their subtrees are never entered at all.
 */
final class WorkspaceScan {

    private static final Logger logger = Logger.getLogger(WorkspaceScan.class.getName());

    /** Config file naming the groups in a workspace. */
    static final String CONFIG_FILE_NAME = "drl-lsp-kbases.json";

    /**
     * Directories never worth walking into: version-control metadata and
     * dependency caches, which cannot contain hand-written rules.
     */
    private static final Set<String> NEVER_WALKED = Set.of("node_modules", ".git", ".svn", ".hg");

    /**
     * Path fragments identifying a build's copy of a resource tree. A DRL found
     * beneath one is a build artifact of a source file that also exists, and
     * grouping the copy would shadow the file the user actually edits.
     *
     * <p>Unlike a list of directory names, these are specific enough to mean
     * something: they are the same roots {@link DrlPackageReader} recognizes when
     * deriving a package from a path.
     */
    private static final String[] BUILD_OUTPUT_ROOTS = {
            "/target/classes/",
            "/target/test-classes/",
            "/build/resources/",
            "/out/production/",
            "/BOOT-INF/classes/",
    };

    /** Depth bound, counted from the workspace root. */
    private static final int MAX_DEPTH = 24;

    private final List<Path> drlFiles;
    private final List<Path> kmoduleFiles;
    private final List<Path> configFiles;

    private WorkspaceScan(List<Path> drlFiles, List<Path> kmoduleFiles, List<Path> configFiles) {
        this.drlFiles = Collections.unmodifiableList(drlFiles);
        this.kmoduleFiles = Collections.unmodifiableList(kmoduleFiles);
        this.configFiles = Collections.unmodifiableList(configFiles);
    }

    static WorkspaceScan empty() {
        return new WorkspaceScan(List.of(), List.of(), List.of());
    }

    List<Path> drlFiles() {
        return drlFiles;
    }

    List<Path> kmoduleFiles() {
        return kmoduleFiles;
    }

    List<Path> configFiles() {
        return configFiles;
    }

    /**
     * Classifies a file list supplied by the client instead of walking.
     *
     * <p>Preferred over {@link #of}: a client knows which files its user
     * considers part of the project — VS Code's {@code findFiles} applies
     * {@code files.exclude}, {@code search.exclude} and the workspace's ignore
     * files — so nothing here has to guess, or keep a guess up to date.
     */
    static WorkspaceScan ofProvided(List<Path> files) {
        Collector collector = new Collector();
        for (Path file : files) {
            if (file != null) {
                collector.classify(file.toAbsolutePath().normalize());
            }
        }
        WorkspaceScan scan = new WorkspaceScan(collector.drlFiles, collector.kmoduleFiles,
                collector.configFilesAsList());
        logger.fine(() -> "Client supplied " + files.size() + " path(s): " + scan.drlFiles.size()
                + " DRL, " + scan.kmoduleFiles.size() + " kmodule.xml, " + scan.configFiles.size()
                + " " + CONFIG_FILE_NAME);
        return scan;
    }

    /**
     * Walks {@code workspaceRoot}, returning what was found. Fallback for a
     * client that supplies no file list. An unreadable subtree is skipped rather
     * than failing the scan: partial grouping beats none, and the
     * same-directory fallback still covers whatever is missed.
     */
    static WorkspaceScan of(Path workspaceRoot) {
        if (workspaceRoot == null || !Files.isDirectory(workspaceRoot)) {
            return empty();
        }
        Collector collector = new Collector();
        try {
            Files.walkFileTree(workspaceRoot, Set.of(), MAX_DEPTH, collector);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Workspace scan for DRL file grouping did not complete: " + workspaceRoot, e);
        }
        WorkspaceScan scan = new WorkspaceScan(collector.drlFiles, collector.kmoduleFiles,
                collector.configFilesAsList());
        logger.fine(() -> "Workspace scan: " + scan.drlFiles.size() + " DRL file(s), "
                + scan.kmoduleFiles.size() + " kmodule.xml, " + scan.configFiles.size() + " "
                + CONFIG_FILE_NAME);
        return scan;
    }

    /** Files and directories matched by the globs of an adopted grouping source. */
    record GlobMatches(List<Path> files, List<Path> directories) {

        static GlobMatches empty() {
            return new GlobMatches(List.of(), List.of());
        }
    }

    /**
     * A second, opt-in walk that resolves the {@code include} and
     * {@code pathsRelativeTo} globs of adopted grouping sources. Only runs when
     * a workspace actually declares such a source, so the common case pays for
     * one walk, not two.
     *
     * <p>Globs are matched against paths relative to {@code workspaceRoot},
     * always with forward slashes, so one config file reads the same on every
     * platform.
     */
    static GlobMatches matching(Path workspaceRoot, List<String> fileGlobs, List<String> directoryGlobs) {
        if (workspaceRoot == null || !Files.isDirectory(workspaceRoot)
                || (fileGlobs.isEmpty() && directoryGlobs.isEmpty())) {
            return GlobMatches.empty();
        }
        List<PathMatcher> fileMatchers = compile(fileGlobs);
        List<PathMatcher> directoryMatchers = compile(directoryGlobs);
        Path root = workspaceRoot.toAbsolutePath().normalize();

        List<Path> files = new ArrayList<>();
        List<Path> directories = new ArrayList<>();
        try {
            Files.walkFileTree(root, Set.of(), MAX_DEPTH, new FileVisitor<Path>() {

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    Path name = dir.getFileName();
                    if (name != null && NEVER_WALKED.contains(name.toString().toLowerCase(Locale.ROOT))) {
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                    if (matchesAny(directoryMatchers, root, dir)) {
                        directories.add(dir.toAbsolutePath().normalize());
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    if (attrs.isRegularFile() && matchesAny(fileMatchers, root, file)) {
                        files.add(file.toAbsolutePath().normalize());
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            logger.log(Level.WARNING, "Glob scan for adopted grouping sources did not complete: " + root, e);
        }
        return new GlobMatches(Collections.unmodifiableList(files), Collections.unmodifiableList(directories));
    }

    /**
     * Narrows already-scanned paths to those matching one glob, so a set of
     * adopted sources can share a single walk and still each see only its own
     * matches.
     */
    static List<Path> filterByGlob(Path workspaceRoot, List<Path> candidates, String glob) {
        List<PathMatcher> matcher = compile(List.of(glob));
        if (matcher.isEmpty() || workspaceRoot == null) {
            return List.of();
        }
        Path root = workspaceRoot.toAbsolutePath().normalize();
        List<Path> matched = new ArrayList<>();
        for (Path candidate : candidates) {
            if (matchesAny(matcher, root, candidate)) {
                matched.add(candidate);
            }
        }
        return Collections.unmodifiableList(matched);
    }

    /** Whether {@code file} sits under a build's copy of a resource tree. */
    private static boolean isBuildOutput(Path file) {
        String normalized = file.toString().replace('\\', '/');
        for (String root : BUILD_OUTPUT_ROOTS) {
            if (normalized.contains(root)) {
                return true;
            }
        }
        return false;
    }

    private static List<PathMatcher> compile(List<String> globs) {
        List<PathMatcher> matchers = new ArrayList<>(globs.size());
        for (String glob : globs) {
            try {
                matchers.add(FileSystems.getDefault().getPathMatcher("glob:" + glob.replace('\\', '/')));
            } catch (Exception e) {
                logger.warning("Ignoring malformed grouping glob '" + glob + "': " + e.getMessage());
            }
        }
        return matchers;
    }

    private static boolean matchesAny(List<PathMatcher> matchers, Path root, Path candidate) {
        if (matchers.isEmpty()) {
            return false;
        }
        Path relative;
        try {
            relative = root.relativize(candidate.toAbsolutePath().normalize());
        } catch (IllegalArgumentException e) {
            return false;
        }
        Path forwardSlashed = Path.of(relative.toString().replace('\\', '/'));
        for (PathMatcher matcher : matchers) {
            if (matcher.matches(forwardSlashed)) {
                return true;
            }
        }
        return false;
    }

    private static final class Collector implements FileVisitor<Path> {

        private final List<Path> drlFiles = new ArrayList<>();
        private final List<Path> kmoduleFiles = new ArrayList<>();
        private final Set<Path> configFiles = new LinkedHashSet<>();

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
            Path name = dir.getFileName();
            if (name != null && NEVER_WALKED.contains(name.toString().toLowerCase(Locale.ROOT))) {
                return FileVisitResult.SKIP_SUBTREE;
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
            if (attrs.isRegularFile()) {
                classify(file.toAbsolutePath().normalize());
            }
            return FileVisitResult.CONTINUE;
        }

        /** Files a build produced are skipped wherever the path came from. */
        private void classify(Path absolute) {
            Path nameElement = absolute.getFileName();
            if (nameElement == null || isBuildOutput(absolute)) {
                return;
            }
            String name = nameElement.toString();
            if (name.toLowerCase(Locale.ROOT).endsWith(".drl")) {
                drlFiles.add(absolute);
            } else if (name.equals("kmodule.xml")) {
                kmoduleFiles.add(absolute);
            } else if (name.equals(CONFIG_FILE_NAME)) {
                configFiles.add(absolute);
            }
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
            return FileVisitResult.CONTINUE;
        }

        private List<Path> configFilesAsList() {
            return new ArrayList<>(configFiles);
        }
    }
}
