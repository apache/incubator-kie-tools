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

import org.guvnor.common.services.project.context.ProjectContextChangeEvent;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryEnvironmentConfigurations;
import org.guvnor.structure.repositories.RepositoryService;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.examples.model.ExampleOrganizationalUnit;
import org.kie.workbench.common.screens.examples.model.ExampleProject;
import org.kie.workbench.common.screens.examples.model.ExampleRepository;
import org.kie.workbench.common.screens.examples.model.ExampleTargetRepository;
import org.kie.workbench.common.screens.examples.service.ExamplesService;
import org.kie.workbench.common.screens.explorer.backend.server.ExplorerServiceHelper;
import org.kie.workbench.common.screens.explorer.service.ActiveOptions;
import org.kie.workbench.common.screens.library.api.LibraryInfo;
import org.kie.workbench.common.screens.library.api.LibraryPreferences;
import org.kie.workbench.common.screens.library.api.OrganizationalUnitRepositoryInfo;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.authz.AuthorizationManager;

import static org.jgroups.util.Util.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.*;


@RunWith( MockitoJUnitRunner.class )
public class LibraryServiceImplTest {

    @Mock
    private OrganizationalUnitService ouService;

    @Mock
    private RepositoryService repositoryService;

    @Mock
    private KieProjectService kieProjectService;

    @Mock
    private LibraryPreferences preferences;

    @Mock
    private AuthorizationManager authorizationManager;

    @Mock
    private SessionInfo sessionInfo;

    @Mock
    private ExplorerServiceHelper explorerServiceHelper;

    @Mock
    private KieProjectService projectService;

    @Mock
    private ExamplesService examplesService;

    @Mock
    private IOService ioService;

    @Mock
    private OrganizationalUnit ou1;

    @Mock
    private OrganizationalUnit ou2;

    @Mock
    private Repository repo1;

    @Mock
    private Repository repo2Default;

    private LibraryServiceImpl libraryService;
    private List<OrganizationalUnit> ous;
    private Set<Project> projectsMock;

    @Before
    public void setup() {
        ous = Arrays.asList( ou1, ou2 );
        when( ouService.getOrganizationalUnits() ).thenReturn( ous );
        when( ou1.getIdentifier() ).thenReturn( "ou1" );
        when( ou2.getIdentifier() ).thenReturn( "ou2" );
        when( repo1.getAlias() ).thenReturn( "repo_created_by_user" );
        when( repo1.getBranches() ).thenReturn( Arrays.asList( "repo1-branch1", "repo1-branch2" ) );
        when( repo2Default.getAlias() ).thenReturn( "ou2-repo-alias" );
        when( repo2Default.getRoot() ).thenReturn( mock( Path.class ) );
        when( repo2Default.getBranches() ).thenReturn( Collections.singletonList( "repo2-branch1" ) );
        when( ou2.getRepositories() ).thenReturn( Arrays.asList( repo1, repo2Default ) );

        doReturn( true ).when( authorizationManager ).authorize( any( Repository.class ), any( User.class ) );
        doReturn( false ).when( authorizationManager ).authorize( eq( repo1 ), any( User.class ) );

        projectsMock = new HashSet<>();
        projectsMock.add( mock( Project.class ) );
        projectsMock.add( mock( Project.class ) );
        projectsMock.add( mock( Project.class ) );

        libraryService = spy( new LibraryServiceImpl( ouService,
                                                      repositoryService,
                                                      kieProjectService,
                                                      preferences,
                                                      authorizationManager,
                                                      sessionInfo,
                                                      explorerServiceHelper,
                                                      projectService,
                                                      examplesService,
                                                      ioService ) );
    }

    @Test
    public void getDefaultOrganizationalUnitRepositoryInfoTest() {
        final OrganizationalUnitRepositoryInfo info = mock( OrganizationalUnitRepositoryInfo.class );
        doReturn( info ).when( libraryService ).getOrganizationalUnitRepositoryInfo( any( OrganizationalUnit.class ) );

        assertEquals( info, libraryService.getDefaultOrganizationalUnitRepositoryInfo() );
    }

