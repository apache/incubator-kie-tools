package org.uberfire.client.mvp;

import org.uberfire.backend.vfs.Path;

/**
 * Client-local event to indicate that a save command has been executed
 */
public class SaveInProgressEvent {

    private final Path path;
    
    public SaveInProgressEvent(Path path) {
        this.path = path;
    }

    public Path getPath() {
        return path;
    }
    
}
