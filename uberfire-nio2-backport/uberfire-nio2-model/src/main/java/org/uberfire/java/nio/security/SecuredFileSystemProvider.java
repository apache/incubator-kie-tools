package org.uberfire.java.nio.security;

import org.uberfire.java.nio.file.spi.FileSystemProvider;

/**
 * Specialization of {@link FileSystemProvider} for file systems that require username/password authentication and
 * support authorization of certain actions.
 */
public interface SecuredFileSystemProvider extends FileSystemProvider {

    /**
     * Sets the authenticator that decides which username/password pairs are valid for the file systems managed by this
     * provider.
     *
     * @param authenticator The authenticator to use. Must not be null.
     */
    void setAuthenticator( final FileSystemAuthenticator authenticator );

    void setAuthorizer( final FileSystemAuthorizer authorizer );

}
