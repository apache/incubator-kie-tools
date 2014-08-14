package org.uberfire.java.nio.security;

import org.uberfire.java.nio.file.FileSystem;

/**
 * Strategy for authorizing users to perform actions in a secured file system.
 */
public interface FileSystemAuthorizer {

    /**
     * Returns true if the given user is permitted to perform actions within the given file system.
     *
     * @param fs
     * @param fileSystemUser
     * @return
     */
    boolean authorize( final FileSystem fs,
                       final FileSystemUser fileSystemUser );

}
