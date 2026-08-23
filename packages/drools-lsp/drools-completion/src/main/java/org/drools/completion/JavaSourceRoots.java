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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Discovers Java source roots to feed {@link JavaSourceTypeIndex#build}: a
 * bounded Maven-convention walk of {@code workspaceRoot} for directories
 * ending {@code src/main/java}, unioned with each of {@code configuredPaths}
 * that resolves to an existing directory.
 */
final class JavaSourceRoots {

    private static final Logger logger = Logger.getLogger(JavaSourceRoots.class.getName());

    private static final int MAX_DEPTH = 8;
    private static final Path SRC_MAIN_JAVA = Path.of("src", "main", "java");

    private JavaSourceRoots() {
    }

    /**
     * Convention roots (dirs under {@code workspaceRoot}, depth ≤ {@value
     * #MAX_DEPTH}, ending {@code src/main/java}) unioned with {@code
     * configuredPaths} (each absolute, or resolved against {@code
     * workspaceRoot}) that exist and are directories. Both contributions are
     * always included — auto-discovery is not a fallback for configured
     * entries. Walk failures are swallowed (logged at FINE) so a partially
     * unreadable tree still yields whatever roots were found. Order:
     * convention roots in walk-encounter order, then configured entries;
     * duplicates (by normalized absolute path) are dropped.
     */
    static List<Path> discover(Path workspaceRoot, List<String> configuredPaths) {
        LinkedHashSet<Path> seen = new LinkedHashSet<>();
        List<Path> out = new ArrayList<>();

        if (workspaceRoot != null && Files.isDirectory(workspaceRoot)) {
            try (Stream<Path> walk = Files.walk(workspaceRoot, MAX_DEPTH)) {
                walk.filter(Files::isDirectory)
                        .filter(p -> p.endsWith(SRC_MAIN_JAVA))
                        .forEach(p -> addIfNew(out, seen, p));
            } catch (IOException | RuntimeException e) {
                logger.fine(() -> "Failed to walk " + workspaceRoot + " for source roots: " + e.getMessage());
            }
        }

        if (configuredPaths != null) {
            for (String configured : configuredPaths) {
                if (configured == null || configured.isBlank()) {
                    continue;
                }
                Path candidate = Path.of(configured);
                if (!candidate.isAbsolute() && workspaceRoot != null) {
                    candidate = workspaceRoot.resolve(candidate);
                }
                if (Files.isDirectory(candidate)) {
                    addIfNew(out, seen, candidate);
                }
            }
        }

        return out;
    }

    private static void addIfNew(List<Path> out, LinkedHashSet<Path> seen, Path p) {
        Path key = p.toAbsolutePath().normalize();
        if (seen.add(key)) {
            out.add(p);
        }
    }
}
