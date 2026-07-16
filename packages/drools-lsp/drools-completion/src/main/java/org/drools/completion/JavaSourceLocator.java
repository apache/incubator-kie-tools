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
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.SymbolKind;

/**
 * Maps a classpath FQCN to its project Maven source file, when present, along
 * with the declaration's range and kind. Shared by go-to-definition and type
 * hierarchy: when the resolved classpath contains
 * {@code <module>/target/classes/pkg/Type.class}, the source is
 * {@code <module>/src/main/java/pkg/Type.java} if it exists.
 *
 * <p>JAR classes (and anything without project source) yield {@code null} — i.e.
 * no navigable location.
 */
final class JavaSourceLocator {

    private static final Logger logger = Logger.getLogger(JavaSourceLocator.class.getName());

    /** {@code class|interface|enum|record <Simple>}; %s is the quoted simple name. */
    private static final String TYPE_DECL_TEMPLATE = "\\b(class|interface|enum|record)\\s+(%s)\\b";

    private JavaSourceLocator() {
    }

    /** A resolved Java source: its {@link Location} plus the declaration's symbol kind. */
    static final class Result {
        final Location location;
        final SymbolKind kind;

        Result(Location location, SymbolKind kind) {
            this.location = location;
            this.kind = kind;
        }
    }

    /**
     * Resolves {@code fqcn} to its project Maven source, or {@code null} when no
     * source is found (JAR-only classes, or no build-output directories).
     */
    static Result locate(String fqcn, Set<Path> buildOutputDirs) {
        if (fqcn == null || fqcn.isEmpty() || buildOutputDirs == null || buildOutputDirs.isEmpty()) {
            return null;
        }
        String relClass = fqcn.replace('.', '/') + ".class";
        String relJava = fqcn.replace('.', '/') + ".java";
        String simpleName = fqcn.substring(fqcn.lastIndexOf('.') + 1);

        for (Path outputDir : buildOutputDirs) {
            if (!Files.isRegularFile(outputDir.resolve(relClass))) {
                continue;
            }
            // <module>/target/classes → <module>
            Path target = outputDir.getParent();
            Path module = target == null ? null : target.getParent();
            if (module == null) {
                continue;
            }
            Path javaFile = module.resolve("src/main/java").resolve(relJava);
            if (!Files.isRegularFile(javaFile)) {
                continue;
            }
            return readDeclaration(javaFile, simpleName);
        }
        return null;
    }

    private static Result readDeclaration(Path javaFile, String simpleName) {
        Pattern decl = Pattern.compile(String.format(TYPE_DECL_TEMPLATE, Pattern.quote(simpleName)));
        String uri = javaFile.toUri().toString();
        try {
            List<String> lines = Files.readAllLines(javaFile);
            for (int i = 0; i < lines.size(); i++) {
                Matcher m = decl.matcher(lines.get(i));
                if (m.find()) {
                    Range range = new Range(new Position(i, m.start(2)),
                                            new Position(i, m.start(2) + simpleName.length()));
                    return new Result(new Location(uri, range), kindOf(m.group(1)));
                }
            }
        } catch (Exception e) {
            logger.fine(() -> "Could not locate declaration in " + javaFile + ": " + e.getMessage());
        }
        // Source exists but the declaration line wasn't found — anchor at the top.
        return new Result(new Location(uri, new Range(new Position(0, 0), new Position(0, 0))),
                          SymbolKind.Class);
    }

    private static SymbolKind kindOf(String keyword) {
        switch (keyword) {
            case "interface":
                return SymbolKind.Interface;
            case "enum":
                return SymbolKind.Enum;
            default:
                return SymbolKind.Class;  // class, record
        }
    }
}
