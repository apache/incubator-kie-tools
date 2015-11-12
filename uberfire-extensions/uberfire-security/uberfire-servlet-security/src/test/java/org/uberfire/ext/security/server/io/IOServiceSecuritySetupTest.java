package org.uberfire.ext.security.server.io;

import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.exception.UnauthorizedException;
import org.junit.Test;
import org.uberfire.backend.server.security.FileSystemResourceAdaptor;
import org.uberfire.commons.lifecycle.PriorityDisposableRegistry;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.security.Resource;
import org.uberfire.security.authz.AuthorizationManager;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class IOServiceSecuritySetupTest {

    @Test
    public void nonSecureExecuted() {
        final FileSystem fs = mock( FileSystem.class );
        final IOSecurityService service = new IOSecurityService( new MockIOService(), new MockAuthenticationService(), new AuthorizationManager() {
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
        final IOSecurityService service = new IOSecurityService( new MockIOService(), new MockAuthenticationService(), new AuthorizationManager() {
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
