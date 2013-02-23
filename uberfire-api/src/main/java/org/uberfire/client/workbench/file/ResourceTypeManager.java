package org.uberfire.client.workbench.file;

import java.util.Collection;

import org.uberfire.backend.vfs.Path;

public interface ResourceTypeManager {

    Collection<ResourceType> getRegisteredTypes();

    ResourceType resolve( Path path );
}
