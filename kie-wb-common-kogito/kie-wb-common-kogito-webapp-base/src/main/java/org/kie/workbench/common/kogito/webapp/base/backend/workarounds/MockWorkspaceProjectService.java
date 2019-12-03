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
import java.util.Collections;
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

@Service
@ApplicationScoped
public class MockWorkspaceProjectService implements WorkspaceProjectService {

    private static final WorkspaceProject PROJECT = new WorkspaceProject();
    private static final Collection<WorkspaceProject> PROJECTS = Collections.singleton(PROJECT);

    @Override
    public Collection<WorkspaceProject> getAllWorkspaceProjects() {
        return PROJECTS;
    }

    @Override
    public Collection<WorkspaceProject> getAllWorkspaceProjects(final OrganizationalUnit organizationalUnit) {
        return PROJECTS;
    }

    @Override
    public Collection<WorkspaceProject> getAllWorkspaceProjectsByName(final OrganizationalUnit organizationalUnit,
                                                                      final String name) {
        return PROJECTS;
    }

    @Override
    public boolean spaceHasNoProjectsWithName(final OrganizationalUnit organizationalUnit,
                                              final String name,
                                              final WorkspaceProject projectToIgnore) {
        return false;
    }

    @Override
    public WorkspaceProject newProject(final OrganizationalUnit organizationalUnit,
                                       final POM pom) {
        return PROJECT;
    }

    @Override
    public WorkspaceProject newProject(final OrganizationalUnit organizationalUnit,
                                       final POM pom,
                                       final DeploymentMode mode) {
        return PROJECT;
    }

    @Override
    public WorkspaceProject newProject(final OrganizationalUnit organizationalUnit,
                                       final POM pom,
                                       final DeploymentMode mode,
                                       final List<Contributor> contributor) {
        return PROJECT;
    }

    @Override
    public String createFreshProjectName(final OrganizationalUnit organizationalUnit,
                                         final String name) {
        return "";
    }

    @Override
    public WorkspaceProject resolveProject(final Repository repository) {
        return PROJECT;
    }

    @Override
    public WorkspaceProject resolveProject(final Space space,
                                           final Branch branch) {
        return PROJECT;
    }

    @Override
    public WorkspaceProject resolveProject(final Space space,
                                           final Module module) {
        return PROJECT;
    }

    @Override
    public WorkspaceProject resolveProject(final Path module) {
        return PROJECT;
    }

    @Override
    public WorkspaceProject resolveProject(final Space space,
                                           final Path module) {
        return PROJECT;
    }

    @Override
    public WorkspaceProject resolveProject(final Space space,
                                           final String name) {
        return PROJECT;
    }

    @Override
    public WorkspaceProject resolveProjectByRepositoryAlias(final Space space,
                                                            final String repositoryAlias) {
        return PROJECT;
    }

    @Override
    public WorkspaceProject resolveProject(final Space space,
                                           final String projectName,
                                           final String branchName) {
        return PROJECT;
    }
}
