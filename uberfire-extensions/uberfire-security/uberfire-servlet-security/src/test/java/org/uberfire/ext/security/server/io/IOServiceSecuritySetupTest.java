/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.security.server.io;

import java.util.Arrays;

import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.exception.UnauthorizedException;
import org.junit.Test;
import org.uberfire.backend.server.security.FileSystemResourceAdaptor;
import org.uberfire.commons.lifecycle.PriorityDisposableRegistry;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;
import org.uberfire.security.Resource;
import org.uberfire.security.authz.AuthorizationManager;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class IOServiceSecuritySetupTest {

    @Test
    public void nonSecureExecuted() {
        final FileSystem fs = mock( FileSystem.class );
        final Path rootPath = mock( Path.class );

        when( fs.getRootDirectories() ).thenReturn( Arrays.asList( rootPath ) );
        when( rootPath.getFileSystem() ).thenReturn( fs );

        final IOSecurityService service = new IOSecurityService( new MockIOService(),
                                                                 new MockAuthenticationService(),
                                                                 new AuthorizationManager() {
                                                                     @Override
                                                                     public boolean supports( Resource resource ) {
                                                                         return resource instanceof FileSystemResourceAdaptor;
                                                                     }

                                                                     @Override
                                                                     public boolean authorize( Resource resource,
                                                                                               User user ) throws UnauthorizedException {
                                                                         return true;
                                                                     }
                                                                 } );

        assertTrue( PriorityDisposableRegistry.getDisposables().contains( service ) );

        try {
            service.startBatch( fs );
        } catch ( Exception e ) {
            fail( "error" );
        }
    }

    @Test
    public void secureExecuted() {
        final FileSystem fs = mock( FileSystem.class );
        final Path rootPath = mock( Path.class );

        when( fs.getRootDirectories() ).thenReturn( Arrays.asList( rootPath ) );
        when( rootPath.getFileSystem() ).thenReturn( fs );

        final IOSecurityService service = new IOSecurityService( new MockIOService(),
                                                                 new MockAuthenticationService(),
                                                                 new AuthorizationManager() {
                                                                     @Override
                                                                     public boolean supports( Resource resource ) {
                                                                         return resource instanceof FileSystemResourceAdaptor;
                                                                     }

                                                                     @Override
                                                                     public boolean authorize( Resource resource,
                                                                                               User user ) throws UnauthorizedException {
                                                                         return false;
                                                                     }
                                                                 } );

        try {
            service.startBatch( fs );
            fail( "error" );
        } catch ( SecurityException e ) {
        } catch ( Exception e ) {
            fail( "error" );
        }
    }

}
