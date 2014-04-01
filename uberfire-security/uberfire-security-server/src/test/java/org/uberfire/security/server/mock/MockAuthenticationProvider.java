package org.uberfire.security.server.mock;

import java.util.List;
import java.util.Map;

import org.jboss.errai.security.shared.exception.UnauthenticatedException;
import org.uberfire.security.SecurityContext;
import org.uberfire.security.auth.AuthenticationProvider;
import org.uberfire.security.auth.AuthenticationResult;
import org.uberfire.security.auth.AuthenticationStatus;
import org.uberfire.security.auth.Credential;
import org.uberfire.security.auth.Principal;
import org.uberfire.security.impl.auth.PrincipalImpl;
import org.uberfire.security.impl.auth.UserNameCredential;


public class MockAuthenticationProvider implements AuthenticationProvider {

    @Override
    public void initialize( Map<String, ?> options ) {
        System.out.println( "MockAuthenticationProvider got options " + options );
    }

    @Override
    public boolean supportsCredential( Credential credential ) {
        return true;
    }

    @Override
    public AuthenticationResult authenticate( final Credential credential, final SecurityContext securityContext ) throws UnauthenticatedException {
        return new AuthenticationResult() {

            @Override
            public AuthenticationStatus getStatus() {
                return AuthenticationStatus.SUCCESS;
            }

            @Override
            public Principal getPrincipal() {
                return new PrincipalImpl(((UserNameCredential) credential).getUserName());
            }

            @Override
            public List<String> getMessages() {
                throw new UnsupportedOperationException( "Not implemented." );
            }
        };
    }

}
