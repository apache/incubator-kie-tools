package org.kie.workbench.common.services.builder;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;

import org.kie.commons.io.IOService;
import org.kie.commons.validation.PortablePreconditions;
import org.kie.workbench.common.services.datamodel.events.InvalidateDMOProjectCacheEvent;
import org.kie.workbench.common.services.project.service.model.POM;
import org.kie.workbench.common.services.project.service.POMService;
import org.kie.workbench.common.services.project.service.ProjectService;
import org.kie.workbench.common.services.backend.cache.LRUCache;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

/**
 * A simple LRU cache for Builders
 */
@ApplicationScoped
public class LRUBuilderCache extends LRUCache<Path, Builder> {

    @Inject
    private Paths paths;

    @Inject
    private POMService pomService;

    @Inject
    private ProjectService projectService;

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    public synchronized void invalidateProjectCache( @Observes final InvalidateDMOProjectCacheEvent event ) {
        PortablePreconditions.checkNotNull( "event",
                                            event );
        final Path resourcePath = event.getResourcePath();
        final Path pathToPom = projectService.resolvePathToPom( resourcePath );

        //If resource was not within a Project there's nothing to invalidate
        if ( pathToPom != null ) {
            invalidateCache( pathToPom );
        }
    }

    public synchronized Builder assertBuilder( final Path resourcePath ) {
        final Path pathToPom = projectService.resolvePathToPom( resourcePath );
        Builder builder = getEntry( pathToPom );
        if ( builder == null ) {
            final POM gav = pomService.load( pathToPom );
            final Path projectPath = projectService.resolveProject( pathToPom );
            builder = new Builder( paths.convert( projectPath ),
                                   gav.getGav().getArtifactId(),
                                   paths,
                                   ioService,
                                   projectService );
            setEntry( pathToPom,
                      builder );
        }
        return builder;
    }

}