    @Test
    public void getOrganizationalUnitRepositoryInfoTest() {
        when( preferences.getRepositoryAlias() ).thenReturn( "repository1" );
        doAnswer( invocationOnMock -> getRepository( (String) invocationOnMock.getArguments()[ 2 ] ) )
                .when( repositoryService ).createRepository( any( OrganizationalUnit.class ),
                                                             anyString(),
                                                             anyString(),
                                                             any( RepositoryEnvironmentConfigurations.class ) );

        final Repository repository1 = getRepository( "repository1" );
        final Repository repository2 = getRepository( "repository2" );
        final Repository repository3 = getRepository( "repository3" );
        final Repository repository4 = getRepository( "organizationalUnit4-repository1" );

        final OrganizationalUnit organizationalUnit1 = getOrganizationalUnit( "organizationalUnit1", repository1 );
        final OrganizationalUnit organizationalUnit2 = getOrganizationalUnit( "organizationalUnit2", repository2, repository3 );
        final OrganizationalUnit organizationalUnit3 = getOrganizationalUnit( "organizationalUnit3" );
        final OrganizationalUnit organizationalUnit4 = getOrganizationalUnit( "organizationalUnit4", repository4 );

        final List<OrganizationalUnit> organizationalUnits = new ArrayList<>();
        organizationalUnits.add( organizationalUnit1 );
        organizationalUnits.add( organizationalUnit2 );
        organizationalUnits.add( organizationalUnit3 );
        organizationalUnits.add( organizationalUnit4 );
        doReturn( organizationalUnits ).when( ouService ).getOrganizationalUnits();

        organizationalUnitWithPrimaryRepositoryExistent( organizationalUnit1, "repository1" );
        organizationalUnitWithTwoRepositoriesSelectsTheFirst( organizationalUnit2, "repository2" );
        organizationalUnitWithNoRepositoriesCreatesThePrimaryRepository( organizationalUnit3, "repository1" );
        organizationalUnitWithNoRepositoriesCreatesTheSecondaryRepositorySincePrimaryAlreadyExists( organizationalUnit3, "organizationalUnit3-repository1", repository1 );
        organizationalUnitWithSecondaryRepositoryExistent( organizationalUnit4, "organizationalUnit4-repository1" );
    }

    @Test
    public void getLibraryInfoTest() {
        final Repository repository = mock( Repository.class );
        final Set<Project> projects = new HashSet<>();
        projects.add( mock( Project.class ) );
        doReturn( projects ).when( kieProjectService ).getProjects( eq( repository ),
                                                                    anyString() );

        final LibraryInfo libraryInfo = libraryService.getLibraryInfo( repository );

        assertEquals( "master", libraryInfo.getSelectedBranch() );
        assertEquals( projects, libraryInfo.getProjects() );
    }

    @Test
    public void newProjectTest() {
        when( preferences.getOuIdentifier() ).thenReturn( "ou2" );
        when( preferences.getRepositoryAlias() ).thenReturn( "repo-alias" );
        when( preferences.getOuAlias() ).thenReturn( "team" );
        when( preferences.getProjectDefaultBranch() ).thenReturn( "master" );
        when( preferences.getProjectGroupId() ).thenReturn( "projectGroupID" );
        when( preferences.getProjectVersion() ).thenReturn( "1.0" );

        libraryService.createProject( "projectName", "ou2", "baseURL" );

        verify( kieProjectService ).newProject( eq( repo2Default.getRoot() ), any(), eq( "baseURL" ), any() );
    }

    @Test
    public void thereIsNotAProjectInTheWorkbenchTest() {
        final Boolean thereIsAProjectInTheWorkbench = libraryService.thereIsAProjectInTheWorkbench();

        assertFalse( thereIsAProjectInTheWorkbench );

        verify( kieProjectService, times( 1 ) ).getProjects( any( Repository.class ), anyString() );
        verify( kieProjectService ).getProjects( repo2Default, "repo2-branch1" );
    }

    @Test
    public void thereIsAProjectInTheWorkbenchTest() {
        Set<Project> projects = new HashSet<>();
        projects.add( mock( Project.class ) );
        doReturn( projects ).when( kieProjectService ).getProjects( any( Repository.class ), anyString() );

        final Boolean thereIsAProjectInTheWorkbench = libraryService.thereIsAProjectInTheWorkbench();

        assertTrue( thereIsAProjectInTheWorkbench );

        verify( kieProjectService, times( 1 ) ).getProjects( any( Repository.class ), anyString() );
        verify( kieProjectService ).getProjects( repo2Default, "repo2-branch1" );
    }

