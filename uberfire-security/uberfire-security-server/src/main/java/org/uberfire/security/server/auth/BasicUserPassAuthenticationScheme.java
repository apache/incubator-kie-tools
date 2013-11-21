package org.uberfire.security.server.auth;

import org.uberfire.security.SecurityContext;
import org.uberfire.security.auth.AuthenticationScheme;
import org.uberfire.security.auth.Credential;
import org.uberfire.security.impl.auth.UsernamePasswordCredential;
import org.uberfire.security.server.UserPassSecurityContext;

import static org.uberfire.commons.validation.Preconditions.*;

public class BasicUserPassAuthenticationScheme implements AuthenticationScheme {

    @Override
    public boolean isAuthenticationRequest( SecurityContext context ) {
        return true;
    }

    @Override
    public void challengeClient( SecurityContext context ) {
    }

    @Override
    public Credential buildCredential( SecurityContext context ) {

        final UserPassSecurityContext basicSecurityContext = checkInstanceOf( "context", context, UserPassSecurityContext.class );

        return new UsernamePasswordCredential( basicSecurityContext.getUsername(), basicSecurityContext.getPassword() );
    }
}
