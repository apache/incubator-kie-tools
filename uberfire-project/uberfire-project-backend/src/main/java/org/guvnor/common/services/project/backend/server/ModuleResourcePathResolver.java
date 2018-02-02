/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.common.services.project.backend.server;

import org.guvnor.common.services.project.model.Package;
import org.uberfire.backend.vfs.Path;

/**
 * Resolves the by default target path within a module for a given resource based on it's type.
 */
public interface ModuleResourcePathResolver {

    /**
     * @return Defines the resolver priority. If two resolvers are found for the same resource type, the one with highest
     * priority will be used.
     */
    int getPriority();

    /**
     * Indicates if this path resolver resolves the given resource type.
     * @param resourceType a file extension.
     * @return true if current resolver resolves the given resourceType, false in any other case.
     */
    boolean accept(final String resourceType);

    /**
     * Given a package resolves where the currently accepted resourceType should be placed by default.
     * e.g. for a package org.kie and a drl extension, the by default target path will be src/main/resources/org/kie,
     * and for a java extension with the same package the by default target path will be src/main/java/org/kie
     * @param pkg A package within a module.
     * @return the expected by default path for the given extension.
     */
    Path resolveDefaultPath(final Package pkg);
}