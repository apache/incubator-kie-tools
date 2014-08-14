package org.uberfire.java.nio.security;

/**
 * Authentication strategy used by secured file system implementations such as Git.
 */
public interface FileSystemAuthenticator {

    /**
     * Attempts to authenticate with the target filesystem using the given filesystem username and password.
     *
     * @param username
     *            the filesystem username to authenticate as. Must not be null.
     * @param password
     *            the given user's password. Can be null if the target filesystem supports null passwords.
     * @return the filesystem user if authentication was successful; null if authentication failed.
     */
    FileSystemUser authenticate( final String username,
                          final String password );
}
