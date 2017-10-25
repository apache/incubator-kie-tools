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

import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.workingset.client.model.WorkingSetSettings;
import org.guvnor.structure.repositories.Repository;
import org.uberfire.backend.vfs.Path;

public interface ProjectServiceCore<T> {

    WorkingSetSettings loadWorkingSetConfig(final Path project);

    /**
     * Gets all the project from a given repository. Security checks are omitted.
     * @param repository
     * @param branch the branch where we are looking for the projects
     * @return
     */
    Set<Project> getAllProjects(final Repository repository,
                                final String branch);

    /**
     * Gets a list of the projects in a particular repository. Security checks are applied.
     * @param repository
     * @param branch the branch where we are looking for the projects
     * @return
     */
    Set<Project> getProjects(final Repository repository,
                             final String branch);

    /**
     * Creates a new project to the given path.
     * @param repositoryRoot
     * @param pom
     * @param baseURL the base URL where the Guvnor is hosted in web container
     * @return
     */
    T newProject(final Path repositoryRoot,
                 final POM pom,
                 final String baseURL);

    /**
     * Creates a new project to the given path.
     * @param repositoryRoot
     * @param pom
     * @param baseURL the base URL where the Guvnor is hosted in web container
     * @param mode Should creation check for the existence of other Artifacts with the same GAV
     * @return
     */
    T newProject(final Path repositoryRoot,
                 final POM pom,
                 final String baseURL,
                 final DeploymentMode mode);

    /**
     * Creates a new package as a child of the provide package.
     * @param pkg
     * @param packageName
     * @return
     */
    org.guvnor.common.services.project.model.Package newPackage(final Package pkg,
                                                                final String packageName);

    /**
     * Add a group to a project; limiting access to users with the group
     * @param project The Project
     * @param group The required group
     */
    void addGroup(final Project project,
                  final String group);

    /**
     * Remove a group from a project
     * @param project The Project
     * @param group The group
     */
    void removeGroup(final Project project,
                     final String group);

    Path rename(final Path pathToPomXML,
                final String newName,
                final String comment);

    void delete(final Path pathToPomXML,
                final String comment);

    void copy(final Path pathToPomXML,
              final String newName,
              final String comment);

    void reImport(final Path pathToPomXML);
}
