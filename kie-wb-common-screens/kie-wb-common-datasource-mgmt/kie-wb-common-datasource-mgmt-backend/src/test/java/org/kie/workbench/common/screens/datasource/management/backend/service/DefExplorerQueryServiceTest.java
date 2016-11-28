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

package org.kie.workbench.common.screens.datasource.management.backend.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.guvnor.common.services.project.model.Project;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.organizationalunit.impl.OrganizationalUnitImpl;
import org.guvnor.structure.repositories.PublicURI;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.datasource.management.model.DataSourceDefInfo;
import org.kie.workbench.common.screens.datasource.management.model.DriverDefInfo;
import org.kie.workbench.common.screens.datasource.management.service.DataSourceDefQueryService;
import org.kie.workbench.common.screens.datasource.management.service.DefExplorerQuery;
import org.kie.workbench.common.screens.datasource.management.service.DefExplorerQueryResult;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.security.authz.AuthorizationManager;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( MockitoJUnitRunner.class )
public class DefExplorerQueryServiceTest {

    @Mock
    private DataSourceDefQueryService dataSourceDefQueryService;

    @Mock
    private KieProjectService projectService;

    @Mock
    private OrganizationalUnitService organizationalUnitService;

    @Mock
    private AuthorizationManager authorizationManager;

    @Mock
    private User identity;

    @InjectMocks
    private DefExplorerQueryServiceImpl explorerQueryService;

    private List<OrganizationalUnit> organizationalUnits;

    private OrganizationalUnit o1, o2, o3;

    private Repository repo_o1_1, repo_o1_2, repo_o1_3;

    private Repository repo_o2_1, repo_o2_2;

    private Repository repo_o3_1, repo_o3_2, repo_o3_3;

    @Mock
    private Project project1;

    @Mock
    private Path rootPath1;

    @Mock
    private Project project2;

    @Mock
    private Path rootPath2;

    @Mock
    private Project project3;

    @Mock
    private Path rootPath3;

    @Mock
    private DataSourceDefInfo ds1;

    @Mock
    private DataSourceDefInfo ds2;

    @Mock
    private DriverDefInfo driver1;

    @Mock
    private DriverDefInfo driver2;

    @Mock
    private DriverDefInfo driver3;

    @Before
    public void setup() {

        createOrganizationalUnits();

        //setup current organizational units
        when( organizationalUnitService.getOrganizationalUnits() ).thenReturn( organizationalUnits );

        when( organizationalUnitService.getOrganizationalUnit( o1.getName() ) ).thenReturn( o1 );
        when( organizationalUnitService.getOrganizationalUnit( o2.getName() ) ).thenReturn( o2 );
        when( organizationalUnitService.getOrganizationalUnit( o3.getName() ) ).thenReturn( o3 );

        //emulates the authorizations for current identity.
        //o1 is not authorized
        when( authorizationManager.authorize( o1, identity ) ).thenReturn( false );

        //o2 is authorized
        when( authorizationManager.authorize( o2, identity ) ).thenReturn( true );
        //repo_o2_1 is authorized
        when( authorizationManager.authorize( repo_o2_1, identity ) ).thenReturn( true );
        //repo_o2_2 is authorized
        when( authorizationManager.authorize( repo_o2_2, identity ) ).thenReturn( true );

        //o3 is authorized
        when( authorizationManager.authorize( o3, identity ) ).thenReturn( true );
        //repo_o3_1 is authorized
        when( authorizationManager.authorize( repo_o3_1, identity ) ).thenReturn( true );
        //repo_o3_2 is not authorized
        when( authorizationManager.authorize( repo_o3_2, identity ) ).thenReturn( false );
        //repo_o3_3 is authorized
        when( authorizationManager.authorize( repo_o3_3, identity ) ).thenReturn( true );

        //prepare the projects
        when ( project1.getProjectName() ).thenReturn( "project1" );
        when ( project1.getRootPath() ).thenReturn( rootPath1 );
        when ( project2.getProjectName() ).thenReturn( "project2" );
        when ( project2.getRootPath() ).thenReturn( rootPath2 );
        when ( project3.getProjectName() ).thenReturn( "project3" );
        when ( project3.getRootPath() ).thenReturn( rootPath3 );

        Set<Project> projects = new HashSet<>( );
        projects.add( project1 );
        projects.add( project2 );
        projects.add( project3 );

        //repo_o3_3 has -> project1, project2 and project3
        when ( projectService.getProjects( eq( repo_o3_3 ), anyString() ) ).thenReturn( projects );

        //project1 is authorized
        when( authorizationManager.authorize( project1, identity ) ).thenReturn( true );
        //project2 is authorized
        when( authorizationManager.authorize( project2, identity ) ).thenReturn( true );
        //project3 is not authorized.
        when( authorizationManager.authorize( project3, identity ) ).thenReturn( false );

        //project1 has data sources ds1 and ds2, and drivers driver1, driver2 and driver3
        ArrayList<DataSourceDefInfo> project2DSs = new ArrayList<>( );
        project2DSs.add( ds1 );
        project2DSs.add( ds2 );

        ArrayList<DriverDefInfo> project2Drivers = new ArrayList<>( );
        project2Drivers.add( driver1 );
        project2Drivers.add( driver2 );
        project2Drivers.add( driver3 );

        when( dataSourceDefQueryService.findProjectDataSources( project2 ) ).thenReturn( project2DSs );
        when( dataSourceDefQueryService.findProjectDrivers( project2 ) ).thenReturn( project2Drivers );
    }

