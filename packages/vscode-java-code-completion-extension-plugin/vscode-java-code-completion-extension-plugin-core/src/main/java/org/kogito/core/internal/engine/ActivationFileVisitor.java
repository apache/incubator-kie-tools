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
import java.nio.file.Files;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Optional;
import java.util.stream.Stream;

import org.eclipse.jdt.ls.core.internal.JavaLanguageServerPlugin;
import org.kogito.core.internal.util.WorkspaceUtil;

public class ActivationFileVisitor extends SimpleFileVisitor<Path> {

    protected static final String IMPORT_ACTIVATOR = "import org.kie.api.project.KieActivator;";
    protected static final String ANNOTATION_ACTIVATOR = "@KieActivator";
    protected static final String JAVA_EXTENSION = ".java";

    private Path activatorPath;

    public Optional<Path> searchActivatorPath() {
        try {
            Files.walkFileTree(Paths.get(WorkspaceUtil.getProjectLocation()), this);
        } catch (IOException e) {
            JavaLanguageServerPlugin.logException("Error trying to read workspace tree", e);
        }

        return Optional.ofNullable(activatorPath);
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        String filePath = file.toString();
        if (!filePath.endsWith(JAVA_EXTENSION)) {
            return FileVisitResult.CONTINUE;
        }

        JavaLanguageServerPlugin.logInfo("Java file found: " + filePath);
        long linesThatMatch;

        try (Stream<String> linesStream = Files.lines(file)) {
            linesThatMatch = linesStream.filter(this::containsActivator).count();
        }

        if (linesThatMatch >= 2) {
            JavaLanguageServerPlugin.logInfo("Activator found: " + filePath);
            this.activatorPath = file;
            return FileVisitResult.TERMINATE;
        } else {
            return FileVisitResult.CONTINUE;
        }
    }

    private boolean containsActivator(String line) {
        return line.contains(IMPORT_ACTIVATOR) || line.contains(ANNOTATION_ACTIVATOR);
    }

}
