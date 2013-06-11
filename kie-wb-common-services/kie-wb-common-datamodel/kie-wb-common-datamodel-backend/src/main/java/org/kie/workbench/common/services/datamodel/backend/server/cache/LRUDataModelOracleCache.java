package org.kie.workbench.common.services.datamodel.backend.server.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;

import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.file.DirectoryStream;
import org.kie.commons.validation.PortablePreconditions;
import org.kie.workbench.common.services.datamodel.backend.server.builder.packages.PackageDataModelOracleBuilder;
import org.kie.workbench.common.services.datamodel.events.InvalidateDMOPackageCacheEvent;
import org.kie.workbench.common.services.datamodel.events.InvalidateDMOProjectCacheEvent;
import org.kie.workbench.common.services.datamodel.oracle.PackageDataModelOracle;
import org.kie.workbench.common.services.datamodel.oracle.ProjectDataModelOracle;
import org.kie.workbench.common.services.project.service.ProjectService;
import org.kie.workbench.common.services.backend.cache.LRUCache;
import org.kie.workbench.common.services.backend.file.FileDiscoveryService;
import org.kie.workbench.common.services.backend.file.FileExtensionFilter;
import org.kie.workbench.common.services.project.service.model.*;
import org.kie.workbench.common.services.shared.project.Package;
import org.kie.workbench.common.services.shared.builder.model.BuildMessage;
import org.kie.workbench.common.services.shared.builder.model.IncrementalBuildResults;
import org.kie.workbench.common.services.shared.project.Project;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

/**
 * A simple LRU cache for Package DataModelOracles
 */
@ApplicationScoped
@Named("PackageDataModelOracleCache")
public class LRUDataModelOracleCache extends LRUCache<Package, PackageDataModelOracle> {

    private static final DirectoryStream.Filter<org.kie.commons.java.nio.file.Path> FILTER_ENUMERATIONS = new FileExtensionFilter( ".enumeration" );

    private static final DirectoryStream.Filter<org.kie.commons.java.nio.file.Path> FILTER_DSLS = new FileExtensionFilter( ".dsl" );

    private static final DirectoryStream.Filter<org.kie.commons.java.nio.file.Path> FILTER_GLOBALS = new FileExtensionFilter( ".global.drl" );

    @Inject
    private Paths paths;

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private FileDiscoveryService fileDiscoveryService;

    @Inject
    @Named("ProjectDataModelOracleCache")
    private LRUProjectDataModelOracleCache cacheProjects;

    @Inject
    private ProjectService projectService;

    @Inject
    private Event<IncrementalBuildResults> incrementalBuildResultsEvent;

    public synchronized void invalidatePackageCache( @Observes final InvalidateDMOPackageCacheEvent event ) {
        PortablePreconditions.checkNotNull( "event",
                                            event );
        final Path resourcePath = event.getResourcePath();
        final Package pkg = projectService.resolvePackage( resourcePath );

        //If resource was not within a Package there's nothing to invalidate
        if ( pkg != null ) {
            invalidateCache( pkg );
        }
    }

    public synchronized void invalidateProjectPackagesCache( @Observes final InvalidateDMOProjectCacheEvent event ) {
        PortablePreconditions.checkNotNull( "event",
                                            event );
        final Path resourcePath = event.getResourcePath();
        final Project project = projectService.resolveProject( resourcePath );

        //If resource was not within a Project there's nothing to invalidate
        if ( project == null ) {
            return;
        }

        final String projectUri = project.getPath().toURI();
        final List<Package> cacheEntriesToInvalidate = new ArrayList<Package>();
        for ( final Package pkg : getKeys() ) {
            //TODO {manstis}
            //final String packageUri = packagePath.toURI();
            //if ( packageUri.startsWith( projectUri ) ) {
            //    cacheEntriesToInvalidate.add( pkg );
            //}
        }
        for ( final Package pkg : cacheEntriesToInvalidate ) {
            invalidateCache( pkg );
        }
    }

