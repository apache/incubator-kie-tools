package org.uberfire.java.nio.security;

public interface UserPassAuthenticator {

    boolean authenticate( final String username,
                          final String password,
                          final Session session );
}