    /**
     * Tests a query with no parameters, basically should return the list of organizational units accessible by current user.
     */
    @Test
    public void testEmptyQuery() {
        DefExplorerQuery query = new DefExplorerQuery( );
        DefExplorerQueryResult result  = explorerQueryService.executeQuery( query );

        //o1 should not be included in the result.
        assertFalse( result.getOrganizationalUnits().contains( o1 ) );
        //o2 must be included in the result.
        assertTrue( result.getOrganizationalUnits().contains( o2 ) );
        //o3 must be included in the result.
        assertTrue( result.getOrganizationalUnits().contains( o3 ) );

        //repositories, projects, data sources and drivers should be empty since no organizational unit was selected
        assertTrue( result.getRepositories().isEmpty() );
        assertTrue( result.getProjects().isEmpty() );
        assertTrue( result.getDataSourceDefs().isEmpty() );
        assertTrue( result.getProjects().isEmpty() );
    }

    /**
     * Tests a query for a given Organizational Unit as the only parameter. In this case the query should return the
     * list of available authorized repositories for the selected OU.
     */
    @Test
    public void testQueryForOrganizationalUnit() {
        DefExplorerQuery query = new DefExplorerQuery(  );
        query.setOrganizationalUnit( o2 );

        DefExplorerQueryResult result = explorerQueryService.executeQuery( query );

        //o1 should not be included in the result since it's not authorized.
        assertFalse( result.getOrganizationalUnits().contains( o1 ) );
        //o2 must be included in the result.
        assertTrue( result.getOrganizationalUnits().contains( o2 ) );
        //o3 is still included in the result since organizational units are piggybacked to let the UI be refreshed
        assertTrue( result.getOrganizationalUnits().contains( o3 ) );

        //authorized repositories for organizational unit o2 must be in the result.
        assertTrue( result.getRepositories().contains( repo_o2_1 ) );
        assertTrue( result.getRepositories().contains( repo_o2_2 ) );
        assertEquals( 2, result.getRepositories().size() );
    }

    /**
     * Tests a query for a given Organizational Unit and a given repository. In this case the list of available
     * authorized projects for the given repository should be returned.
     */
    @Test
    public void testQueryForOrganizationalUnitRepository() {
        DefExplorerQuery query = new DefExplorerQuery(  );
        query.setOrganizationalUnit( o3 );
        query.setRepository( repo_o3_3 );

        DefExplorerQueryResult result = explorerQueryService.executeQuery( query );
        verifyResultForQueryForOrganizationalUnitRepository( result );
    }

    private void verifyResultForQueryForOrganizationalUnitRepository( DefExplorerQueryResult result ) {
        //o1 should not be included in the result.
        assertFalse( result.getOrganizationalUnits().contains( o1 ) );
        //o2 is still included in the result since organizational units are piggybacked to let the UI be refreshed
        assertTrue( result.getOrganizationalUnits().contains( o2 ) );
        //o3 must be included in the result
        assertTrue( result.getOrganizationalUnits().contains( o3 ) );

        //the authorized repositories for organizational unit o3 must be in the result, since they are piggybacked.
        assertTrue( result.getRepositories().contains( repo_o3_1 ) );
        //repo_o3_2 is not authorized
        assertFalse( result.getRepositories().contains( repo_o3_2 ) );
        assertTrue( result.getRepositories().contains( repo_o3_3 ) );
        assertEquals( 2, result.getRepositories().size() );

        //and the authorized projects in repo_o3_2 should be also returned, i.e. project1 and project2
        //project1 is authorized and thus must be in the result.
        assertTrue( result.getProjects().contains( project1 ) );
        //project2 is authorized and thus must be in the result.
        assertTrue( result.getProjects().contains( project2 ) );
        //project3 is not authorized, and can't be in the result.
        assertFalse( result.getProjects().contains( project3 ) );
        assertEquals( 2, result.getProjects().size() );
    }

