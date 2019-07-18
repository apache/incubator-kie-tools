/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.screens.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.enterprise.event.Event;

import org.guvnor.common.services.project.backend.server.utils.PathUtil;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.guvnor.structure.backend.repositories.ConfiguredRepositories;
import org.guvnor.structure.contributors.Contributor;
import org.guvnor.structure.contributors.ContributorType;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.organizationalunit.config.SpaceConfigStorage;
import org.guvnor.structure.organizationalunit.config.SpaceConfigStorageRegistry;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.NewBranchEvent;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryService;
import org.guvnor.structure.repositories.RepositoryUpdatedEvent;
import org.guvnor.structure.security.RepositoryAction;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.examples.model.ExampleRepository;
import org.kie.workbench.common.screens.examples.model.ImportProject;
import org.kie.workbench.common.screens.examples.service.ExamplesService;
import org.kie.workbench.common.screens.explorer.backend.server.ExplorerServiceHelper;
import org.kie.workbench.common.screens.library.api.AssetInfo;
import org.kie.workbench.common.screens.library.api.AssetQueryResult;
import org.kie.workbench.common.screens.library.api.AssetQueryResult.ResultType;
import org.kie.workbench.common.screens.library.api.LibraryInfo;
import org.kie.workbench.common.screens.library.api.OrganizationalUnitRepositoryInfo;
import org.kie.workbench.common.screens.library.api.ProjectAssetsQuery;
import org.kie.workbench.common.screens.library.api.preferences.LibraryInternalPreferences;
import org.kie.workbench.common.screens.library.api.preferences.LibraryOrganizationalUnitPreferences;
import org.kie.workbench.common.screens.library.api.preferences.LibraryPreferences;
import org.kie.workbench.common.screens.library.api.preferences.LibraryProjectPreferences;
import org.kie.workbench.common.services.refactoring.backend.server.query.standard.FindAllLibraryAssetsQuery;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRequest;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRow;
import org.kie.workbench.common.services.refactoring.service.RefactoringQueryService;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.commons.cluster.ClusterService;
import org.uberfire.ext.security.management.api.AbstractEntityManager;
import org.uberfire.ext.security.management.api.service.UserManagerService;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.NoSuchFileException;
import org.uberfire.java.nio.file.spi.FileSystemProvider;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mocks.SessionInfoMock;
import org.uberfire.paging.PageResponse;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.spaces.Space;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LibraryServiceImplTest {

    @Mock
    private OrganizationalUnitService ouService;

    @Mock
    private AuthorizationManager authorizationManager;

    @Mock
    private SessionInfo sessionInfo;

    @Mock
    private WorkspaceProjectService projectService;

    @Mock
    private KieModuleService moduleService;

    @Mock
    private LibraryPreferences preferences;

    @Mock
    private LibraryInternalPreferences internalPreferences;

    @Mock
    private ExplorerServiceHelper explorerServiceHelper;

    @Mock
    private ExamplesService examplesService;

    @Mock
    private RefactoringQueryService refactoringQueryService;

    @Mock
    private IOService ioService;

    @Mock
    private UserManagerService userManagerService;

    @Mock
    private IndexStatusOracle indexOracle;

    @Mock
    private RepositoryService repositoryService;

    @Mock
    private PathUtil pathUtil;

    private final PathUtil realPathUtil = new PathUtil();

    @Mock
    private ConfiguredRepositories configuredRepositories;

    @Mock
    private EventSourceMock<RepositoryUpdatedEvent> repositoryUpdatedEvent;

    @Mock
    private SpaceConfigStorageRegistry spaceConfigStorageRegistry;

    @Mock
    private ClusterService clusterService;

    @Mock
    private Event<NewBranchEvent> newBranchEvent;

    @Mock
    private OrganizationalUnit ou1;

    @Mock
    private OrganizationalUnit ou2;

    @Mock
    private Repository repo1;

    @Mock
    private Repository repo2Default;

    @Captor
    private ArgumentCaptor<RefactoringPageRequest> pageRequestArgumentCaptor;

    @Captor
    private ArgumentCaptor<POM> pomArgumentCaptor;

    private LibraryServiceImpl libraryService;
    private List<OrganizationalUnit> ous;
    private Set<Module> modulesMock;

    @Before
    public void setup() {
        ous = Arrays.asList(ou1,
                            ou2);
        when(ouService.getOrganizationalUnits()).thenReturn(ous);
        when(ou1.getIdentifier()).thenReturn("ou1");
        when(ou2.getIdentifier()).thenReturn("ou2");
        when(repo1.getAlias()).thenReturn("repo_created_by_user");
        final List<Branch> repo1Branches = Arrays.asList(makeBranch("repo1-branch1",
                                                                    repo1.getAlias()),
                                                         makeBranch("repo1-branch2",
                                                                    repo1.getAlias()));
        when(repo1.getBranches()).thenReturn(repo1Branches);
        when(repo1.getBranch(anyString())).then(inv -> repo1Branches.stream().filter(b -> b.getName().equals(inv.getArgumentAt(0, String.class))).findFirst());
        when(repo2Default.getAlias()).thenReturn("ou2-repo-alias");
        final List<Branch> repo2Branches = Collections.singletonList(makeBranch("repo2-branch1",
                                                                                repo2Default.getAlias()));
        when(repo2Default.getBranches()).thenReturn(repo2Branches);
        when(ou2.getRepositories()).thenReturn(Arrays.asList(repo1,
                                                             repo2Default));

        when(indexOracle.isIndexed(any())).thenReturn(true);

        modulesMock = new HashSet<>();
        modulesMock.add(mock(Module.class));
        modulesMock.add(mock(Module.class));
        modulesMock.add(mock(Module.class));

        when(preferences.getOrganizationalUnitPreferences()).thenReturn(spy(new LibraryOrganizationalUnitPreferences()));
        when(preferences.getProjectPreferences()).thenReturn(spy(new LibraryProjectPreferences()));

        sessionInfo = new SessionInfoMock();

        libraryService = spy(new LibraryServiceImpl(ouService,
                                                    refactoringQueryService,
                                                    preferences,
                                                    authorizationManager,
                                                    sessionInfo,
                                                    explorerServiceHelper,
                                                    projectService,
                                                    moduleService,
                                                    examplesService,
                                                    ioService,
                                                    internalPreferences,
                                                    userManagerService,
                                                    indexOracle,
                                                    repositoryService,
                                                    pathUtil,
                                                    newBranchEvent,
                                                    configuredRepositories,
                                                    repositoryUpdatedEvent,
                                                    spaceConfigStorageRegistry,
                                                    clusterService
        ));
    }

    @Test
    public void queryingUnindexedProjectGivesUnindexedResult() throws Exception {
        Branch branch = new Branch("fake-branch",
                                   mockPath("default:///a/b/c"));
        final WorkspaceProject project = new WorkspaceProject(ou1,
                                                              repo1,
                                                              branch,
                                                              mock(Module.class));
        when(indexOracle.isIndexed(project)).thenReturn(false);
        when(ioService.exists(any())).thenReturn(true);

        final ProjectAssetsQuery query = new ProjectAssetsQuery(project,
                                                                "",
                                                                0,
                                                                10,
                                                                Collections.emptyList());

        AssetQueryResult result = libraryService.getProjectAssets(query);
        assertEquals(ResultType.Unindexed,
                     result.getResultType());
        assertFalse(result.getAssetInfos().isPresent());
    }

    @Test
    public void getDefaultOrganizationalUnitRepositoryInfoTest() {
        final OrganizationalUnitRepositoryInfo info = mock(OrganizationalUnitRepositoryInfo.class);
        doReturn(info).when(libraryService).getOrganizationalUnitRepositoryInfo(any(OrganizationalUnit.class));

        assertEquals(info,
                     libraryService.getDefaultOrganizationalUnitRepositoryInfo());
    }

    @Test
    public void getOrganizationalUnitRepositoryInfoForNullOrganizationalUnitTest() {
        assertNull(libraryService.getOrganizationalUnitRepositoryInfo(null));
    }

    @Test
    public void getLibraryInfoTest() {
        final Path path1 = mockPath("file://project1");
        final WorkspaceProject project1 = mock(WorkspaceProject.class);
        final Repository repository1 = mock(Repository.class);
        when(project1.getRootPath()).thenReturn(path1);
        when(project1.getRepository()).thenReturn(repository1);

        final Path path2 = mockPath("file://project2");
        final WorkspaceProject project2 = mock(WorkspaceProject.class);
        final Repository repository2 = mock(Repository.class);
        when(project2.getRootPath()).thenReturn(path2);
        when(project2.getRepository()).thenReturn(repository2);

        doReturn(true).when(authorizationManager).authorize(same(repository2),
                                                            eq(RepositoryAction.READ),
                                                            any());

        doReturn(true).when(ioService).exists(any());

        final Set<WorkspaceProject> projects = new HashSet<>();
        projects.add(project1);
        projects.add(project2);
        doReturn(projects).when(projectService).getAllWorkspaceProjects(ou1);

        final LibraryInfo libraryInfo = libraryService.getLibraryInfo(ou1);

        assertEquals(1,
                     libraryInfo.getProjects().size());
        assertSame(project2,
                   libraryInfo.getProjects().iterator().next());
    }

    @Test
    public void newModuleTest() {
        when(preferences.getOrganizationalUnitPreferences().getName()).thenReturn("ou2");
        when(preferences.getOrganizationalUnitPreferences().getAliasInSingular()).thenReturn("team");
        when(preferences.getProjectPreferences().getBranch()).thenReturn("master");
        when(preferences.getProjectPreferences().getVersion()).thenReturn("1.0");

        final OrganizationalUnit organizationalUnit = mock(OrganizationalUnit.class);
        when(organizationalUnit.getDefaultGroupId()).thenReturn("ouGroupID");

        libraryService.createProject("Module Name!",
                                     organizationalUnit,
                                     "description",
                                     DeploymentMode.VALIDATED);

        verify(projectService).newProject(eq(organizationalUnit),
                                          pomArgumentCaptor.capture(),
                                          any());

        final POM pom = pomArgumentCaptor.getValue();
        assertEquals("Module Name!",
                     pom.getName());
        assertEquals("ouGroupID",
                     pom.getGav().getGroupId());
        assertEquals("ModuleName",
                     pom.getGav().getArtifactId());
        assertEquals("description",
                     pom.getDescription());
    }

    @Test
    public void createProjectTest() {
        final List<Contributor> spaceContributors = new ArrayList<>();
        spaceContributors.add(new Contributor("user1", ContributorType.OWNER));
        spaceContributors.add(new Contributor("user2", ContributorType.ADMIN));
        spaceContributors.add(new Contributor("admin", ContributorType.CONTRIBUTOR));

        final OrganizationalUnit organizationalUnit = mock(OrganizationalUnit.class);
        doReturn("ou").when(organizationalUnit).getName();
        doReturn("org.ou").when(organizationalUnit).getDefaultGroupId();
        doReturn(spaceContributors).when(organizationalUnit).getContributors();
        doReturn(organizationalUnit).when(ouService).getOrganizationalUnit("ou");

        final POM pom = mock(POM.class);

        libraryService.createProject(organizationalUnit,
                                     pom,
                                     DeploymentMode.VALIDATED);

        verify(ouService, never()).updateOrganizationalUnit(anyString(), anyString(), anyList());

        final List<Contributor> projectContributors = new ArrayList<>();
        projectContributors.add(new Contributor("user1", ContributorType.OWNER));
        projectContributors.add(new Contributor("user2", ContributorType.ADMIN));
        projectContributors.add(new Contributor("admin", ContributorType.OWNER));
        verify(projectService).newProject(organizationalUnit,
                                          pom,
                                          DeploymentMode.VALIDATED,
                                          projectContributors);
    }

    @Test
    public void createProjectWithUserThatIsNotASpaceContributorTest() {
        final List<Contributor> spaceContributors = new ArrayList<>();
        spaceContributors.add(new Contributor("user1", ContributorType.OWNER));
        spaceContributors.add(new Contributor("user2", ContributorType.ADMIN));
        spaceContributors.add(new Contributor("user3", ContributorType.CONTRIBUTOR));

        final OrganizationalUnit organizationalUnit = mock(OrganizationalUnit.class);
        doReturn("ou").when(organizationalUnit).getName();
        doReturn("org.ou").when(organizationalUnit).getDefaultGroupId();
        doReturn(spaceContributors).when(organizationalUnit).getContributors();
        doReturn(organizationalUnit).when(ouService).getOrganizationalUnit("ou");

        final POM pom = mock(POM.class);

        libraryService.createProject(organizationalUnit,
                                     pom,
                                     DeploymentMode.VALIDATED);


        final List<Contributor> updatedSpaceContributors = new ArrayList<>();
        updatedSpaceContributors.add(new Contributor("user1", ContributorType.OWNER));
        updatedSpaceContributors.add(new Contributor("user2", ContributorType.ADMIN));
        updatedSpaceContributors.add(new Contributor("user3", ContributorType.CONTRIBUTOR));
        updatedSpaceContributors.add(new Contributor("admin", ContributorType.CONTRIBUTOR));
        verify(ouService).updateOrganizationalUnit(anyString(), anyString(), eq(updatedSpaceContributors));

        final List<Contributor> projectContributors = new ArrayList<>();
        projectContributors.add(new Contributor("user1", ContributorType.OWNER));
        projectContributors.add(new Contributor("user2", ContributorType.ADMIN));
        projectContributors.add(new Contributor("user3", ContributorType.CONTRIBUTOR));
        projectContributors.add(new Contributor("admin", ContributorType.OWNER));
        verify(projectService).newProject(organizationalUnit,
                                          pom,
                                          DeploymentMode.VALIDATED,
                                          projectContributors);
    }

    @Test
    public void thereIsNotAModuleInTheWorkbenchTest() {

        final Boolean thereIsAModuleInTheWorkbench = libraryService.thereIsAProjectInTheWorkbench();

        assertFalse(thereIsAModuleInTheWorkbench);

        verify(projectService,
               times(1)).getAllWorkspaceProjects();
    }

    @Test
    public void thereIsAModuleInTheWorkbenchTest() {
        Set<WorkspaceProject> projects = new HashSet<>();
        projects.add(new WorkspaceProject(ou1,
                                          repo1,
                                          new Branch("master",
                                                     mock(Path.class)),
                                          mock(Module.class)));
        doReturn(projects).when(projectService).getAllWorkspaceProjects();

        final Boolean thereIsAModuleInTheWorkbench = libraryService.thereIsAProjectInTheWorkbench();

        assertTrue(thereIsAModuleInTheWorkbench);

        verify(projectService,
               times(1)).getAllWorkspaceProjects();
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNullModuleAssetsTest() {
        libraryService.getProjectAssets(null);
    }

    @Test
    public void emptyFirstPage() throws Exception {
        final Branch branch = mock(Branch.class);
        final WorkspaceProject project = spy(new WorkspaceProject(ou1,
                                                                  repo1,
                                                                  branch,
                                                                  null));
        final Path path = mock(Path.class);

        when(branch.getPath()).thenReturn(path);
        when(path.toURI()).thenReturn("file://a/b/c");

        doReturn(true).when(ioService).exists(any());

        final ProjectAssetsQuery query = new ProjectAssetsQuery(project,
                                                                "",
                                                                0,
                                                                10,
                                                                Collections.emptyList());

        final PageResponse<RefactoringPageRow> pageRowPageResponse = new PageResponse<>();
        pageRowPageResponse.setPageRowList(new ArrayList<>());
        when(refactoringQueryService.query(any(RefactoringPageRequest.class))).thenReturn(pageRowPageResponse);

        libraryService.getProjectAssets(query);

        verify(refactoringQueryService).query(pageRequestArgumentCaptor.capture());

        final RefactoringPageRequest pageRequest = pageRequestArgumentCaptor.getValue();

        assertEquals(FindAllLibraryAssetsQuery.NAME,
                     pageRequest.getQueryName());
        assertEquals(1,
                     pageRequest.getQueryTerms().size());

        assertEquals("file://a/b/c",
                     pageRequest.getQueryTerms().iterator().next().getValue());

        assertEquals(0,
                     pageRequest.getStartRowIndex());
        assertEquals(10,
                     (int) pageRequest.getPageSize());
    }

    @Test
    public void queryWithAFilter() throws Exception {

        final Branch branch = mock(Branch.class);
        final Path path = mockPath("file://the_project");
        final WorkspaceProject project = spy(new WorkspaceProject(mock(OrganizationalUnit.class),
                                                                  repo1,
                                                                  branch,
                                                                  null));

        when(branch.getPath()).thenReturn(path);

        doReturn(true).when(ioService).exists(any());

        final ProjectAssetsQuery query = new ProjectAssetsQuery(project,
                                                                "helloo",
                                                                10,
                                                                20,
                                                                Arrays.asList("xml"));

        final PageResponse<RefactoringPageRow> pageRowPageResponse = new PageResponse<>();
        pageRowPageResponse.setPageRowList(new ArrayList<>());
        when(refactoringQueryService.query(any(RefactoringPageRequest.class))).thenReturn(pageRowPageResponse);
        libraryService.getProjectAssets(query);

        verify(refactoringQueryService).query(pageRequestArgumentCaptor.capture());

        final RefactoringPageRequest pageRequest = pageRequestArgumentCaptor.getValue();

        assertEquals(FindAllLibraryAssetsQuery.NAME,
                     pageRequest.getQueryName());
        assertEquals(3,
                     pageRequest.getQueryTerms().size());

        assertQueryTermsContains(pageRequest.getQueryTerms(),
                                 "file://the_project");
        assertQueryTermsContains(pageRequest.getQueryTerms(),
                                 "*helloo*");
        assertQueryTermsContains(pageRequest.getQueryTerms(),
                                 ".*(xml)");

        assertEquals(10,
                     pageRequest.getStartRowIndex());
        assertEquals(20,
                     (int) pageRequest.getPageSize());
    }

    @Test
    public void queryAnItemThatIsInLuceneIndexButAlreadyDeletedFromGitRepository() throws Exception {

        final Path path = mockPath("file://the_project");

        final Branch branch = mock(Branch.class);

        when(branch.getPath()).thenReturn(path);
        final WorkspaceProject project = spy(new WorkspaceProject(ou1,
                                                                  repo1,
                                                                  branch,
                                                                  null));

        doReturn(true).when(ioService).exists(any());

        final ProjectAssetsQuery query = new ProjectAssetsQuery(project,
                                                                "",
                                                                10,
                                                                20,
                                                                Collections.emptyList());

        final PageResponse<RefactoringPageRow> pageRowPageResponse = new PageResponse<>();
        final ArrayList<RefactoringPageRow> assetPageRowList = new ArrayList<>();
        final RefactoringPageRow pageRow = mock(RefactoringPageRow.class);
        final Path filePath = mockPath("file://the_project/delete.me");
        when(filePath.getFileName()).thenReturn("delete.me");
        when(pageRow.getValue()).thenReturn(filePath);
        assetPageRowList.add(pageRow);

        pageRowPageResponse.setPageRowList(assetPageRowList);
        when(refactoringQueryService.query(any(RefactoringPageRequest.class))).thenReturn(pageRowPageResponse);

        when(ioService.readAttributes(any())).thenThrow(new NoSuchFileException());

        final AssetQueryResult result = libraryService.getProjectAssets(query);

        assertEquals(ResultType.Normal,
                     result.getResultType());
        assertTrue(result.getAssetInfos().isPresent());
        List<AssetInfo> projectAssets = result.getAssetInfos().get();
        assertTrue(projectAssets.isEmpty());
    }

    private Path mockPath(final String uri) {
        final Path path = mock(Path.class);
        when(path.toURI()).thenReturn(uri);
        return path;
    }

    private void assertQueryTermsContains(final Set<ValueIndexTerm> terms,
                                          final String value) {
        assertTrue(terms.stream().filter((t) -> t.getValue().equals(value)).findFirst().isPresent());
    }

    @Test
    public void assertLoadPreferences() {
        libraryService.getPreferences();

        verify(preferences).load();
    }

    @Test
    public void hasProjectsTest() {
        final Path path = mockPath("file://the_project");
        final WorkspaceProject project = mock(WorkspaceProject.class);
        when(project.getRootPath()).thenReturn(path);
        doReturn(true).when(ioService).exists(any());

        final Set<WorkspaceProject> projects = new HashSet<>();
        projects.add(project);
        doReturn(projects).when(projectService).getAllWorkspaceProjects(ou1);

        assertTrue(libraryService.hasProjects(ou1));
    }

    @Test
    public void hasAssetsTest() {
        doReturn(true).when(ioService).exists(any());

        final Package package1 = mock(Package.class);
        final Module project1 = mock(Module.class);
        doReturn(package1).when(moduleService).resolveDefaultPackage(project1);
        doReturn(true).when(explorerServiceHelper).hasAssets(package1);

        final Package package2 = mock(Package.class);
        final Module project2 = mock(Module.class);
        doReturn(package2).when(moduleService).resolveDefaultPackage(project2);
        doReturn(false).when(explorerServiceHelper).hasAssets(package2);

        assertTrue(libraryService.hasAssets(new WorkspaceProject(mock(OrganizationalUnit.class),
                                                                 mock(Repository.class),
                                                                 mock(Branch.class),
                                                                 project1)));
        assertFalse(libraryService.hasAssets(new WorkspaceProject(mock(OrganizationalUnit.class),
                                                                  mock(Repository.class),
                                                                  mock(Branch.class),
                                                                  project2)));
    }

    @Test
    public void unexistentProjectDosNotHaveAssetsTest() {
        final Path path = mockPath("file://the_project");
        final Package package1 = mock(Package.class);
        final WorkspaceProject project1 = mock(WorkspaceProject.class);

        when(project1.getRootPath()).thenReturn(path);
        final Module module = mock(Module.class);
        when(project1.getMainModule()).thenReturn(module);
        doReturn(false).when(ioService).exists(any());
        doReturn(package1).when(moduleService).resolveDefaultPackage(module);
        doReturn(true).when(explorerServiceHelper).hasAssets(package1);

        assertFalse(libraryService.hasAssets(project1));
    }

    @Test
    public void getCustomExampleProjectsTest() {
        System.setProperty("org.kie.project.examples.repository.url",
                           "importProjectsUrl");

        final Set<ImportProject> importProjects = new HashSet<>();
        importProjects.add(mock(ImportProject.class));
        doReturn(importProjects).when(examplesService).getProjects(new ExampleRepository("importProjectsUrl"));

        final Set<ImportProject> loadedImportProjects = libraryService.getExampleProjects();

        assertEquals(importProjects,
                     loadedImportProjects);
    }

    @Test
    public void getDefaultExampleProjectsTest() {
        System.setProperty("org.kie.project.examples.repository.url",
                           "");

        final ExampleRepository playgroundRepository = new ExampleRepository("playgroundRepositoryUrl");
        doReturn(playgroundRepository).when(examplesService).getPlaygroundRepository();

        final Set<ImportProject> importProjects = new HashSet<>();
        importProjects.add(mock(ImportProject.class));
        doReturn(importProjects).when(examplesService).getProjects(playgroundRepository);

        final Set<ImportProject> loadedImportProjects = libraryService.getExampleProjects();

        assertEquals(importProjects,
                     loadedImportProjects);
    }

    @Test
    public void createPOM() {
        final OrganizationalUnit organizationalUnit = mock(OrganizationalUnit.class);
        when(organizationalUnit.getDefaultGroupId()).thenReturn("ouGroupID");

        when(preferences.getProjectPreferences().getVersion()).thenReturn("1.0");
        when(preferences.getProjectPreferences().getDescription()).thenReturn("desc");

        GAV gav = libraryService.createGAV("proj",
                                           organizationalUnit);
        POM proj = libraryService.createPOM("proj",
                                            "description",
                                            gav);

        assertEquals("proj",
                     proj.getName());
        assertEquals("description",
                     proj.getDescription());
        assertEquals(gav,
                     proj.getGav());
    }

    @Test
    public void createGAV() {
        final OrganizationalUnit organizationalUnit = mock(OrganizationalUnit.class);
        when(organizationalUnit.getDefaultGroupId()).thenReturn("ouGroupID");

        when(preferences.getProjectPreferences().getVersion()).thenReturn("1.0");

        GAV gav = libraryService.createGAV("proj",
                                           organizationalUnit);

        assertEquals(organizationalUnit.getDefaultGroupId(),
                     gav.getGroupId());
        assertEquals("proj",
                     gav.getArtifactId());
        assertEquals(preferences.getProjectPreferences().getVersion(),
                     gav.getVersion());
    }

    @Test
    public void testGetNumberOfAssets() throws Exception {
        final Path path = mock(Path.class);
        final WorkspaceProject project = mock(WorkspaceProject.class);
        final ProjectAssetsQuery query = mock(ProjectAssetsQuery.class);

        doReturn(project).when(query).getProject();
        doReturn(path).when(project).getRootPath();
        doReturn("some.unique.uri").when(path).toURI();

        libraryService.getNumberOfAssets(query);

        verify(refactoringQueryService).queryHitCount(any(RefactoringPageRequest.class));
    }

    @Test
    public void addBranchTest() throws URISyntaxException {
        final WorkspaceProject project = mock(WorkspaceProject.class);
        doReturn(repo1).when(project).getRepository();
        doReturn(new Space("my-space")).when(project).getSpace();

        doReturn(mock(SpaceConfigStorage.class)).when(spaceConfigStorageRegistry).get("my-space");

        final org.uberfire.java.nio.file.Path baseBranchPath = mock(org.uberfire.java.nio.file.Path.class);
        final Path path = repo1.getBranches().stream().filter(b -> b.getName().equals("repo1-branch1")).findFirst().get().getPath();
        final FileSystem fileSystem = mock(FileSystem.class);
        final FileSystemProvider fileSystemProvider = mock(FileSystemProvider.class);
        doReturn(fileSystemProvider).when(fileSystem).provider();
        doReturn(fileSystem).when(baseBranchPath).getFileSystem();
        doReturn(baseBranchPath).when(pathUtil).convert(path);

        doReturn(repo1).when(repositoryService).getRepository(any());

        Branch newBranch = makeBranch("new-branch", "repo1");
        Branch branch1Branch = makeBranch("repo1-branch1", "repo1");

        when(repo1.getBranch(any(Path.class))).thenReturn(Optional.of(newBranch)).thenReturn(Optional.of(branch1Branch));

        final org.uberfire.java.nio.file.Path newBranchPath = mock(org.uberfire.java.nio.file.Path.class);
        doReturn(newBranchPath).when(ioService).get(new URI("default://new-branch@repo1/"));

        doReturn("default://new-branch@repo1/").when(pathUtil).replaceBranch(anyString(), anyString());

        final ArgumentCaptor<NewBranchEvent> newBranchEventArgumentCaptor = ArgumentCaptor.forClass(NewBranchEvent.class);

        libraryService.addBranch("new-branch", "repo1-branch1", project);

        verify(fileSystemProvider).copy(baseBranchPath, newBranchPath);
        verify(repositoryUpdatedEvent).fire(any());
        verify(newBranchEvent).fire(newBranchEventArgumentCaptor.capture());

        final NewBranchEvent newBranchEvent = newBranchEventArgumentCaptor.getValue();
        assertEquals("new-branch", newBranchEvent.getNewBranchName());
        assertEquals("repo1-branch1", newBranchEvent.getFromBranchName());
        assertEquals(repo1, newBranchEvent.getRepository());
    }

    @Test
    public void removeBranchTest() {
        final Branch masterBranch = makeBranch("master", "repo1");
        final org.uberfire.java.nio.file.Path baseBranchPath = mock(org.uberfire.java.nio.file.Path.class);
        final FileSystem fileSystem = mock(FileSystem.class);
        final FileSystemProvider fileSystemProvider = mock(FileSystemProvider.class);
        doReturn(fileSystemProvider).when(fileSystem).provider();
        doReturn(fileSystem).when(baseBranchPath).getFileSystem();
        doReturn(baseBranchPath).when(pathUtil).convert(masterBranch.getPath());

        final WorkspaceProject project = mock(WorkspaceProject.class);
        doReturn(repo1).when(project).getRepository();
        doReturn(new Space("my-space")).when(project).getSpace();

        doReturn(mock(SpaceConfigStorage.class)).when(spaceConfigStorageRegistry).get("my-space");

        libraryService.removeBranch(project, masterBranch);

        verify(ioService).delete(baseBranchPath);
    }

    @Test
    public void getAllUsersSuccessTest() {
        final User user = mock(User.class);
        doReturn("admin").when(user).getIdentifier();
        final List<User> users = Collections.singletonList(user);
        final AbstractEntityManager.SearchResponse<User> searchResponse = mock(AbstractEntityManager.SearchResponse.class);
        doReturn(users).when(searchResponse).getResults();
        doReturn(searchResponse).when(userManagerService).search(any());

        final List<String> allUsers = libraryService.getAllUsers();

        assertEquals(1, allUsers.size());
        assertEquals("admin", allUsers.get(0));
    }

    @Test
    public void getAllUsersWithExceptionTest() {
        doThrow(new RuntimeException()).when(userManagerService).search(any());

        final List<String> allUsers = libraryService.getAllUsers();

        assertTrue(allUsers.isEmpty());
    }

    private Branch makeBranch(final String branchName,
                              final String repoName) {
        final Path path = mock(Path.class);
        doReturn("default://" + branchName + "@" + repoName + "/").when(path).toURI();
        return new Branch(branchName,
                          path);
    }
}
