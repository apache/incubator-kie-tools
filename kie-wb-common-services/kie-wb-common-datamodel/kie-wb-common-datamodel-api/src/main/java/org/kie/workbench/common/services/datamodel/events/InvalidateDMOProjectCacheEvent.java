package org.kie.workbench.common.services.datamodel.events;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.commons.validation.PortablePreconditions;
import org.uberfire.backend.vfs.Path;

/**
 * Event to invalidate all entries in a DataModelOracleCache for the Project containing the given resource.
 * The resource path is used within the Event as all editors that could affect the validity of a DataModelOracleCache
 * entry will know their resource's Path but not the Project path without performing a server round-trip to resolve such.
 */
@Portable
public class InvalidateDMOProjectCacheEvent {

    private Path resourcePath;

    public InvalidateDMOProjectCacheEvent() {
    }

    public InvalidateDMOProjectCacheEvent( final Path resourcePath ) {
        PortablePreconditions.checkNotNull( "resourcePath",
                                            resourcePath );
        this.resourcePath = resourcePath;
    }

    public Path getResourcePath() {
        return this.resourcePath;
    }

}
