/*
 * Copyright 2015 JBoss Inc
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
import javax.enterprise.inject.Instance;

import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.organizationalunit.impl.OrganizationalUnitImpl;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.uberfire.io.IOSearchService;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.workbench.type.ResourceTypeDefinition;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SearchServiceImplTest {

    private IOSearchService ioSearchService;

    private IOService ioService;

    private OrganizationalUnitService organizationalUnitService;

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

    @Before
    public void setup() {
        ioSearchService = mock( IOSearchService.class );
        ioService = mock( IOService.class );
        organizationalUnitService = mock( OrganizationalUnitService.class );
        identity = mock( User.class );
        authorizationManager = mock( AuthorizationManager.class );
        typeRegister = new MockTypeRegister();

        searchService = new SearchServiceImpl( ioSearchService,
                                               ioService,
                                               organizationalUnitService,
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

}
