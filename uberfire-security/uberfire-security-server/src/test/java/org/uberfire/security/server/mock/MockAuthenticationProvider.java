package org.uberfire.security.server.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.uberfire.security.Role;
import org.uberfire.security.SecurityContext;
import org.uberfire.security.auth.AuthenticationException;
import org.uberfire.security.auth.AuthenticationProvider;
import org.uberfire.security.auth.AuthenticationResult;
import org.uberfire.security.auth.AuthenticationStatus;
import org.uberfire.security.auth.Credential;
import org.uberfire.security.auth.Principal;
import org.uberfire.security.impl.IdentityImpl;
import org.uberfire.security.impl.RoleImpl;
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
    public AuthenticationResult authenticate( final Credential credential, final SecurityContext securityContext ) throws AuthenticationException {
        return new AuthenticationResult() {

            @Override
            public AuthenticationStatus getStatus() {
                return AuthenticationStatus.SUCCESS;
            }

            @Override
            public Principal getPrincipal() {
                List<Role> roles = new ArrayList<Role>();
                String userName = ((UserNameCredential) credential).getUserName();
                roles.add( new RoleImpl( userName ) );
                return new IdentityImpl( userName, roles );
            }

            @Override
            public List<String> getMessages() {
                throw new UnsupportedOperationException( "Not implemented." );
            }
        };
    }

}