    //Check the DataModelOracle for the Package has been created, otherwise create one!
    public synchronized PackageDataModelOracle assertPackageDataModelOracle( final Project project,
                                                                             final Package pkg ) {
        PackageDataModelOracle oracle = getEntry( pkg );
        if ( oracle == null ) {
            oracle = makePackageDataModelOracle( project,
                                                 pkg );
            setEntry( pkg,
                      oracle );
        }
        return oracle;
    }

    private PackageDataModelOracle makePackageDataModelOracle( final Project project,
                                                               final Package pkg ) {
        final String packageName = pkg.getPackageName();
        final PackageDataModelOracleBuilder dmoBuilder = PackageDataModelOracleBuilder.newPackageOracleBuilder( packageName );
        final ProjectDataModelOracle projectOracle = cacheProjects.assertProjectDataModelOracle( project );
        dmoBuilder.setProjectOracle( projectOracle );

        //Add Guvnor enumerations
        loadEnumsForPackage( dmoBuilder,
                             pkg );

        //Add DSLs
        loadDslsForPackage( dmoBuilder,
                            pkg );

        //Add Globals
        loadGlobalsForPackage( dmoBuilder,
                               pkg );

        //Report any incremental Build errors to Users
        if ( !dmoBuilder.getErrors().isEmpty() ) {
            final IncrementalBuildResults results = new IncrementalBuildResults();
            final List<String> errors = dmoBuilder.getErrors();
            for ( final String error : errors ) {
                results.addAddedMessage( makeMessage( error ) );
            }
            incrementalBuildResultsEvent.fire( results );
        }

        return dmoBuilder.build();
    }

    private BuildMessage makeMessage( final String msg ) {
        final BuildMessage buildMessage = new BuildMessage();
        buildMessage.setLevel( BuildMessage.Level.ERROR );
        buildMessage.setText( msg );
        return buildMessage;
    }

    private void loadEnumsForPackage( final PackageDataModelOracleBuilder dmoBuilder,
                                      final Package pkg ) {
        final org.kie.commons.java.nio.file.Path nioPackagePath = paths.convert( pkg.getPackageMainResourcesPath() );
        final Collection<org.kie.commons.java.nio.file.Path> enumFiles = fileDiscoveryService.discoverFiles( nioPackagePath,
                                                                                                             FILTER_ENUMERATIONS );
        for ( final org.kie.commons.java.nio.file.Path path : enumFiles ) {
            final String enumDefinition = ioService.readAllString( path );
            dmoBuilder.addEnum( enumDefinition );
        }
    }

    private void loadDslsForPackage( final PackageDataModelOracleBuilder dmoBuilder,
                                     final Package pkg ) {
        final org.kie.commons.java.nio.file.Path nioPackagePath = paths.convert( pkg.getPackageMainResourcesPath() );
        final Collection<org.kie.commons.java.nio.file.Path> dslFiles = fileDiscoveryService.discoverFiles( nioPackagePath,
                                                                                                            FILTER_DSLS );
        for ( final org.kie.commons.java.nio.file.Path path : dslFiles ) {
            final String dslDefinition = ioService.readAllString( path );
            dmoBuilder.addDsl( dslDefinition );
        }
    }

    private void loadGlobalsForPackage( final PackageDataModelOracleBuilder dmoBuilder,
                                        final Package pkg ) {
        final org.kie.commons.java.nio.file.Path nioPackagePath = paths.convert( pkg.getPackageMainResourcesPath() );
        final Collection<org.kie.commons.java.nio.file.Path> globalFiles = fileDiscoveryService.discoverFiles( nioPackagePath,
                                                                                                               FILTER_GLOBALS );
        for ( final org.kie.commons.java.nio.file.Path path : globalFiles ) {
            final String definition = ioService.readAllString( path );
            dmoBuilder.addGlobals( definition );
        }
    }

}
