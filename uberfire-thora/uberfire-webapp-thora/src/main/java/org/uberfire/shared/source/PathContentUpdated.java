package org.uberfire.shared.source;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;

@Portable
public class PathContentUpdated {

    private Path path;

    public PathContentUpdated() {
    }

    public PathContentUpdated( Path path ) {
        this.path = path;
    }

    public Path getPath() {
        return path;
    }
}
