/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.screens.search.backend.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import javax.enterprise.inject.Instance;

import org.guvnor.common.services.backend.metadata.DublinCoreAttributesMock;
import org.guvnor.common.services.backend.metadata.VersionAttributesMock;
import org.guvnor.common.services.backend.metadata.attribute.OtherMetaView;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.organizationalunit.impl.OrganizationalUnitImpl;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.screens.search.model.QueryMetadataPageRequest;
import org.kie.workbench.common.screens.search.model.SearchPageRow;
import org.kie.workbench.common.screens.search.model.SearchTermPageRequest;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.mockito.Matchers;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.io.IOSearchService;
import org.uberfire.io.IOService;
import org.uberfire.io.attribute.DublinCoreView;
import org.uberfire.java.nio.base.version.VersionAttributeView;
import org.uberfire.java.nio.file.Path;
import org.uberfire.paging.PageResponse;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.workbench.type.ResourceTypeDefinition;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SearchServiceImplTest {

    private IOSearchService ioSearchService;

    private IOService ioService;

    private OrganizationalUnitService organizationalUnitService;

    private KieProjectService projectService;

    protected User identity;

    private AuthorizationManager authorizationManager;

    private Instance<ResourceTypeDefinition> typeRegister;

    private SearchServiceImpl searchService;

    private final OrganizationalUnit ou1 = new OrganizationalUnitImpl( "ou1",
                                                                       "owner",
                                                                       "ou1" );
    private final OrganizationalUnit ou2 = new OrganizationalUnitImpl( "ou2",
                                                                       "owner",
                                                                       "ou2" );
    private final Repository repo1 = mock( Repository.class );

    private final Repository repo2 = mock( Repository.class );

    private final KieProject project1 = mock( KieProject.class );

    @Before
    public void setup() {
        ioSearchService = mock( IOSearchService.class );
        ioService = mock( IOService.class );
        organizationalUnitService = mock( OrganizationalUnitService.class );
        projectService = mock( KieProjectService.class );
        identity = mock( User.class );
        authorizationManager = mock( AuthorizationManager.class );
        typeRegister = new MockTypeRegister();

        searchService = new SearchServiceImpl( ioSearchService,
                                               ioService,
                                               organizationalUnitService,
                                               projectService,
                                               identity,
                                               authorizationManager,
                                               typeRegister );
        searchService.init();

        final Collection<OrganizationalUnit> allOUs = new ArrayList<OrganizationalUnit>();

        final org.uberfire.backend.vfs.Path repo1Root = mock( org.uberfire.backend.vfs.Path.class );
        when( repo1Root.toURI() ).thenReturn( "file://repo1/p0" );
        final org.uberfire.backend.vfs.Path repo2Root = mock( org.uberfire.backend.vfs.Path.class );
        when( repo2Root.toURI() ).thenReturn( "file://repo2/p1" );

        when( repo1.getAlias() ).thenReturn( "repo1" );
        when( repo1.getRoot() ).thenReturn( repo1Root );

        when( repo2.getAlias() ).thenReturn( "repo2" );
        when( repo2.getRoot() ).thenReturn( repo2Root );

        ou1.getRepositories().add( repo1 );
        ou2.getRepositories().add( repo2 );

        allOUs.add( ou1 );
        allOUs.add( ou2 );

        when( organizationalUnitService.getOrganizationalUnits() ).thenReturn( allOUs );
    }

    @Test
    public void testRepositoryAccessLackOUPermissions() {
        //Setup access rights - Revoke access to OU2
        when( authorizationManager.authorize( ou1,
                                              identity ) ).thenReturn( true );
        when( authorizationManager.authorize( ou2,
                                              identity ) ).thenReturn( false );

        //Setup access rights - Grant access to all Repositories
        when( authorizationManager.authorize( repo1,
                                              identity ) ).thenReturn( true );
        when( authorizationManager.authorize( repo2,
                                              identity ) ).thenReturn( true );

        final Path[] paths = searchService.getAuthorizedRepositoryRoots();

        assertEquals( 1,
                      paths.length );
        assertEquals( "p0",
                      paths[ 0 ].getFileName().toString() );
    }

    @Test
    public void testRepositoryAccessLackRepositoryPermissions() {
        //Setup access rights - Grant access to all OUs
        when( authorizationManager.authorize( ou1,
                                              identity ) ).thenReturn( true );
        when( authorizationManager.authorize( ou2,
                                              identity ) ).thenReturn( true );

        //Setup access rights - Revoke access to repo2
        when( authorizationManager.authorize( repo1,
                                              identity ) ).thenReturn( true );
        when( authorizationManager.authorize( repo2,
                                              identity ) ).thenReturn( false );

        final Path[] paths = searchService.getAuthorizedRepositoryRoots();

        assertEquals( 1,
                      paths.length );
        assertEquals( "p0",
                      paths[ 0 ].getFileName().toString() );
    }

    @Test
    public void testFullTextSearchProjectAccess() {
        //Setup access rights - Grant access to all OUs
        when( authorizationManager.authorize( ou1,
                                              identity ) ).thenReturn( true );
        when( authorizationManager.authorize( ou2,
                                              identity ) ).thenReturn( true );

        //Setup access rights - Grant access to all Repositories
        when( authorizationManager.authorize( repo1,
                                              identity ) ).thenReturn( true );
        when( authorizationManager.authorize( repo2,
                                              identity ) ).thenReturn( true );

        //Setup access rights - Grant access to Project1
        when( authorizationManager.authorize( project1,
                                              identity ) ).thenReturn( true );

        final SearchTermPageRequest pageRequest = new SearchTermPageRequest( "smurf",
                                                                             0,
                                                                             5 );

        //Setup search
        final org.uberfire.backend.vfs.Path vfsPath = PathFactory.newPath( "file1", "default://project1/file1" );
        final Path nioPath = Paths.convert( vfsPath );
        when( ioSearchService.fullTextSearchHits( eq( "smurf" ),
                                                  Matchers.<Path>anyVararg() ) ).thenReturn( 1 );
        when( ioSearchService.fullTextSearch( eq( "smurf" ),
                                              any( Integer.class ),
                                              any( Integer.class ),
                                              Matchers.<Path>anyVararg() ) ).thenReturn( new ArrayList<Path>() {{
            add( nioPath );
        }} );

        when( projectService.resolveProject( any( org.uberfire.backend.vfs.Path.class ) ) ).thenReturn( project1 );

        final DublinCoreView dublinCoreView = mock( DublinCoreView.class );
        final OtherMetaView otherMetaView = mock( OtherMetaView.class );
        final VersionAttributeView versionAttributeView = mock( VersionAttributeView.class );
        when( dublinCoreView.readAttributes() ).thenReturn( new DublinCoreAttributesMock() );
        when( versionAttributeView.readAttributes() ).thenReturn( new VersionAttributesMock( Collections.EMPTY_LIST ) );
        when( ioService.getFileAttributeView( any( Path.class ),
                                              eq( DublinCoreView.class ) ) ).thenReturn( dublinCoreView );
        when( ioService.getFileAttributeView( any( Path.class ),
                                              eq( OtherMetaView.class ) ) ).thenReturn( otherMetaView );
        when( ioService.getFileAttributeView( any( Path.class ),
                                              eq( VersionAttributeView.class ) ) ).thenReturn( versionAttributeView );

        //Perform search
        final PageResponse<SearchPageRow> results = searchService.fullTextSearch( pageRequest );
        assertEquals( 1,
                      results.getTotalRowSize() );
        assertEquals( vfsPath.getFileName(),
                      results.getPageRowList().get( 0 ).getPath().getFileName() );
    }

    @Test
    public void testFullTextSearchProjectAccessLackPermissions() {
        //Setup access rights - Grant access to all OUs
        when( authorizationManager.authorize( ou1,
                                              identity ) ).thenReturn( true );
        when( authorizationManager.authorize( ou2,
                                              identity ) ).thenReturn( true );

        //Setup access rights - Grant access to all Repositories
        when( authorizationManager.authorize( repo1,
                                              identity ) ).thenReturn( true );
        when( authorizationManager.authorize( repo2,
                                              identity ) ).thenReturn( true );

        //Setup access rights - Revoke access to Project1
        when( authorizationManager.authorize( project1,
                                              identity ) ).thenReturn( false );

        final SearchTermPageRequest pageRequest = new SearchTermPageRequest( "smurf",
                                                                             0,
                                                                             5 );

        //Setup search
        final org.uberfire.backend.vfs.Path vfsPath = PathFactory.newPath( "file1", "default://project1/file1" );
        final Path nioPath = Paths.convert( vfsPath );
        when( ioSearchService.fullTextSearchHits( eq( "smurf" ),
                                                  Matchers.<Path>anyVararg() ) ).thenReturn( 1 );
        when( ioSearchService.fullTextSearch( eq( "smurf" ),
                                              any( Integer.class ),
                                              any( Integer.class ),
                                              Matchers.<Path>anyVararg() ) ).thenReturn( new ArrayList<Path>() {{
            add( nioPath );
        }} );

        when( projectService.resolveProject( any( org.uberfire.backend.vfs.Path.class ) ) ).thenReturn( project1 );

        final DublinCoreView dublinCoreView = mock( DublinCoreView.class );
        final OtherMetaView otherMetaView = mock( OtherMetaView.class );
        final VersionAttributeView versionAttributeView = mock( VersionAttributeView.class );
        when( dublinCoreView.readAttributes() ).thenReturn( new DublinCoreAttributesMock() );
        when( versionAttributeView.readAttributes() ).thenReturn( new VersionAttributesMock( Collections.EMPTY_LIST ) );
        when( ioService.getFileAttributeView( any( Path.class ),
                                              eq( DublinCoreView.class ) ) ).thenReturn( dublinCoreView );
        when( ioService.getFileAttributeView( any( Path.class ),
                                              eq( OtherMetaView.class ) ) ).thenReturn( otherMetaView );
        when( ioService.getFileAttributeView( any( Path.class ),
                                              eq( VersionAttributeView.class ) ) ).thenReturn( versionAttributeView );

        //Perform search
        final PageResponse<SearchPageRow> results = searchService.fullTextSearch( pageRequest );
        assertEquals( 0,
                      results.getTotalRowSize() );
    }

    @Test
    public void testMetadataSearchProjectAccess() {
        //Setup access rights - Grant access to all OUs
        when( authorizationManager.authorize( ou1,
                                              identity ) ).thenReturn( true );
        when( authorizationManager.authorize( ou2,
                                              identity ) ).thenReturn( true );

        //Setup access rights - Grant access to all Repositories
        when( authorizationManager.authorize( repo1,
                                              identity ) ).thenReturn( true );
        when( authorizationManager.authorize( repo2,
                                              identity ) ).thenReturn( true );

        //Setup access rights - Grant access to Project1
        when( authorizationManager.authorize( project1,
                                              identity ) ).thenReturn( true );

        final QueryMetadataPageRequest pageRequest = new QueryMetadataPageRequest( Collections.EMPTY_MAP,
                                                                                   null,
                                                                                   null,
                                                                                   null,
                                                                                   null,
                                                                                   0,
                                                                                   5 );

        //Setup search
        final org.uberfire.backend.vfs.Path vfsPath = PathFactory.newPath( "file1", "default://project1/file1" );
        final Path nioPath = Paths.convert( vfsPath );
        when( ioSearchService.searchByAttrsHits( any( Map.class ),
                                                 Matchers.<Path>anyVararg() ) ).thenReturn( 1 );
        when( ioSearchService.searchByAttrs( any( Map.class ),
                                             any( Integer.class ),
                                             any( Integer.class ),
                                             Matchers.<Path>anyVararg() ) ).thenReturn( new ArrayList<Path>() {{
            add( nioPath );
        }} );

        when( projectService.resolveProject( any( org.uberfire.backend.vfs.Path.class ) ) ).thenReturn( project1 );

        final DublinCoreView dublinCoreView = mock( DublinCoreView.class );
        final OtherMetaView otherMetaView = mock( OtherMetaView.class );
        final VersionAttributeView versionAttributeView = mock( VersionAttributeView.class );
        when( dublinCoreView.readAttributes() ).thenReturn( new DublinCoreAttributesMock() );
        when( versionAttributeView.readAttributes() ).thenReturn( new VersionAttributesMock( Collections.EMPTY_LIST ) );
        when( ioService.getFileAttributeView( any( Path.class ),
                                              eq( DublinCoreView.class ) ) ).thenReturn( dublinCoreView );
        when( ioService.getFileAttributeView( any( Path.class ),
                                              eq( OtherMetaView.class ) ) ).thenReturn( otherMetaView );
        when( ioService.getFileAttributeView( any( Path.class ),
                                              eq( VersionAttributeView.class ) ) ).thenReturn( versionAttributeView );

        //Perform search
        final PageResponse<SearchPageRow> results = searchService.queryMetadata( pageRequest );
        assertEquals( 1,
                      results.getTotalRowSize() );
        assertEquals( vfsPath.getFileName(),
                      results.getPageRowList().get( 0 ).getPath().getFileName() );
    }

    @Test
    public void testMetadataSearchProjectAccessLackPermissions() {
        //Setup access rights - Grant access to all OUs
        when( authorizationManager.authorize( ou1,
                                              identity ) ).thenReturn( true );
        when( authorizationManager.authorize( ou2,
                                              identity ) ).thenReturn( true );

        //Setup access rights - Grant access to all Repositories
        when( authorizationManager.authorize( repo1,
                                              identity ) ).thenReturn( true );
        when( authorizationManager.authorize( repo2,
                                              identity ) ).thenReturn( true );

        //Setup access rights - Revoke access to Project1
        when( authorizationManager.authorize( project1,
                                              identity ) ).thenReturn( false );

        final QueryMetadataPageRequest pageRequest = new QueryMetadataPageRequest( Collections.EMPTY_MAP,
                                                                                   null,
                                                                                   null,
                                                                                   null,
                                                                                   null,
                                                                                   0,
                                                                                   5 );

        //Setup search
        final org.uberfire.backend.vfs.Path vfsPath = PathFactory.newPath( "file1", "default://project1/file1" );
        final Path nioPath = Paths.convert( vfsPath );
        when( ioSearchService.searchByAttrsHits( any( Map.class ),
                                                 Matchers.<Path>anyVararg() ) ).thenReturn( 1 );
        when( ioSearchService.searchByAttrs( any( Map.class ),
                                             any( Integer.class ),
                                             any( Integer.class ),
                                             Matchers.<Path>anyVararg() ) ).thenReturn( new ArrayList<Path>() {{
            add( nioPath );
        }} );

        when( projectService.resolveProject( any( org.uberfire.backend.vfs.Path.class ) ) ).thenReturn( project1 );

        final DublinCoreView dublinCoreView = mock( DublinCoreView.class );
        final OtherMetaView otherMetaView = mock( OtherMetaView.class );
        final VersionAttributeView versionAttributeView = mock( VersionAttributeView.class );
        when( dublinCoreView.readAttributes() ).thenReturn( new DublinCoreAttributesMock() );
        when( versionAttributeView.readAttributes() ).thenReturn( new VersionAttributesMock( Collections.EMPTY_LIST ) );
        when( ioService.getFileAttributeView( any( Path.class ),
                                              eq( DublinCoreView.class ) ) ).thenReturn( dublinCoreView );
        when( ioService.getFileAttributeView( any( Path.class ),
                                              eq( OtherMetaView.class ) ) ).thenReturn( otherMetaView );
        when( ioService.getFileAttributeView( any( Path.class ),
                                              eq( VersionAttributeView.class ) ) ).thenReturn( versionAttributeView );

        //Perform search
        final PageResponse<SearchPageRow> results = searchService.queryMetadata( pageRequest );
        assertEquals( 0,
                      results.getTotalRowSize() );
    }

    @Test
    public void testFullTextSearchOutsideProjectStructure() {
        //Setup access rights - Grant access to all OUs
        when( authorizationManager.authorize( ou1,
                                              identity ) ).thenReturn( true );
        when( authorizationManager.authorize( ou2,
                                              identity ) ).thenReturn( true );

        //Setup access rights - Grant access to all Repositories
        when( authorizationManager.authorize( repo1,
                                              identity ) ).thenReturn( true );
        when( authorizationManager.authorize( repo2,
                                              identity ) ).thenReturn( true );

        //Setup access rights - Grant access to Project1
        when( authorizationManager.authorize( project1,
                                              identity ) ).thenReturn( true );

        final SearchTermPageRequest pageRequest = new SearchTermPageRequest( "smurf",
                                                                             0,
                                                                             5 );

        //Setup search
        final org.uberfire.backend.vfs.Path vfsPath = PathFactory.newPath( "file1", "default://project1/file1" );
        final Path nioPath = Paths.convert( vfsPath );
        when( ioSearchService.fullTextSearchHits( eq( "smurf" ),
                                                  Matchers.<Path>anyVararg() ) ).thenReturn( 1 );
        when( ioSearchService.fullTextSearch( eq( "smurf" ),
                                              any( Integer.class ),
                                              any( Integer.class ),
                                              Matchers.<Path>anyVararg() ) ).thenReturn( new ArrayList<Path>() {{
            add( nioPath );
        }} );

        when( projectService.resolveProject( any( org.uberfire.backend.vfs.Path.class ) ) ).thenReturn( null );

        final DublinCoreView dublinCoreView = mock( DublinCoreView.class );
        final OtherMetaView otherMetaView = mock( OtherMetaView.class );
        final VersionAttributeView versionAttributeView = mock( VersionAttributeView.class );
        when( dublinCoreView.readAttributes() ).thenReturn( new DublinCoreAttributesMock() );
        when( versionAttributeView.readAttributes() ).thenReturn( new VersionAttributesMock( Collections.EMPTY_LIST ) );
        when( ioService.getFileAttributeView( any( Path.class ),
                                              eq( DublinCoreView.class ) ) ).thenReturn( dublinCoreView );
        when( ioService.getFileAttributeView( any( Path.class ),
                                              eq( OtherMetaView.class ) ) ).thenReturn( otherMetaView );
        when( ioService.getFileAttributeView( any( Path.class ),
                                              eq( VersionAttributeView.class ) ) ).thenReturn( versionAttributeView );

        //Perform search
        final PageResponse<SearchPageRow> results = searchService.fullTextSearch( pageRequest );
        assertEquals( 1,
                      results.getTotalRowSize() );
        assertEquals( vfsPath.getFileName(),
                      results.getPageRowList().get( 0 ).getPath().getFileName() );
    }

    @Test
    public void testMetadataSearchOutsideProjectStructure() {
        //Setup access rights - Grant access to all OUs
        when( authorizationManager.authorize( ou1,
                                              identity ) ).thenReturn( true );
        when( authorizationManager.authorize( ou2,
                                              identity ) ).thenReturn( true );

        //Setup access rights - Grant access to all Repositories
        when( authorizationManager.authorize( repo1,
                                              identity ) ).thenReturn( true );
        when( authorizationManager.authorize( repo2,
                                              identity ) ).thenReturn( true );

        //Setup access rights - Grant access to Project1
        when( authorizationManager.authorize( project1,
                                              identity ) ).thenReturn( true );

        final QueryMetadataPageRequest pageRequest = new QueryMetadataPageRequest( Collections.EMPTY_MAP,
                                                                                   null,
                                                                                   null,
                                                                                   null,
                                                                                   null,
                                                                                   0,
                                                                                   5 );

        //Setup search
        final org.uberfire.backend.vfs.Path vfsPath = PathFactory.newPath( "file1", "default://project1/file1" );
        final Path nioPath = Paths.convert( vfsPath );
        when( ioSearchService.searchByAttrsHits( any( Map.class ),
                                                 Matchers.<Path>anyVararg() ) ).thenReturn( 1 );
        when( ioSearchService.searchByAttrs( any( Map.class ),
                                             any( Integer.class ),
                                             any( Integer.class ),
                                             Matchers.<Path>anyVararg() ) ).thenReturn( new ArrayList<Path>() {{
            add( nioPath );
        }} );

        when( projectService.resolveProject( any( org.uberfire.backend.vfs.Path.class ) ) ).thenReturn( null );

        final DublinCoreView dublinCoreView = mock( DublinCoreView.class );
        final OtherMetaView otherMetaView = mock( OtherMetaView.class );
        final VersionAttributeView versionAttributeView = mock( VersionAttributeView.class );
        when( dublinCoreView.readAttributes() ).thenReturn( new DublinCoreAttributesMock() );
        when( versionAttributeView.readAttributes() ).thenReturn( new VersionAttributesMock( Collections.EMPTY_LIST ) );
        when( ioService.getFileAttributeView( any( Path.class ),
                                              eq( DublinCoreView.class ) ) ).thenReturn( dublinCoreView );
        when( ioService.getFileAttributeView( any( Path.class ),
                                              eq( OtherMetaView.class ) ) ).thenReturn( otherMetaView );
        when( ioService.getFileAttributeView( any( Path.class ),
                                              eq( VersionAttributeView.class ) ) ).thenReturn( versionAttributeView );

        //Perform search
        final PageResponse<SearchPageRow> results = searchService.queryMetadata( pageRequest );
        assertEquals( 1,
                      results.getTotalRowSize() );
        assertEquals( vfsPath.getFileName(),
                      results.getPageRowList().get( 0 ).getPath().getFileName() );
    }

}
