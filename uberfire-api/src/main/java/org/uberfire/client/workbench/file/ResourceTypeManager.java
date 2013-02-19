package org.uberfire.client.workbench.file;

import org.uberfire.backend.vfs.Path;

public interface ResourceTypeManager {

    ResourceType resolve( Path path );
}
