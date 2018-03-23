/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package org.guvnor.common.services.project.backend.server;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.guvnor.common.services.project.events.NewProjectEvent;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.service.ModuleService;
import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryCopier;
import org.guvnor.structure.repositories.RepositoryEnvironmentConfigurations;
import org.guvnor.structure.repositories.RepositoryService;
import org.guvnor.structure.repositories.impl.git.GitRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.io.IOService;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.spaces.Space;
import org.uberfire.spaces.SpacesAPI;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class WorkspaceProjectMigrationServiceImplTest {

    private WorkspaceProjectMigrationServiceImpl service;

    @Mock
    private ModuleService moduleService;

    @Mock
    private RepositoryService repositoryService;

    @Mock
    private EventSourceMock<NewProjectEvent> newProjectEvent;

    @Mock
    private IOService ioService;

    @Mock
    private RepositoryCopier repositoryCopier;

    @Mock
    private Path legacyMasterBranchProject1RootPath;

    @Mock
    private Path legacyDevBranchProject1RootPath;

    @Mock
    private Path legacyDevBranchProject2RootPath;

    @Mock
    private OrganizationalUnitService organizationalUnitService;

    @Mock
    private OrganizationalUnit organizationalUnit;

    @Mock
    private WorkspaceProjectService workspaceProjectService;

    @Mock
    private Space space;

    @Captor
    private ArgumentCaptor<Path> pathArgumentCaptor;

    @Captor
    private ArgumentCaptor<NewProjectEvent> newProjectEventArgumentCaptor;

    private Branch legacyMasterBranch;
    private Branch legacyDevBranch;

    @Before
    public void setUp() throws Exception {

        doReturn(mock(WorkspaceProject.class)).when(workspaceProjectService).resolveProject(any(Repository.class));

        doAnswer(new Answer<org.uberfire.java.nio.file.Path>() {
            @Override
            public org.uberfire.java.nio.file.Path answer(InvocationOnMock invocationOnMock) throws Throwable {
                return Paths.convert(PathFactory.newPath("file",
                                                         invocationOnMock.getArguments()[0].toString()));
            }
        }).when(ioService).get(any(URI.class));

        service = new WorkspaceProjectMigrationServiceImpl(workspaceProjectService,
                                                           repositoryService,
                                                           organizationalUnitService,
                                                           newProjectEvent,
                                                           repositoryCopier,
                                                           moduleService,
                                                           ioService);

        when(repositoryCopier.makeSafeRepositoryName(anyString())).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocationOnMock) throws Throwable {
                return (String) invocationOnMock.getArguments()[0];
            }
        });

        legacyMasterBranch = mockBranch("legacyMasterBranch");
        legacyDevBranch = mockBranch("legacyDevBranch");

        final Repository legacyRepository = mockLegacyRepository();

        doReturn(new Space("space")).when(organizationalUnit).getSpace();
        final WorkspaceProject legacyWorkspaceProject = new WorkspaceProject(organizationalUnit,
                                                                             legacyRepository,
                                                                             legacyMasterBranch,
                                                                             null);

        setUpMasterBranch();
        setUpDevBranch();

        mockRepository(organizationalUnit);

        service.migrate(legacyWorkspaceProject);
    }

    @Test
    public void createOnlyTwoRepositories() throws Exception {

        verify(repositoryService,
               times(2)).createRepository(any(OrganizationalUnit.class),
                                          anyString(),
                                          anyString(),
                                          any(RepositoryEnvironmentConfigurations.class));
    }

    @Test
    public void copy() throws Exception {

        verify(repositoryCopier).copy(eq(space),
                                      eq(legacyMasterBranchProject1RootPath),
                                      pathArgumentCaptor.capture());
        verify(repositoryCopier).copy(eq(space),
                                      eq(legacyDevBranchProject1RootPath),
                                      pathArgumentCaptor.capture());
        verify(repositoryCopier).copy(eq(space),
                                      eq(legacyDevBranchProject2RootPath),
                                      pathArgumentCaptor.capture());

        final List<Path> allValues = pathArgumentCaptor.getAllValues();

        assertEquals(3, allValues.size());
        assertNotNull(allValues.get(0));
        assertNotNull(allValues.get(1));
        assertNotNull(allValues.get(2));
    }

    @Test
    public void fireNewProjectEvents() throws Exception {
        verify(newProjectEvent,
               times(2)).fire(newProjectEventArgumentCaptor.capture());

        final List<NewProjectEvent> allValues = newProjectEventArgumentCaptor.getAllValues();

        assertNotNull(allValues.get(0).getWorkspaceProject());
        assertNotNull(allValues.get(1).getWorkspaceProject());
    }

    private void setUpDevBranch() {
        final HashSet<Module> devBranchModules = new HashSet<>();

        devBranchModules.add(mockModule("legacyProject1",
                                        legacyDevBranchProject1RootPath));
        devBranchModules.add(mockModule("legacyProject2",
                                        legacyDevBranchProject2RootPath));

        doReturn(devBranchModules).when(moduleService).getAllModules(legacyDevBranch);
    }

    private void setUpMasterBranch() {
        final HashSet<Module> masterBranchModules = new HashSet<>();

        masterBranchModules.add(mockModule("legacyProject1",
                                           legacyMasterBranchProject1RootPath));

        doReturn(masterBranchModules).when(moduleService).getAllModules(legacyMasterBranch);
    }

    private Branch mockBranch(final String branchName) {
        final Branch masterBranch = mock(Branch.class);
        doReturn(branchName).when(masterBranch).getName();
        return masterBranch;
    }

    private Repository mockLegacyRepository() {
        final Repository legacyRepository = mock(Repository.class);
        final ArrayList<Branch> branches = new ArrayList<>();
        branches.add(legacyMasterBranch);
        branches.add(legacyDevBranch);
        doReturn(branches).when(legacyRepository).getBranches();

        final ArrayList<OrganizationalUnit> ous = new ArrayList<>();
        ous.add(organizationalUnit);
        doReturn(ous).when(organizationalUnitService).getOrganizationalUnits(legacyRepository);

        return legacyRepository;
    }

    private void mockRepository(final OrganizationalUnit organizationalUnit) {
        doAnswer(new Answer<Repository>() {
            @Override
            public Repository answer(InvocationOnMock invocationOnMock) throws Throwable {
                final Repository newRepository = mock(Repository.class);
                doReturn(invocationOnMock.getArguments()[2]).when(newRepository).getAlias();
                doReturn(SpacesAPI.Scheme.FILE).when(newRepository).getScheme();
                doReturn(space).when(newRepository).getSpace();
                return newRepository;
            }
        }).when(repositoryService).createRepository(eq(organizationalUnit),
                                                    eq(GitRepository.SCHEME.toString()),
                                                    anyString(),
                                                    any(RepositoryEnvironmentConfigurations.class));
    }

    private Module mockModule(final String myOldProject,
                              final Path myOldProjectRootPath) {
        final Module module = mock(Module.class);
        doReturn(myOldProject).when(module).getModuleName();
        doReturn(myOldProjectRootPath).when(module).getRootPath();
        return module;
    }
}