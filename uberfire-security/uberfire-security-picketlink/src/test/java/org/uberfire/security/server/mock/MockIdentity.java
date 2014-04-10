package org.uberfire.security.server.mock;

import java.io.Serializable;

import org.picketlink.Identity;
import org.picketlink.authentication.AuthenticationException;
import org.picketlink.credential.DefaultLoginCredentials;
import org.picketlink.idm.model.Account;
import org.picketlink.idm.model.basic.User;


public class MockIdentity implements Identity {

    /**
     * When false, all login attempts fail. When true, login attempts (where credentials returns a non-null credential)
     * always succeed.
     */
    private boolean allowsLogins = true;

    private Account account = null;

    private DefaultLoginCredentials credentials;

    public MockIdentity() {
    }

    @Override
    public boolean isLoggedIn() {
        return account != null;
    }

    @Override
    public Account getAccount() {
        return account;
    }

    @Override
    public AuthenticationResult login() throws AuthenticationException {
        if (allowsLogins && credentials.getCredential() != null) {
            account = new User(credentials.getUserId());
            return AuthenticationResult.SUCCESS;
        }
        return AuthenticationResult.FAILED;
    }

    @Override
    public void logout() {
        account = null;
    }

    @Override
    public boolean hasPermission( Object resource, String operation ) {
        return false;
    }

    @Override
    public boolean hasPermission( Class<?> resourceClass, Serializable identifier, String operation ) {
        return false;
    }

    public void setAllowsLogins( boolean allowsLogins ) {
        this.allowsLogins = allowsLogins;
    }

    public void setCredentials( DefaultLoginCredentials credentials ) {
        this.credentials = credentials;
    }

    /**
     * Forces this identity into the "logged in" or "logged out" state.
     * 
     * @param user
     *            the user who this Identity will claim is logged in, or null to force "not logged in".
     */
    public void setLoggedInUser( User user ) {
        account = user;
    }
}
