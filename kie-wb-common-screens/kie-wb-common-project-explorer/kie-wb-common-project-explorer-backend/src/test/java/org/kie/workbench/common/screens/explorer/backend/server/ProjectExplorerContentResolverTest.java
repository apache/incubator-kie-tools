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
import org.guvnor.structure.repositories.impl.git.GitRepository;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Ignore;
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

public class ProjectExplorerContentResolverTest {

    protected final SimpleFileSystemProvider fs = new SimpleFileSystemProvider();
    private ProjectExplorerContentResolver resolver;
    private OrganizationalUnit organizationalUnit;
    private GitRepository repository;
    private Set<Project> masterProjects;
    private Set<Project> devProjects;

    private HelperWrapper helperWrapper;

    @Before
    public void setUp() throws Exception {

        //Ensure URLs use the default:// scheme
        fs.forceAsDefault();

        KieProjectService projectService = mock( KieProjectService.class );
        ExplorerServiceHelper helper = mock( ExplorerServiceHelper.class );
        AuthorizationManager authorizationManager = mock( AuthorizationManager.class );
        OrganizationalUnitService organizationalUnitService = mock( OrganizationalUnitService.class );
        ExplorerServiceHelper explorerServiceHelper = mock( ExplorerServiceHelper.class );

        repository = getGitRepository();

        organizationalUnit = spy( new OrganizationalUnitImpl( "demo", "demo", "demo" ) );
        organizationalUnit.getRepositories().add( repository );
        ArrayList<OrganizationalUnit> organizationalUnits = new ArrayList<OrganizationalUnit>();
        organizationalUnits.add( organizationalUnit );

        UserExplorerData userExplorerData = new UserExplorerData();

        userExplorerData.setOrganizationalUnit( organizationalUnit );

        masterProjects = new HashSet<Project>();
        masterProjects.add( createProject( "master", "project 1" ) );
        masterProjects.add( createProject( "master", "project 2" ) );

        devProjects = new HashSet<Project>();
        devProjects.add( createProject( "dev-1.0.0", "project 1" ) );
        devProjects.add( createProject( "dev-1.0.0", "project 2" ) );

        helperWrapper = new HelperWrapper( helper );

        when( helper.getLastContent() ).thenAnswer( new Answer<Object>() {
            @Override
            public Object answer( InvocationOnMock invocationOnMock ) throws Throwable {
                return helperWrapper.getUserExplorerLastData();
            }
        } );
        when( helper.loadUserContent() ).thenReturn( userExplorerData );
        when(
                helper.getFolderListing( any( FolderItem.class ), any( Project.class ), any( Package.class ), any( ActiveOptions.class ) )
            ).thenReturn(
                new FolderListing( getFileItem(), Collections.EMPTY_LIST, Collections.EMPTY_LIST )
                        );

        when( organizationalUnitService.getOrganizationalUnits() ).thenReturn( organizationalUnits );
        when( organizationalUnitService.getOrganizationalUnit( "demo" ) ).thenReturn( organizationalUnit );
        when( authorizationManager.authorize( any( OrganizationalUnit.class ), any( User.class ) ) ).thenReturn( true );
        when( authorizationManager.authorize( any( Project.class ), any( User.class ) ) ).thenReturn( true );
        when( projectService.getProjects( repository, "master" ) ).thenReturn( masterProjects );
        when( projectService.getProjects( repository, "dev-1.0.0" ) ).thenReturn( devProjects );
        when( projectService.resolveDefaultPackage( any( Project.class ) ) ).thenReturn( new Package() );

        resolver = new ProjectExplorerContentResolver(
                projectService,
                helper,
                authorizationManager,
                organizationalUnitService,
                explorerServiceHelper );
    }

    private FolderItem getFileItem() {
        Path path = mock( Path.class );
        return new FolderItem( path, "someitem", FolderItemType.FILE );
    }

    @Test
    public void testResolveNullQueryBusinessView() throws Exception {
        final ProjectExplorerContentQuery query = new ProjectExplorerContentQuery();
        final ActiveOptions options = new ActiveOptions();
        options.add( Option.TREE_NAVIGATOR );
        options.add( Option.EXCLUDE_HIDDEN_ITEMS );
        options.add( Option.BUSINESS_CONTENT );
        query.setOptions( options );
        resolver.resolve( query );
    }

    @Test
    public void testResolveNullQueryTechnicalView() throws Exception {
        final ProjectExplorerContentQuery query = new ProjectExplorerContentQuery();
        final ActiveOptions options = new ActiveOptions();
        options.add( Option.TREE_NAVIGATOR );
        options.add( Option.EXCLUDE_HIDDEN_ITEMS );
        options.add( Option.TECHNICAL_CONTENT );
        query.setOptions( options );
        resolver.resolve( query );
    }