    @Test( expected = IllegalArgumentException.class )
    public void getNullProjectAssetsTest() {
        libraryService.getProjectAssets( null );
    }

    @Test
    public void getProjectAssetsTest() {
        libraryService.getProjectAssets( mock( Project.class ) );

        verify( projectService ).resolveDefaultPackage( any( Project.class ) );
        verify( explorerServiceHelper ).getAssetsRecursively( any( Package.class ), any( ActiveOptions.class ) );
    }

    @Test
    public void assertLoadPreferences() {
        libraryService.getPreferences();

        verify( preferences ).load();
    }

    @Test
    public void hasProjectsTest() {
        final Repository emptyRepository = mock( Repository.class );
        final Repository repository = mock( Repository.class );
        final Set<Project> projects = new HashSet<>();
        projects.add( mock( Project.class ) );
        doReturn( projects ).when( kieProjectService ).getProjects( eq( repository ),
                                                                    anyString() );

        assertTrue( libraryService.hasProjects( repository ) );
        assertFalse( libraryService.hasProjects( emptyRepository ) );
    }

    @Test
    public void hasAssetsTest() {
        final Package package1 = mock( Package.class );
        final Project project1 = mock( Project.class );
        doReturn( package1 ).when( projectService ).resolveDefaultPackage( project1 );
        doReturn( true ).when( explorerServiceHelper ).hasAssets( package1 );

        final Package package2 = mock( Package.class );
        final Project project2 = mock( Project.class );
        doReturn( package2 ).when( projectService ).resolveDefaultPackage( project2 );
        doReturn( false ).when( explorerServiceHelper ).hasAssets( package2 );

        assertTrue( libraryService.hasAssets( project1 ) );
        assertFalse( libraryService.hasAssets( project2 ) );
    }

    @Test
    public void getExampleProjectsImportUrlDefinedTest() {
        final Set<ExampleProject> exampleProjects = new HashSet<>();
        exampleProjects.add( mock( ExampleProject.class ) );

        when( preferences.getImportProjectsUrl() ).thenReturn( "importProjectsUrl" );
        doReturn( exampleProjects ).when( examplesService ).getProjects( new ExampleRepository( "importProjectsUrl" ) );

        final Set<ExampleProject> loadedExampleProjects = libraryService.getExampleProjects();

        assertEquals( exampleProjects, loadedExampleProjects );
    }

    @Test
    public void getExampleProjectsImportUrlNotDefinedTest() {
        final ExampleRepository playgroundRepository = new ExampleRepository( "playgroundRepositoryUrl" );

        when( preferences.getImportProjectsUrl() ).thenReturn( "" );
        doReturn( playgroundRepository ).when( examplesService ).getPlaygroundRepository();

        libraryService.getExampleProjects();

        verify( examplesService, times( 1 ) ).getProjects( playgroundRepository );
    }

    @Test
    public void importProjectTest() {
        final OrganizationalUnit organizationalUnit = mock( OrganizationalUnit.class );
        final Repository repository = mock( Repository.class );
        final ExampleProject exampleProject = mock( ExampleProject.class );

        final Project project = mock( Project.class );
        final ProjectContextChangeEvent projectContextChangeEvent = mock( ProjectContextChangeEvent.class );
        doReturn( project ).when( projectContextChangeEvent ).getProject();
        doReturn( projectContextChangeEvent ).when( examplesService ).setupExamples( any( ExampleOrganizationalUnit.class ),
                                                                                                   any( ExampleTargetRepository.class ),
                                                                                                   anyList() );

        final Project importedProject = libraryService.importProject( organizationalUnit,
                                                                      repository,
                                                                      exampleProject );

        assertEquals( project, importedProject );
    }

    @Test
    public void createPOM() {
        when( preferences.getProjectGroupId() ).thenReturn( "projectGroupID" );
        when( preferences.getProjectVersion() ).thenReturn( "1.0" );
        when( preferences.getProjectDescription() ).thenReturn( "desc" );

        GAV gav = libraryService.createGAV( "proj", preferences );
        POM proj = libraryService.createPOM( "proj", preferences, gav );

        assertEquals( "proj", proj.getName() );
        assertEquals( preferences.getProjectDescription(), proj.getDescription() );
        assertEquals( gav, proj.getGav() );

    }

