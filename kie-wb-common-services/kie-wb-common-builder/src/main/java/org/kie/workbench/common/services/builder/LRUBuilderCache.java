package org.kie.workbench.common.services.builder;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;

import org.kie.commons.io.IOService;
import org.kie.commons.validation.PortablePreconditions;
import org.kie.workbench.common.services.backend.cache.LRUCache;
import org.kie.workbench.common.services.datamodel.events.InvalidateDMOProjectCacheEvent;
import org.kie.workbench.common.services.project.service.POMService;
import org.kie.workbench.common.services.project.service.ProjectService;
import org.kie.workbench.common.services.project.service.model.POM;
import org.kie.workbench.common.services.shared.project.Project;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

/**
 * A simple LRU cache for Builders
 */
@ApplicationScoped
public class LRUBuilderCache extends LRUCache<Project, Builder> {

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
        final Project project = projectService.resolveProject( resourcePath );

        //If resource was not within a Project there's nothing to invalidate
        if ( project != null ) {
            invalidateCache( project );
        }
    }

    public synchronized Builder assertBuilder( final Project project ) {
        final Path pathToPom = projectService.resolvePathToPom( project.getPath() );
        Builder builder = getEntry( project );
        if ( builder == null ) {
            final POM gav = pomService.load( pathToPom );
            builder = new Builder( paths.convert( project.getPath() ),
                                   gav.getGav().getArtifactId(),
                                   paths,
                                   ioService,
                                   projectService );
            setEntry( project,
                      builder );
        }
        return builder;
    }

}