    @Test
    public void testResolveWithOUNullQueryBusinessView() throws Exception {
        final ProjectExplorerContentQuery query = new ProjectExplorerContentQuery( organizationalUnit );
        final ActiveOptions options = new ActiveOptions();
        options.add( Option.TREE_NAVIGATOR );
        options.add( Option.EXCLUDE_HIDDEN_ITEMS );
        options.add( Option.BUSINESS_CONTENT );
        query.setOptions( options );
        resolver.resolve( query );
    }

    @Test
    public void testResolveWithOUNullQueryTechnicalView() throws Exception {
        final ProjectExplorerContentQuery query = new ProjectExplorerContentQuery( organizationalUnit );
        final ActiveOptions options = new ActiveOptions();
        options.add( Option.TREE_NAVIGATOR );
        options.add( Option.EXCLUDE_HIDDEN_ITEMS );
        options.add( Option.TECHNICAL_CONTENT );
        query.setOptions( options );
        resolver.resolve( query );
    }

    @Test
    public void testResolveWithOUWithRepositoryNullQueryBusinessView() throws Exception {
        final ProjectExplorerContentQuery query = new ProjectExplorerContentQuery( organizationalUnit,
                                                                                   getGitRepository(),
                                                                                   "master" );
        final ActiveOptions options = new ActiveOptions();
        options.add( Option.TREE_NAVIGATOR );
        options.add( Option.EXCLUDE_HIDDEN_ITEMS );
        options.add( Option.BUSINESS_CONTENT );
        query.setOptions( options );
        resolver.resolve( query );
    }

    @Test
    public void testResolveWithOUWithRepositoryNullQueryTechnicalView() throws Exception {
        final ProjectExplorerContentQuery query = new ProjectExplorerContentQuery( organizationalUnit,
                                                                                   getGitRepository(),
                                                                                   "master" );
        final ActiveOptions options = new ActiveOptions();
        options.add( Option.TREE_NAVIGATOR );
        options.add( Option.EXCLUDE_HIDDEN_ITEMS );
        options.add( Option.TECHNICAL_CONTENT );
        query.setOptions( options );
        resolver.resolve( query );
    }

    @Test
    public void testChangeProjectOnBusinessView() throws Exception {

        ProjectExplorerContent content = resolver.resolve( getContentQuery( "master", createProject( "master", "project 1" ), Option.BUSINESS_CONTENT ) );
        helperWrapper.reset();

        assertEquals( "master", content.getBranch() );
        assertNotNull( content.getProject() ); // This will be the default project
        assertEquals( "master@project 1", content.getProject().getRootPath().toURI() );

        content = resolver.resolve( getContentQuery( "dev-1.0.0", createProject( "dev-1.0.0", "project 1" ), Option.BUSINESS_CONTENT ) );
        helperWrapper.reset();

        assertEquals( "dev-1.0.0", content.getBranch() );
        assertEquals( "project 1", content.getProject().getProjectName() );
        assertEquals( "dev-1.0.0@project 1", content.getProject().getRootPath().toURI() );

        content = resolver.resolve( getContentQuery( "dev-1.0.0", createProject( "dev-1.0.0", "project 2" ), Option.BUSINESS_CONTENT ) );
        helperWrapper.reset();

        assertEquals( "dev-1.0.0", content.getBranch() );
        assertEquals( "project 2", content.getProject().getProjectName() );
        assertEquals( "dev-1.0.0@project 2", content.getProject().getRootPath().toURI() );

        content = resolver.resolve( getContentQuery( "master", createProject( "master", "project 2" ), Option.BUSINESS_CONTENT ) );
        helperWrapper.reset();

        assertEquals( "master", content.getBranch() );
        assertEquals( "project 2", content.getProject().getProjectName() );
        assertEquals( "master@project 2", content.getProject().getRootPath().toURI() );

    }

    @Test
    public void testChangeProjectOnTechnicalView() {

        ProjectExplorerContent projectExplorerContent = resolver.resolve( getContentQuery( "master", createProject( "master", "project 1" ), Option.TECHNICAL_CONTENT ) );
        helperWrapper.reset();

        Content content = resolver.setupSelectedItems( getContentQuery( "master", createProject( "master", "project 2" ), Option.TECHNICAL_CONTENT, null, getFileItem() ) );
        helperWrapper.reset();

        assertEquals( "demo", content.getSelectedOrganizationalUnit().getName() );
        assertEquals( "master", content.getSelectedBranch() );
        assertEquals( "master@project 2", content.getSelectedProject().getRootPath().toURI() );
        assertNull( content.getSelectedItem() );
        assertNull( content.getSelectedPackage() );
    }

