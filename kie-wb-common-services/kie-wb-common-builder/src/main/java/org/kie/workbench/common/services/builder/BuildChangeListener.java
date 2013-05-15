package org.kie.workbench.common.services.builder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.kie.commons.validation.PortablePreconditions;
import org.kie.guvnor.project.service.ProjectService;
import org.kie.workbench.common.services.shared.builder.BuildService;
import org.kie.workbench.common.services.shared.config.AppConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.widgets.events.ResourceAddedEvent;
import org.uberfire.client.workbench.widgets.events.ResourceBatchChangesEvent;
import org.uberfire.client.workbench.widgets.events.ResourceChange;
import org.uberfire.client.workbench.widgets.events.ResourceDeletedEvent;
import org.uberfire.client.workbench.widgets.events.ResourceUpdatedEvent;

/**
 * Listener for changes to project resources to handle incremental builds
 */
@ApplicationScoped
public class BuildChangeListener {

    private static final String INCREMENTAL_BUILD_PROPERTY_NAME = "build.enable-incremental";

    private static final Logger log = LoggerFactory.getLogger( BuildChangeListener.class );

    @Inject
    private Paths paths;

    @Inject
    private ProjectService projectService;

    @Inject
    private BuildService buildService;

    @Inject
    private AppConfigService appConfigService;

    @Inject
    private BuildExecutorServiceFactory executorServiceProducer;
    private ExecutorService executor;

    private boolean isIncrementalEnabled = false;

    @PostConstruct
    private void setup() {
        executor = executorServiceProducer.getExecutorService();
        isIncrementalEnabled = isIncrementalBuildEnabled();
    }

    private boolean isIncrementalBuildEnabled() {
        final String value = appConfigService.loadPreferences().get( INCREMENTAL_BUILD_PROPERTY_NAME );
        return Boolean.parseBoolean( value );
    }

    @PreDestroy
    private void destroyExecutorService() {
        try {
            executor.shutdown();
            if ( !executor.awaitTermination( 10,
                                             TimeUnit.SECONDS ) ) {
                executor.shutdownNow();
                if ( !executor.awaitTermination( 10,
                                                 TimeUnit.SECONDS ) ) {
                    System.err.println( "executor did not terminate" );
                }
            }
        } catch ( InterruptedException e ) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public void addResource( @Observes final ResourceAddedEvent resourceAddedEvent ) {
        //Do nothing if incremental builds are disabled
        if ( !isIncrementalEnabled ) {
            return;
        }

        //Perform incremental build
        PortablePreconditions.checkNotNull( "resourceAddedEvent",
                                            resourceAddedEvent );
        final Path resource = resourceAddedEvent.getPath();

        //If resource is not within a Package it cannot be used for an incremental build
        final Path packagePath = projectService.resolvePackage( resource );
        if ( packagePath == null ) {
            return;
        }

        //Schedule an incremental build
        executor.execute( new Runnable() {

            @Override
            public void run() {
                try {
                    buildService.addPackageResource( resource );
                } catch ( Exception e ) {
                    log.error( e.getMessage(),
                               e );
                }
            }
        } );
    }

    public void deleteResource( @Observes final ResourceDeletedEvent resourceDeletedEvent ) {
        //Do nothing if incremental builds are disabled
        if ( !isIncrementalEnabled ) {
            return;
        }

        //Perform incremental build
        PortablePreconditions.checkNotNull( "resourceDeletedEvent",
                                            resourceDeletedEvent );
        final Path resource = resourceDeletedEvent.getPath();

        //If resource is not within a Package it cannot be used for an incremental build
        final Path packagePath = projectService.resolvePackage( resource );
        if ( packagePath == null ) {
            return;
        }

        //Schedule an incremental build
        executor.execute( new Runnable() {

            @Override
            public void run() {
                try {
                    buildService.deletePackageResource( resource );
                } catch ( Exception e ) {
                    log.error( e.getMessage(),
                               e );
                }
            }
        } );
    }

    public void updateResource( @Observes final ResourceUpdatedEvent resourceUpdatedEvent ) {
        //Do nothing if incremental builds are disabled
        if ( !isIncrementalEnabled ) {
            return;
        }

        //Perform incremental build
        PortablePreconditions.checkNotNull( "resourceUpdatedEvent",
                                            resourceUpdatedEvent );
        final Path resource = resourceUpdatedEvent.getPath();

        //If resource is not within a Project it cannot be used for an incremental build
        final Path projectPath = projectService.resolveProject( resource );
        if ( projectPath == null ) {
            return;
        }

        //The pom.xml or kmodule.xml cannot be processed incrementally
        final boolean isPomFile = projectService.isPom( resource );
        final boolean isKModuleFile = projectService.isKModule( resource );
        if ( isPomFile || isKModuleFile ) {
            scheduleProjectResourceUpdate( resource );
        } else {
            schedulePackageResourceUpdate( resource );
        }
    }

    //Schedule an incremental build for a project resource
    private void scheduleProjectResourceUpdate( final Path resource ) {
        executor.execute( new Runnable() {

            @Override
            public void run() {
                try {
                    buildService.updateProjectResource( resource );
                } catch ( Exception e ) {
                    log.error( e.getMessage(),
                               e );
                }
            }
        } );
    }

    //Schedule an incremental build for a package resource
    private void schedulePackageResourceUpdate( final Path resource ) {
        executor.execute( new Runnable() {

            @Override
            public void run() {
                try {
                    buildService.updatePackageResource( resource );
                } catch ( Exception e ) {
                    log.error( e.getMessage(),
                               e );
                }
            }
        } );
    }

    public void batchResourceChanges( @Observes final ResourceBatchChangesEvent resourceBatchChangesEvent ) {
        //Do nothing if incremental builds are disabled
        if ( !isIncrementalEnabled ) {
            return;
        }

        //Perform incremental build
        PortablePreconditions.checkNotNull( "resourceBatchChangesEvent",
                                            resourceBatchChangesEvent );

        //Block changes together with their respective project as Builder operates at the Project level
        final Set<ResourceChange> batch = resourceBatchChangesEvent.getBatch();
        final Map<Path, Set<ResourceChange>> projectBatchChanges = new HashMap<Path, Set<ResourceChange>>();
        for ( ResourceChange change : batch ) {
            PortablePreconditions.checkNotNull( "path",
                                                change.getPath() );
            final Path resource = change.getPath();

            //If resource is not within a Package it cannot be used for an incremental build
            final Path projectPath = projectService.resolveProject( resource );
            final Path packagePath = projectService.resolvePackage( resource );
            if ( projectPath != null && packagePath != null ) {
                if ( !projectBatchChanges.containsKey( projectPath ) ) {
                    projectBatchChanges.put( projectPath,
                                             new HashSet<ResourceChange>() );
                }
                final Set<ResourceChange> projectChanges = projectBatchChanges.get( projectPath );
                projectChanges.add( change );
            }
        }

        //Schedule an incremental build for each Project
        for ( final Map.Entry<Path, Set<ResourceChange>> e : projectBatchChanges.entrySet() ) {
            executor.execute( new Runnable() {

                @Override
                public void run() {
                    try {
                        buildService.applyBatchResourceChanges( e.getKey(),
                                                                e.getValue() );
                    } catch ( Exception e ) {
                        log.error( e.getMessage(),
                                   e );
                    }
                }
            } );
        }
    }

}
