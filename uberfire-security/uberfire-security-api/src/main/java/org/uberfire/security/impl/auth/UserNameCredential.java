package org.uberfire.security.impl.auth;

import org.uberfire.security.auth.Credential;

public class UserNameCredential implements Credential {
    private final String userName;

    public UserNameCredential(final String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }
}
