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
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Optional;

import javax.enterprise.inject.Instance;

import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.WorkspaceProject;
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
import org.guvnor.structure.repositories.RepositoryService;
import org.guvnor.structure.repositories.impl.git.GitRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.spaces.Space;
import org.uberfire.spaces.SpacesAPI;

@RunWith(MockitoJUnitRunner.class)
public class WorkspaceProjectServiceImplResolveWorkspaceWorkspaceProjectTest {

    WorkspaceProjectService workspaceProjectService;

    @Mock
    RepositoryService repositoryService;

    @Mock
    Instance<ModuleService<? extends Module>> moduleServices;

    @Mock
    ModuleService moduleService;

    @Mock
    OrganizationalUnitService organizationalUnitService;

    @Mock
    OrganizationalUnit ou;

    @Mock
    Repository repository;

    @Mock
    SpacesAPI spaces;

    @Mock
    Branch branch;

    @Mock
    Module module;

    @Mock
    ModuleRepositoryResolver repositoryResolver;

    @Mock
    SpaceConfigStorageRegistry spaceConfigStorageRegistry;

    @Mock
    SpaceConfigStorage spaceConfigStorage;

    private Path path;
    private Path branchRoot;
    private Branch masterBranch;

    private Space space;

    @Before
    public void setUp() throws Exception {
        path = PathFactory.newPath("testFile",
                                   "file:///files/TestDataObject.java");
        branchRoot = PathFactory.newPath("testFile",
                                         "file:///branchRoot/");
        space = new Space("test-realm");

        doReturn(ou).when(organizationalUnitService).getParentOrganizationalUnit(repository);
        doReturn(space.getName()).when(ou).getName();
        doReturn(ou).when(organizationalUnitService).getOrganizationalUnit(space.getName());

        doReturn(Optional.of(branch)).when(repository).getDefaultBranch();
        doReturn(branchRoot).when(branch).getPath();

        doReturn(repository).when(repositoryService).getRepository(Mockito.eq(space), any(Path.class));
        doReturn(space).when(repository).getSpace();

        doReturn(module).when(moduleService).resolveModule(any());

        masterBranch = new Branch("master",
                                  path);

        doReturn(moduleService).when(moduleServices).get();

        when(spaceConfigStorageRegistry.get(anyString())).thenReturn(spaceConfigStorage);
        when(spaceConfigStorageRegistry.getBatch(anyString())).thenReturn(new SpaceConfigStorageRegistryImpl.SpaceStorageBatchImpl(spaceConfigStorage));
        when(spaceConfigStorageRegistry.exist(anyString())).thenReturn(true);

        workspaceProjectService = new WorkspaceProjectServiceImpl(organizationalUnitService,
                                                                  repositoryService,
                                                                  spaces,
                                                                  new EventSourceMock<>(),
                                                                  moduleServices,
                                                                  repositoryResolver,
                                                                  spaceConfigStorageRegistry);
    }

    @Test
    public void resolveProjectPath() throws Exception {
        final WorkspaceProject workspaceProject = workspaceProjectService.resolveProject(space, path);

        assertEquals(ou,
                     workspaceProject.getOrganizationalUnit());
        assertEquals(repository,
                     workspaceProject.getRepository());
        assertEquals(branch,
                     workspaceProject.getBranch());
        assertEquals(module,
                     workspaceProject.getMainModule());
    }

    @Test(expected = RuntimeException.class)
    public void resolveProjectPathOfInexistentRepository() throws Exception {
        doReturn(null).when(repositoryService).getRepository(Mockito.eq(space), any(Path.class));

        final WorkspaceProject workspaceProject = workspaceProjectService.resolveProject(space, path);
    }

    @Test
    public void resolveProjectModule() throws Exception {

        final WorkspaceProject workspaceProject = workspaceProjectService.resolveProject(space,
                                                                                         new Module(path,
                                                                                                    mock(Path.class),
                                                                                                    mock(POM.class)));

        assertEquals(ou,
                     workspaceProject.getOrganizationalUnit());
        assertEquals(repository,
                     workspaceProject.getRepository());
        assertEquals(branch,
                     workspaceProject.getBranch());
        assertEquals(module,
                     workspaceProject.getMainModule());
    }

    @Test
    public void resolveProjectRepository() throws Exception {

        final GitRepository repository = new GitRepository("alias", space);
        final HashMap<String, Branch> branches = new HashMap<>();
        branches.put("master", new Branch("master",
                                          path));

        repository.setBranches(branches);
        final WorkspaceProject workspaceProject = workspaceProjectService.resolveProject(repository);

        assertEquals(ou,
                     workspaceProject.getOrganizationalUnit());
        assertEquals(this.repository,
                     workspaceProject.getRepository());
        assertEquals(branch,
                     workspaceProject.getBranch());
        assertEquals(module,
                     workspaceProject.getMainModule());
    }

    @Test
    public void resolveProjectBranch() throws Exception {

        final WorkspaceProject workspaceProject = workspaceProjectService.resolveProject(space, masterBranch);

        assertEquals(ou,
                     workspaceProject.getOrganizationalUnit());
        assertEquals(this.repository,
                     workspaceProject.getRepository());
        assertEquals(branch,
                     workspaceProject.getBranch());
        assertEquals(module,
                     workspaceProject.getMainModule());
    }
}