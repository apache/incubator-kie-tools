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

import java.nio.file.Path;

public class ActivationChecker {

    private Path activatorPath = null;

    public void check() {
        var fileVisitor = new ActivationFileVisitor();
        var activatorPathOpt = fileVisitor.searchActivatorPath();
        if (activatorPathOpt.isPresent()) {
            activatorPath = fileVisitor.searchActivatorPath().get().toAbsolutePath();
        }
    }

    public boolean existActivator() {
        return activatorPath != null && activatorPath.toFile().exists();
    }

    public Path getActivatorPath() {
        if (existActivator()) {
            return activatorPath;
        } else {
            throw new ActivationCheckerException("Activator URI is not present");
        }
    }

}