    @Test
    public void testChangeProjectOnTechnicalViewWhenThereIsAFolderItemButNoActiveProjectOrganizationalUnitOrRepository() {


        ProjectExplorerContent projectExplorerContent = resolver.resolve( getContentQuery( "master", createProject( "master", "project 1" ), Option.TECHNICAL_CONTENT ) );
        helperWrapper.reset();


        ProjectExplorerContentQuery projectExplorerContentQuery = new ProjectExplorerContentQuery(
                null,
                null,
                null,
                null,
                null,
                getFileItem()
        );

        ActiveOptions options = new ActiveOptions();
        options.add( Option.TECHNICAL_CONTENT );
        projectExplorerContentQuery.setOptions( options );

        helperWrapper.excludePackage();
        Content content = resolver.setupSelectedItems( projectExplorerContentQuery );
        helperWrapper.reset();

        assertEquals( "demo", content.getSelectedOrganizationalUnit().getName() );
        assertEquals( "master", content.getSelectedBranch() );
        assertEquals( "master@project 1", content.getSelectedProject().getRootPath().toURI() );
        assertNotNull( content.getSelectedItem() );
        assertNull( content.getSelectedPackage() );
    }

    @Test
    public void testChangeFromBusinessToTechnicalView() {
        resolver.resolve( getContentQuery( "master", createProject( "master", "project 1" ), Option.BUSINESS_CONTENT ) );
        helperWrapper.reset();

        Content content = resolver.setupSelectedItems( getContentQuery( "master", createProject( "master", "project 1" ), Option.TECHNICAL_CONTENT, null, getFileItem() ) );

        assertEquals( "demo", content.getSelectedOrganizationalUnit().getName() );
        assertEquals( "master", content.getSelectedBranch() );
        assertEquals( "master@project 1", content.getSelectedProject().getRootPath().toURI() );
        assertNull( content.getSelectedItem() );
        assertNull( content.getSelectedPackage() );
    }

    private Project createProject( final String branch,
                                   final String projectName ) {
        return new Project( createMockPath( branch, projectName ), createMockPath( branch, projectName ), projectName );
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

    @Test
    @Ignore
    public void testBranchChange() throws Exception {

//        getContentQuery("dev-1.0.0");
//
//        UserExplorerLastData userExplorerLastData = new UserExplorerLastData();
//        userExplorerLastData.setPackage(organizationalUnit, getGitRepository("master"), null, null);
//
//
//        when(authorizationManager.authorize(any(Resource.class), any(User.class))).thenReturn(true);
//
//        when(helper.getLastContent()).thenReturn(userExplorerLastData);
//
//        when(helper.loadUserContent()).thenReturn(new UserExplorerData());
//
//        when(ioService.newDirectoryStream(any(org.uberfire.java.nio.file.Path.class))).thenReturn(new DirectoryStream<org.uberfire.java.nio.file.Path>() {
//            @Override
//            public void close() throws IOException {
//
//            }
//
//            @Override
//            public Iterator<org.uberfire.java.nio.file.Path> iterator() {
//                return Collections.<org.uberfire.java.nio.file.Path>emptyList().iterator();
//            }
//        });
//
//        when(organizationalUnitService.getOrganizationalUnit(anyString())).thenReturn(organizationalUnit);
//        ArrayList<Repository> repositories = new ArrayList<Repository>();
//        repositories.add(getGitRepository("master"));
//        when(organizationalUnit.getRepositories()).thenReturn(repositories);
//
//        ProjectExplorerContent content = resolver.resolve(contentQuery);
//        assertEquals("dev-1.0.0", content.getRepository().getCurrentBranch());
    }

    private ProjectExplorerContentQuery getContentQuery( final String branchName,
                                                         final Project project,
                                                         final Option content ) {
        return getContentQuery( branchName, project, content, null, null );
    }

    private ProjectExplorerContentQuery getContentQuery( final String branchName,
                                                         final Project project,
                                                         final Option content,
                                                         final Package pkg,
                                                         final FolderItem item ) {

        ProjectExplorerContentQuery projectExplorerContentQuery = new ProjectExplorerContentQuery(
                organizationalUnit,
                getGitRepository(),
                branchName,
                project,
                pkg,
                item
        );

        ActiveOptions options = new ActiveOptions();
        options.add( Option.TREE_NAVIGATOR );
        options.add( Option.EXCLUDE_HIDDEN_ITEMS );
        options.add( content );
        projectExplorerContentQuery.setOptions( options );

        return projectExplorerContentQuery;
    }

    private GitRepository getGitRepository() {
        GitRepository repository = new GitRepository();

        HashMap<String, Path> branches = new HashMap<String, Path>();
        Path pathToMaster = PathFactory.newPath( "/", "file://master@project/" );
        branches.put( "master", pathToMaster );
        Path pathToDev = PathFactory.newPath( "/", "file://dev-1.0.0@project/" );
        branches.put( "dev-1.0.0", pathToDev );

        repository.setRoot( pathToMaster );
        repository.setBranches( branches );
        return repository;
    }
}