    /**
     * Tests a query for a given Organizational Unit, a Repository, and a selected Project.
     * In this case the list of available data sources and drivers for the given repository should be returned.
     */
    @Test
    public void testQueryForOrganizationalUnitRepositoryProject() {
        DefExplorerQuery query = new DefExplorerQuery(  );
        query.setOrganizationalUnit( o3 );
        query.setRepository( repo_o3_3 );
        query.setProject( project2 );

        DefExplorerQueryResult result = explorerQueryService.executeQuery( query );

        //organizational units, repositories and projects are piggybacked, so the result should include
        //the same structure information as testQueryForOrganizationalUnitRepository
        verifyResultForQueryForOrganizationalUnitRepository( result );

        //additionally all data sources and drivers from project2 should be in the result.
        assertTrue( result.getDataSourceDefs().contains( ds1 ) );
        assertTrue( result.getDataSourceDefs().contains( ds2 ) );

        assertTrue( result.getDriverDefs().contains( driver1 ) );
        assertTrue( result.getDriverDefs().contains( driver2 ) );
        assertTrue( result.getDriverDefs().contains( driver3 ) );
    }

    private void createOrganizationalUnits() {
        organizationalUnits = new ArrayList<>(  );
        o1 = new OrganizationalUnitImpl( "o1", "owner1", "group1" );
        repo_o1_1 = new RepositoryMock( "repo_o1_1", "repo_o1_1" );
        repo_o1_2 = new RepositoryMock( "repo_o1_2", "repo_o1_2" );
        repo_o1_3 = new RepositoryMock( "repo_o1_3", "repo_o1_3" );
        o1.getRepositories().add( repo_o1_1 );
        o1.getRepositories().add( repo_o1_2 );
        o1.getRepositories().add( repo_o1_3 );
        organizationalUnits.add( o1 );

        o2 = new OrganizationalUnitImpl( "o2", "owner2", "group2" );
        repo_o2_1 = new RepositoryMock( "repo_o2_1", "repo_o2_1" );
        repo_o2_2 = new RepositoryMock( "repo_o2_2", "repo_o2_2" );
        o2.getRepositories().add( repo_o2_1 );
        o2.getRepositories().add( repo_o2_2 );
        organizationalUnits.add( o2 );

        o3 = new OrganizationalUnitImpl( "o3", "owner3", "group3" );
        repo_o3_1 = new RepositoryMock( "repo_o3_1", "repo_o3_1" );
        repo_o3_2 = new RepositoryMock( "repo_o3_2", "repo_o3_2" );
        repo_o3_3 =  new RepositoryMock( "repo_o3_3", "repo_o3_3" );
        o3.getRepositories().add( repo_o3_1 );
        o3.getRepositories().add( repo_o3_2 );
        o3.getRepositories().add( repo_o3_3 );
        organizationalUnits.add( o3 );
    }

    private class RepositoryMock implements Repository {

        String alias;

        String identifier;

        public RepositoryMock( String alias, String identifier ) {
            this.alias = alias;
            this.identifier = identifier;
        }

        @Override public String getAlias() {
            return alias;
        }

        @Override public String getScheme() {
            return null;
        }

        @Override public Map<String, Object> getEnvironment() {
            return null;
        }

        @Override public void addEnvironmentParameter( String key, Object value ) {

        }

        @Override public boolean isValid() {
            return true;
        }

        @Override public String getUri() {
            return null;
        }

        @Override public List<PublicURI> getPublicURIs() {
            return null;
        }

        @Override public Path getRoot() {
            return null;
        }

        @Override public Path getBranchRoot( String branch ) {
            return null;
        }

        @Override public void setRoot( Path root ) {

        }

        @Override public Collection<String> getGroups() {
            return null;
        }

        @Override public Collection<String> getBranches() {
            return null;
        }

        @Override public String getDefaultBranch() {
            return null;
        }

        @Override public boolean requiresRefresh() {
            return false;
        }

        @Override public void markAsCached() {

        }

        @Override public String getIdentifier() {
            return identifier;
        }
    }

}
