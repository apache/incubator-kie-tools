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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MavenClasspathResolver {

    private static final Logger logger = Logger.getLogger(MavenClasspathResolver.class.getName());

    public static Set<Path> resolve(Path rootDir) {
        Set<Path> classpathEntries = new LinkedHashSet<>();
        List<Path> pomFiles = findPomFiles(rootDir);

        for (Path pom : pomFiles) {
            Path moduleDir = pom.getParent();
            try {
                Set<Path> moduleCp = resolveModule(moduleDir);
                classpathEntries.addAll(moduleCp);
            } catch (Exception e) {
                logger.log(Level.WARNING, "Failed to resolve classpath for " + pom + ": " + e.getMessage());
            }
        }

        return classpathEntries;
    }

    /**
     * Returns only the modules' compiled-output directories (target/classes and
     * the like), located via filesystem conventions without invoking {@code mvn}.
     *
     * <p>This is the fast half of {@link #resolve(Path)}: it lets the server index
     * the project's own classes immediately, before the slower dependency-JAR
     * resolution (which shells out to {@code mvn}) has completed.
     */
    public static Set<Path> resolveBuildOutputDirs(Path rootDir) {
        Set<Path> dirs = new LinkedHashSet<>();
        for (Path pom : findPomFiles(rootDir)) {
            dirs.addAll(BuildOutputLocator.findClassDirs(pom.getParent()));
        }
        return dirs;
    }

    // Matches <module>some/path</module> entries in a Maven POM.
    private static final Pattern MODULE_ELEMENT = Pattern.compile("<module>([^<]+)</module>");

    /**
     * Returns the pom.xml files whose modules form the classpath.
     *
     * <p>When a root {@code pom.xml} exists, resolution is scoped to it: the root
     * plus any modules it explicitly declares. Walking the full workspace tree is
     * intentionally avoided in that case — it would pull in adjacent projects'
     * dependencies and contaminate the class index.
     *
     * <p>When there is no root {@code pom.xml} (a pom-less or non-standard
     * layout), it falls back to scanning the tree for pom.xml files (skipping
     * {@code target/} and hidden directories) so completion/hover still get a
     * best-effort classpath.
     */
    static List<Path> findPomFiles(Path rootDir) {
        Path rootPom = rootDir.resolve("pom.xml");
        if (!Files.exists(rootPom)) {
            logger.fine(() -> "No pom.xml at " + rootDir + "; falling back to tree scan");
            return walkForPomFiles(rootDir);
        }

        List<Path> result = new ArrayList<>();
        result.add(rootPom);

        // Read declared <module> entries from the root POM and add each module's
        // pom.xml. Handles multi-module projects without recursing into unrelated
        // sibling directories.
        String pomContent;
        try {
            pomContent = Files.readString(rootPom);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Failed to read root pom.xml: " + rootPom, e);
            return result;
        }
        Matcher m = MODULE_ELEMENT.matcher(pomContent);
        while (m.find()) {
            String modulePath = m.group(1).trim();
            Path modulePom = rootDir.resolve(modulePath).resolve("pom.xml");
            if (Files.exists(modulePom)) {
                result.add(modulePom);
            } else {
                logger.warning(() -> "Declared module pom.xml not found: " + modulePom);
            }
        }
        return result;
    }

    /**
     * Fallback used when no root {@code pom.xml} is present: walks {@code rootDir}
     * for every pom.xml, skipping {@code target/} and hidden directories.
     */
    private static List<Path> walkForPomFiles(Path rootDir) {
        List<Path> result = new ArrayList<>();
        try {
            Files.walkFileTree(rootDir, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    String dirName = dir.getFileName().toString();
                    if (dirName.equals("target") || dirName.startsWith(".")) {
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    if (file.getFileName().toString().equals("pom.xml")) {
                        result.add(file);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            logger.log(Level.WARNING, "Failed to walk directory tree: " + rootDir, e);
        }
        return result;
    }

    private static Set<Path> resolveModule(Path moduleDir) {
        Set<Path> entries = new LinkedHashSet<>();

        // The module's own compiled output -- located via filesystem conventions,
        // so this works even when mvn is unavailable and copes with non-standard
        // build layouts.
        entries.addAll(BuildOutputLocator.findClassDirs(moduleDir));

        // Dependency JARs -- best-effort via mvn; skipped gracefully when mvn is
        // absent, offline, or otherwise fails.
        resolveDependencyClasspath(moduleDir, entries);

        return entries;
    }

    private static void resolveDependencyClasspath(Path moduleDir, Set<Path> entries) {
        Path cpFile;
        try {
            cpFile = Files.createTempFile("drools-lsp-cp-", ".txt");
        } catch (IOException e) {
            logger.log(Level.WARNING, "Failed to create temp file for classpath resolution", e);
            return;
        }
        try {
            String mvnCommand = System.getProperty("os.name").toLowerCase().contains("win") ? "mvn.cmd" : "mvn";
            ProcessBuilder pb = new ProcessBuilder(
                mvnCommand, "-f", moduleDir.resolve("pom.xml").toString(),
                "dependency:build-classpath",
                "-Dmdep.outputFile=" + cpFile.toAbsolutePath()
            );
            pb.redirectErrorStream(true);
            Process process = pb.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                while (reader.readLine() != null) {
                    // drain
                }
            }

            boolean finished = process.waitFor(60, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                logger.warning("mvn dependency:build-classpath timed out for " + moduleDir);
                return;
            }
            if (process.exitValue() != 0) {
                logger.warning("mvn dependency:build-classpath failed for " + moduleDir + " with exit code " + process.exitValue());
                return;
            }

            String cpContent = Files.readString(cpFile).trim();
            if (!cpContent.isEmpty()) {
                Arrays.stream(cpContent.split(File.pathSeparator))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Path::of)
                    .forEach(entries::add);
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to resolve dependency classpath for " + moduleDir + ": " + e.getMessage());
        } finally {
            try { Files.deleteIfExists(cpFile); } catch (IOException ignored) {}
        }
    }
}
