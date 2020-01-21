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
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.HashMap;
import java.util.Optional;
import java.util.Date;
import java.util.Arrays;

import javax.enterprise.inject.Instance;

import org.guvnor.common.services.project.backend.server.utils.PathUtil;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.service.ModuleRepositoryResolver;
import org.guvnor.common.services.project.service.ModuleService;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.guvnor.structure.backend.organizationalunit.config.SpaceConfigStorageRegistryImpl;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.organizationalunit.config.SpaceConfigStorage;
import org.guvnor.structure.organizationalunit.config.SpaceConfigStorageRegistry;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryService;
import org.guvnor.structure.repositories.changerequest.ChangeRequestService;
import org.guvnor.structure.repositories.impl.git.GitRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.spaces.Space;
import org.uberfire.spaces.SpacesAPI;
import org.uberfire.io.IOService;
import org.uberfire.io.impl.IOServiceDotFileImpl;

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

    @Mock
    PathUtil pathUtil;

    @Mock
    ChangeRequestService changeRequestService;

    @Mock
    POMService pomService;

    @Mock
    SessionInfo sessionInfo;

    private Path path;
    private Path branchRoot;
    private Branch masterBranch;
    private IOService ioService;
    private Space space;

    @Before
    public void setUp() throws Exception {
        ioService = new IOServiceDotFileImpl();

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
                                                                  new EventSourceMock<>(),
                                                                  new EventSourceMock<>(),
                                                                  moduleServices,
                                                                  repositoryResolver,
                                                                  ioService,
                                                                  spaceConfigStorageRegistry,
                                                                  pathUtil,
                                                                  changeRequestService,
                                                                  pomService);
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

    @Test(expected = IllegalArgumentException.class)
    public void resolveNullProjectAndNullBranch() {
        mockRepositoriesAndBranches();

        final WorkspaceProject workspaceProject = workspaceProjectService.resolveProject(
                space,
                null,
                null);

        assertNull(workspaceProject);
    }

    @Test(expected = IllegalArgumentException.class)
    public void resolveNonExistingProject() {
        mockRepositoriesAndBranches();

        final WorkspaceProject workspaceProject = workspaceProjectService.resolveProject(
                space,
                "project7",
                null);
    }

    @Test
    public void resolveProjectAndNullBranch() {
        mockRepositoriesAndBranches();

        final WorkspaceProject workspaceProject = workspaceProjectService.resolveProject(
                space,
                "project2",
                null);

        assertNotNull(workspaceProject);
        assertNotNull(workspaceProject.getBranch());
        assertEquals("master", workspaceProject.getBranch().getName());
        assertNotNull(workspaceProject.getMainModule());
    }

    @Test
    public void resolveProjectAndMasterBranch() {
        mockRepositoriesAndBranches();

        final WorkspaceProject workspaceProject = workspaceProjectService.resolveProject(
                space,
                "project2",
                "master");

        assertNotNull(workspaceProject);
        assertNotNull(workspaceProject.getBranch());
        assertEquals("master", workspaceProject.getBranch().getName());
        assertNotNull(workspaceProject.getMainModule());
    }

    @Test(expected = IllegalArgumentException.class)
    public void resolveProjectAndNonExistingBranch() {
        mockRepositoriesAndBranches();

        final WorkspaceProject workspaceProject = workspaceProjectService.resolveProject(
                space,
                "project2",
                "branch7");

        assertNotNull(workspaceProject);
        assertNotNull(workspaceProject.getBranch());
        assertEquals("master", workspaceProject.getBranch().getName());
        assertNull(workspaceProject.getMainModule());
    }

    @Test
    public void resolveProjectAndBranch() {
        mockRepositoriesAndBranches();

        final WorkspaceProject workspaceProject = workspaceProjectService.resolveProject(
                space,
                "project2",
                "branch4");

        assertNotNull(workspaceProject);
        assertNotNull(workspaceProject.getBranch());
        assertEquals("branch4", workspaceProject.getBranch().getName());
        assertNotNull(workspaceProject.getMainModule());
    }

    private void mockRepositoriesAndBranches() {
        Branch branch1 = createBranch("master");
        Branch branch2 = createBranch("branch2");
        Branch branch3 = createBranch("master");
        Branch branch4 = createBranch("branch4");
        Branch branch5 = createBranch("branch5");

        Repository project1 = mock(Repository.class);
        when(project1.getAlias()).thenReturn("project1");
        when(project1.getSpace()).thenReturn(space);
        when(project1.getDefaultBranch()).thenReturn(Optional.of(branch1));
        when(project1.getBranches())
            .thenReturn(Arrays.asList(branch1,
                                      branch2));

        Repository project2 = mock(Repository.class);
        when(project2.getAlias()).thenReturn("project2");
        when(project2.getSpace()).thenReturn(space);
        when(project2.getDefaultBranch()).thenReturn(Optional.of(branch3));
        when(project2.getBranches())
            .thenReturn(Arrays.asList(branch3,
                                      branch4,
                                      branch5));

        when(repositoryService.getRepository(any(), any())).thenReturn(project2);
        when(repositoryService.getAllRepositories(any(), anyBoolean()))
            .thenReturn(Arrays.asList(project1,
                                      project2));
    }

    private Branch createBranch(String name) {
        Path path = Paths.convert(
            ioService.newFileSystem(
                URI.create("git://test/" + name + new Date().getTime()),
                new HashMap<String, Object>() {{
                    put("init", Boolean.TRUE);
            }}).getRootDirectories().iterator().next());

        Branch branch = mock(Branch.class);

        when(branch.getName()).thenReturn(name);
        when(branch.getPath()).thenReturn(path);

        return branch;
    }
}
