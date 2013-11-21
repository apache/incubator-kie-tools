package org.uberfire.java.nio.security;

import org.uberfire.security.auth.AuthenticationManager;
import org.uberfire.security.authz.AuthorizationManager;

public interface SecurityAware {

    void setAuthenticationManager( final AuthenticationManager authenticationManager );

    void setAuthorizationManager( final AuthorizationManager authorizationManager );

}
