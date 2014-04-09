package org.uberfire.java.nio.security;

import org.jboss.errai.security.shared.service.AuthenticationService;
import org.uberfire.security.authz.AuthorizationManager;

public interface SecurityAware {

    void setAuthenticationManager( final AuthenticationService authenticationService );

    void setAuthorizationManager( final AuthorizationManager authorizationManager );

}
