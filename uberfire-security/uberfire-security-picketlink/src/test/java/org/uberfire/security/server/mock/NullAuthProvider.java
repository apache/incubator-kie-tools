package org.uberfire.security.server.mock;

import java.util.Map;

import org.jboss.errai.security.shared.exception.UnauthenticatedException;
import org.uberfire.security.SecurityContext;
import org.uberfire.security.auth.AuthenticationProvider;
import org.uberfire.security.auth.AuthenticationResult;
import org.uberfire.security.auth.Credential;

/**
 * An auth provider that never returns true to the {@link #supportsCredential(Credential)} call.
 */
public class NullAuthProvider implements AuthenticationProvider {

    @Override
    public void initialize( Map<String, ?> options ) {
        // no op
    }

    @Override
    public boolean supportsCredential( Credential credential ) {
        return false;
    }

    @Override
    public AuthenticationResult authenticate( Credential credential, SecurityContext securityContext ) throws UnauthenticatedException {
        throw new UnsupportedOperationException("Should not have been called");
    }

}
