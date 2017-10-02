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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.enterprise.event.Event;

import org.guvnor.common.services.project.context.ProjectContextChangeEvent;
import org.guvnor.common.services.project.events.NewProjectEvent;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.organizationalunit.impl.OrganizationalUnitImpl;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryEnvironmentConfigurations;
import org.guvnor.structure.repositories.RepositoryService;
import org.guvnor.structure.repositories.impl.git.GitRepository;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigType;
import org.guvnor.structure.server.config.ConfigurationFactory;
import org.guvnor.structure.server.repositories.RepositoryFactory;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.examples.model.ExampleOrganizationalUnit;
import org.kie.workbench.common.screens.examples.model.ExampleProject;
import org.kie.workbench.common.screens.examples.model.ExampleRepository;
import org.kie.workbench.common.screens.examples.model.ExampleTargetRepository;
import org.kie.workbench.common.screens.examples.model.ExamplesMetaData;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.rpc.SessionInfo;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ExamplesServiceImplTest {

    @Mock
    private IOService ioService;

    @Mock
    private ConfigurationFactory configurationFactory;

    @Mock
    private RepositoryFactory repositoryFactory;

    @Mock
    private KieProjectService projectService;

    @Mock
    private RepositoryService repositoryService;

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

    private ExamplesServiceImpl service;

    @Before
    public void setup() {
        service = spy(new ExamplesServiceImpl(ioService,
                                              configurationFactory,
                                              repositoryFactory,
                                              projectService,
                                              repositoryService,
                                              ouService,
                                              metadataService,
                                              newProjectEvent,
                                              sessionInfo));
        when(ouService.getOrganizationalUnits()).thenReturn(new HashSet<OrganizationalUnit>() {{
            add(new OrganizationalUnitImpl("ou1Name",
                                           "ou1Owner",
                                           "ou1GroupId"));
            add(new OrganizationalUnitImpl("ou2Name",
                                           "ou2Owner",
                                           "ou2GroupId"));
        }});
        when(projectService.resolveProject(any(Path.class))).thenAnswer(new Answer<KieProject>() {
            @Override
            public KieProject answer(final InvocationOnMock invocationOnMock) throws Throwable {
                final Path path = (Path) invocationOnMock.getArguments()[0];
                final KieProject project = new KieProject(path,
                                                          path,
                                                          path,
                                                          path,
                                                          path,
                                                          path,
                                                          "");
                return project;
            }
        });
        when(sessionInfo.getId()).thenReturn("sessionId");
        when(sessionInfo.getIdentity()).thenReturn(user);
        when(user.getIdentifier()).thenReturn("user");
        when(configurationFactory.newConfigGroup(any(ConfigType.class),
                                                 anyString(),
                                                 anyString())).thenReturn(mock(ConfigGroup.class));
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
        assertNotNull(metaData.getOrganizationalUnits());
        assertEquals(2,
                     metaData.getOrganizationalUnits().size());

        assertNotNull(metaData.getRepository().getUrl());

        final Set<ExampleOrganizationalUnit> exampleOrganizationalUnits = metaData.getOrganizationalUnits();
        assertTrue(exampleOrganizationalUnits.contains(new ExampleOrganizationalUnit("ou1Name")));
        assertTrue(exampleOrganizationalUnits.contains(new ExampleOrganizationalUnit("ou2Name")));
    }

    @Test
    public void testGetProjects_NullRepository() {
        final Set<ExampleProject> projects = service.getProjects(null);
        assertNotNull(projects);
        assertEquals(0,
                     projects.size());
    }

    @Test
    public void testGetProjects_NullRepositoryUrl() {
        final Set<ExampleProject> projects = service.getProjects(new ExampleRepository(null));
        assertNotNull(projects);
        assertEquals(0,
                     projects.size());
    }

    @Test
    public void testGetProjects_EmptyRepositoryUrl() {
        final Set<ExampleProject> projects = service.getProjects(new ExampleRepository(""));
        assertNotNull(projects);
        assertEquals(0,
                     projects.size());
    }

    @Test
    public void testGetProjects_WhiteSpaceRepositoryUrl() {
        final Set<ExampleProject> projects = service.getProjects(new ExampleRepository("   "));
        assertNotNull(projects);
        assertEquals(0,
                     projects.size());
    }

    @Test
    public void testGetProjects_DefaultDescription() {
        final Path projectRoot = mock(Path.class);
        final KieProject project = mock(KieProject.class);
        when(project.getRootPath()).thenReturn(projectRoot);
        when(project.getProjectName()).thenReturn("project1");
        when(projectRoot.toURI()).thenReturn("default:///project1");
        when(metadataService.getTags(any(Path.class))).thenReturn(Arrays.asList("tag1",
                                                                                "tag2"));

        final GitRepository repository = new GitRepository("guvnorng-playground");
        when(repositoryFactory.newRepository(any(ConfigGroup.class))).thenReturn(repository);
        when(projectService.getProjects(eq(repository),
                                        any(String.class))).thenReturn(new HashSet<Project>() {{
            add(project);
        }});

        service.setPlaygroundRepository(mock(ExampleRepository.class));

        final Set<ExampleProject> projects = service.getProjects(new ExampleRepository("https://github.com/guvnorngtestuser1/guvnorng-playground.git"));
        assertNotNull(projects);
        assertEquals(1,
                     projects.size());
        assertTrue(projects.contains(new ExampleProject(projectRoot,
                                                        "project1",
                                                        "Example 'project1' project",
                                                        Arrays.asList("tag1",
                                                                      "tag2"))));
    }

    @Test
    public void testGetProjects_CustomDescription() {
        final Path projectRoot = mock(Path.class);
        final KieProject project = mock(KieProject.class);
        when(project.getRootPath()).thenReturn(projectRoot);
        when(project.getProjectName()).thenReturn("project1");
        when(projectRoot.toURI()).thenReturn("default:///project1");
        when(ioService.exists(any(org.uberfire.java.nio.file.Path.class))).thenReturn(true);
        when(ioService.readAllString(any(org.uberfire.java.nio.file.Path.class))).thenReturn("This is custom description.\n\n This is a new line.");
        when(metadataService.getTags(any(Path.class))).thenReturn(Arrays.asList("tag1",
                                                                                "tag2"));

        final GitRepository repository = new GitRepository("guvnorng-playground");
        when(repositoryFactory.newRepository(any(ConfigGroup.class))).thenReturn(repository);
        when(projectService.getProjects(eq(repository),
                                        any(String.class))).thenReturn(new HashSet<Project>() {{
            add(project);
        }});

        final Set<ExampleProject> projects = service.getProjects(new ExampleRepository("https://github.com/guvnorngtestuser1/guvnorng-playground.git"));
        assertNotNull(projects);
        assertEquals(1,
                     projects.size());
        assertTrue(projects.contains(new ExampleProject(projectRoot,
                                                        "project1",
                                                        "This is custom description. This is a new line.",
                                                        Arrays.asList("tag1",
                                                                      "tag2"))));
    }

    @Test
    public void testGetProjects_PomDescription() {
        final Path projectRoot = mock(Path.class);
        final POM pom = mock(POM.class);
        final KieProject project = mock(KieProject.class);
        when(pom.getDescription()).thenReturn("pom description");
        when(project.getRootPath()).thenReturn(projectRoot);
        when(project.getProjectName()).thenReturn("project1");
        when(project.getPom()).thenReturn(pom);
        when(projectRoot.toURI()).thenReturn("default:///project1");
        when(metadataService.getTags(any(Path.class))).thenReturn(Arrays.asList("tag1",
                                                                                "tag2"));

        final GitRepository repository = new GitRepository("guvnorng-playground");
        when(repositoryFactory.newRepository(any(ConfigGroup.class))).thenReturn(repository);
        when(projectService.getProjects(eq(repository),
                                        any(String.class))).thenReturn(new HashSet<Project>() {{
            add(project);
        }});

        final Set<ExampleProject> projects = service.getProjects(new ExampleRepository("https://github.com/guvnorngtestuser1/guvnorng-playground.git"));
        assertNotNull(projects);
        assertEquals(1,
                     projects.size());
        assertTrue(projects.contains(new ExampleProject(projectRoot,
                                                        "project1",
                                                        "pom description",
                                                        Arrays.asList("tag1",
                                                                      "tag2"))));
    }

    @Test
    public void testValidateRepositoryName() {
        final String name = "name";
        service.validateRepositoryName(name);
        verify(repositoryService,
               times(1)).validateRepositoryName(eq(name));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetupExamples_NullOrganizationalUnit() {
        service.setupExamples(null,
                              mock(ExampleTargetRepository.class),
                              "master",
                              mock(List.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetupExamples_NullRepository() {
        service.setupExamples(mock(ExampleOrganizationalUnit.class),
                              null,
                              "master",
                              mock(List.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetupExamples_NullBranch() {
        service.setupExamples(mock(ExampleOrganizationalUnit.class),
                              mock(ExampleTargetRepository.class),
                              null,
                              mock(List.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetupExamples_NullProject() {
        service.setupExamples(mock(ExampleOrganizationalUnit.class),
                              mock(ExampleTargetRepository.class),
                              "master",
                              null);
    }

    @Test(expected = IllegalStateException.class)
    public void testSetupExamples_ZeroProjects() {
        service.setupExamples(mock(ExampleOrganizationalUnit.class),
                              mock(ExampleTargetRepository.class),
                              "master",
                              Collections.<ExampleProject>emptyList());
    }

    @Test
    public void testSetupExamples_NewOrganizationalUnitNewRepository() {
        final ExampleOrganizationalUnit exOU = mock(ExampleOrganizationalUnit.class);
        final ExampleTargetRepository exRepository = mock(ExampleTargetRepository.class);
        final ExampleProject exProject = mock(ExampleProject.class);
        final List<ExampleProject> exProjects = new ArrayList<ExampleProject>() {{
            add(exProject);
        }};
        final OrganizationalUnit ou = mock(OrganizationalUnit.class);
        final GitRepository repository = mock(GitRepository.class);
        final Path repositoryRoot = mock(Path.class);
        final Path projectRoot = mock(Path.class);

        when(exOU.getName()).thenReturn("ou");
        when(exRepository.getAlias()).thenReturn("repository");
        when(exProject.getName()).thenReturn("project");
        when(exProject.getRoot()).thenReturn(projectRoot);

        when(repository.getBranchRoot("master")).thenReturn(repositoryRoot);
        when(repositoryRoot.toURI()).thenReturn("default:///");
        when(projectRoot.toURI()).thenReturn("default:///project");

        when(ouService.getOrganizationalUnit(eq("ou"))).thenReturn(null);
        when(ouService.createOrganizationalUnit(eq("ou"),
                                                eq(""),
                                                eq(""))).thenReturn(ou);
        when(repositoryService.getRepository(eq("repository"))).thenReturn(null);
        when(repositoryService.createRepository(eq(ou),
                                                eq(GitRepository.SCHEME),
                                                eq("repository"),
                                                any(RepositoryEnvironmentConfigurations.class))).thenReturn(repository);

        final ProjectContextChangeEvent event = service.setupExamples(exOU,
                                                                      exRepository,
                                                                      "master",
                                                                      exProjects);

        assertEquals(ou,
                     event.getOrganizationalUnit());
        assertEquals(repository,
                     event.getRepository());
        assertEquals(repository.getDefaultBranch(),
                     event.getBranch());
        assertEquals(exProject.getRoot().toURI(),
                     event.getProject().getRootPath().toURI());

        verify(ouService,
               times(1)).createOrganizationalUnit(eq("ou"),
                                                  eq(""),
                                                  eq(""));
        verify(repositoryService,
               times(1)).createRepository(eq(ou),
                                          eq(GitRepository.SCHEME),
                                          eq("repository"),
                                          any(RepositoryEnvironmentConfigurations.class));
        verify(newProjectEvent,
               times(1)).fire(any(NewProjectEvent.class));
    }

    @Test
    public void testSetupExamples_ExistingOrganizationalUnitExistingRepository() {
        final ExampleOrganizationalUnit exOU = mock(ExampleOrganizationalUnit.class);
        final ExampleTargetRepository exRepository = mock(ExampleTargetRepository.class);
        final ExampleProject exProject = mock(ExampleProject.class);
        final List<ExampleProject> exProjects = new ArrayList<ExampleProject>() {{
            add(exProject);
        }};
        final OrganizationalUnit ou = mock(OrganizationalUnit.class);
        final GitRepository repository = mock(GitRepository.class);
        final Path repositoryRoot = mock(Path.class);
        final Path projectRoot = mock(Path.class);

        when(exOU.getName()).thenReturn("ou");
        when(exRepository.getAlias()).thenReturn("repository");
        when(exProject.getName()).thenReturn("project");
        when(exProject.getRoot()).thenReturn(projectRoot);

        when(repository.getBranchRoot("master")).thenReturn(repositoryRoot);
        when(repositoryRoot.toURI()).thenReturn("default:///");
        when(projectRoot.toURI()).thenReturn("default:///project");

        when(ouService.getOrganizationalUnit(eq("ou"))).thenReturn(ou);
        when(repositoryService.getRepository(eq("repository"))).thenReturn(repository);

        final ProjectContextChangeEvent event = service.setupExamples(exOU,
                                                                      exRepository,
                                                                      "master",
                                                                      exProjects);

        assertEquals(ou,
                     event.getOrganizationalUnit());
        assertEquals(repository,
                     event.getRepository());
        assertEquals(repository.getDefaultBranch(),
                     event.getBranch());
        assertEquals(exProject.getRoot().toURI(),
                     event.getProject().getRootPath().toURI());

        verify(ouService,
               never()).createOrganizationalUnit(eq("ou"),
                                                 eq(""),
                                                 eq(""));
        verify(repositoryService,
               never()).createRepository(any(OrganizationalUnit.class),
                                         any(String.class),
                                         any(String.class),
                                         any(RepositoryEnvironmentConfigurations.class));
        verify(newProjectEvent,
               times(1)).fire(any(NewProjectEvent.class));
    }

    @Test
    public void testSetupExamples_ProjectCopy() {
        final ExampleOrganizationalUnit exOU = mock(ExampleOrganizationalUnit.class);
        final ExampleTargetRepository exRepository = mock(ExampleTargetRepository.class);
        final ExampleProject exProject1 = mock(ExampleProject.class);
        final ExampleProject exProject2 = mock(ExampleProject.class);
        final List<ExampleProject> exProjects = new ArrayList<ExampleProject>() {{
            add(exProject1);
            add(exProject2);
        }};
        final OrganizationalUnit ou = mock(OrganizationalUnit.class);
        final GitRepository repository = mock(GitRepository.class);
        final Path repositoryRoot = mock(Path.class);
        final Path project1Root = mock(Path.class);
        final Path project2Root = mock(Path.class);

        when(exOU.getName()).thenReturn("ou");
        when(exRepository.getAlias()).thenReturn("repository");
        when(exProject1.getName()).thenReturn("project1");
        when(exProject1.getRoot()).thenReturn(project1Root);
        when(exProject2.getName()).thenReturn("project2");
        when(exProject2.getRoot()).thenReturn(project2Root);

        when(repository.getBranchRoot("dev_branch")).thenReturn(repositoryRoot);
        when(repository.getDefaultBranch()).thenReturn("master");
        when(repositoryRoot.toURI()).thenReturn("default:///");
        when(project1Root.toURI()).thenReturn("default:///project1");
        when(project2Root.toURI()).thenReturn("default:///project2");

        when(ouService.getOrganizationalUnit(eq("ou"))).thenReturn(ou);
        when(repositoryService.getRepository(eq("repository"))).thenReturn(repository);

        final ProjectContextChangeEvent event = service.setupExamples(exOU,
                                                                      exRepository,
                                                                      "dev_branch",
                                                                      exProjects);

        assertEquals(ou,
                     event.getOrganizationalUnit());
        assertEquals(repository,
                     event.getRepository());
        assertEquals(repository.getDefaultBranch(),
                     event.getBranch());
        assertEquals(exProject1.getRoot().toURI(),
                     event.getProject().getRootPath().toURI());

        verify(ouService,
               never()).createOrganizationalUnit(eq("ou"),
                                                 eq(""),
                                                 eq(""));
        verify(repositoryService,
               never()).createRepository(any(OrganizationalUnit.class),
                                         any(String.class),
                                         any(String.class),
                                         any(RepositoryEnvironmentConfigurations.class));
        verify(ioService,
               times(1)).startBatch(any(FileSystem.class));
        verify(ioService,
               times(1)).endBatch();
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
    public void resolveGitRepositoryNotClonedBefore() {
        ExampleRepository playgroundRepository = new ExampleRepository("file:///home/user/folder/.kie-wb-playground");
        service.setPlaygroundRepository(playgroundRepository);

        ConfigGroup configGroup = mock(ConfigGroup.class);
        when(configurationFactory.newConfigGroup(any(ConfigType.class),
                                                 anyString(),
                                                 anyString())).thenReturn(configGroup);

        Repository repository = mock(Repository.class);
        when(repositoryFactory.newRepository(configGroup)).thenReturn(repository);

        Repository result = service.resolveGitRepository(playgroundRepository);

        assertEquals(repository,
                     result);

        verify(repositoryFactory,
               times(1)).newRepository(configGroup);
    }

    @Test
    public void resolveGitRepositoryClonedBefore() {
        ExampleRepository playgroundRepository = new ExampleRepository("file:///home/user/folder/.kie-wb-playground");
        service.setPlaygroundRepository(playgroundRepository);

        GitRepository repository = mock(GitRepository.class);
        Map<String, Object> repositoryEnvironment = new HashMap<>();
        repositoryEnvironment.put("origin",
                                  playgroundRepository.getUrl());
        when(repository.getEnvironment()).thenReturn(repositoryEnvironment);

        service.getClonedRepositories().add(repository);

        Repository result = service.resolveGitRepository(playgroundRepository);

        assertEquals(repository,
                     result);

        verify(repositoryFactory,
               never()).newRepository(any(ConfigGroup.class));
    }
}
