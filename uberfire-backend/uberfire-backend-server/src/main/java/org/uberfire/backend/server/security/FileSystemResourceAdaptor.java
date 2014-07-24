package org.uberfire.backend.server.security;

import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.security.Resource;

public class FileSystemResourceAdaptor implements Resource {

    private final FileSystem fileSystem;

    public FileSystemResourceAdaptor( FileSystem fileSystem ) {
        this.fileSystem = fileSystem;
    }

    public FileSystem getFileSystem() {
        return fileSystem;
    }
}
