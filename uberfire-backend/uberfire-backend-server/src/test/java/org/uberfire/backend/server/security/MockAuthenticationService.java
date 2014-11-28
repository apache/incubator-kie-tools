package org.uberfire.backend.server.security;

import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.api.identity.UserImpl;
import org.jboss.errai.security.shared.service.AuthenticationService;


public class MockAuthenticationService implements AuthenticationService {

    public static final User FAKE_USER = new UserImpl( "fake" );
    
    @Override
    public User login( String username, String password ) {
        return FAKE_USER;
    }

    @Override
    public boolean isLoggedIn() {
        return true;
    }

    @Override
    public void logout() {
    }

    @Override
    public User getUser() {
        return FAKE_USER;
    }

}
