/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.guvnor.asset.management.service;

import java.util.List;

import org.guvnor.asset.management.model.RepositoryStructureModel;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.backend.vfs.Path;

@Remote
public interface RepositoryStructureService {

    Path initRepositoryStructure(final GAV gav,
                                 final Repository repo,
                                 final DeploymentMode mode);

    Path initRepositoryStructure(final POM pom,
                                 final String baseUrl,
                                 final Repository repo,
                                 final boolean multiProject,
                                 final DeploymentMode mode);

    Repository updateManagedStatus(final Repository repo,
                                   final boolean managed);

    Path convertToMultiProjectStructure(final List<Project> projects,
                                        final GAV parentGav,
                                        final Repository repo,
                                        final boolean updateChildrenGav,
                                        final String comment);

    RepositoryStructureModel load(final Repository repository,
                                  final String branch);

    RepositoryStructureModel load(final Repository repository,
                                  final String branch,
                                  final boolean includeModules);

    void save(final Path pathToPomXML,
              final RepositoryStructureModel model,
              final String comment);

    boolean isValidProjectName(final String name);

    boolean isValidGroupId(final String groupId);

    boolean isValidArtifactId(final String artifactId);

    boolean isValidVersion(final String version);

    void delete(final Path pathToPomXML,
                final String comment);
}
