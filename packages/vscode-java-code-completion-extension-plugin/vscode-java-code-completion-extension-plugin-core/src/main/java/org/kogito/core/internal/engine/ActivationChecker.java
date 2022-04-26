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
import java.nio.file.Paths;

import org.eclipse.jdt.ls.core.internal.JavaLanguageServerPlugin;
import org.kogito.core.internal.util.WorkspaceUtil;

public class ActivationChecker {

    private final WorkspaceUtil workspaceUtil;
    private String activatorUri = null;

    public ActivationChecker(WorkspaceUtil workspaceUtil) {
        this.workspaceUtil = workspaceUtil;
    }

    public void check() {
        ActivationFileVisitor visitor = new ActivationFileVisitor();
        try {
            Files.walkFileTree(Paths.get(workspaceUtil.getWorkspace()), visitor);
        } catch (IOException e) {
            JavaLanguageServerPlugin.logException("Error trying to read workspace tree", e);
        }
        if (visitor.isPresent()) {
            activatorUri = visitor.getActivatorFile().toAbsolutePath().toString();
        }
    }

    public boolean existActivator() {
        return new File(activatorUri).exists();
    }

    public String getActivatorUri() {
        if (this.activatorUri != null && !this.activatorUri.isEmpty()) {
            return "file://" + this.activatorUri;
        } else {
            throw new IllegalStateException("Activator URI is not present");
        }
    }

}
