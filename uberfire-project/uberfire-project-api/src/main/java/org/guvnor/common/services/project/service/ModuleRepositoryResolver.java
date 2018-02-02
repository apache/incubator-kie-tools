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

import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.MavenRepositoryMetadata;
import org.guvnor.common.services.project.model.Module;
import org.jboss.errai.bus.server.annotations.Remote;

@Remote
public interface ModuleRepositoryResolver {

    /**
     * Get a collection of Repositories a Module will resolve artifacts against. The list will include
     * any Repositories defined in settings.xml
     * @return
     */
    Set<MavenRepositoryMetadata> getRemoteRepositoriesMetaData();

    /**
     * Get a collection of Repositories a Module will resolve artifacts against. The list will include
     * any Repositories defined in the Module's pom <distributionManagement> section, any Repositories
     * defined in the Module's pom or parent pom(s) and in settings.xml
     * @param module The Module to retrieve Repository information.
     * @return
     */
    Set<MavenRepositoryMetadata> getRemoteRepositoriesMetaData(final Module module);

    /**
     * Get a collection of Repositories that a given GAV resolve against.
     * @param gav The GAV for the artifact to resolve
     * @param filter An optional Set of MavenRepositoryMetadata to filter the results. Those in the filter are included. If a filter is not provided all results are returned.
     * @return A collection of RemoteRepositories that resolve the provided GAV; i.e. an Artifact already exists for the GAV
     */
    Set<MavenRepositoryMetadata> getRepositoriesResolvingArtifact(final GAV gav,
                                                                  final MavenRepositoryMetadata... filter);

    /**
     * Get a collection of Repositories that a given GAV resolve against.
     * @param gav The GAV for the artifact to resolve
     * @param module The Module who's RemoteRepository information will be used to resolve the artifact.
     * @param filter An optional Set of MavenRepositoryMetadata to filter the results. Those in the filter are included. If a filter is not provided all results are returned.
     * @return A collection of RemoteRepositories that resolve the provided GAV; i.e. an Artifact already exists for the GAV
     */
    Set<MavenRepositoryMetadata> getRepositoriesResolvingArtifact(final GAV gav,
                                                                  final Module module,
                                                                  final MavenRepositoryMetadata... filter);

    /**
     * Get a collection of Repositories that a given GAV resolve against.
     * @param pom The POM containing prospective GAV and repository configuration (in addition to settings.xml)
     * @param filter An optional Set of MavenRepositoryMetadata to filter the results. Those in the filter are included. If a filter is not provided all results are returned.
     * @return A collection of RemoteRepositories that resolve the provided GAV; i.e. an Artifact already exists for the GAV
     */
    Set<MavenRepositoryMetadata> getRepositoriesResolvingArtifact(final String pom,
                                                                  final MavenRepositoryMetadata... filter);
}
