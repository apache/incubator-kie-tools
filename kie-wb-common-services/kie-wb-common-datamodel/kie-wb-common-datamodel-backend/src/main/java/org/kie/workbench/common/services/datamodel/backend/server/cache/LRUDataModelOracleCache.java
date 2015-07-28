/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.services.datamodel.backend.server.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;

import org.drools.workbench.models.datamodel.oracle.PackageDataModelOracle;
import org.drools.workbench.models.datamodel.oracle.ProjectDataModelOracle;
import org.guvnor.common.services.backend.cache.LRUCache;
import org.guvnor.common.services.backend.file.FileDiscoveryService;
import org.guvnor.common.services.project.builder.events.InvalidateDMOPackageCacheEvent;
import org.guvnor.common.services.project.builder.events.InvalidateDMOProjectCacheEvent;
import org.guvnor.common.services.project.builder.model.BuildMessage;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.shared.message.Level;
import org.kie.api.builder.KieModule;
import org.kie.scanner.KieModuleMetaData;
import org.kie.workbench.common.services.backend.builder.LRUBuilderCache;
import org.kie.workbench.common.services.backend.file.DSLFileFilter;
import org.kie.workbench.common.services.backend.file.EnumerationsFileFilter;
import org.kie.workbench.common.services.backend.file.GlobalsFileFilter;
import org.kie.workbench.common.services.datamodel.backend.server.builder.packages.PackageDataModelOracleBuilder;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.DirectoryStream;

/**
 * A simple LRU cache for Package DataModelOracles
 */
@ApplicationScoped
@Named("PackageDataModelOracleCache")
public class LRUDataModelOracleCache extends LRUCache<Package, PackageDataModelOracle> {

    private static final DirectoryStream.Filter<org.uberfire.java.nio.file.Path> FILTER_ENUMERATIONS = new EnumerationsFileFilter();

    private static final DirectoryStream.Filter<org.uberfire.java.nio.file.Path> FILTER_GLOBALS = new GlobalsFileFilter();

    private static final DirectoryStream.Filter<org.uberfire.java.nio.file.Path> FILTER_DSLS = new DSLFileFilter();

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private FileDiscoveryService fileDiscoveryService;

    @Inject
    @Named("ProjectDataModelOracleCache")
    private LRUProjectDataModelOracleCache cacheProjects;

    @Inject
    private KieProjectService projectService;

    @Inject
    private LRUBuilderCache builderCache;

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
        final KieProject project = projectService.resolveProject( resourcePath );

        //If resource was not within a Project there's nothing to invalidate
        if ( project == null ) {
            return;
        }

        final String projectUri = project.getRootPath().toURI();
        final List<Package> cacheEntriesToInvalidate = new ArrayList<Package>();
        for ( final Package pkg : getKeys() ) {
            final Path packageMainSrcPath = pkg.getPackageMainSrcPath();
            final Path packageTestSrcPath = pkg.getPackageTestSrcPath();
            final Path packageMainResourcesPath = pkg.getPackageMainResourcesPath();
            final Path packageTestResourcesPath = pkg.getPackageTestResourcesPath();
            if ( packageMainSrcPath != null && packageMainSrcPath.toURI().startsWith( projectUri ) ) {
                cacheEntriesToInvalidate.add( pkg );
            } else if ( packageTestSrcPath != null && packageTestSrcPath.toURI().startsWith( projectUri ) ) {
                cacheEntriesToInvalidate.add( pkg );
            } else if ( packageMainResourcesPath != null && packageMainResourcesPath.toURI().startsWith( projectUri ) ) {
                cacheEntriesToInvalidate.add( pkg );
            } else if ( packageTestResourcesPath != null && packageTestResourcesPath.toURI().startsWith( projectUri ) ) {
                cacheEntriesToInvalidate.add( pkg );
            }
        }
        for ( final Package pkg : cacheEntriesToInvalidate ) {
            invalidateCache( pkg );
        }
    }

    //Check the DataModelOracle for the Package has been created, otherwise create one!
    public synchronized PackageDataModelOracle assertPackageDataModelOracle( final KieProject project,
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

    private PackageDataModelOracle makePackageDataModelOracle( final KieProject project,
                                                               final Package pkg ) {
        final String packageName = pkg.getPackageName();
        final PackageDataModelOracleBuilder dmoBuilder = PackageDataModelOracleBuilder.newPackageOracleBuilder( packageName );
        final ProjectDataModelOracle projectOracle = cacheProjects.assertProjectDataModelOracle( project );
        dmoBuilder.setProjectOracle( projectOracle );

        //Add Guvnor enumerations
        loadEnumsForPackage( dmoBuilder,
                             project,
                             pkg );

        //Add DSLs
        loadDslsForPackage( dmoBuilder,
                            pkg );

        //Add Globals
        loadGlobalsForPackage( dmoBuilder,
                               pkg );

        return dmoBuilder.build();
    }

    private BuildMessage makeMessage( final String msg ) {
        final BuildMessage buildMessage = new BuildMessage();
        buildMessage.setLevel( Level.ERROR );
        buildMessage.setText( msg );
        return buildMessage;
    }

    private void loadEnumsForPackage( final PackageDataModelOracleBuilder dmoBuilder,
                                      final KieProject project,
                                      final Package pkg ) {
        final KieModule module = builderCache.assertBuilder( project ).getKieModuleIgnoringErrors();
        final ClassLoader classLoader = KieModuleMetaData.Factory.newKieModuleMetaData( module ).getClassLoader();
        final org.uberfire.java.nio.file.Path nioPackagePath = Paths.convert( pkg.getPackageMainResourcesPath() );
        final Collection<org.uberfire.java.nio.file.Path> enumFiles = fileDiscoveryService.discoverFiles( nioPackagePath,
                                                                                                          FILTER_ENUMERATIONS );
        for ( final org.uberfire.java.nio.file.Path path : enumFiles ) {
            final String enumDefinition = ioService.readAllString( path );
            dmoBuilder.addEnum( enumDefinition,
                                classLoader );
        }
    }

    private void loadDslsForPackage( final PackageDataModelOracleBuilder dmoBuilder,
                                     final Package pkg ) {
        final org.uberfire.java.nio.file.Path nioPackagePath = Paths.convert( pkg.getPackageMainResourcesPath() );
        final Collection<org.uberfire.java.nio.file.Path> dslFiles = fileDiscoveryService.discoverFiles( nioPackagePath,
                                                                                                         FILTER_DSLS );
        for ( final org.uberfire.java.nio.file.Path path : dslFiles ) {
            final String dslDefinition = ioService.readAllString( path );
            dmoBuilder.addDsl( dslDefinition );
        }
    }

    private void loadGlobalsForPackage( final PackageDataModelOracleBuilder dmoBuilder,
                                        final Package pkg ) {
        final org.uberfire.java.nio.file.Path nioPackagePath = Paths.convert( pkg.getPackageMainResourcesPath() );
        final Collection<org.uberfire.java.nio.file.Path> globalFiles = fileDiscoveryService.discoverFiles( nioPackagePath,
                                                                                                            FILTER_GLOBALS );
        for ( final org.uberfire.java.nio.file.Path path : globalFiles ) {
            final String definition = ioService.readAllString( path );
            dmoBuilder.addGlobals( definition );
        }
    }

}
