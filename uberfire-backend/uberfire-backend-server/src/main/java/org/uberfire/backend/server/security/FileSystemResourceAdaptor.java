package org.uberfire.backend.server.security;

import java.util.Collection;
import java.util.Collections;

import org.uberfire.java.nio.base.FileSystemId;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.security.Resource;
import org.uberfire.security.authz.RuntimeResource;

public class FileSystemResourceAdaptor implements RuntimeResource {

    private final FileSystem fileSystem;

    public FileSystemResourceAdaptor( FileSystem fileSystem ) {
        this.fileSystem = fileSystem;
    }

    public FileSystem getFileSystem() {
        return fileSystem;
    }

    @Override
    public String getSignatureId() {
        if (fileSystem instanceof FileSystemId) {
            return ((FileSystemId) fileSystem).id();
        }
        return fileSystem.toString();
    }

    @Override
    public Collection<String> getRoles() {

        return Collections.emptyList();
    }

    @Override
    public Collection<String> getTraits() {
        return Collections.emptyList();
    }
}
