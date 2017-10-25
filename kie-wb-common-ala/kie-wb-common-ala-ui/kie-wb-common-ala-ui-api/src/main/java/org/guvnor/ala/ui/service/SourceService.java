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

package org.guvnor.ala.ui.service;

import java.util.Collection;

import org.guvnor.common.services.project.model.Project;
import org.jboss.errai.bus.server.annotations.Remote;

/**
 * Service for establishing/selecting the source configuration parameters to be used for launching a pipeline, etc.
 */
@Remote
public interface SourceService {

    /**
     * Gets the list of organizational unit names accessible by current user.
     * @return a list of organizational unit names.
     */
    Collection<String> getOrganizationUnits();

    /**
     * Gets the list of repositories accessible by current user in a given organizational unit.
     * @param organizationalUnit a desired organizational unit name.
     * @return a list of repository names.
     */
    Collection<String> getRepositories(final String organizationalUnit);

    /**
     * Gets the list of branch names in a given repository.
     * @param repository a repository name.
     * @return a list of branch names.
     */
    Collection<String> getBranches(final String repository);

    /**
     * Gets the list of projects accessible by current user in a given repository and branch.
     * @param repositoryAlias a repository name.
     * @param branch a branch name.
     * @return a list of projects.
     */
    Collection<Project> getProjects(final String repositoryAlias,
                                    final String branch);
}