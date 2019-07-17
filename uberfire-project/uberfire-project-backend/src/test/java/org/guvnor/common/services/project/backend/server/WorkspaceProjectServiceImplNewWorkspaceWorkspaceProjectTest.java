/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.guvnor.common.services.project.backend.server;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import javax.enterprise.inject.Instance;

import org.guvnor.common.services.project.events.NewProjectEvent;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.common.services.project.service.ModuleRepositoryResolver;
import org.guvnor.common.services.project.service.ModuleService;
import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.guvnor.structure.backend.organizationalunit.config.SpaceConfigStorageRegistryImpl;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.organizationalunit.config.SpaceConfigStorage;
import org.guvnor.structure.organizationalunit.config.SpaceConfigStorageRegistry;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryEnvironmentConfigurations;
import org.guvnor.structure.repositories.RepositoryService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.spaces.Space;
import org.uberfire.spaces.SpacesAPI;

@RunWith(MockitoJUnitRunner.class)
public class WorkspaceProjectServiceImplNewWorkspaceWorkspaceProjectTest {

    WorkspaceProjectService workspaceProjectService;

    @Mock
    RepositoryService repositoryService;
    @Mock
    Instance<ModuleService<? extends Module>> moduleServices;

    @Mock
    ModuleService moduleService;

    @Mock
    private OrganizationalUnit ou;

    @Mock
    private Space space;

    @Mock
    private Repository repository;

    @Mock
    private Path repositoryRoot;

    @Mock
    private Branch branch;

    @Mock
    private EventSourceMock<NewProjectEvent> newProjectEvent;

    @Mock
    private Module module;

    @Mock
    private SpacesAPI spaces;

    @Mock
    private ModuleRepositoryResolver repositoryResolver;

    @Mock
    private SpaceConfigStorageRegistry spaceConfigStorageRegistry;

    @Mock
    private SpaceConfigStorage spaceConfigStorage;

    private POM pom;

    @Before
    public void setUp() throws Exception {

        doReturn(Optional.of(branch)).when(repository).getDefaultBranch();
        doReturn(repositoryRoot).when(branch).getPath();

        doReturn(repository).when(repositoryService).createRepository(eq(ou),
                                                                      eq("git"),
                                                                      eq("myproject"),
                                                                      any(RepositoryEnvironmentConfigurations.class));

        pom = new POM("my project",
                      "my description",
                      "url",
                      new GAV("groupId",
                              "artifactId",
                              "version"));

        when(ou.getSpace()).thenReturn(space);
        when(space.getName()).thenReturn("ou");

        when(repositoryService.createRepository(any(),
                                           anyString(),
                                           anyString(),
                                           any(),
                                           any())).thenReturn(repository);

        doReturn(moduleService).when(moduleServices).get();

        when(spaceConfigStorageRegistry.get(anyString())).thenReturn(spaceConfigStorage);
        when(spaceConfigStorageRegistry.getBatch(anyString())).thenReturn(new SpaceConfigStorageRegistryImpl.SpaceStorageBatchImpl(spaceConfigStorage));
        when(spaceConfigStorageRegistry.exist(anyString())).thenReturn(true);

        workspaceProjectService = new WorkspaceProjectServiceImpl(mock(OrganizationalUnitService.class),
                                                                  repositoryService,
                                                                  spaces,
                                                                  newProjectEvent,
                                                                  moduleServices,
                                                                  repositoryResolver,
                                                                  spaceConfigStorageRegistry);
    }

    @Test
    public void newProjectDefault() throws Exception {

        doReturn(module).when(moduleService).newModule(eq(repositoryRoot),
                                                       eq(pom),
                                                       eq(DeploymentMode.VALIDATED));

        final WorkspaceProject workspaceProject = workspaceProjectService.newProject(ou,
                                                                                     pom);
        assertProject(workspaceProject);
        verify(newProjectEvent).fire(any());

        verify(spaceConfigStorage).startBatch();
        verify(repositoryService).createRepository(eq(ou),
                                                   eq("git"),
                                                   eq("myproject"),
                                                   any(),
                                                   any());
        verify(spaceConfigStorage).endBatch();
    }

    @Test
    public void newProjectValidated() throws Exception {

        doReturn(module).when(moduleService).newModule(eq(repositoryRoot),
                                                       eq(pom),
                                                       eq(DeploymentMode.VALIDATED));

        final WorkspaceProject workspaceProject = workspaceProjectService.newProject(ou,
                                                                                     pom,
                                                                                     DeploymentMode.VALIDATED);
        assertProject(workspaceProject);
        verify(newProjectEvent).fire(any());

        verify(spaceConfigStorage).startBatch();
        verify(repositoryService).createRepository(eq(ou),
                                                   eq("git"),
                                                   eq("myproject"),
                                                   any(),
                                                   any());
        verify(spaceConfigStorage).endBatch();
    }

    @Test
    public void newProjectForced() throws Exception {
        doReturn(module).when(moduleService).newModule(eq(repositoryRoot),
                                                       eq(pom),
                                                       eq(DeploymentMode.FORCED));

        final WorkspaceProject workspaceProject = workspaceProjectService.newProject(ou,
                                                                                     pom,
                                                                                     DeploymentMode.FORCED);
        assertProject(workspaceProject);
        verify(newProjectEvent).fire(any());

        verify(spaceConfigStorage).startBatch();
        verify(repositoryService).createRepository(eq(ou),
                                           eq("git"),
                                           eq("myproject"),
                                           any(),
                                           any());
        verify(spaceConfigStorage).endBatch();
    }

    private void assertProject(final WorkspaceProject workspaceProject) {
        assertEquals(ou,
                     workspaceProject.getOrganizationalUnit());
        assertEquals(repository,
                     workspaceProject.getRepository());
        assertEquals(branch,
                     workspaceProject.getBranch());
        assertEquals(module,
                     workspaceProject.getMainModule());
    }
}