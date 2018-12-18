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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.enterprise.inject.Instance;

import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.service.ModuleRepositoryResolver;
import org.guvnor.common.services.project.service.ModuleService;
import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.organizationalunit.impl.OrganizationalUnitImpl;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.server.spaces.SpacesAPIImpl;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.spaces.Space;
import org.uberfire.spaces.SpacesAPI;

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

        doReturn(moduleService).when(moduleServices).get();
        doReturn(allRepositories).when(repositoryService).getAllRepositoriesFromAllUserSpaces();

        workspaceProjectService = new WorkspaceProjectServiceImpl(organizationalUnitService,
                                                                  repositoryService,
                                                                  spaces,
                                                                  new EventSourceMock<>(),
                                                                  moduleServices,
                                                                  repositoryResolver);
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
                               repository2)).when(repositoryService).getAllRepositories(Mockito.eq(space1));
        doReturn(Arrays.asList(repository3)).when(repositoryService).getAllRepositories(Mockito.eq(space2));
        doReturn(Collections.singletonList(repository3)).when(repositoryService).getRepositories(Mockito.eq(space2));
    }

    @Test
    public void getAllProjects() throws Exception {

        final Collection<WorkspaceProject> allWorkspaceProjects = workspaceProjectService.getAllWorkspaceProjects();

        assertEquals(3,
                     allWorkspaceProjects.size());
    }

    @Test
    public void getAllProjectsForOU1() throws Exception {
        final Collection<WorkspaceProject> allWorkspaceProjects = workspaceProjectService.getAllWorkspaceProjects(ou1);

        assertContains(repository1,
                       allWorkspaceProjects);
        assertContains(repository2,
                       allWorkspaceProjects);

        assertEquals(2,
                     allWorkspaceProjects.size());
    }

    @Test
    public void getAllProjectsForOU2() throws Exception {
        final Collection<WorkspaceProject> allWorkspaceProjects = workspaceProjectService.getAllWorkspaceProjects(ou2);

        assertContains(repository3,
                       allWorkspaceProjects);

        assertEquals(1,
                     allWorkspaceProjects.size());
    }

    @Test
    public void getAllProjectsWithName() throws Exception {
        final Collection<WorkspaceProject> allWorkspaceProjects = workspaceProjectService.getAllWorkspaceProjectsByName(ou1,
                                                                                                                        "repository-with-same-alias");

        assertContains(repository2,
                       allWorkspaceProjects);

        assertEquals(1,
                     allWorkspaceProjects.size());
    }

    @Test
    public void spaceHasProjectsWithName() throws Exception {
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
    public void noProjects() throws Exception {
        final OrganizationalUnit organizationalUnit = mock(OrganizationalUnit.class);
        doReturn("myOU").when(organizationalUnit).getName();

        doReturn(organizationalUnit).when(organizationalUnitService).getOrganizationalUnit("myOU");

        assertTrue(workspaceProjectService.getAllWorkspaceProjects(organizationalUnit).isEmpty());
    }

    @Test
    public void testReturnSameNameIfRepositoryDoesNotExist() {
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
    public void testCreateNewNameIfRepositoryExists() {
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