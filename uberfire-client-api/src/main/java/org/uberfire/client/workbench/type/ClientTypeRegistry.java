package org.uberfire.client.workbench.type;

import java.util.Collection;

import org.uberfire.backend.vfs.Path;

public interface ClientTypeRegistry {

    Collection<ClientResourceType> getRegisteredTypes();

    ClientResourceType resolve( final Path path );

    String resolveWildcardPattern( final String shortName );

}
