/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
 *
 */

package org.kie.workbench.common.screens.examples.backend.server;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import javax.enterprise.event.Event;

import org.guvnor.common.services.project.backend.server.utils.PathUtil;
import org.guvnor.common.services.project.context.WorkspaceProjectContextChangeEvent;
import org.guvnor.common.services.project.events.NewProjectEvent;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.guvnor.structure.backend.organizationalunit.config.SpaceConfigStorageRegistryImpl;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.organizationalunit.config.RepositoryInfo;
import org.guvnor.structure.organizationalunit.config.SpaceConfigStorage;
import org.guvnor.structure.organizationalunit.config.SpaceConfigStorageRegistry;
import org.guvnor.structure.organizationalunit.impl.OrganizationalUnitImpl;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.EnvironmentParameters;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryEnvironmentConfigurations;
import org.guvnor.structure.repositories.RepositoryService;
import org.guvnor.structure.repositories.impl.git.GitRepository;
import org.guvnor.structure.server.repositories.RepositoryFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.examples.model.Credentials;
import org.kie.workbench.common.screens.examples.model.ExampleRepository;
import org.kie.workbench.common.screens.examples.model.ImportProject;
import org.kie.workbench.common.screens.examples.validation.ImportProjectValidators;
import org.kie.workbench.common.screens.projecteditor.service.ProjectScreenService;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.spi.FileSystemProvider;
import org.uberfire.java.nio.fs.jgit.JGitFileSystem;
import org.uberfire.java.nio.fs.jgit.JGitPathImpl;
import org.uberfire.spaces.Space;

