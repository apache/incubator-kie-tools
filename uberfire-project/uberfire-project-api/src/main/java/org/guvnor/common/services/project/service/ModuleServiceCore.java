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

import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.structure.repositories.Branch;
import org.uberfire.backend.vfs.Path;

public interface ModuleServiceCore<T> {

    /**
     * Gets all the modules from a given branch.
     * @param branch the branch where we are looking for the modules
     * @return
     */
    Set<Module> getAllModules(final Branch branch);

    /**
     * Creates a new module to the given path.
     * @param repositoryRoot
     * @param pom
     * @return
     */
    T newModule(final Path repositoryRoot,
                final POM pom);

    /**
     * Creates a new module to the given path.
     * @param repositoryRoot
     * @param pom
     * @param mode Should creation check for the existence of other Artifacts with the same GAV
     * @return
     */
    T newModule(final Path repositoryRoot,
                final POM pom,
                final DeploymentMode mode);

    /**
     * Creates a new package as a child of the provide package.
     * @param pkg
     * @param packageName
     * @return
     */
    org.guvnor.common.services.project.model.Package newPackage(final Package pkg,
                                                                final String packageName);

    Path rename(final Path pathToPomXML,
                final String newName,
                final String comment);

    void delete(final Path pathToPomXML,
                final String comment);

    void copy(final Path pathToPomXML,
              final String newName,
              final String comment);

    void reImport(final Path pathToPomXML);

    void createModuleDirectories(final Path repositoryRoot);
}
