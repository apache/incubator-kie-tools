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

import java.io.File;
import java.util.Optional;

import javax.enterprise.inject.Instance;

import org.assertj.core.api.Assertions;
import org.guvnor.common.services.project.backend.server.utils.PathUtil;
import org.guvnor.common.services.project.events.NewProjectEvent;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.common.services.project.service.ModuleRepositoryResolver;
import org.guvnor.common.services.project.service.ModuleService;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.guvnor.common.services.shared.exceptions.GenericPortableException;
import org.guvnor.structure.backend.organizationalunit.config.SpaceConfigStorageRegistryImpl;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.organizationalunit.config.SpaceConfigStorage;
import org.guvnor.structure.organizationalunit.config.SpaceConfigStorageRegistry;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryEnvironmentConfigurations;
import org.guvnor.structure.repositories.RepositoryService;
import org.guvnor.structure.repositories.changerequest.ChangeRequestService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.fs.jgit.JGitFileSystem;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProvider;
import org.uberfire.java.nio.fs.jgit.JGitPathImpl;
import org.uberfire.java.nio.fs.jgit.util.Git;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.spaces.Space;
import org.uberfire.spaces.SpacesAPI;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @Mock
    IOService ioService;

    @Mock
    PathUtil pathUtil;

    @Mock
    ChangeRequestService changeRequestService;

    @Mock
    POMService pomService;

    @Mock
    SessionInfo sessionInfo;

    private POM pom;

    @Before
    public void setUp() throws Exception {

        doReturn(Optional.of(branch)).when(repository).getDefaultBranch();
        doReturn(repositoryRoot).when(branch).getPath();

        doReturn(repository).when(repositoryService).createRepository(eq(ou),
                                                                      eq("git"),
                                                                      eq("myproject"),
                                                                      any(RepositoryEnvironmentConfigurations.class));

        pom = createPOM("my project");

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
    public void newProjectFromTemplateTest() {
        final WorkspaceProjectServiceImpl impl = (WorkspaceProjectServiceImpl) spy(this.workspaceProjectService);

        final Repository templateRepository = mock(Repository.class);
        final Branch templateRepositoryBranch = mock(Branch.class);
        doReturn(Optional.of(templateRepositoryBranch)).when(templateRepository).getDefaultBranch();
        doReturn(repositoryRoot).when(templateRepositoryBranch).getPath();

        final JGitPathImpl nioPath = mock(JGitPathImpl.class);
        final JGitFileSystem fs = mock(JGitFileSystem.class);
        final Git git = mock(Git.class);
        doNothing().when(git).removeRemote(anyString(),
                                           anyString());
        doReturn(git).when(fs).getGit();
        doReturn(fs).when(nioPath).getFileSystem();
        doReturn(nioPath).when(pathUtil).convert(any(Path.class));

        final org.eclipse.jgit.lib.Repository gitRepository = mock(org.eclipse.jgit.lib.Repository.class);
        final File repositoryDirectory = new File("repositoryDirectory");
        doReturn(repositoryDirectory).when(gitRepository).getDirectory();
        doReturn(gitRepository).when(git).getRepository();

        final Path pomPath = mock(Path.class);
        doReturn(pomPath).when(impl).resolvePathFromParent(any(Path.class),
                                                           eq(POMServiceImpl.POM_XML));

        final POM templatePom = createPOM("my template");
        doReturn(templatePom).when(pomService).load(pomPath);

        doNothing().when(moduleService).createModuleDirectories(any(Path.class));
        doReturn(module).when(moduleService).resolveModule(any(Path.class));

        final Path branchRoot = PathFactory.newPath("testFile",
                                                    "file:///branchRoot/");
        doReturn(branchRoot).when(branch).getPath();

        final WorkspaceProject workspaceProject = impl.newProject(ou,
                                                                  pom,
                                                                  DeploymentMode.VALIDATED,
                                                                  null,
                                                                  templateRepository);

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
    public void newProjectFromTemplateWithRemoteUrlTest() {
        final String remoteUrl = "myUrl";
        final WorkspaceProjectServiceImpl impl = (WorkspaceProjectServiceImpl) spy(this.workspaceProjectService);

        final Repository templateRepository = mock(Repository.class);
        final Branch templateRepositoryBranch = mock(Branch.class);
        doReturn(Optional.of(templateRepositoryBranch)).when(templateRepository).getDefaultBranch();
        doReturn(repositoryRoot).when(templateRepositoryBranch).getPath();

        final JGitPathImpl nioPath = mock(JGitPathImpl.class);
        final JGitFileSystem fs = mock(JGitFileSystem.class);
        final JGitFileSystemProvider provider = mock(JGitFileSystemProvider.class);
        final Git git = mock(Git.class);
        doNothing().when(git).removeRemote(anyString(),
                                           anyString());
        doNothing().when(git).addRemote(anyString(),
                                        anyString());
        doReturn(git).when(fs).getGit();
        doReturn(fs).when(nioPath).getFileSystem();
        doReturn(provider).when(fs).provider();
        doNothing().when(provider).executePostCommitHook(fs);
        doReturn(nioPath).when(pathUtil).convert(any(Path.class));

        final org.eclipse.jgit.lib.Repository gitRepository = mock(org.eclipse.jgit.lib.Repository.class);
        final File repositoryDirectory = new File("repositoryDirectory");
        doReturn(repositoryDirectory).when(gitRepository).getDirectory();
        doReturn(gitRepository).when(git).getRepository();

        final Path pomPath = mock(Path.class);
        doReturn(pomPath).when(impl).resolvePathFromParent(any(Path.class),
                                                           eq(POMServiceImpl.POM_XML));

        final POM templatePom = createPOM("my template");
        doReturn(templatePom).when(pomService).load(pomPath);

        doNothing().when(moduleService).createModuleDirectories(any(Path.class));
        doReturn(module).when(moduleService).resolveModule(any(Path.class));

        final Path branchRoot = PathFactory.newPath("testFile",
                                                    "file:///branchRoot/");
        doReturn(branchRoot).when(branch).getPath();

        final WorkspaceProject workspaceProject = impl.newProject(ou,
                                                                  pom,
                                                                  DeploymentMode.VALIDATED,
                                                                  null,
                                                                  templateRepository,
                                                                  remoteUrl);

        assertProject(workspaceProject);
        verify(newProjectEvent).fire(any());

        verify(spaceConfigStorage).startBatch();
        verify(repositoryService).createRepository(eq(ou),
                                                   eq("git"),
                                                   eq("myproject"),
                                                   any(),
                                                   any());
        verify(spaceConfigStorage).endBatch();
        verify(provider).executePostCommitHook(fs);
        verify(git).addRemote(anyString(),
                              eq(remoteUrl));
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
    public void testNewProjectErrorRepositoryWithoutDefaultBranch() {
        when(repository.getDefaultBranch()).thenReturn(Optional.empty());

        testNewProjectError(GenericPortableException.class, "New repository should always have a branch.");
    }


    @Test
    public void testNewProjectErrorCreatingModule() {
        final String errorMessage = "Impossible to create module";

        doThrow(new IllegalStateException(errorMessage)).when(moduleService).newModule(any(), any(), any());

        testNewProjectError(GenericPortableException.class, errorMessage);
    }

    private void testNewProjectError(final Class<? extends Exception> expectedExceptionType, final String expectedMessage) {
        Assertions.assertThatThrownBy(() -> workspaceProjectService.newProject(ou, pom))
                .isInstanceOf(expectedExceptionType)
                .hasMessage(expectedMessage);

        verify(repositoryService).removeRepository(any(), anyString());
        verify(newProjectEvent, never()).fire(any());
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

    private POM createPOM(final String name) {
        return new POM(name,
                       "my description",
                       "url",
                       new GAV("groupId",
                               "artifactId",
                               "version"));
    }
}
