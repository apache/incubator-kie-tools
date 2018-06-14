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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import javax.enterprise.event.Event;

import org.ext.uberfire.social.activities.model.SocialUser;
import org.ext.uberfire.social.activities.service.SocialUserRepositoryAPI;
import org.guvnor.common.services.project.backend.server.utils.PathUtil;
import org.guvnor.common.services.project.events.NewProjectEvent;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.organizationalunit.impl.OrganizationalUnitImpl;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryEnvironmentConfigurations;
import org.guvnor.structure.repositories.RepositoryService;
import org.guvnor.structure.repositories.impl.git.GitRepository;
import org.guvnor.structure.security.RepositoryAction;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.examples.model.ExampleProject;
import org.kie.workbench.common.screens.examples.model.ExampleRepository;
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
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.NoSuchFileException;
import org.uberfire.java.nio.file.spi.FileSystemProvider;
import org.uberfire.java.nio.fs.jgit.JGitFileSystem;
import org.uberfire.java.nio.fs.jgit.JGitPathImpl;
import org.uberfire.paging.PageResponse;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.spaces.Space;

import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LibraryServiceImplTest {

    @Mock
    private OrganizationalUnitService ouService;

    @Mock
    private AuthorizationManager authorizationManager;

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
    private SocialUserRepositoryAPI socialUserRepositoryAPI;

    @Mock
    private IndexStatusOracle indexOracle;

    @Mock
    private RepositoryService repositoryService;

    @Mock
    private Event<NewProjectEvent> newProjectEvent;

    @Mock
    private PathUtil pathUtil;

    private final PathUtil realPathUtil = new PathUtil();

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

    private Branch makeBranch(final String branchName) {
        return new Branch(branchName,
                          mock(Path.class));
    }

    @Before
    public void setup() {
        ous = Arrays.asList(ou1,
                            ou2);
        when(ouService.getOrganizationalUnits()).thenReturn(ous);
        when(ou1.getIdentifier()).thenReturn("ou1");
        when(ou2.getIdentifier()).thenReturn("ou2");
        when(repo1.getAlias()).thenReturn("repo_created_by_user");
        when(repo1.getBranches()).thenReturn(Arrays.asList(makeBranch("repo1-branch1"),
                                                           makeBranch("repo1-branch2")));
        when(repo2Default.getAlias()).thenReturn("ou2-repo-alias");
        when(repo2Default.getBranches()).thenReturn(Collections.singletonList(makeBranch("repo2-branch1")));
        when(ou2.getRepositories()).thenReturn(Arrays.asList(repo1,
                                                             repo2Default));

        when(indexOracle.isIndexed(any())).thenReturn(true);

        modulesMock = new HashSet<>();
        modulesMock.add(mock(Module.class));
        modulesMock.add(mock(Module.class));
        modulesMock.add(mock(Module.class));

        when(preferences.getOrganizationalUnitPreferences()).thenReturn(spy(new LibraryOrganizationalUnitPreferences()));
        when(preferences.getProjectPreferences()).thenReturn(spy(new LibraryProjectPreferences()));

        libraryService = spy(new LibraryServiceImpl(ouService,
                                                    refactoringQueryService,
                                                    preferences,
                                                    authorizationManager,
                                                    mock(SessionInfo.class),
                                                    explorerServiceHelper,
                                                    projectService,
                                                    moduleService,
                                                    examplesService,
                                                    repositoryService,
                                                    ioService,
                                                    internalPreferences,
                                                    socialUserRepositoryAPI,
                                                    indexOracle,
                                                    newProjectEvent,
                                                    pathUtil
        ));
    }

    @Test
    public void queryingUnindexedProjectGivesUnindexedResult() throws Exception {
        Branch branch = new Branch("fake-branch", mockPath("default:///a/b/c"));
        final WorkspaceProject project = new WorkspaceProject(ou1, repo1, branch, mock(Module.class));
        when(indexOracle.isIndexed(project)).thenReturn(false);
        when(ioService.exists(any())).thenReturn(true);

        final ProjectAssetsQuery query = new ProjectAssetsQuery(project,
                                                                "",
                                                                0,
                                                                10,
                                                                Collections.emptyList());

        AssetQueryResult result = libraryService.getProjectAssets(query);
        assertEquals(ResultType.Unindexed, result.getResultType());
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

        assertEquals(1, libraryInfo.getProjects().size());
        assertSame(project2, libraryInfo.getProjects().iterator().next());
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
        final WorkspaceProject project = spy(new WorkspaceProject(ou1, repo1, branch, null));
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
        final WorkspaceProject project =  spy(new WorkspaceProject(mock(OrganizationalUnit.class),
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

        assertEquals(ResultType.Normal, result.getResultType());
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

        final Set<ExampleProject> exampleProjects = new HashSet<>();
        exampleProjects.add(mock(ExampleProject.class));
        doReturn(exampleProjects).when(examplesService).getProjects(new ExampleRepository("importProjectsUrl"));

        final Set<ExampleProject> loadedExampleProjects = libraryService.getExampleProjects();

        assertEquals(exampleProjects,
                     loadedExampleProjects);
    }

    @Test
    public void getDefaultExampleProjectsTest() {
        System.setProperty("org.kie.project.examples.repository.url",
                           "");

        final ExampleRepository playgroundRepository = new ExampleRepository("playgroundRepositoryUrl");
        doReturn(playgroundRepository).when(examplesService).getPlaygroundRepository();

        final Set<ExampleProject> exampleProjects = new HashSet<>();
        exampleProjects.add(mock(ExampleProject.class));
        doReturn(exampleProjects).when(examplesService).getProjects(playgroundRepository);

        final Set<ExampleProject> loadedExampleProjects = libraryService.getExampleProjects();

        assertEquals(exampleProjects,
                     loadedExampleProjects);
    }

    @Test
    public void importProjectWithCredentialsTest() {
        final OrganizationalUnit organizationalUnit = mock(OrganizationalUnit.class);
        final Repository repo = mock(Repository.class);
        final WorkspaceProject project = mock(WorkspaceProject.class);

        final String repositoryURL = "file:///some/path/to/fake-repo.git";
        final String username = "fakeUser";
        final String password = "fakePassword";

        final ArgumentCaptor<RepositoryEnvironmentConfigurations> configCaptor = ArgumentCaptor.forClass(RepositoryEnvironmentConfigurations.class);

        when(repositoryService.createRepository(any(), any(), any(), configCaptor.capture())).thenReturn(repo);
        when(projectService.resolveProject(any(Repository.class))).thenReturn(project);

        final WorkspaceProject observedProject = libraryService.importProject(organizationalUnit, repositoryURL, username, password);

        verify(repositoryService).createRepository(same(organizationalUnit), eq(GitRepository.SCHEME.toString()), eq("fake-repo"), any());
        RepositoryEnvironmentConfigurations observedConfig = configCaptor.getValue();
        assertEquals(username, observedConfig.getUserName());
        assertEquals(password, observedConfig.getPassword());
        assertEquals(repositoryURL, observedConfig.getOrigin());

        verify(projectService).resolveProject(same(repo));

        assertSame(project, observedProject);
    }

    @Test
    public void importProjectWithoutCredentialsTest() {
        final OrganizationalUnit organizationalUnit = mock(OrganizationalUnit.class);
        final Repository repo = mock(Repository.class);
        final WorkspaceProject project = mock(WorkspaceProject.class);

        final String repositoryURL = "file:///some/path/to/fake-repo.git";
        final String username = null;
        final String password = null;

        final ArgumentCaptor<RepositoryEnvironmentConfigurations> configCaptor = ArgumentCaptor.forClass(RepositoryEnvironmentConfigurations.class);

        when(repositoryService.createRepository(any(), any(), any(), configCaptor.capture())).thenReturn(repo);
        when(projectService.resolveProject(any(Repository.class))).thenReturn(project);

        final WorkspaceProject observedProject = libraryService.importProject(organizationalUnit, repositoryURL, username, password);

        verify(repositoryService).createRepository(same(organizationalUnit), eq(GitRepository.SCHEME.toString()), eq("fake-repo"), any());
        RepositoryEnvironmentConfigurations observedConfig = configCaptor.getValue();
        assertEquals(username, observedConfig.getUserName());
        assertEquals(password, observedConfig.getPassword());
        assertEquals(repositoryURL, observedConfig.getOrigin());

        verify(projectService).resolveProject(same(repo));

        assertSame(project, observedProject);
    }

    @Test
    public void importDefaultProjectTest() {
        final OrganizationalUnit organizationalUnit = new OrganizationalUnitImpl("myteam", "admin", "org.whatever");
        organizationalUnit.getRepositories();

        final Path exampleRoot = mock(Path.class);
        final org.uberfire.java.nio.file.Path exampleRootNioPath = mock(org.uberfire.java.nio.file.Path.class);
        when(pathUtil.convert(exampleRoot)).thenReturn(exampleRootNioPath);
        final ExampleProject exampleProject = new ExampleProject(exampleRoot, "example", "description", emptyList());

        String repoURL = "file:///some/repo/url";
        when(pathUtil.getNiogitRepoPath(any())).thenReturn(repoURL);

        final Repository repository = new GitRepository("example", new Space("myteam"));
        final WorkspaceProject project = new WorkspaceProject(organizationalUnit,
                                                              repository,
                                                              new Branch("master", mock(Path.class)),
                                                              new Module());
        when(projectService.resolveProject(repository)).thenReturn(project);
        when(repositoryService.createRepository(same(organizationalUnit), eq(GitRepository.SCHEME.toString()), any(), any())).thenReturn(repository);

        final WorkspaceProject importedProject = libraryService.importProject(organizationalUnit,
                                                                              exampleProject);

        assertSame(project, importedProject);
        final ArgumentCaptor<RepositoryEnvironmentConfigurations> configsCaptor = ArgumentCaptor.forClass(RepositoryEnvironmentConfigurations.class);
        verify(repositoryService).createRepository(same(organizationalUnit), eq(GitRepository.SCHEME.toString()), any(), configsCaptor.capture());
        final RepositoryEnvironmentConfigurations configs = configsCaptor.getValue();
        assertEquals(repoURL, configs.getOrigin());
        assertNull(configs.getSubdirectory());
        verify(projectService).resolveProject(repository);
    }

    @Test
    public void importProjectInSubdirectory() {
        final OrganizationalUnit organizationalUnit = new OrganizationalUnitImpl("myteam", "admin", "org.whatever");
        organizationalUnit.getRepositories();


        final String exampleURI = "default://master@system/repo/example";
        final Path exampleRoot = PathFactory.newPath("example", exampleURI);
        final JGitFileSystem fs = mock(JGitFileSystem.class);
        final FileSystemProvider provider = mock(FileSystemProvider.class);
        when(fs.provider()).thenReturn(provider);
        final org.uberfire.java.nio.file.Path exampleRootNioPath = JGitPathImpl.create(fs, "/example", "master@system/repo", true);
        final org.uberfire.java.nio.file.Path repoRoot = exampleRootNioPath.getParent();
        when(fs.getRootDirectories()).thenReturn(() -> Stream.of(repoRoot).iterator());
        when(pathUtil.convert(exampleRoot)).thenReturn(exampleRootNioPath);
        when(pathUtil.stripProtocolAndBranch(any())).then(inv -> realPathUtil.stripProtocolAndBranch(inv.getArgumentAt(0, String.class)));
        when(pathUtil.stripRepoNameAndSpace(any())).then(inv -> realPathUtil.stripRepoNameAndSpace(inv.getArgumentAt(0, String.class)));
        when(pathUtil.convert(any(org.uberfire.java.nio.file.Path.class))).then(inv -> realPathUtil.convert(inv.getArgumentAt(0, org.uberfire.java.nio.file.Path.class)));
        when(pathUtil.extractBranch(any())).then(inv -> realPathUtil.extractBranch(inv.getArgumentAt(0, String.class)));

        final ExampleProject exampleProject = new ExampleProject(exampleRoot, "example", "description", emptyList());

        String repoURL = "file:///some/repo/url";
        when(pathUtil.getNiogitRepoPath(any())).thenReturn(repoURL);

        final Repository repository = new GitRepository("example", new Space("myteam"));
        final WorkspaceProject project = new WorkspaceProject(organizationalUnit,
                                                              repository,
                                                              new Branch("master", mock(Path.class)),
                                                              new Module());
        when(projectService.resolveProject(repository)).thenReturn(project);
        when(repositoryService.createRepository(same(organizationalUnit), eq(GitRepository.SCHEME.toString()), any(), any())).thenReturn(repository);

        final WorkspaceProject importedProject = libraryService.importProject(organizationalUnit,
                                                                              exampleProject);

        assertSame(project, importedProject);
        final ArgumentCaptor<RepositoryEnvironmentConfigurations> configsCaptor = ArgumentCaptor.forClass(RepositoryEnvironmentConfigurations.class);
        verify(repositoryService).createRepository(same(organizationalUnit), eq(GitRepository.SCHEME.toString()), any(), configsCaptor.capture());
        final RepositoryEnvironmentConfigurations configs = configsCaptor.getValue();
        assertEquals(repoURL, configs.getOrigin());
        assertEquals("example", configs.getSubdirectory());
        verify(projectService).resolveProject(repository);
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
    public void getAllUsersTest() {
        List<SocialUser> allUsers = new ArrayList<>();
        allUsers.add(new SocialUser("system"));
        allUsers.add(new SocialUser("admin"));
        allUsers.add(new SocialUser("user"));
        doReturn(allUsers).when(socialUserRepositoryAPI).findAllUsers();

        final List<SocialUser> users = libraryService.getAllUsers();

        assertEquals(2,
                     users.size());
        assertEquals("admin",
                     users.get(0).getUserName());
        assertEquals("user",
                     users.get(1).getUserName());
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
}
