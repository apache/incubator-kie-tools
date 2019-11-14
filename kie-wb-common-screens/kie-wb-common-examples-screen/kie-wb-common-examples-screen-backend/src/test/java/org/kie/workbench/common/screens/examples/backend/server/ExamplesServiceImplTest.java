/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.examples.backend.server;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.enterprise.event.Event;

import org.apache.commons.io.FileUtils;
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
import org.guvnor.structure.repositories.RepositoryService;
import org.guvnor.structure.repositories.impl.git.GitRepository;
import org.guvnor.structure.server.repositories.RepositoryFactory;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.examples.model.ExampleOrganizationalUnit;
import org.kie.workbench.common.screens.examples.model.ExampleRepository;
import org.kie.workbench.common.screens.examples.model.ExamplesMetaData;
import org.kie.workbench.common.screens.examples.model.ImportProject;
import org.kie.workbench.common.screens.examples.validation.ImportProjectValidators;
import org.kie.workbench.common.screens.projecteditor.service.ProjectScreenService;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.spaces.Space;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.screens.examples.backend.server.ImportUtils.makeGitRepository;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ExamplesServiceImplTest {

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

    @Spy
    private Event<NewProjectEvent> newProjectEvent = new EventSourceMock<NewProjectEvent>() {
        @Override
        public void fire(final NewProjectEvent event) {
            //Do nothing. Default implementation throws an exception.
        }
    };

    @Mock
    private SessionInfo sessionInfo;

    @Mock
    private User user;

    @Mock
    private WorkspaceProjectService projectService;

    @Mock
    private ProjectScreenService projectScreenService;

    @Mock
    private ImportProjectValidators validators;

    @Mock
    private SpaceConfigStorageRegistry spaceConfigStorageRegistry;

    @Mock
    private SpaceConfigStorage spaceConfigStorage;

    @Mock
    private OrganizationalUnit ou;

    @Mock
    private Space space;

    private ExamplesServiceImpl service;

    @Captor
    private ArgumentCaptor<RepositoryInfo> captor;

    private Map<String, OrganizationalUnit> organizationalUnits = new HashMap();

    @Mock
    private FileSystem systemFS;

    private PathUtil pathUtil = new PathUtil();

    @Mock
    private RepositoryService repositoryService;

    @Before
    public void setup() throws Exception {

        when(spaceConfigStorageRegistry.get(anyString())).thenReturn(spaceConfigStorage);
        when(spaceConfigStorageRegistry.getBatch(anyString())).thenReturn(new SpaceConfigStorageRegistryImpl.SpaceStorageBatchImpl(spaceConfigStorage));
        when(spaceConfigStorageRegistry.exist(anyString())).thenReturn(true);

        when(ou.getSpace()).thenReturn(space);
        when(space.getName()).thenReturn("ou");

        service = spy(new ExamplesServiceImpl(ioService,
                                              repositoryFactory,
                                              moduleService,
                                              ouService,
                                              projectService,
                                              metadataService,
                                              newProjectEvent,
                                              projectScreenService,
                                              validators,
                                              spaceConfigStorageRegistry,
                                              systemFS,
                                              pathUtil,
                                              repositoryService));

        FileUtils.deleteDirectory(new File(".kie-wb-playground"));

        when(this.validators.getValidators()).thenReturn(new ArrayList<>());

        doAnswer(invocationOnMock -> {
            String spaceName = (String) invocationOnMock.getArguments()[0];
            String defaultGroupId = (String) invocationOnMock.getArguments()[1];
            OrganizationalUnitImpl o = new OrganizationalUnitImpl(spaceName, defaultGroupId);
            organizationalUnits.put(spaceName, o);
            return o;
        }).when(ouService).createOrganizationalUnit(anyString(), anyString());

        doAnswer(invocationOnMock -> organizationalUnits.get(invocationOnMock.getArguments()[0]))
                .when(ouService).getOrganizationalUnit(anyString());

        when(ouService.getOrganizationalUnits()).thenReturn(new HashSet<OrganizationalUnit>() {{
            add(new OrganizationalUnitImpl("ou1Name",
                                           "ou1GroupId"));
            add(new OrganizationalUnitImpl("ou2Name",
                                           "ou2GroupId"));
        }});
        when(moduleService.resolveModule(any(Path.class))).thenAnswer((Answer<KieModule>) invocationOnMock -> {
            final Path path = (Path) invocationOnMock.getArguments()[0];
            final KieModule module = new KieModule(path,
                                                   path,
                                                   path,
                                                   path,
                                                   path,
                                                   path,
                                                   mock(POM.class));
            return module;
        });
        when(sessionInfo.getId()).thenReturn("sessionId");
        when(sessionInfo.getIdentity()).thenReturn(user);
        when(user.getIdentifier()).thenReturn("user");

        doAnswer(invocationOnMock -> organizationalUnits.containsKey(invocationOnMock.getArguments()[0]))
                .when(service).existSpace(any());
    }

    @After
    public void tearDown() throws Exception {
        FileUtils.deleteDirectory(new File(".kie-wb-playground"));
    }

    @Test
    public void testMD5Sum() {
        URL resource = getClass().getClassLoader().getResource("test.zip");
        String md5 = service.calculateMD5(resource);
        assertEquals("088947832487928ebbc994abe4e2ac33", md5);
    }

    @Test
    public void initPlaygroundRepository() {
        //Emulate @PostConstruct mechanism
        service.initPlaygroundRepository();

        final ExampleRepository exampleRepository = service.getPlaygroundRepository();

        assertNotNull(exampleRepository);
    }

    @Test
    public void testGetMetaData() {
        //Emulate @PostConstruct mechanism
        service.initPlaygroundRepository();

        final ExamplesMetaData metaData = service.getMetaData();

        assertNotNull(metaData);
        assertNotNull(metaData.getRepository());

        assertNotNull(metaData.getRepository().getUrl());
    }

    @Test
    public void testGetProjects_NullRepository() {
        final Set<ImportProject> modules = service.getProjects(ou, null);
        assertNotNull(modules);
        assertEquals(0,
                     modules.size());
    }

    @Test
    public void testGetProjects_NullRepositoryUrl() {
        final Set<ImportProject> modules = service.getProjects(ou, new ExampleRepository(null));
        assertNotNull(modules);
        assertEquals(0,
                     modules.size());
    }

    @Test
    public void testGetProjects_EmptyRepositoryUrl() {
        final Set<ImportProject> modules = service.getProjects(ou, new ExampleRepository(""));
        assertNotNull(modules);
        assertEquals(0,
                     modules.size());
    }

    @Test
    public void testGetProjects_WhiteSpaceRepositoryUrl() {
        final Set<ImportProject> modules = service.getProjects(ou, new ExampleRepository("   "));
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

        final GitRepository repository = makeGitRepository();
        when(repositoryFactory.newRepository(any(RepositoryInfo.class))).thenReturn(repository);
        when(moduleService.getAllModules(any(Branch.class))).thenReturn(new HashSet<Module>() {{
            add(module);
        }});

        service.setPlaygroundRepository(mock(ExampleRepository.class));

        String origin = "https://github.com/guvnorngtestuser1/guvnorng-playground.git";
        final Set<ImportProject> modules = service.getProjects(ou, new ExampleRepository(origin));
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
                                                      Collections.emptyList(),
                                                      false)));
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

        final GitRepository repository = makeGitRepository();
        when(repositoryFactory.newRepository(any(RepositoryInfo.class))).thenReturn(repository);
        when(moduleService.getAllModules(any(Branch.class))).thenReturn(new HashSet<Module>() {{
            add(module);
        }});

        String origin = "https://github.com/guvnorngtestuser1/guvnorng-playground.git";
        final Set<ImportProject> modules = service.getProjects(ou, new ExampleRepository(origin));
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
                                                      Collections.emptyList(),
                                                      false)));
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

        final GitRepository repository = makeGitRepository();
        when(repositoryFactory.newRepository(any(RepositoryInfo.class))).thenReturn(repository);
        when(moduleService.getAllModules(any(Branch.class))).thenReturn(new HashSet<Module>() {{
            add(module);
        }});

        String origin = "https://github.com/guvnorngtestuser1/guvnorng-playground.git";
        final Set<ImportProject> modules = service.getProjects(ou, new ExampleRepository(origin));
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
                                                      Collections.emptyList(),
                                                      false)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetupExamples_NullOrganizationalUnit() {
        service.setupExamples(null,
                              mock(List.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetupExamples_NullModule() {
        service.setupExamples(mock(ExampleOrganizationalUnit.class),
                              null);
    }

    @Test(expected = IllegalStateException.class)
    public void testSetupExamples_ZeroModules() {
        service.setupExamples(mock(ExampleOrganizationalUnit.class),
                              Collections.emptyList());
    }

    @Test
    public void testSetupExamples_NewOrganizationalUnitNewRepository() {
        final ExampleOrganizationalUnit exOU = mock(ExampleOrganizationalUnit.class);
        final ImportProject exModule = mock(ImportProject.class);
        doReturn("module").when(exModule).getName();
        final List<ImportProject> exModules = Collections.singletonList(exModule);
        final GitRepository repository = mock(GitRepository.class);
        final Path repositoryRoot = mock(Path.class);
        final Path moduleRoot = mock(Path.class);

        when(exOU.getName()).thenReturn("ou");
        when(exModule.getName()).thenReturn("module");
        when(exModule.getRoot()).thenReturn(moduleRoot);

        when(repository.getDefaultBranch()).thenReturn(Optional.of(new Branch("master",
                                                                              repositoryRoot)));
        when(repositoryRoot.toURI()).thenReturn("default:///");
        when(moduleRoot.toURI()).thenReturn("default:///module");

        when(ouService.getOrganizationalUnit(eq("ou"))).thenReturn(null);
        when(ouService.createOrganizationalUnit(eq("ou"),
                                                eq(""))).thenReturn(ou);
        final WorkspaceProject project = spy(new WorkspaceProject());

        doReturn(project).when(service).importProject(eq(ou),
                                                      eq(exModule));

        doReturn("project").when(project).getName();
        doReturn(project).when(projectService).resolveProject(repository);

        final WorkspaceProjectContextChangeEvent event = service.setupExamples(exOU,
                                                                               exModules);

        assertNull(event.getOrganizationalUnit());
        assertEquals(project,
                     event.getWorkspaceProject());

        verify(ouService,
               times(1)).createOrganizationalUnit(eq("ou"),
                                                  eq(""));
        verify(service,
               times(1)).importProject(eq(ou),
                                       eq(exModule));
        verify(newProjectEvent,
               times(1)).fire(any(NewProjectEvent.class));
    }

    @Test
    public void testSetupExamples_ProjectCopy() {
        final ExampleOrganizationalUnit exOU = mock(ExampleOrganizationalUnit.class);
        final ImportProject exProject1 = mock(ImportProject.class);
        doReturn("project 1").when(exProject1).getName();
        final ImportProject exProject2 = mock(ImportProject.class);
        doReturn("project 2").when(exProject1).getName();
        final List<ImportProject> exProjects = Arrays.asList(exProject1,
                                                             exProject2);

        final GitRepository repository1 = mock(GitRepository.class);
        final Path repositoryRoot = mock(Path.class);
        final Path module1Root = mock(Path.class);
        final Path module2Root = mock(Path.class);

        when(exOU.getName()).thenReturn("ou");
        when(exProject1.getName()).thenReturn("module1");
        when(exProject1.getRoot()).thenReturn(module1Root);
        when(exProject2.getName()).thenReturn("module2");
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

        when(ouService.getOrganizationalUnit(eq("ou"))).thenReturn(ou);

        final WorkspaceProject project = spy(new WorkspaceProject());

        doReturn(project).when(service).importProject(eq(ou),
                                                      eq(exProject1));

        doReturn(project).when(service).importProject(eq(ou),
                                                      eq(exProject2));
        doReturn("project").when(project).getName();
        doReturn(project).when(projectService).resolveProject(repository1);

        final WorkspaceProjectContextChangeEvent event = service.setupExamples(exOU,
                                                                               exProjects);

        assertNull(event.getWorkspaceProject());
        assertEquals(ou,
                     event.getOrganizationalUnit());

        verify(ouService,
               never()).createOrganizationalUnit(eq("ou"),
                                                 eq(""));
        verify(service,
               times(2)).importProject(eq(ou),
                                       any(ImportProject.class));

        verify(newProjectEvent,
               times(2)).fire(any(NewProjectEvent.class));
    }

    @Test
    public void resolveRepositoryUrlOnWindows() {
        doReturn("\\").when(service).getFileSeparator();

        final String playgroundDirectoryPath = "C:\\folder\\.kie-wb-playground";

        final String repositoryUrl = service.resolveRepositoryUrl(playgroundDirectoryPath);

        assertEquals("file:///C:/folder/.kie-wb-playground",
                     repositoryUrl);
    }

    @Test
    public void resolveRepositoryUrlOnUnix() {
        doReturn("/").when(service).getFileSeparator();

        final String playgroundDirectoryPath = "/home/user/folder/.kie-wb-playground";

        final String repositoryUrl = service.resolveRepositoryUrl(playgroundDirectoryPath);

        assertEquals("file:///home/user/folder/.kie-wb-playground",
                     repositoryUrl);
    }

    @Test
    public void testInitializeTwice() {

        service.initPlaygroundRepository();
        service.initPlaygroundRepository();

        verify(service, times(2)).existSpace(any());

        verify(service, times(1)).createPlaygroundHiddenSpace(any());
    }

    @Test
    public void testIsOldPlayground() {
        service.md5 = "1234asd";
        service.playgroundSpaceName = ".playground-" + service.md5;
        OrganizationalUnit ou1 = mock(OrganizationalUnit.class);
        when(ou1.getName()).thenReturn(".playground-1234");
        assertTrue(service.isOldPlayground(ou1));
    }

    @Test
    public void testExistSpace() {
        java.nio.file.Path path = Paths.get("src/test/resources/niogit-test");
        assertTrue(service.existSpace(".playground-hidden", path));
        assertFalse(service.existSpace(".playground-1235", path));
    }
}

