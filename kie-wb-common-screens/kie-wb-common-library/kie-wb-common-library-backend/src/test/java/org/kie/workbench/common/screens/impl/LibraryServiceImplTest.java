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

import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.repositories.EnvironmentParameters;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryEnvironmentConfigurations;
import org.guvnor.structure.repositories.RepositoryService;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.explorer.backend.server.ExplorerServiceHelper;
import org.kie.workbench.common.screens.explorer.service.ActiveOptions;
import org.kie.workbench.common.screens.library.api.LibraryInfo;
import org.kie.workbench.common.screens.library.api.LibraryPreferences;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.authz.AuthorizationManager;

import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.jgroups.util.Util.assertEquals;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
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
        //cdi
        LibraryServiceImpl cdiProxy = new LibraryServiceImpl();

        ous = Arrays.asList( ou1, ou2 );
        when( ouService.getOrganizationalUnits() ).thenReturn( ous );
        when( ou1.getIdentifier() ).thenReturn( "ou1" );
        when( ou2.getIdentifier() ).thenReturn( "ou2" );
        when( repo1.getAlias() ).thenReturn( "repo_created_by_user" );
        when( repo1.getBranches() ).thenReturn( Arrays.asList( "repo1-branch1", "repo1-branch2" ) );
        when( repo2Default.getAlias() ).thenReturn( "ou2-repo-alias" );
        when( repo2Default.getRoot() ).thenReturn( mock( Path.class ) );
        when( repo2Default.getBranches() ).thenReturn( Arrays.asList( "repo2-branch1" ) );
        when( ou2.getRepositories() ).thenReturn( Arrays.asList( repo1, repo2Default ) );

        doReturn( true ).when( authorizationManager ).authorize( any( Repository.class ), any( User.class ) );
        doReturn( false ).when( authorizationManager ).authorize( eq( repo1 ), any( User.class ) );

        projectsMock = new HashSet<>();
        projectsMock.add( mock( Project.class ) );
        projectsMock.add( mock( Project.class ) );
        projectsMock.add( mock( Project.class ) );

        libraryService = new LibraryServiceImpl( ouService, repositoryService, kieProjectService, preferences, authorizationManager, sessionInfo, explorerServiceHelper, projectService, ioService );
    }

    @Test
    public void getDefaultOUTest() {

        when( preferences.getOuIdentifier() ).thenReturn( "ou2" );

        assertEquals( ou2, libraryService.getDefaultOrganizationalUnit() );
    }

    @Test
    public void getDefaultOUTestShouldCreateDefaultOUwhenThereIsNoOU() {

        when( preferences.getOuIdentifier() ).thenReturn( "new-ou" );
        when( preferences.getOuOwner() ).thenReturn( "owner" );
        when( preferences.getOuGroupId() ).thenReturn( "group" );

        libraryService.getDefaultOrganizationalUnit();

        verify( ouService ).createOrganizationalUnit( "new-ou", "owner", "group" );
    }

    @Test
    public void getDefaultRepository() {

        when( preferences.getRepositoryDefaultScheme() ).thenReturn( "scheme" );
        when( preferences.getRepositoryAlias() ).thenReturn( "repo-alias" );

        Repository defaultRepository = libraryService.getDefaultRepository( ou2 );

        assertEquals( repo2Default, defaultRepository );
        verify( repositoryService, never() )
                .createRepository( any(), any(), any(), any() );
    }

    @Test
    public void getDefaultRepositoryShouldCreateRepoWhenThereIsNoRepo() {

        when( preferences.getRepositoryDefaultScheme() ).thenReturn( "scheme" );
        when( preferences.getRepositoryAlias() ).thenReturn( "repo-alias" );

        libraryService.getDefaultRepository( ou1 );

        String repoAlias = ou1.getIdentifier() + "-" + "repo-alias";

        verify( repositoryService )
                .createRepository( eq( ou1 ),
                                   eq( "scheme" ),
                                   eq( repoAlias ),
                                   any( RepositoryEnvironmentConfigurations.class ) );
    }

    @Test
    public void getDefaultRepositoryName() {

        when( preferences.getRepositoryAlias() ).thenReturn( "repo-alias" );

        String repoAlias = ou1.getIdentifier() + "-" + preferences.getRepositoryAlias();

        assertEquals( repoAlias, libraryService.getDefaultRepositoryName( ou1 ) );
    }

    @Test
    public void getDefaultRepositoryEnvironmentConfigurations() {

        RepositoryEnvironmentConfigurations conf = libraryService
                .getDefaultRepositoryEnvironmentConfigurations();

        Boolean managed = (Boolean) conf.getConfigurationMap().get( EnvironmentParameters.MANAGED );
        assertNull( managed );
    }

    @Test
    public void getProjects() {

        when( preferences.getProjectDefaultBranch() ).thenReturn( "master" );
        when( preferences.getRepositoryAlias() ).thenReturn( "repo-alias" );

        Repository defaultRepository = libraryService.getDefaultRepository( ou2 );

        libraryService.getProjects( ou2 );

        verify( kieProjectService ).getProjects( defaultRepository, preferences.getProjectDefaultBranch() );
    }

    @Test
    public void getDefaultLibraryInfo() {

        when( preferences.getOuIdentifier() ).thenReturn( "ou2" );
        when( preferences.getRepositoryAlias() ).thenReturn( "repo-alias" );
        when( preferences.getOuAlias() ).thenReturn( "team" );
        when( preferences.getProjectDefaultBranch() ).thenReturn( "master" );


        when( kieProjectService.getProjects( repo2Default, preferences.getProjectDefaultBranch() ) )
                .thenReturn( projectsMock );

        LibraryInfo defaultLibraryInfo = libraryService.getDefaultLibraryInfo();

        assertTrue( defaultLibraryInfo.isFullLibrary() );
        assertEquals( "team", defaultLibraryInfo.getOuAlias() );
        assertEquals( projectsMock, defaultLibraryInfo.getProjects() );
        assertEquals( ou2, defaultLibraryInfo.getDefaultOrganizationUnit() );
        assertEquals( ou2, defaultLibraryInfo.getSelectedOrganizationUnit() );
        assertEquals( ous, defaultLibraryInfo.getOrganizationUnits() );
    }

    @Test
    public void getLibraryInfo() {

        //ou1 now is default
        when( preferences.getOuIdentifier() ).thenReturn( "ou1" );
        when( preferences.getRepositoryAlias() ).thenReturn( "repo-alias" );
        when( preferences.getOuAlias() ).thenReturn( "team" );
        when( preferences.getProjectDefaultBranch() ).thenReturn( "master" );

        when( kieProjectService.getProjects( repo2Default, preferences.getProjectDefaultBranch() ) )
                .thenReturn( projectsMock );

        //ou1 now is default
        LibraryInfo defaultLibraryInfo = libraryService.getLibraryInfo( "ou2" );

        assertTrue( defaultLibraryInfo.isFullLibrary() );
        assertEquals( "team", defaultLibraryInfo.getOuAlias() );
        assertEquals( projectsMock, defaultLibraryInfo.getProjects() );
        assertEquals( ou1, defaultLibraryInfo.getDefaultOrganizationUnit() );
        assertEquals( ou2, defaultLibraryInfo.getSelectedOrganizationUnit() );
        assertEquals( ous, defaultLibraryInfo.getOrganizationUnits() );
    }

    @Test
    public void newProject() {
        when( preferences.getOuIdentifier() ).thenReturn( "ou2" );
        when( preferences.getRepositoryAlias() ).thenReturn( "repo-alias" );
        when( preferences.getOuAlias() ).thenReturn( "team" );
        when( preferences.getProjectDefaultBranch() ).thenReturn( "master" );
        when( preferences.getProjectGroupId() ).thenReturn( "projectGroupID" );
        when( preferences.getProjectVersion() ).thenReturn( "1.0" );

        libraryService.newProject( "projectName", "ou2", "baseURL" );

        verify( kieProjectService ).newProject( eq( repo2Default.getRoot() ), any(), eq( "baseURL" ), any() );
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

    @Test
    public void assertLoadPreferences() {

        libraryService.getPreferences();

        verify( preferences ).load();
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
}
