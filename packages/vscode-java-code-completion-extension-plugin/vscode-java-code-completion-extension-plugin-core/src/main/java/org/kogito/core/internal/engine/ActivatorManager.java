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

/**
 * Scope of this class is to manage the Activator file. This is a support java class defined in ACTIVATOR_FILE_NAME,
 * which is required to enable the Auto-Completion process. All the templates are applied inside this file, so the
 * Auto-Completion request will be managed within this file. The Activator Java Class needs to be automatically created
 * BEFORE every Auto-Completion command request to work, and eventually remove it when the command request is completed.
 */
public class ActivatorManager {

    private static final String ACTIVATOR_FILE_NAME = "KieJCActivator.java";

    private Path activatorPath = null;

    /**
     * It returns TRUE if the Activator is correctly set and enabled. This happens when at least a Java File is found
     * in the project and the Activator path has been set. The Activator path is lazily loaded at the first request.
     * If not enabled, it walks over the workspace to find at least a VALID Java Class and eventually define the Activator
     * path, in the same directory of the First Valid Java file found and the ACTIVATOR_FILE_NAME as Name
     * @return
     */
    public boolean isActivatorEnabled() {
        if (activatorPath != null) {
            return true;
        }
        ActivatorFileVisitor visitor = new ActivatorFileVisitor();
        try {
            Files.walkFileTree(Paths.get(WorkspaceUtil.getWorkspace()), visitor);
        } catch (IOException e) {
            JavaLanguageServerPlugin.logException("Error trying to read workspace tree", e);
            activatorPath = null;
            return false;
        }
        if (visitor.isPresent()) {
            activatorPath = Path.of(visitor.getActivatorFile().toAbsolutePath().getParent() + File.separator + ACTIVATOR_FILE_NAME);
            return true;
        }
        activatorPath = null;
        return false;
    }

    /**
     * String representation of the Activator Path with "file://" as prefix
     * @return
     */
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
