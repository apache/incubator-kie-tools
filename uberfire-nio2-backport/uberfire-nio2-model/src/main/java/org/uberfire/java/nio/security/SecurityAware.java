package org.uberfire.java.nio.security;

public interface SecurityAware {

    void setUserPassAuthenticator( final UserPassAuthenticator authenticator );

    void setAuthorizationManager( final AuthorizationManager authorizationManager );

}
