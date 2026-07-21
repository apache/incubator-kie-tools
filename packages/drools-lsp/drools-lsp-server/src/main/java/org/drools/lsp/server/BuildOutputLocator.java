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

package org.drools.lsp.server;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Locates a module's compiled-output directories using filesystem conventions
 * only -- no build tool is invoked and no extra dependencies are required.
 *
 * <p>This lets class indexing pick up the project's own compiled classes even
 * when {@code mvn} is not available on the {@code PATH}, and copes with
 * non-standard or non-Maven build layouts (Gradle, IDE output dirs, custom
 * output roots).
 *
 * <p>Dependency JARs are out of scope here -- those are resolved separately by
 * {@link MavenClasspathResolver}.
 */
public class BuildOutputLocator {

    private static final Logger logger = Logger.getLogger(BuildOutputLocator.class.getName());

    /** Conventional output directories, relative to a module root, in priority order. */
    private static final List<String> CONVENTIONAL_OUTPUT_DIRS = List.of(
        "target/classes",            // Maven (main)
        "target/test-classes",       // Maven (test)
        "build/classes/java/main",   // Gradle (main)
        "build/classes/java/test",   // Gradle (test)
        "out/production/classes",    // IntelliJ
        "bin/main",                  // Eclipse / Buildship
        "bin"                        // Eclipse (classic)
    );

    /** Subdirectory names that mark a build-tool intermediate rather than a package root. */
    private static final Set<String> LANGUAGE_SUBDIRS = Set.of("java", "kotlin", "groovy", "scala");

    /** Maximum depth, relative to the module root, for the fallback scan. */
    private static final int MAX_FALLBACK_DEPTH = 8;

    private BuildOutputLocator() {
    }

    /**
     * Returns the existing compiled-output directories for {@code moduleDir},
     * de-duplicated and in priority order.
     *
     * <p>Conventional locations are probed first. Only if none exist does a
     * bounded scan look for a custom output root: a directory named
     * {@code classes} that actually contains compiled output. Arbitrary custom
     * output directory names cannot be detected without reading build metadata,
     * which is intentionally out of scope for this dependency-free locator.
     */
    public static List<Path> findClassDirs(Path moduleDir) {
        Set<Path> dirs = new LinkedHashSet<>();

        for (String relative : CONVENTIONAL_OUTPUT_DIRS) {
            Path candidate = moduleDir.resolve(relative);
            if (Files.isDirectory(candidate)) {
                dirs.add(candidate.normalize());
            }
        }

        if (dirs.isEmpty()) {
            dirs.addAll(scanForClassDirs(moduleDir));
        }

        return new ArrayList<>(dirs);
    }

    private static Set<Path> scanForClassDirs(Path moduleDir) {
        Set<Path> found = new LinkedHashSet<>();
        try {
            Files.walkFileTree(moduleDir, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    Path name = dir.getFileName();
                    String dirName = name == null ? "" : name.toString();

                    if (dirName.startsWith(".") || dirName.equals("src") || dirName.equals("node_modules")) {
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                    if (moduleDir.relativize(dir).getNameCount() > MAX_FALLBACK_DEPTH) {
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                    if (dirName.equals("classes") && !hasLanguageSubdir(dir) && containsClassFile(dir)) {
                        found.add(dir.normalize());
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            logger.log(Level.WARNING, "Failed to scan for class directories under " + moduleDir, e);
        }
        return found;
    }

    private static boolean hasLanguageSubdir(Path dir) {
        try (Stream<Path> children = Files.list(dir)) {
            return children.anyMatch(p -> Files.isDirectory(p)
                && LANGUAGE_SUBDIRS.contains(p.getFileName().toString()));
        } catch (IOException e) {
            return false;
        }
    }

    private static boolean containsClassFile(Path dir) {
        try (Stream<Path> walk = Files.walk(dir)) {
            return walk.anyMatch(p -> p.toString().endsWith(".class") && Files.isRegularFile(p));
        } catch (IOException e) {
            return false;
        }
    }
}
