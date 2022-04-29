/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.kogito.core.internal.engine;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.stream.Stream;

import org.eclipse.jdt.ls.core.internal.JavaLanguageServerPlugin;

/**
 * Scope of this Visitor class is to find at least a Valid Java Class file.
 * A Java class file is valid if :
 * - It has the .java extension
 * - It has a `package ' statement
 * - It has a 'public class ' statement
 */
public class ActivatorFileVisitor extends SimpleFileVisitor<Path> {

    protected static final String PACKAGE_ACTIVATOR = "package ";
    protected static final String PUBLIC_CLASS_DECLARATION = "public class ";
    protected static final String JAVA_EXTENSION = ".java";

    private boolean present;
    private Path activatorPath;

    public ActivatorFileVisitor() {
        this.present = false;
    }

    @Override
    public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
        String fileName = path.toFile().getName();
        if (!fileName.endsWith(JAVA_EXTENSION) ) {
            return FileVisitResult.CONTINUE;
        }

        String javaFileName = fileName.replaceAll("\\.\\w+$", "");

        long linesThatMatch = 0;
        try (Stream<String> s = Files.lines(path)) {
            linesThatMatch = s.filter(line -> containsActivator(line, javaFileName)).limit(2).count();
        }

        if (linesThatMatch == 2) {
            this.present = true;
            JavaLanguageServerPlugin.logInfo("Valid Java Class File found: " + fileName);
            this.activatorPath = path;
            return FileVisitResult.TERMINATE;
        } else {
            return FileVisitResult.CONTINUE;
        }
    }

    private boolean containsActivator(String line, String fileName) {
        return line.contains(PACKAGE_ACTIVATOR) || line.contains(PUBLIC_CLASS_DECLARATION + fileName);
    }

    public boolean isPresent() {
        return present;
    }

    public Path getActivatorFile() {
        return activatorPath;
    }
}