    @Test
    public void createGAV() {
        when( preferences.getProjectGroupId() ).thenReturn( "projectGroupID" );
        when( preferences.getProjectVersion() ).thenReturn( "1.0" );

        GAV gav = libraryService.createGAV( "proj", preferences );

        assertEquals( preferences.getProjectGroupId(), gav.getGroupId() );
        assertEquals( "proj", gav.getArtifactId() );
        assertEquals( preferences.getProjectVersion(), gav.getVersion() );
    }

    private void organizationalUnitWithSecondaryRepositoryExistent( final OrganizationalUnit organizationalUnit,
                                                                    final String repositoryIdentifier ) {
        final OrganizationalUnitRepositoryInfo info5 = libraryService.getOrganizationalUnitRepositoryInfo( organizationalUnit );
        assertOrganizationalUnitRepositoryInfo( info5, 4, organizationalUnit.getIdentifier(), 1, repositoryIdentifier );
    }

    private void organizationalUnitWithNoRepositoriesCreatesTheSecondaryRepositorySincePrimaryAlreadyExists( final OrganizationalUnit organizationalUnit,
                                                                                                             final String repositoryIdentifier,
                                                                                                             final Repository alreadyExistentPrimaryRepository ) {
        doReturn( alreadyExistentPrimaryRepository ).when( repositoryService ).getRepository( "repository1" );
        final OrganizationalUnitRepositoryInfo info = libraryService.getOrganizationalUnitRepositoryInfo( organizationalUnit );
        assertOrganizationalUnitRepositoryInfo( info, 4, organizationalUnit.getIdentifier(), 0, repositoryIdentifier );
    }

    private void organizationalUnitWithNoRepositoriesCreatesThePrimaryRepository( final OrganizationalUnit organizationalUnit,
                                                                                  final String repositoryIdentifier ) {
        doReturn( null ).when( repositoryService ).getRepository( "repository1" );
        final OrganizationalUnitRepositoryInfo info = libraryService.getOrganizationalUnitRepositoryInfo( organizationalUnit );
        assertOrganizationalUnitRepositoryInfo( info, 4, organizationalUnit.getIdentifier(), 0, repositoryIdentifier );
    }

    private void organizationalUnitWithTwoRepositoriesSelectsTheFirst( final OrganizationalUnit organizationalUnit,
                                                                       final String repositoryIdentifier ) {
        final OrganizationalUnitRepositoryInfo info = libraryService.getOrganizationalUnitRepositoryInfo( organizationalUnit );
        assertOrganizationalUnitRepositoryInfo( info, 4, organizationalUnit.getIdentifier(), 2, repositoryIdentifier );
    }

    private void organizationalUnitWithPrimaryRepositoryExistent( final OrganizationalUnit organizationalUnit,
                                                                  final String repositoryIdentifier ) {
        final OrganizationalUnitRepositoryInfo info = libraryService.getOrganizationalUnitRepositoryInfo( organizationalUnit );
        assertOrganizationalUnitRepositoryInfo( info, 4, organizationalUnit.getIdentifier(), 1, repositoryIdentifier );
    }

    private void assertOrganizationalUnitRepositoryInfo( final OrganizationalUnitRepositoryInfo info,
                                                         final int totalOfOrganizationalUnits,
                                                         final String organizationalUnitIdentifier,
                                                         final int totalOfRepositories,
                                                         final String repositoryAlias ) {
        assertEquals( totalOfOrganizationalUnits, info.getOrganizationalUnits().size() );
        assertEquals( organizationalUnitIdentifier, info.getSelectedOrganizationalUnit().getIdentifier() );
        assertEquals( totalOfRepositories, info.getRepositories().size() );
        assertEquals( repositoryAlias, info.getSelectedRepository().getAlias() );
    }

    private Repository getRepository( final String alias ) {
        final Repository repository1 = mock( Repository.class );
        doReturn( alias ).when( repository1 ).getAlias();

        return repository1;
    }

    private OrganizationalUnit getOrganizationalUnit( final String identifier,
                                                      final Repository... repositories ) {
        final OrganizationalUnit organizationalUnit = mock( OrganizationalUnit.class );
        doReturn( identifier ).when( organizationalUnit ).getIdentifier();
        doReturn( Arrays.asList( repositories ) ).when( organizationalUnit ).getRepositories();

        return organizationalUnit;
    }
}
