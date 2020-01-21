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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.enterprise.inject.Instance;
import javax.enterprise.event.Event;

import org.guvnor.common.services.project.backend.server.utils.PathUtil;
import org.guvnor.common.services.project.model.GAV;
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
import org.guvnor.structure.organizationalunit.impl.OrganizationalUnitImpl;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.NewBranchEvent;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryService;
import org.guvnor.structure.repositories.RepositoryUpdatedEvent;
import org.guvnor.structure.repositories.changerequest.ChangeRequestService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.ArgumentCaptor;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.server.spaces.SpacesAPIImpl;
import org.uberfire.backend.vfs.Path;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.spi.FileSystemProvider;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mocks.SessionInfoMock;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.spaces.Space;
import org.uberfire.spaces.SpacesAPI;
import org.uberfire.io.IOService;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class WorkspaceProjectServiceImplTest {

    WorkspaceProjectService workspaceProjectService;

    @Mock
    OrganizationalUnitService organizationalUnitService;

    @Mock
    RepositoryService repositoryService;

    @Mock
    Instance<ModuleService<? extends Module>> moduleServices;

    @Mock
    Repository repository1;

    @Mock
    Repository repository2;

    @Mock
    Repository repository3;

    @Mock
    ModuleService moduleService;

    @Mock
    ModuleRepositoryResolver repositoryResolver;

    @Mock
    SpaceConfigStorageRegistry spaceConfigStorageRegistry;

    @Mock
    SpaceConfigStorage spaceConfigStorage;

    @Mock
    IOService ioService;

    @Mock
    PathUtil pathUtil;

    @Mock
    ChangeRequestService changeRequestService;

    @Mock
    POMService pomService;

    @Mock
    EventSourceMock<RepositoryUpdatedEvent> repositoryUpdatedEvent;

    @Mock
    Event<NewBranchEvent> newBranchEvent;

    @Mock
    SessionInfo sessionInfo;

    SpacesAPI spaces = new SpacesAPIImpl();

    Space space1;
    Space space2;

    private OrganizationalUnit ou1;
    private OrganizationalUnit ou2;
    private List<Repository> allRepositories;

    @Before
    public void setUp() throws Exception {

        setUpOUs();

        setUpRepositories();

        sessionInfo = new SessionInfoMock();

        doReturn(moduleService).when(moduleServices).get();
        doReturn(allRepositories).when(repositoryService).getAllRepositoriesFromAllUserSpaces();

        when(spaceConfigStorageRegistry.get(anyString())).thenReturn(spaceConfigStorage);
        when(spaceConfigStorageRegistry.getBatch(anyString())).thenReturn(new SpaceConfigStorageRegistryImpl.SpaceStorageBatchImpl(spaceConfigStorage));
        when(spaceConfigStorageRegistry.exist(anyString())).thenReturn(true);

        workspaceProjectService = new WorkspaceProjectServiceImpl(organizationalUnitService,
                                                                  repositoryService,
                                                                  spaces,
                                                                  new EventSourceMock<>(),
                                                                  repositoryUpdatedEvent,
                                                                  newBranchEvent,
                                                                  moduleServices,
                                                                  repositoryResolver,
                                                                  ioService,
                                                                  spaceConfigStorageRegistry,
                                                                  pathUtil,
                                                                  changeRequestService,
                                                                  pomService);
    }

    private void setUpOUs() {
        ou1 = new OrganizationalUnitImpl("ou1",
                                         "defaultGroupID");
        ou2 = new OrganizationalUnitImpl("ou2",
                                         "defaultGroupID");
        space1 = spaces.getSpace("ou1");
        space2 = spaces.getSpace("ou2");

        doReturn(ou1).when(organizationalUnitService).getOrganizationalUnit("ou1");
        doReturn(ou2).when(organizationalUnitService).getOrganizationalUnit("ou2");

        final List<OrganizationalUnit> allOUs = new ArrayList<>();
        allOUs.add(ou1);
        allOUs.add(ou2);
        doReturn(allOUs).when(organizationalUnitService).getOrganizationalUnits();
        doReturn(allOUs).when(organizationalUnitService).getAllOrganizationalUnits();

        ou1.getRepositories().add(repository1);
        ou1.getRepositories().add(repository2);

        ou2.getRepositories().add(repository3);
    }

    private void setUpRepositories() {

        doReturn(Optional.of(mock(Branch.class))).when(repository1).getDefaultBranch();
        doReturn("repository1").when(repository1).getAlias();
        doReturn("space1/repository1").when(repository1).getIdentifier();
        doReturn(Optional.of(mock(Branch.class))).when(repository2).getDefaultBranch();
        doReturn("repository-with-same-alias").when(repository2).getAlias();
        doReturn("space1/repository-with-same-alias").when(repository2).getIdentifier();
        doReturn(Optional.of(mock(Branch.class))).when(repository3).getDefaultBranch();
        doReturn("repository-with-same-alias").when(repository3).getAlias();
        doReturn("space2/repository-with-same-alias").when(repository3).getIdentifier();

        allRepositories = new ArrayList<>();
        allRepositories.add(repository1);
        allRepositories.add(repository2);
        allRepositories.add(repository3);

        doReturn(allRepositories).when(repositoryService).getAllRepositoriesFromAllUserSpaces();
        doReturn(allRepositories).when(repositoryService).getAllRepositoriesFromAllUserSpaces();
        doReturn(Arrays.asList(repository1,
                               repository2)).when(repositoryService).getRepositories(Mockito.eq(space1));
        doReturn(Arrays.asList(repository1,
                               repository2)).when(repositoryService).getAllRepositories(Mockito.eq(space1),
                                                                                        anyBoolean());
        doReturn(Arrays.asList(repository3)).when(repositoryService).getAllRepositories(Mockito.eq(space2),
                                                                                        anyBoolean());
        doReturn(Collections.singletonList(repository3)).when(repositoryService).getRepositories(Mockito.eq(space2));
    }

    @Test
    public void getAllProjects() {

        final Collection<WorkspaceProject> allWorkspaceProjects = workspaceProjectService.getAllWorkspaceProjects();

        assertEquals(3,
                     allWorkspaceProjects.size());
    }

    @Test
    public void getAllProjectsForOU1() {
        final Collection<WorkspaceProject> allWorkspaceProjects = workspaceProjectService.getAllWorkspaceProjects(ou1);

        assertContains(repository1,
                       allWorkspaceProjects);
        assertContains(repository2,
                       allWorkspaceProjects);

        assertEquals(2,
                     allWorkspaceProjects.size());
    }

    @Test
    public void getAllProjectsForOU2() {
        final Collection<WorkspaceProject> allWorkspaceProjects = workspaceProjectService.getAllWorkspaceProjects(ou2);

        assertContains(repository3,
                       allWorkspaceProjects);

        assertEquals(1,
                     allWorkspaceProjects.size());
    }

    @Test
    public void getAllProjectsWithName() {
        final Collection<WorkspaceProject> allWorkspaceProjects = workspaceProjectService.getAllWorkspaceProjectsByName(ou1,
                                                                                                                        "repository-with-same-alias");

        assertContains(repository2,
                       allWorkspaceProjects);

        assertEquals(1,
                     allWorkspaceProjects.size());
    }

    @Test
    public void spaceHasProjectsWithName() {
        final boolean hasNoProjects = workspaceProjectService.spaceHasNoProjectsWithName(ou1,
                                                                                         "repository1",
                                                                                         new WorkspaceProject(ou1,
                                                                                                              repository2,
                                                                                                              repository2.getDefaultBranch().get(),
                                                                                                              null));

        assertFalse(hasNoProjects);
    }

    @Test
    public void spaceHasNoProjectsWithName() throws Exception {
        final boolean hasNoProjects = workspaceProjectService.spaceHasNoProjectsWithName(ou1,
                                                                                         "other-project",
                                                                                         new WorkspaceProject(ou1,
                                                                                                              repository1,
                                                                                                              repository1.getDefaultBranch().get(),
                                                                                                              null));

        assertTrue(hasNoProjects);
    }

    @Test
    public void spaceHasProjectsWithNameSameProject() throws Exception {
        final boolean hasNoProjects = workspaceProjectService.spaceHasNoProjectsWithName(ou1,
                                                                                         "repository1",
                                                                                         new WorkspaceProject(ou1,
                                                                                                              repository1,
                                                                                                              repository1.getDefaultBranch().get(),
                                                                                                              null));

        assertTrue(hasNoProjects);
    }

    @Test
    public void noProjects() {
        final OrganizationalUnit organizationalUnit = mock(OrganizationalUnit.class);
        doReturn("myOU").when(organizationalUnit).getName();

        doReturn(organizationalUnit).when(organizationalUnitService).getOrganizationalUnit("myOU");

        assertTrue(workspaceProjectService.getAllWorkspaceProjects(organizationalUnit).isEmpty());
    }

    @Test
    public void testReturnSameNameIfProjectDoesNotExist() {
        String projectName = "projectA";
        POM pom = new POM(projectName,
                          "description",
                          "url",
                          null);

        WorkspaceProjectServiceImpl impl = (WorkspaceProjectServiceImpl) this.workspaceProjectService;
        String newName = impl.createFreshProjectName(this.ou1,
                                                     pom.getName());

        assertEquals(projectName,
                     newName);
    }

    @Test
    public void testCreateNewNameIfProjectExists() {
        {
            POM pom = new POM("repository1",
                              "description",
                              "url",
                              null);

            WorkspaceProjectServiceImpl impl = (WorkspaceProjectServiceImpl) this.workspaceProjectService;
            String newName = impl.createFreshProjectName(this.ou1,
                                                         pom.getName());

            assertEquals("repository1-1",
                         newName);
        }

        {

            doReturn(Optional.of(mock(Branch.class))).when(repository2).getDefaultBranch();
            doReturn("repository1-1").when(repository2).getAlias();

            POM pom = new POM("repository1",
                              "description",
                              "url",
                              null);

            WorkspaceProjectServiceImpl impl = (WorkspaceProjectServiceImpl) this.workspaceProjectService;
            String newName = impl.createFreshProjectName(this.ou1,
                                                         pom.getName());

            assertEquals("repository1-2",
                         newName);
        }
    }

    @Test
    public void testReturnSameNameIfRepositoryDoesNotExist() {
        String repositoryName = "repositoryA";
        WorkspaceProjectServiceImpl impl = (WorkspaceProjectServiceImpl) this.workspaceProjectService;
        String newName = impl.createFreshRepositoryAlias(this.ou1,
                                                         repositoryName);

        assertEquals(repositoryName,
                     newName);
    }

    @Test
    public void testCreateNewNameIfRepositoryExists() {
        {
            String repositoryName = "repository1";
            WorkspaceProjectServiceImpl impl = (WorkspaceProjectServiceImpl) this.workspaceProjectService;
            String newName = impl.createFreshProjectName(this.ou1,
                                                         repositoryName);

            assertEquals("repository1-1",
                         newName);
        }

        {
            doReturn(Optional.of(mock(Branch.class))).when(repository2).getDefaultBranch();
            doReturn("repository1-1").when(repository2).getAlias();

            WorkspaceProjectServiceImpl impl = (WorkspaceProjectServiceImpl) this.workspaceProjectService;
            String newName = impl.createFreshProjectName(this.ou1,
                                                         "repository1");

            assertEquals("repository1-2",
                         newName);
        }
    }

    @Test
    public void testErrorWhenNewProject() {
        String repository1 = "repository1";
        POM pom = new POM(repository1,
                          "description",
                          "url",
                          null);
        when(this.repositoryResolver.getRepositoriesResolvingArtifact(any(GAV.class))).thenReturn(Collections.emptySet());
        when(this.repositoryService.createRepository(eq(this.ou1),
                                                     eq("git"),
                                                     any(),
                                                     any()))
                .thenReturn(this.repository1);

        when(repositoryService.createRepository(any(), anyString(), anyString(), any(), any())).thenReturn(repository2);

        doReturn(Optional.of(mock(Branch.class))).when(repository2).getDefaultBranch();
        when(repository2.getAlias()).thenReturn(repository1);

        when(this.moduleService.newModule(any(),
                                          any(),
                                          any()))
                .thenThrow(new RuntimeException("Expected error"));

        try {
            this.workspaceProjectService.newProject(this.ou1,
                                                    pom);
        } catch (Exception e) {
            verify(this.repositoryService).removeRepository(new Space(this.ou1.getName()),
                                                            repository1);
        }
    }

    @Test
    public void addBranchTest() throws URISyntaxException {
        final WorkspaceProject project = mock(WorkspaceProject.class);
        doReturn(repository1).when(project).getRepository();
        doReturn(repository1).when(repositoryService).getRepositoryFromSpace(any(), any());

        final List<Branch> repo1Branches = Arrays.asList(makeBranch("repo1-branch1",
                                                                    repository1.getAlias()),
                                                         makeBranch("repo1-branch2",
                                                                    repository1.getAlias()));
        when(repository1.getBranches()).thenReturn(repo1Branches);
        when(repository1.getBranch(anyString())).then(inv -> repo1Branches.stream().filter(b -> b.getName().equals(inv.getArgumentAt(0, String.class))).findFirst());

        doReturn(new Space("my-space")).when(project).getSpace();
        doReturn(mock(SpaceConfigStorage.class)).when(spaceConfigStorageRegistry).get("my-space");

        final org.uberfire.java.nio.file.Path baseBranchPath = mock(org.uberfire.java.nio.file.Path.class);
        final Path path = repository1.getBranches().stream().filter(b -> b.getName().equals("repo1-branch1")).findFirst().get().getPath();
        final FileSystem fileSystem = mock(FileSystem.class);
        final FileSystemProvider fileSystemProvider = mock(FileSystemProvider.class);
        doReturn(fileSystemProvider).when(fileSystem).provider();
        doReturn(fileSystem).when(baseBranchPath).getFileSystem();
        doReturn(baseBranchPath).when(pathUtil).convert(path);

        doReturn(repository1).when(repositoryService).getRepository(any());

        Branch newBranch = makeBranch("new-branch", "repo1");
        Branch branch1Branch = makeBranch("repo1-branch1", "repo1");

        when(repository1.getBranch(any(Path.class))).thenReturn(Optional.of(newBranch)).thenReturn(Optional.of(branch1Branch));

        final org.uberfire.java.nio.file.Path newBranchPath = mock(org.uberfire.java.nio.file.Path.class);
        doReturn(newBranchPath).when(ioService).get(new URI("default://new-branch@repo1/"));

        doReturn("default://new-branch@repo1/").when(pathUtil).replaceBranch(anyString(), anyString());

        final ArgumentCaptor<NewBranchEvent> newBranchEventArgumentCaptor = ArgumentCaptor.forClass(NewBranchEvent.class);

        workspaceProjectService.addBranch("new-branch",
                                          "repo1-branch1",
                                          project,
                                          "user");

        verify(fileSystemProvider).copy(baseBranchPath, newBranchPath);

        verify(repositoryUpdatedEvent).fire(any());
        verify(newBranchEvent).fire(newBranchEventArgumentCaptor.capture());

        final NewBranchEvent newBranchEvent = newBranchEventArgumentCaptor.getValue();
        assertEquals("new-branch", newBranchEvent.getNewBranchName());
        assertEquals("repo1-branch1", newBranchEvent.getFromBranchName());
        assertEquals(repository1, newBranchEvent.getRepository());
    }

    @Test
    public void removeBranchTest() {
        final Branch otherBranch = makeBranch("repo1-branch1", "repo1");
        final org.uberfire.java.nio.file.Path baseBranchPath = mock(org.uberfire.java.nio.file.Path.class);
        final FileSystem fileSystem = mock(FileSystem.class);
        final FileSystemProvider fileSystemProvider = mock(FileSystemProvider.class);
        doReturn(fileSystemProvider).when(fileSystem).provider();
        doReturn(fileSystem).when(baseBranchPath).getFileSystem();
        doReturn(baseBranchPath).when(pathUtil).convert(any(Path.class));

        final List<Branch> repo1Branches = Arrays.asList(makeBranch("repo1-branch1",
                                                                    repository1.getAlias()),
                                                         makeBranch("repo1-branch2",
                                                                    repository1.getAlias()));
        when(repository1.getBranches()).thenReturn(repo1Branches);
        when(repository1.getBranch(anyString())).then(inv -> repo1Branches.stream().filter(b -> b.getName().equals(inv.getArgumentAt(0, String.class))).findFirst());

        final WorkspaceProject project = mock(WorkspaceProject.class);
        doReturn(repository1).when(project).getRepository();
        final Space space = new Space("my-space");
        doReturn(space).when(project).getSpace();
        doReturn(mock(SpaceConfigStorage.class)).when(spaceConfigStorageRegistry).get("my-space");
        doReturn(repository1).when(repositoryService).getRepositoryFromSpace(space, "repository1");

        workspaceProjectService.removeBranch(otherBranch.getName(),
                                             project,
                                             "user");

        verify(ioService).startBatch(fileSystem);
        verify(ioService).delete(baseBranchPath);
        verify(ioService).endBatch();
        verify(repositoryUpdatedEvent).fire(any());
    }

    private Branch makeBranch(final String branchName,
        final String repoName) {
        final Path path = mock(Path.class);
        doReturn("default://" + branchName + "@" + repoName + "/").when(path).toURI();
        return new Branch(branchName, path);
    }

    private void assertContains(final Repository repository,
                                final Collection<WorkspaceProject> allWorkspaceProjects) {

        for (final WorkspaceProject workspaceProject : allWorkspaceProjects) {
            if (workspaceProject.getRepository().equals(repository)) {
                return;
            }
        }

        fail("Could not find " + repository);
    }
}
