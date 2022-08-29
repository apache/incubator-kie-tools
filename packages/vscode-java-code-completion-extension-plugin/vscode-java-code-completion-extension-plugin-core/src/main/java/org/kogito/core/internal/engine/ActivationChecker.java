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
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.jdt.ls.core.internal.JavaLanguageServerPlugin;
import org.kogito.core.internal.util.WorkspaceUtil;

public class ActivationChecker {

    private final WorkspaceUtil workspaceUtil;
    private Path activatorPath = null;

    public ActivationChecker(WorkspaceUtil workspaceUtil) {
        this.workspaceUtil = workspaceUtil;
    }

    public void check() {
        ActivationFileVisitor visitor = new ActivationFileVisitor();
        try {
            Files.walkFileTree(Paths.get(workspaceUtil.getProjectLocation()), visitor);
        } catch (IOException e) {
            JavaLanguageServerPlugin.logException("Error trying to read workspace tree", e);
        }
        if (visitor.isPresent()) {
            this.activatorPath = visitor.getActivatorFile().toAbsolutePath();
        }
    }

    public boolean existActivator() {
        return activatorPath != null && activatorPath.toFile().exists();
    }

    public String getActivatorUri() {
        if (existActivator()) {
            return activatorPath.toUri().toASCIIString();
        } else {
            throw new ActivationCheckerException("Activator URI is not present");
        }
    }

}