import static java.util.Collections.emptyList;
import static org.junit.Assert.*;
import static org.kie.workbench.common.screens.examples.backend.server.ImportUtils.makeGitRepository;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProjectImportServiceImplTest {

    @Mock
    private IOService ioService;

    @Mock
    private RepositoryFactory repositoryFactory;

    @Mock
    private KieModuleService moduleService;

    @Mock
    private OrganizationalUnitService ouService;

    @Mock
    private MetadataService metadataService;

    @Mock
    private WorkspaceProjectService projectService;

    @Mock
    private ProjectScreenService projectScreenService;

    private ProjectImportServiceImpl service;

    @Mock
    private ImportProjectValidators validators;

    @Mock
    private PathUtil pathUtil;

    private final PathUtil realPathUtil = new PathUtil();

    @Mock
    private Event<NewProjectEvent> newProjectEvent;

    @Mock
    private RepositoryService repoService;

    @Mock
    private SpaceConfigStorageRegistry spaceConfigStorageRegistry;

    @Mock
    private SpaceConfigStorage spaceConfigStorage;

    @Mock
    private OrganizationalUnit organizationalUnit;

    @Mock
    private Space space;

    @Before
    public void setup() {
        service = spy(new ProjectImportServiceImpl(ioService,
                                                   metadataService,
                                                   repositoryFactory,
                                                   moduleService,
                                                   validators,
                                                   pathUtil,
                                                   projectService,
                                                   projectScreenService,
                                                   newProjectEvent,
                                                   repoService,
                                                   spaceConfigStorageRegistry));

        when(spaceConfigStorageRegistry.get(anyString())).thenReturn(spaceConfigStorage);
        when(spaceConfigStorageRegistry.getBatch(anyString())).thenReturn(new SpaceConfigStorageRegistryImpl.SpaceStorageBatchImpl(spaceConfigStorage));
        when(spaceConfigStorageRegistry.exist(anyString())).thenReturn(true);

        when(organizationalUnit.getName()).thenReturn("ou");
        when(organizationalUnit.getSpace()).thenReturn(space);
        when(space.getName()).thenReturn("ou");
    }

    @Test
    public void testGetProjects_NullRepository() {
        final Set<ImportProject> modules = service.getProjects(null);
        assertNotNull(modules);
        assertEquals(0,
                     modules.size());
    }

    @Test
    public void testGetProjects_NullRepositoryUrl() {
        final Set<ImportProject> modules = service.getProjects(new ExampleRepository(null));
        assertNotNull(modules);
        assertEquals(0,
                     modules.size());
    }

    @Test
    public void testGetProjects_EmptyRepositoryUrl() {
        final Set<ImportProject> modules = service.getProjects(new ExampleRepository(""));
        assertNotNull(modules);
        assertEquals(0,
                     modules.size());
    }

    @Test
    public void testGetProjects_WhiteSpaceRepositoryUrl() {
        final Set<ImportProject> modules = service.getProjects(new ExampleRepository("   "));
        assertNotNull(modules);
        assertEquals(0,
                     modules.size());
    }

    @Test
    public void testGetProjects_DefaultDescription() {
        final Path moduleRoot = mock(Path.class);
        final KieModule module = mock(KieModule.class);
        when(module.getRootPath()).thenReturn(moduleRoot);
        when(module.getModuleName()).thenReturn("module1");
        when(moduleRoot.toURI()).thenReturn("default:///module1");
        when(metadataService.getTags(any(Path.class))).thenReturn(Arrays.asList("tag1",
                                                                                "tag2"));
        when(pathUtil.convert(any(Path.class))).thenCallRealMethod();

        final GitRepository repository = makeGitRepository();
        when(repositoryFactory.newRepository(any(RepositoryInfo.class))).thenReturn(repository);
        when(moduleService.getAllModules(any(Branch.class))).thenReturn(new HashSet<Module>() {{
            add(module);
        }});
        doReturn(Collections.singletonList("master")).when(service).getBranches(any(org.uberfire.java.nio.file.Path.class),
                                                                                any(Path.class));

        String origin = "https://github.com/guvnorngtestuser1/guvnorng-playground.git";
        final Set<ImportProject> modules = service.getProjects(new ExampleRepository(origin));
        assertNotNull(modules);
        assertEquals(1,
                     modules.size());
        assertTrue(modules.contains(new ImportProject(moduleRoot,
                                                      "module1",
                                                      "Example 'module1' module",
                                                      origin,
                                                      Arrays.asList("tag1",
                                                                    "tag2"),
                                                      null,
                                                      Collections.singletonList("master"),
                                                      true)));
    }

    @Test
    public void testGetProjects_CustomDescription() {
        final Path moduleRoot = mock(Path.class);
        final KieModule module = mock(KieModule.class);
        when(module.getRootPath()).thenReturn(moduleRoot);
        when(module.getModuleName()).thenReturn("module1");
        when(moduleRoot.toURI()).thenReturn("default:///module1");
        when(ioService.exists(any(org.uberfire.java.nio.file.Path.class))).thenReturn(true);
        when(ioService.readAllString(any(org.uberfire.java.nio.file.Path.class))).thenReturn("This is custom description.\n\n This is a new line.");
        when(metadataService.getTags(any(Path.class))).thenReturn(Arrays.asList("tag1",
                                                                                "tag2"));
        when(pathUtil.convert(any(Path.class))).thenCallRealMethod();

        final GitRepository repository = makeGitRepository();
        when(repositoryFactory.newRepository(any(RepositoryInfo.class))).thenReturn(repository);
        when(moduleService.getAllModules(any(Branch.class))).thenReturn(new HashSet<Module>() {{
            add(module);
        }});
        doReturn(Collections.singletonList("master")).when(service).getBranches(any(org.uberfire.java.nio.file.Path.class),
                                                                                any(Path.class));

        String origin = "https://github.com/guvnorngtestuser1/guvnorng-playground.git";
        final Set<ImportProject> modules = service.getProjects(new ExampleRepository(origin));
        assertNotNull(modules);
        assertEquals(1,
                     modules.size());
        assertTrue(modules.contains(new ImportProject(moduleRoot,
                                                      "module1",
                                                      "This is custom description. This is a new line.",
                                                      origin,
                                                      Arrays.asList("tag1",
                                                                    "tag2"),
                                                      null,
                                                      Collections.singletonList("master"),
                                                      true)));
    }

    @Test
    public void testGetProjects_PomDescription() {
        final Path moduleRoot = mock(Path.class);
        final POM pom = mock(POM.class);
        final KieModule module = mock(KieModule.class);
        when(pom.getDescription()).thenReturn("pom description");
        when(module.getRootPath()).thenReturn(moduleRoot);
        when(module.getModuleName()).thenReturn("module1");
        when(module.getPom()).thenReturn(pom);
        when(moduleRoot.toURI()).thenReturn("default:///module1");
        when(metadataService.getTags(any(Path.class))).thenReturn(Arrays.asList("tag1",
                                                                                "tag2"));
        when(pathUtil.convert(any(Path.class))).thenCallRealMethod();

        final GitRepository repository = makeGitRepository();
        when(repositoryFactory.newRepository(any(RepositoryInfo.class))).thenReturn(repository);
        when(moduleService.getAllModules(any(Branch.class))).thenReturn(new HashSet<Module>() {{
            add(module);
        }});
        doReturn(Collections.singletonList("master")).when(service).getBranches(any(org.uberfire.java.nio.file.Path.class),
                                                                                any(Path.class));

        String origin = "https://github.com/guvnorngtestuser1/guvnorng-playground.git";
        final Set<ImportProject> modules = service.getProjects(new ExampleRepository(origin));
        assertNotNull(modules);
        assertEquals(1,
                     modules.size());
        assertTrue(modules.contains(new ImportProject(moduleRoot,
                                                      "module1",
                                                      "pom description",
                                                      origin,
                                                      Arrays.asList("tag1",
                                                                    "tag2"),
                                                      null,
                                                      Collections.singletonList("master"),
                                                      true)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testImportProjects_NullOrganizationalUnit() {
        service.importProjects(null,
                               mock(List.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testImportProjects_NullModule() {
        service.importProjects(mock(OrganizationalUnit.class),
                               null);
    }

    @Test(expected = IllegalStateException.class)
    public void testImportProjects_ZeroModules() {
        service.importProjects(mock(OrganizationalUnit.class),
                               Collections.emptyList());
    }

    @Test
    public void testImportProjects_ProjectImport() {
        final ImportProject exProject1 = mock(ImportProject.class);
        final ImportProject exProject2 = mock(ImportProject.class);
        final List<ImportProject> exProjects = Arrays.asList(exProject1,
                                                             exProject2);
        final GitRepository repository1 = mock(GitRepository.class);
        final Path repositoryRoot = mock(Path.class);
        final Path module1Root = mock(Path.class);
        final Path module2Root = mock(Path.class);

        when(exProject1.getName()).thenReturn("project1");
        when(exProject1.getRoot()).thenReturn(module1Root);
        when(exProject2.getName()).thenReturn("project2");
        when(exProject2.getRoot()).thenReturn(module2Root);

        when(repository1.getBranch("dev_branch")).thenReturn(Optional.of(new Branch("dev_branch",
                                                                                    repositoryRoot)));
        final Optional<Branch> master = Optional.of(new Branch("master",
                                                               PathFactory.newPath("testFile",
                                                                                   "file:///")));
        when(repository1.getDefaultBranch()).thenReturn(master);

        when(repositoryRoot.toURI()).thenReturn("default:///");
        when(module1Root.toURI()).thenReturn("default:///module1");
        when(module2Root.toURI()).thenReturn("default:///module2");

        when(ouService.getOrganizationalUnit(eq("ou"))).thenReturn(organizationalUnit);

        WorkspaceProject project1 = mock(WorkspaceProject.class);
        when(project1.getName()).thenReturn("project1");
        when(project1.getBranch()).thenReturn(master.get());

        WorkspaceProject project2 = mock(WorkspaceProject.class);
        when(project2.getName()).thenReturn("project2");
        when(project2.getBranch()).thenReturn(master.get());

        doReturn(project1).when(service).importProject(eq(organizationalUnit), eq(exProject1));

        doReturn(project2).when(service).importProject(eq(organizationalUnit), eq(exProject2));
        final WorkspaceProject project = spy(new WorkspaceProject());
        doReturn("project").when(project).getName();
        doReturn(project).when(projectService).resolveProject(repository1);

        final WorkspaceProjectContextChangeEvent event = service.importProjects(organizationalUnit, exProjects);

        assertEquals(organizationalUnit, event.getOrganizationalUnit());
        assertEquals(null, event.getWorkspaceProject());

        verify(ouService, never()).createOrganizationalUnit(eq("ou"), eq(""));
        verify(service, times(2)).importProject(eq(organizationalUnit), any());

        verify(spaceConfigStorage).startBatch();
        verify(spaceConfigStorage).endBatch();

    }

    @Test
    public void importProjectWithCredentialsTest() {
        final Repository repo = mock(Repository.class);
        final WorkspaceProject project = mock(WorkspaceProject.class);

        final String repositoryURL = "file:///some/path/to/fake-repo.git";
        final String username = "fakeUser";
        final String password = "fakePassword";
        final List<String> branches = Arrays.asList("master");

        final ArgumentCaptor<RepositoryEnvironmentConfigurations> configCaptor = ArgumentCaptor.forClass(RepositoryEnvironmentConfigurations.class);

        when(repoService.createRepository(any(),
                                          any(),
                                          any(),
                                          configCaptor.capture())).thenReturn(repo);

        when(projectService.resolveProject(any(Repository.class))).thenReturn(project);

        final WorkspaceProject observedProject = service.importProject(organizationalUnit,
                                                                       repositoryURL,
                                                                       username,
                                                                       password,
                                                                       branches);

        verify(repoService).createRepository(same(organizationalUnit),
                                             eq(GitRepository.SCHEME.toString()),
                                             eq("fake-repo"),
                                             any());
        RepositoryEnvironmentConfigurations observedConfig = configCaptor.getValue();
        assertEquals(username,
                     observedConfig.getUserName());
        assertEquals(password,
                     observedConfig.getPassword());
        assertEquals(repositoryURL,
                     observedConfig.getOrigin());
        assertEquals(branches,
                     observedConfig.getBranches());

        verify(projectService).resolveProject(same(repo));

        assertSame(project,
                   observedProject);
    }

    @Test
    public void testProjectImportWithCredentialsTest() {

        final String origin = "file:///some/path/to/fake-repo.git";
        final String username = "fakeUser";
        final String password = "fakePassword";
        final List<String> branches = Arrays.asList("master");

        final ImportProject importProject = mock(ImportProject.class);
        final Path rootPath = mock(Path.class);
        final org.uberfire.java.nio.file.Path convertedRootPath = mock(org.uberfire.java.nio.file.Path.class);
        when(pathUtil.convert(any(Path.class))).thenReturn(convertedRootPath);
        when(importProject.getCredentials()).thenReturn(new Credentials(username,
                                                                        password));
        when(importProject.getRoot()).thenReturn(rootPath);

        when(importProject.getOrigin()).thenReturn(origin);

        when(importProject.getSelectedBranches()).thenReturn(branches);

        service.importProject(organizationalUnit,
                              importProject);

        verify(service).importProject(organizationalUnit,
                                      origin,
                                      username,
                                      password,
                                      branches);
    }

    @Test
    public void testProjectImportWithNullCredentialsTest() {

        final ArgumentCaptor<RepositoryEnvironmentConfigurations> captor = ArgumentCaptor.forClass(RepositoryEnvironmentConfigurations.class);

        final String origin = "file:///some/path/to/fake-repo.git";
        final String username = "fakeUser";
        final String password = null;
        final List<String> branches = Arrays.asList("master");

        final ImportProject importProject = mock(ImportProject.class);
        final Path rootPath = mock(Path.class);
        final org.uberfire.java.nio.file.Path convertedRootPath = mock(org.uberfire.java.nio.file.Path.class);
        when(pathUtil.convert(any(Path.class))).thenReturn(convertedRootPath);
        when(importProject.getCredentials()).thenReturn(new Credentials(username,
                                                                        password));
        when(importProject.getRoot()).thenReturn(rootPath);

        when(importProject.getOrigin()).thenReturn(origin);

        when(importProject.getSelectedBranches()).thenReturn(branches);

        service.importProject(organizationalUnit,
                              importProject);

        verify(service).importProject(organizationalUnit,
                                      origin,
                                      username,
                                      password,
                                      branches);
        verify(repoService).createRepository(any(),
                                             any(),
                                             any(),
                                             captor.capture());

        assertFalse(captor.getValue().containsConfiguration(EnvironmentParameters.USER_NAME));
        assertFalse(captor.getValue().containsConfiguration(EnvironmentParameters.PASSWORD));
    }

    @Test
    public void importProjectWithoutCredentialsTest() {
        final Repository repo = mock(Repository.class);
        final WorkspaceProject project = mock(WorkspaceProject.class);

        final String repositoryURL = "file:///some/path/to/fake-repo.git";
        final String username = null;
        final String password = null;
        final List<String> branches = Arrays.asList("master");

        final ArgumentCaptor<RepositoryEnvironmentConfigurations> configCaptor = ArgumentCaptor.forClass(RepositoryEnvironmentConfigurations.class);

        when(repoService.createRepository(any(),
                                          any(),
                                          any(),
                                          configCaptor.capture())).thenReturn(repo);
        when(projectService.resolveProject(any(Repository.class))).thenReturn(project);

        final WorkspaceProject observedProject = service.importProject(organizationalUnit,
                                                                       repositoryURL,
                                                                       username,
                                                                       password,
                                                                       branches);

        verify(repoService).createRepository(same(organizationalUnit),
                                             eq(GitRepository.SCHEME.toString()),
                                             eq("fake-repo"),
                                             any());
        RepositoryEnvironmentConfigurations observedConfig = configCaptor.getValue();
        assertEquals(username,
                     observedConfig.getUserName());
        assertEquals(password,
                     observedConfig.getPassword());
        assertEquals(repositoryURL,
                     observedConfig.getOrigin());
        assertEquals(branches,
                     observedConfig.getBranches());

        verify(projectService).resolveProject(same(repo));

        assertSame(project,
                   observedProject);
    }

    @Test
    public void importDefaultProjectTest() {
        final OrganizationalUnit organizationalUnit = new OrganizationalUnitImpl("myteam",
                                                                                 "org.whatever");
        organizationalUnit.getRepositories();

        final Path exampleRoot = mock(Path.class);
        final org.uberfire.java.nio.file.Path exampleRootNioPath = mock(org.uberfire.java.nio.file.Path.class);
        when(pathUtil.convert(exampleRoot)).thenReturn(exampleRootNioPath);
        String repoURL = "file:///some/repo/url";
        final ImportProject importProject = new ImportProject(exampleRoot,
                                                              "example",
                                                              "description",
                                                              repoURL,
                                                              emptyList());

        when(pathUtil.getNiogitRepoPath(any())).thenReturn(repoURL);

        final Repository repository = new GitRepository("example",
                                                        new Space("myteam"));
        final WorkspaceProject project = new WorkspaceProject(organizationalUnit,
                                                              repository,
                                                              new Branch("master",
                                                                         mock(Path.class)),
                                                              new Module());
        when(projectService.resolveProject(repository)).thenReturn(project);
        when(repoService.createRepository(same(organizationalUnit),
                                          eq(GitRepository.SCHEME.toString()),
                                          any(),
                                          any())).thenReturn(repository);

        final WorkspaceProject importedProject = service.importProject(organizationalUnit,
                                                                       importProject);

        assertSame(project,
                   importedProject);
        final ArgumentCaptor<RepositoryEnvironmentConfigurations> configsCaptor = ArgumentCaptor.forClass(RepositoryEnvironmentConfigurations.class);
        verify(repoService).createRepository(same(organizationalUnit),
                                             eq(GitRepository.SCHEME.toString()),
                                             any(),
                                             configsCaptor.capture());
        final RepositoryEnvironmentConfigurations configs = configsCaptor.getValue();
        assertEquals(repoURL,
                     configs.getOrigin());
        assertNull(configs.getSubdirectory());
        verify(projectService).resolveProject(repository);
    }

    @Test
    public void importDefaultProjectInWindowsTest() {
        final OrganizationalUnit organizationalUnit = new OrganizationalUnitImpl("myteam",
                                                                                 "org.whatever");
        organizationalUnit.getRepositories();

        final Path exampleRoot = mock(Path.class);
        final org.uberfire.java.nio.file.Path exampleRootNioPath = mock(org.uberfire.java.nio.file.Path.class);
        when(pathUtil.convert(exampleRoot)).thenReturn(exampleRootNioPath);
        String repoURL = "file:///C:/some/repo/url";
        final ImportProject importProject = new ImportProject(exampleRoot,
                                                              "example",
                                                              "description",
                                                              repoURL,
                                                              emptyList());

        when(pathUtil.getNiogitRepoPath(any())).thenReturn(repoURL);

        final Repository repository = new GitRepository("example",
                                                        new Space("myteam"));
        final WorkspaceProject project = new WorkspaceProject(organizationalUnit,
                                                              repository,
                                                              new Branch("master",
                                                                         mock(Path.class)),
                                                              new Module());
        when(projectService.resolveProject(repository)).thenReturn(project);
        when(repoService.createRepository(same(organizationalUnit),
                                          eq(GitRepository.SCHEME.toString()),
                                          any(),
                                          any())).thenReturn(repository);

        final WorkspaceProject importedProject = service.importProject(organizationalUnit,
                                                                       importProject);

        assertSame(project,
                   importedProject);
        final ArgumentCaptor<RepositoryEnvironmentConfigurations> configsCaptor = ArgumentCaptor.forClass(RepositoryEnvironmentConfigurations.class);
        verify(repoService).createRepository(same(organizationalUnit),
                                             eq(GitRepository.SCHEME.toString()),
                                             any(),
                                             configsCaptor.capture());
        final RepositoryEnvironmentConfigurations configs = configsCaptor.getValue();
        assertEquals(repoURL,
                     configs.getOrigin());
        assertNull(configs.getSubdirectory());
        verify(projectService).resolveProject(repository);
    }

    @Test
    public void importProjectInSubdirectory() {
        final OrganizationalUnit organizationalUnit = new OrganizationalUnitImpl("myteam",
                                                                                 "org.whatever");
        organizationalUnit.getRepositories();

        final String exampleURI = "default://master@system/repo/example";
        final Path exampleRoot = PathFactory.newPath("example",
                                                     exampleURI);
        final JGitFileSystem fs = mock(JGitFileSystem.class);
        final FileSystemProvider provider = mock(FileSystemProvider.class);
        when(fs.provider()).thenReturn(provider);
        final org.uberfire.java.nio.file.Path exampleRootNioPath = JGitPathImpl.create(fs,
                                                                                       "/example",
                                                                                       "master@system/repo",
                                                                                       true);
        final org.uberfire.java.nio.file.Path repoRoot = exampleRootNioPath.getParent();
        when(fs.getRootDirectories()).thenReturn(() -> Stream.of(repoRoot).iterator());
        when(pathUtil.convert(exampleRoot)).thenReturn(exampleRootNioPath);
        when(pathUtil.stripProtocolAndBranch(any())).then(inv -> realPathUtil.stripProtocolAndBranch(inv.getArgumentAt(0,
                                                                                                                       String.class)));
        when(pathUtil.stripRepoNameAndSpace(any())).then(inv -> realPathUtil.stripRepoNameAndSpace(inv.getArgumentAt(0,
                                                                                                                     String.class)));
        when(pathUtil.convert(any(org.uberfire.java.nio.file.Path.class))).then(inv -> realPathUtil.convert(inv.getArgumentAt(0,
                                                                                                                              org.uberfire.java.nio.file.Path.class)));
        when(pathUtil.extractBranch(any())).then(inv -> realPathUtil.extractBranch(inv.getArgumentAt(0,
                                                                                                     String.class)));

        String repoURL = "file:///some/repo/url";
        final ImportProject importProject = new ImportProject(exampleRoot,
                                                              "example",
                                                              "description",
                                                              repoURL,
                                                              emptyList());

        when(pathUtil.getNiogitRepoPath(any())).thenReturn(repoURL);

        final Repository repository = new GitRepository("example",
                                                        new Space("myteam"));
        final WorkspaceProject project = new WorkspaceProject(organizationalUnit,
                                                              repository,
                                                              new Branch("master",
                                                                         mock(Path.class)),
                                                              new Module());
        when(projectService.resolveProject(repository)).thenReturn(project);
        when(repoService.createRepository(same(organizationalUnit),
                                          eq(GitRepository.SCHEME.toString()),
                                          any(),
                                          any())).thenReturn(repository);

        final WorkspaceProject importedProject = service.importProject(organizationalUnit,
                                                                       importProject);

        assertSame(project,
                   importedProject);
        final ArgumentCaptor<RepositoryEnvironmentConfigurations> configsCaptor = ArgumentCaptor.forClass(RepositoryEnvironmentConfigurations.class);
        verify(repoService).createRepository(same(organizationalUnit),
                                             eq(GitRepository.SCHEME.toString()),
                                             any(),
                                             configsCaptor.capture());
        final RepositoryEnvironmentConfigurations configs = configsCaptor.getValue();
        assertEquals(repoURL,
                     configs.getOrigin());
        assertEquals("example",
                     configs.getSubdirectory());
        verify(projectService).resolveProject(repository);
    }
}
