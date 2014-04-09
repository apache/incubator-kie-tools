package org.uberfire.security.server.mock;

import org.jboss.errai.security.shared.exception.UnauthenticatedException;
import org.uberfire.security.SecurityContext;
import org.uberfire.security.auth.AuthenticationScheme;
import org.uberfire.security.auth.Credential;

/**
 * An auth scheme that never returns true to the {@link #isAuthenticationRequest(SecurityContext)} call.
 */
public class NullAuthScheme implements AuthenticationScheme {

    @Override
    public boolean isAuthenticationRequest( SecurityContext context ) {
        return false;
    }

    @Override
    public void challengeClient( SecurityContext context ) {
        throw new UnsupportedOperationException("Should not have been called");
    }

    @Override
    public Credential buildCredential( SecurityContext context ) throws UnauthenticatedException {
        throw new UnsupportedOperationException("Should not have been called");
    }

}
