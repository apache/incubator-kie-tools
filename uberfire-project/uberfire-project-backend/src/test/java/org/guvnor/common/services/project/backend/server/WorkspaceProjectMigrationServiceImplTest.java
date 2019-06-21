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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.guvnor.common.services.project.backend.server.utils.PathUtil;
import org.guvnor.common.services.project.events.NewProjectEvent;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.service.ModuleService;
import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.organizationalunit.config.SpaceConfigStorage;
import org.guvnor.structure.organizationalunit.config.SpaceConfigStorageRegistry;
import org.guvnor.structure.organizationalunit.config.SpaceInfo;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
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
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class WorkspaceProjectMigrationServiceImplTest {

    private static final String NIOGIT_PATH = "/home/someone/somehwere/.niogit";

    private static final String SPACE = "testspace";

    private static final String REPO = "testrepo";

    private WorkspaceProjectMigrationServiceImpl service;

    @Mock
    private ModuleService<?> moduleService;

    @Mock
    private RepositoryService repositoryService;

    @Mock
    private EventSourceMock<NewProjectEvent> newProjectEvent;

    @Mock
    private IOService ioService;

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
    private PathUtil pathUtil;

    @Mock
    private WorkspaceProjectService workspaceProjectService;

    @Mock
    private Space space;

    @Mock
    private SpaceConfigStorage spaceConfigStorage;

    @Mock
    private SpaceConfigStorageRegistry spaceConfigStorageRegistry;

    @Captor
    private ArgumentCaptor<NewProjectEvent> newProjectEventArgumentCaptor;

    @Captor
    private ArgumentCaptor<RepositoryEnvironmentConfigurations> configsCaptor;

    private Branch legacyMasterBranch;
    private Branch legacyDevBranch;

    @Before
    public void setUp() throws Exception {

        SpaceInfo spaceInfo = mock(SpaceInfo.class);
        doAnswer(invocation -> null).when(spaceInfo).removeRepository(any());
        doAnswer(invocation -> null).when(spaceInfo).getRepositories(any());
        doReturn(spaceInfo).when(spaceConfigStorage).loadSpaceInfo();
        when(spaceConfigStorageRegistry.get(anyString())).thenReturn(spaceConfigStorage);

        doReturn(mock(WorkspaceProject.class)).when(workspaceProjectService).resolveProject(any(Repository.class));

        doAnswer((Answer<org.uberfire.java.nio.file.Path>) invocationOnMock ->
                Paths.convert(PathFactory.newPath("file",
                                                  invocationOnMock.getArguments()[0].toString()))).when(ioService).get(any(URI.class));

        when(pathUtil.normalizePath(any())).then(inv -> inv.getArgumentAt(0,
                                                                          Path.class));
        when(pathUtil.convert(any(Path.class))).then(inv -> {
            final Path path = inv.getArgumentAt(0,
                                                Path.class);

            final org.uberfire.java.nio.file.Path retVal = mock(org.uberfire.java.nio.file.Path.class);
            when(retVal.toUri()).then(inv1 -> URI.create(path.toURI()));

            return retVal;
        });
        when(pathUtil.getNiogitRepoPath(any())).thenReturn(NIOGIT_PATH);

        service = spy(new WorkspaceProjectMigrationServiceImpl(workspaceProjectService,
                                                               repositoryService,
                                                               organizationalUnitService,
                                                               pathUtil,
                                                               newProjectEvent,
                                                               moduleService,
                                                               spaceConfigStorageRegistry));

        doAnswer(invocation -> null).when(service).cleanupOrigin(any(Repository.class));

        legacyMasterBranch = mockBranch("legacyMasterBranch");
        legacyDevBranch = mockBranch("legacyDevBranch");

        when(legacyDevBranchProject1RootPath.toURI()).thenReturn(uri("legacyDevBranch",
                                                                     "legacyProject1"));
        when(legacyDevBranchProject2RootPath.toURI()).thenReturn(uri("legacyDevBranch",
                                                                     "legacyProject2"));
        when(legacyMasterBranchProject1RootPath.toURI()).thenReturn(uri("legacyMasterBranch",
                                                                        "legacyProject1"));

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

    private String uri(final String branch,
                       final String repo) {
        return "git://" + branch + "@" + SPACE + "/" + REPO + "/" + repo;
    }

    @Test
    public void createOnlyTwoRepositories() throws Exception {

        verify(repositoryService,
               times(2)).createRepository(any(OrganizationalUnit.class),
                                          eq(GitRepository.SCHEME.toString()),
                                          anyString(),
                                          configsCaptor.capture());
        final List<RepositoryEnvironmentConfigurations> allValues = configsCaptor.getAllValues();
        final Set<String> observedSubdirectories = new HashSet<>();
        allValues.forEach(configs -> {
            assertEquals(NIOGIT_PATH,
                         assertInstanceOf(configs.getOrigin(),
                                          String.class));
            assertFalse(assertInstanceOf(configs.getInit(),
                                         Boolean.class));
            assertFalse(assertInstanceOf(configs.getMirror(),
                                         Boolean.class));

            final String subdirectory = assertInstanceOf(configs.getSubdirectory(),
                                                         String.class);
            observedSubdirectories.add(subdirectory);

            @SuppressWarnings("unchecked")
            final List<String> branches = assertInstanceOf(configs.getBranches(),
                                                           List.class);
            final List<String> expectedBranches;
            if (subdirectory.equals("legacyProject1")) {
                expectedBranches = Arrays.asList("legacyMasterBranch",
                                                 "legacyDevBranch");
            } else if (subdirectory.equals("legacyProject2")) {
                expectedBranches = Arrays.asList("legacyDevBranch");
            } else {
                throw new AssertionError("Unrecognized subdirectory: " + subdirectory);
            }

            verify(service,
                   times(2)).cleanupOrigin(any());

            assertEquals("Unexpected branches for subdirectory " + subdirectory,
                         new HashSet<>(expectedBranches),
                         new HashSet<>(branches));
        });

        assertEquals(new HashSet<>(Arrays.asList("legacyProject1",
                                                 "legacyProject2")),
                     observedSubdirectories);
    }

    @Test
    public void fireNewProjectEvents() throws Exception {
        verify(newProjectEvent,
               times(2)).fire(newProjectEventArgumentCaptor.capture());

        final List<NewProjectEvent> allValues = newProjectEventArgumentCaptor.getAllValues();

        assertNotNull(allValues.get(0).getWorkspaceProject());
        assertNotNull(allValues.get(1).getWorkspaceProject());
    }

    private <T> T assertInstanceOf(Object value,
                                   Class<T> clazz) {
        assertTrue(clazz.isInstance(value));

        return clazz.cast(value);
    }

    private void setUpDevBranch() {
        final HashSet<Module> devBranchModules = new HashSet<>();

        final Module mockModule = mockModule("legacyProject1",
                                             legacyDevBranchProject1RootPath);
        devBranchModules.add(mockModule);
        final Module mockModule2 = mockModule("legacyProject2",
                                              legacyDevBranchProject2RootPath);
        devBranchModules.add(mockModule2);

        doReturn(devBranchModules).when(moduleService).getAllModules(legacyDevBranch);
    }

    private void setUpMasterBranch() {
        final HashSet<Module> masterBranchModules = new HashSet<>();

        final Module mockModule = mockModule("legacyProject1",
                                             legacyMasterBranchProject1RootPath);
        masterBranchModules.add(mockModule);

        doReturn(masterBranchModules).when(moduleService).getAllModules(legacyMasterBranch);
        final Path masterRoot = mock(Path.class);
        when(legacyMasterBranch.getPath()).thenReturn(masterRoot);
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

        when(legacyRepository.getDefaultBranch()).thenReturn(Optional.of(legacyMasterBranch));

        doReturn(space).when(legacyRepository).getSpace();

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
        when(module.getModuleName()).thenReturn(myOldProject);
        when(module.getRootPath()).thenReturn(myOldProjectRootPath);
        return module;
    }
}