/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.kogito.webapp.base.backend.workarounds;

import java.util.Collection;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.guvnor.structure.contributors.Contributor;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.backend.vfs.Path;
import org.uberfire.spaces.Space;

import static org.kie.workbench.common.kogito.api.KogitoConstants.NOT_AVAILABLE_IN_KOGITO;

/**
 * kogito does not have the concept of Workspaces or Projects. Therefore this dummy
 * implementation of the WorkspaceProjectService returns non-null sensible defaults.
 */
@Service
@ApplicationScoped
public class MockWorkspaceProjectService implements WorkspaceProjectService {

    @Override
    public Collection<WorkspaceProject> getAllWorkspaceProjects() {
        throw new UnsupportedOperationException(NOT_AVAILABLE_IN_KOGITO);
    }

    @Override
    public Collection<WorkspaceProject> getAllWorkspaceProjects(final OrganizationalUnit organizationalUnit) {
        throw new UnsupportedOperationException(NOT_AVAILABLE_IN_KOGITO);
    }

    @Override
    public Collection<WorkspaceProject> getAllWorkspaceProjectsByName(final OrganizationalUnit organizationalUnit,
                                                                      final String name) {
        throw new UnsupportedOperationException(NOT_AVAILABLE_IN_KOGITO);
    }

    @Override
    public boolean spaceHasNoProjectsWithName(final OrganizationalUnit organizationalUnit,
                                              final String name,
                                              final WorkspaceProject projectToIgnore) {
        throw new UnsupportedOperationException(NOT_AVAILABLE_IN_KOGITO);
    }

    @Override
    public WorkspaceProject newProject(final OrganizationalUnit organizationalUnit,
                                       final POM pom) {
        throw new UnsupportedOperationException(NOT_AVAILABLE_IN_KOGITO);
    }

    @Override
    public WorkspaceProject newProject(final OrganizationalUnit organizationalUnit,
                                       final POM pom,
                                       final DeploymentMode mode) {
        throw new UnsupportedOperationException(NOT_AVAILABLE_IN_KOGITO);
    }

    @Override
    public WorkspaceProject newProject(final OrganizationalUnit organizationalUnit,
                                       final POM pom,
                                       final DeploymentMode mode,
                                       final List<Contributor> contributor) {
        throw new UnsupportedOperationException(NOT_AVAILABLE_IN_KOGITO);
    }

    @Override
    public String createFreshProjectName(final OrganizationalUnit organizationalUnit,
                                         final String name) {
        throw new UnsupportedOperationException(NOT_AVAILABLE_IN_KOGITO);
    }

    @Override
    public WorkspaceProject resolveProject(final Repository repository) {
        throw new UnsupportedOperationException(NOT_AVAILABLE_IN_KOGITO);
    }

    @Override
    public WorkspaceProject resolveProject(final Space space,
                                           final Branch branch) {
        throw new UnsupportedOperationException(NOT_AVAILABLE_IN_KOGITO);
    }

    @Override
    public WorkspaceProject resolveProject(final Space space,
                                           final Module module) {
        throw new UnsupportedOperationException(NOT_AVAILABLE_IN_KOGITO);
    }

    @Override
    public WorkspaceProject resolveProject(final Path module) {
        throw new UnsupportedOperationException(NOT_AVAILABLE_IN_KOGITO);
    }

    @Override
    public WorkspaceProject resolveProject(final Space space,
                                           final Path module) {
        throw new UnsupportedOperationException(NOT_AVAILABLE_IN_KOGITO);
    }

    @Override
    public WorkspaceProject resolveProject(final Space space,
                                           final String name) {
        throw new UnsupportedOperationException(NOT_AVAILABLE_IN_KOGITO);
    }

    @Override
    public WorkspaceProject resolveProjectByRepositoryAlias(final Space space,
                                                            final String repositoryAlias) {
        throw new UnsupportedOperationException(NOT_AVAILABLE_IN_KOGITO);
    }

    @Override
    public WorkspaceProject resolveProject(final Space space,
                                           final String projectName,
                                           final String branchName) {
        throw new UnsupportedOperationException(NOT_AVAILABLE_IN_KOGITO);
    }

    public void addBranch(final String newBranchName,
                          final String baseBranchName,
                          final WorkspaceProject project,
                          final String userIdentifier) {
        throw new UnsupportedOperationException(NOT_AVAILABLE_IN_KOGITO);
    }

    @Override
    public void removeBranch(final String branchName,
                             final WorkspaceProject project,
                             final String userIdentifier) {
        throw new UnsupportedOperationException(NOT_AVAILABLE_IN_KOGITO);
    }
}
