/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.guvnor.common.services.project.service;

import java.util.Set;

import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.uberfire.backend.vfs.Path;

public interface ProjectResourceResolver<T extends Project> {

    /**
     * Given a Resource path resolve it to the containing Project Path. A Project path is the folder containing pom.xml
     * @param resource
     * @return Path to the folder containing the Project's pom.xml file or null if the resource was not in a Project
     */
    T resolveProject(final Path resource);

    Project resolveParentProject(final Path resource);

    Project resolveToParentProject(final Path resource);

    /**
     * Given a Project resolves the calculation of all the packages for this project.
     * @param project
     * @return Collection containing all the packages for the project.
     */
    Set<Package> resolvePackages(final Project project);

    Set<Package> resolvePackages(final Package pkg);

    Package resolveDefaultPackage(final Project project);

    Package resolveDefaultWorkspacePackage(final Project project);

    Package resolveParentPackage(final Package pkg);

    /**
     * Given a package and a resource extension resolves the target path where the resource should be placed by default.
     * e.g. for a package org.kie and a drl extension, the by default target path will be src/main/resources/org/kie,
     * and for a java extension with the same package the by default target path will be src/main/java/org/kie
     * @param pkg A package within a project.
     * @param resourceType a file extension.
     * @return the expected by default path for the given extension.
     */
    Path resolveDefaultPath(final Package pkg,
                            final String resourceType);

    /**
     * Return true if the file is the Project's pom.xml file
     * @param resource
     * @return
     */
    boolean isPom(Path resource);

    /**
     * Given a Resource path resolve it to the containing Package Path. A Package path is the folder containing the resource.
     * The folder must be within a valid Project structure and at least reference /src/main/java, /src/main/resources,
     * src/test/java or src/test/resources (or deeper).
     * @param resource
     * @return Path to the folder containing the resource file or null if the resource is not in a Package.
     */
    org.guvnor.common.services.project.model.Package resolvePackage(final Path resource);
}
