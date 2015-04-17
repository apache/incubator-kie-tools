package org.uberfire.backend.vfs.impl;

import org.uberfire.backend.vfs.Path;

/**
 * Client-local event to indicate that the specified path's lock should be
 * released. This is used in admin functionality for overriding locks. The user
 * currently holding the lock will not be notified and can potentially lose
 * data.
 */
public class ForceUnlockEvent {

    private final Path path;

    public ForceUnlockEvent( Path path ) {
        this.path = path;
    }

    public Path getPath() {
        return path;
    }
}
