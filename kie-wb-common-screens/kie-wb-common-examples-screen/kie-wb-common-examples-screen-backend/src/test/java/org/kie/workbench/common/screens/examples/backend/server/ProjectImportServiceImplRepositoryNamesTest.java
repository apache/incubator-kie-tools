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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import javax.enterprise.event.Event;

import org.guvnor.common.services.project.backend.server.utils.PathUtil;
import org.guvnor.common.services.project.events.NewProjectEvent;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.organizationalunit.impl.OrganizationalUnitImpl;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.RepositoryService;
import org.guvnor.structure.repositories.impl.git.GitRepository;
import org.guvnor.structure.server.repositories.RepositoryFactory;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.examples.model.ImportProject;
import org.kie.workbench.common.screens.examples.validation.ImportProjectValidators;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.kie.workbench.common.screens.projecteditor.service.ProjectScreenService;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.io.IOService;
import org.uberfire.rpc.SessionInfo;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProjectImportServiceImplRepositoryNamesTest {

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
    private SessionInfo sessionInfo;

    @Mock
    private User user;

    @Mock
    private WorkspaceProjectService projectService;

    @Mock
    private ProjectScreenService projectScreenService;

    private ProjectImportServiceImpl service;

    @Mock
    private OrganizationalUnit organizationalUnit;

    private List<ImportProject> importProjects;

    @Captor
    private ArgumentCaptor<ProjectScreenModel> modelCapture;

    @Mock
    private ImportProjectValidators validators;

    private ImportProject exProject1;

    @Mock
    private PathUtil pathUtil;

    @Mock
    private Event<NewProjectEvent> newProjectEvent;

    @Mock
    private RepositoryService repoService;

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
                                                   repoService));

        when(validators.getValidators()).thenReturn(new ArrayList<>());

        when(ouService.getOrganizationalUnits()).thenReturn(new HashSet<OrganizationalUnit>() {{
            add(new OrganizationalUnitImpl("ou1Name",
                                           "ou1GroupId"));
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

        exProject1 = mock(ImportProject.class);
        importProjects = Collections.singletonList(exProject1);
        final OrganizationalUnit ou = mock(OrganizationalUnit.class);
        doReturn("ou").when(ou).getName();

        final GitRepository repository1 = mock(GitRepository.class);
        final Path repositoryRoot = mock(Path.class);
        final Path module1Root = mock(Path.class);

        when(organizationalUnit.getName()).thenReturn("ou");
        when(exProject1.getName()).thenReturn("module1");
        when(exProject1.getRoot()).thenReturn(module1Root);

        when(repository1.getBranch("dev_branch")).thenReturn(Optional.of(new Branch("dev_branch",
                                                                                    repositoryRoot)));
        final Optional<Branch> master = Optional.of(new Branch("master",
                                                               PathFactory.newPath("testFile",
                                                                                   "file:///")));
        when(repository1.getDefaultBranch()).thenReturn(master);

        when(repositoryRoot.toURI()).thenReturn("default:///");
        when(module1Root.toURI()).thenReturn("default:///module1");

        when(ouService.getOrganizationalUnit(eq("ou"))).thenReturn(ou);

        doReturn("module1").when(repository1).getAlias();

        final WorkspaceProject project = spy(new WorkspaceProject());
        doReturn(repository1.getAlias()).when(project).getName();
        doReturn(mock(Module.class)).when(project).getMainModule();
        doReturn(mock(OrganizationalUnit.class)).when(project).getOrganizationalUnit();
        doReturn(project).when(projectService).resolveProject(repository1);
        doReturn(project).when(projectService).resolveProject(any(Path.class));

        final ProjectScreenModel model = new ProjectScreenModel();
        model.setPOM(new POM());
        doReturn(model).when(projectScreenService).load(any());
    }

    @Test
    public void nameIsNotTaken() {
        String module1 = "module1";
        WorkspaceProject project1 = mock(WorkspaceProject.class,
                                         Answers.RETURNS_DEEP_STUBS.get());
        doReturn(module1).when(project1).getName();
        when(project1.getMainModule().getPomXMLPath()).thenReturn(mock(Path.class));

        doReturn(project1).when(service).importProject(eq(organizationalUnit),
                                                       any());

        service.importProjects(organizationalUnit,
                               importProjects);

        verify(projectService,
               never()).createFreshProjectName(any(),
                                               anyString());
        verify(projectScreenService,
               never()).save(any(),
                             any(),
                             any());
    }

    @Test
    public void nameIsTaken() {
        String module1 = "module1";
        String module1_1 = "module1 [1]";

        WorkspaceProject project1 = mock(WorkspaceProject.class,
                                         Answers.RETURNS_DEEP_STUBS.get());
        doReturn(module1).when(project1).getName();
        when(project1.getMainModule().getPomXMLPath()).thenReturn(mock(Path.class));
        List<WorkspaceProject> projects = new ArrayList<>();
        projects.add(project1);
        projects.add(project1);

        doReturn(project1).when(service).importProject(eq(organizationalUnit),
                                                       any());

        doReturn(projects).when(projectService).getAllWorkspaceProjectsByName(any(),
                                                                              eq(module1));

        doReturn(module1_1).when(projectService).createFreshProjectName(any(),
                                                                        eq(module1));

        service.importProjects(organizationalUnit,
                               importProjects);

        verify(projectScreenService).save(any(),
                                          modelCapture.capture(),
                                          any());
        final ProjectScreenModel model = modelCapture.getValue();
        assertEquals(module1_1,
                     model.getPOM().getName());
    }

    @Test
    public void evenTheSecundaryNameIsTaken() {
        String module = "module1";
        String module_1 = "module1 [1]";
        String module_2 = "module1 [2]";

        doReturn(module_2).when(projectService).createFreshProjectName(any(),
                                                                       eq(module));

        WorkspaceProject project1 = mock(WorkspaceProject.class,
                                         Answers.RETURNS_DEEP_STUBS.get());
        doReturn(module).when(project1).getName();
        when(project1.getMainModule().getPomXMLPath()).thenReturn(mock(Path.class));
        List<WorkspaceProject> projects1 = new ArrayList<>();
        projects1.add(project1);
        projects1.add(project1);

        doReturn(project1).when(service).importProject(eq(organizationalUnit),
                                                       eq(exProject1));

        doReturn(projects1).when(projectService).getAllWorkspaceProjectsByName(any(),
                                                                               eq(module));

        WorkspaceProject project2 = mock(WorkspaceProject.class,
                                         Answers.RETURNS_DEEP_STUBS.get());
        doReturn(module_1).when(project2).getName();
        when(project2.getMainModule().getPomXMLPath()).thenReturn(mock(Path.class));
        List<WorkspaceProject> projects2 = new ArrayList<>();
        projects2.add(project2);
        doReturn(projects2).when(projectService).getAllWorkspaceProjectsByName(any(),
                                                                               eq(module_1));

        service.importProjects(organizationalUnit,
                               importProjects);

        verify(projectScreenService).save(any(),
                                          modelCapture.capture(),
                                          any());
        final ProjectScreenModel model = modelCapture.getValue();
        assertEquals(module_2,
                     model.getPOM().getName());
    }
}
