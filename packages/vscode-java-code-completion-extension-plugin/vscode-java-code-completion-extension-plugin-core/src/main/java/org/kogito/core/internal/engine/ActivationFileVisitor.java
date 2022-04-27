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


//JavaFileVisitor
public class ActivationFileVisitor extends SimpleFileVisitor<Path> {

    protected static final String IMPORT_ACTIVATOR = "import org.kie.api.project.KieActivator;";
    protected static final String ANNOTATION_ACTIVATOR = "@KieActivator";
    protected static final String PACKAGE_ACTIVATOR = "package ";
    protected static final String PUBLIC_CLASS_DECLARATION = "public class ";
    protected static final String JAVA_EXTENSION = ".java";

    private boolean present;
    private Path activatorPath;

    public ActivationFileVisitor() {
        this.present = false;
    }

    @Override
    public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
        String fileName = path.toFile().getName();
        if (!fileName.endsWith(JAVA_EXTENSION)) {
            return FileVisitResult.CONTINUE;
        }
        JavaLanguageServerPlugin.logInfo("Java path found: " + fileName);

        String javaFileName = fileName.replaceAll("\\.\\w+$", "");

        long linesThatMatch = 0;
        try (Stream<String> s = Files.lines(path)) {
            linesThatMatch = s.filter(line -> containsActivator(line, javaFileName)).limit(2).count();
        }

        if (linesThatMatch == 2) {
            this.present = true;
            JavaLanguageServerPlugin.logInfo("Activator found: " + fileName);
            this.activatorPath = path;
            return FileVisitResult.TERMINATE;
        } else {
            return FileVisitResult.CONTINUE;
        }
    }

    protected boolean containsActivator(String line, String fileName) {
        return line.contains(PACKAGE_ACTIVATOR) || line.contains(PUBLIC_CLASS_DECLARATION + fileName);
    }

    public boolean isPresent() {
        return present;
    }

    public Path getActivatorFile() {
        return activatorPath;
    }
}
