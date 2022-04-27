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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.jdt.ls.core.internal.JavaLanguageServerPlugin;
import org.kogito.core.internal.util.WorkspaceUtil;

public class ActivatorManager {

    private static final String ACTIVATOR_FILE_NAME = "KieJCActivator.java";

    private String firstJavaFileURI = null;
    private Path activatorPath = null;

    public void check() {
        ActivationFileVisitor visitor = new ActivationFileVisitor();
        try {
            Files.walkFileTree(Paths.get(WorkspaceUtil.getWorkspace()), visitor);
        } catch (IOException e) {
            JavaLanguageServerPlugin.logException("Error trying to read workspace tree", e);
        }
        if (visitor.isPresent()) {
            firstJavaFileURI = visitor.getActivatorFile().toAbsolutePath().toString();
            activatorPath = Path.of(visitor.getActivatorFile().toAbsolutePath().getParent() + File.separator + ACTIVATOR_FILE_NAME);
        }
    }

    public boolean isEnabled() {
        return new File(firstJavaFileURI).exists() && this.activatorPath != null;
    }

    public String getActivatorURI() {
        if (this.activatorPath != null) {
            return "file://" + this.activatorPath;
        } else {
            throw new IllegalStateException("Activator URI is not present");
        }
    }

    public Path getActivatorPath() {
        if (this.activatorPath != null) {
            return activatorPath;
        } else {
            throw new IllegalStateException("Activator Path is not present");
        }
    }

}
