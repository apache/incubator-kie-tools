/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.explorer.backend.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.organizationalunit.impl.OrganizationalUnitImpl;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.impl.git.GitRepository;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.model.FolderItemType;
import org.kie.workbench.common.screens.explorer.model.FolderListing;
import org.kie.workbench.common.screens.explorer.model.ProjectExplorerContent;
import org.kie.workbench.common.screens.explorer.service.ActiveOptions;
import org.kie.workbench.common.screens.explorer.service.Option;
import org.kie.workbench.common.screens.explorer.service.ProjectExplorerContentQuery;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;
import org.uberfire.security.authz.AuthorizationManager;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class ProjectExplorerContentResolverDefaultSelectionsTest {

    private SimpleFileSystemProvider fs = new SimpleFileSystemProvider();
    private ProjectExplorerContentResolver resolver;
    private OrganizationalUnit organizationalUnit;

    private GitRepository repository1;
    private Project repository1Project1;
    private Set<Project> repository1Projects;

    private GitRepository repository2;
    private Project repository2Project1;
    private Set<Project> repository2Projects;

    private UserExplorerData userExplorerData;

    private HelperWrapper helperWrapper;

    @Before
    public void setUp() throws Exception {
        //Ensure URLs use the default:// scheme
        fs.forceAsDefault();

        final KieProjectService projectService = mock( KieProjectService.class );
        final ExplorerServiceHelper helper = mock( ExplorerServiceHelper.class );
        final AuthorizationManager authorizationManager = mock( AuthorizationManager.class );
        final OrganizationalUnitService organizationalUnitService = mock( OrganizationalUnitService.class );
        final ExplorerServiceHelper explorerServiceHelper = mock( ExplorerServiceHelper.class );

        repository1 = getGitRepository( "repo1" );
        repository2 = getGitRepository( "repo2" );

        organizationalUnit = spy( new OrganizationalUnitImpl( "demo",
                                                              "demo",
                                                              "demo" ) );
        organizationalUnit.getRepositories().add( repository1 );
        organizationalUnit.getRepositories().add( repository2 );

        final ArrayList<OrganizationalUnit> organizationalUnits = new ArrayList<OrganizationalUnit>();
        organizationalUnits.add( organizationalUnit );

        repository1Project1 = createProject( "master",
                                             "r1p1" );
        repository1Projects = new HashSet<Project>() {{
            add( repository1Project1 );
        }};

        repository2Project1 = createProject( "master",
                                             "r2p1" );
        repository2Projects = new HashSet<Project>() {{
            add( repository2Project1 );
        }};

        userExplorerData = new UserExplorerData();
        userExplorerData.setOrganizationalUnit( organizationalUnit );
        userExplorerData.addRepository( organizationalUnit,
                                        repository1 );
        userExplorerData.addRepository( organizationalUnit,
                                        repository2 );

        helperWrapper = new HelperWrapper( helper );

        when( helper.getLastContent() ).thenAnswer( new Answer<Object>() {
            @Override
            public Object answer( InvocationOnMock invocationOnMock ) throws Throwable {
                return helperWrapper.getUserExplorerLastData();
            }
        } );
        when( helper.loadUserContent() ).thenReturn( userExplorerData );
        when( helper.getFolderListing( any( FolderItem.class ),
                                       any( Project.class ),
                                       any( Package.class ),
                                       any( ActiveOptions.class ) ) ).thenReturn(
                new FolderListing( createFileItem(),
                                   Collections.EMPTY_LIST,
                                   Collections.EMPTY_LIST ) );

        when( organizationalUnitService.getOrganizationalUnits() ).thenReturn( organizationalUnits );
        when( organizationalUnitService.getOrganizationalUnit( "demo" ) ).thenReturn( organizationalUnit );
        when( authorizationManager.authorize( any( OrganizationalUnit.class ),
                                              any( User.class ) ) ).thenReturn( true );
        when( authorizationManager.authorize( any( Project.class ),
                                              any( User.class ) ) ).thenReturn( true );
        when( projectService.getProjects( repository1,
                                          "master" ) ).thenReturn( repository1Projects );
        when( projectService.getProjects( repository2,
                                          "master" ) ).thenReturn( repository2Projects );
        when( projectService.resolveDefaultPackage( repository1Project1 ) ).thenReturn( createPackage( "master",
                                                                                                       repository1Project1.getProjectName() ) );
        when( projectService.resolveDefaultPackage( repository2Project1 ) ).thenReturn( createPackage( "master",
                                                                                                       repository2Project1.getProjectName() ) );

        resolver = new ProjectExplorerContentResolver( projectService,
                                                       helper,
                                                       authorizationManager,
                                                       organizationalUnitService,
                                                       explorerServiceHelper );
    }

    @Test
    public void testSelectionsEmpty() throws Exception {
        final ProjectExplorerContent content = resolver.resolve( getContentQuery( null,
                                                                                  null,
                                                                                  null ) );

        assertEquals( organizationalUnit,
                      content.getOrganizationalUnit() );

        //The implementation does not order Repositories internally hence we cannot
        //guarantee which Repository will be the default. Just test for consistency.
        if ( content.getRepository().equals( repository1 ) ) {
            assertEquals( repository1Project1,
                          content.getProject() );
        } else if ( content.getRepository().equals( repository2 ) ) {
            assertEquals( repository2Project1,
                          content.getProject() );
        } else {
            fail( "A default Repository should have been selected." );
        }
    }

    @Test
    public void testSelectionsRepository() throws Exception {
        final ProjectExplorerContent content = resolver.resolve( getContentQuery( repository1,
                                                                                  "master",
                                                                                  null ) );

        assertEquals( organizationalUnit,
                      content.getOrganizationalUnit() );
        assertEquals( repository1,
                      content.getRepository() );
        assertEquals( repository1Project1,
                      content.getProject() );
    }

    @Test
    public void testSelectionsProject() throws Exception {
        final ProjectExplorerContent content = resolver.resolve( getContentQuery( repository1,
                                                                                  "master",
                                                                                  repository1Project1 ) );

        assertEquals( organizationalUnit,
                      content.getOrganizationalUnit() );
        assertEquals( repository1,
                      content.getRepository() );
        assertEquals( repository1Project1,
                      content.getProject() );
    }

    @Test
    public void testSelectionsDeleteRepository() throws Exception {
        //Select something in the Repository to be deleted
        final ProjectExplorerContent content1 = resolver.resolve( getContentQuery( repository2,
                                                                                   "master",
                                                                                   repository2Project1 ) );
        assertEquals( organizationalUnit,
                      content1.getOrganizationalUnit() );
        assertEquals( repository2,
                      content1.getRepository() );
        assertEquals( repository2Project1,
                      content1.getProject() );

        //Delete the Repository
        organizationalUnit.getRepositories().remove( repository2 );

        final ProjectExplorerContent content2 = resolver.resolve( getContentQuery( repository2,
                                                                                   "master",
                                                                                   repository2Project1 ) );

        assertEquals( organizationalUnit,
                      content2.getOrganizationalUnit() );
        assertEquals( repository1,
                      content2.getRepository() );
        assertEquals( repository1Project1,
                      content2.getProject() );
    }

    private FolderItem createFileItem() {
        Path path = mock( Path.class );
        return new FolderItem( path,
                               "someitem",
                               FolderItemType.FILE );
    }

    private Project createProject( final String branch,
                                   final String projectName ) {
        return new Project( createMockPath( branch,
                                            projectName ),
                            createMockPath( branch,
                                            projectName ),
                            projectName );
    }

    private Package createPackage( final String branch,
                                   final String projectName ) {
        return new Package( createMockPath( branch,
                                            projectName ),
                            createMockPath( branch,
                                            projectName ),
                            createMockPath( branch,
                                            projectName ),
                            createMockPath( branch,
                                            projectName ),
                            createMockPath( branch,
                                            projectName ),
                            "default",
                            "default",
                            "default" );
    }

    private Path createMockPath( final String branch,
                                 final String projectName ) {

        return new Path() {
            @Override
            public String getFileName() {
                return projectName;
            }

            @Override
            public String toURI() {
                return branch + "@" + projectName;
            }

            @Override
            public int compareTo( Path o ) {
                return toURI().compareTo( o.toURI() );
            }
        };
    }

    private ProjectExplorerContentQuery getContentQuery( final Repository repository,
                                                         final String branch,
                                                         final Project project ) {
        final ProjectExplorerContentQuery projectExplorerContentQuery = new ProjectExplorerContentQuery( organizationalUnit,
                                                                                                         repository,
                                                                                                         branch,
                                                                                                         project );

        final ActiveOptions options = new ActiveOptions();
        options.add( Option.TREE_NAVIGATOR );
        options.add( Option.EXCLUDE_HIDDEN_ITEMS );
        options.add( Option.BUSINESS_CONTENT );
        projectExplorerContentQuery.setOptions( options );

        return projectExplorerContentQuery;
    }

    private GitRepository getGitRepository( final String alias ) {
        final GitRepository repository = new GitRepository( alias );
        final HashMap<String, Path> branches = new HashMap<String, Path>();
        final Path path = PathFactory.newPath( "/", "file://master@project/" );
        branches.put( "master", path );

        repository.setBranches( branches );
        return repository;
    }

}