package org.uberfire.security.server;

import org.uberfire.security.Resource;

public class UserPassSecurityContext extends MapSecurityContext {

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    public UserPassSecurityContext( final Resource resource,
                                    final String username,
                                    final String password ) {
        super( resource );
        content.put( USERNAME, username );
        content.put( PASSWORD, password );
    }

    public void setUsername( final String username ) {
        content.put( USERNAME, username );
    }

    public void setPassword( final String password ) {
        content.put( PASSWORD, password );
    }

    public String getUsername() {
        return content.get( USERNAME ).toString();
    }

    public String getPassword() {
        return content.get( PASSWORD ).toString();
    }

}
