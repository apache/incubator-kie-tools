package org.uberfire.security.server.mock;

import org.uberfire.security.SecurityContext;
import org.uberfire.security.auth.AuthenticationException;
import org.uberfire.security.auth.AuthenticationScheme;
import org.uberfire.security.auth.Credential;
import org.uberfire.security.impl.auth.UsernamePasswordCredential;

/**
 * Treats every request as a login attempt for a user called "test-user" with password
 * "test-password".
 * 
 * @author jfuerth
 */
public class MockAuthenticationScheme implements AuthenticationScheme {

    @Override
    public boolean isAuthenticationRequest( SecurityContext context ) {
        System.out.println( "MockSuccessfulAuthenticationScheme is claiming this is an auth request!" );
        return true;
    }

    @Override
    public void challengeClient( SecurityContext context ) {
        // no op
    }

    @Override
    public Credential buildCredential( SecurityContext context ) throws AuthenticationException {
        System.out.println( "MockSuccessfulAuthenticationScheme is building a credential!" );
        return new UsernamePasswordCredential( "test-user", "test-password" );
    }

}
