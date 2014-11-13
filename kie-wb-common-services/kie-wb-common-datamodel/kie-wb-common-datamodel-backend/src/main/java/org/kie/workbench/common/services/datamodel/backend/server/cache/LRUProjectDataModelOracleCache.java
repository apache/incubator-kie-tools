package org.kie.workbench.common.services.datamodel.backend.server.cache;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.camel.util.AntPathMatcher;
import org.drools.core.rule.TypeMetaInfo;
import org.drools.workbench.models.datamodel.imports.Import;
import org.drools.workbench.models.datamodel.imports.Imports;
import org.drools.workbench.models.datamodel.oracle.ProjectDataModelOracle;
import org.drools.workbench.models.datamodel.oracle.TypeSource;
import org.guvnor.common.services.backend.cache.LRUCache;
import org.guvnor.common.services.project.builder.events.InvalidateDMOProjectCacheEvent;
import org.guvnor.common.services.project.model.ProjectImports;
import org.guvnor.common.services.project.service.POMService;
import org.kie.scanner.KieModuleMetaData;
import org.kie.workbench.common.services.backend.builder.Builder;
import org.kie.workbench.common.services.backend.builder.LRUBuilderCache;
import org.kie.workbench.common.services.datamodel.backend.server.builder.projects.FactBuilder;
import org.kie.workbench.common.services.datamodel.backend.server.builder.projects.ProjectDataModelOracleBuilder;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.kie.workbench.common.services.shared.project.ProjectImportsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Files;

/**
 * A simple LRU cache for Project DataModelOracles
 */
@ApplicationScoped
@Named("ProjectDataModelOracleCache")
public class LRUProjectDataModelOracleCache extends LRUCache<KieProject, ProjectDataModelOracle> {

    private static final Logger log = LoggerFactory.getLogger( LRUProjectDataModelOracleCache.class );

    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    @Inject
    private POMService pomService;

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private KieProjectService projectService;

    @Inject
    private ProjectImportsService importsService;

    @Inject
    private LRUBuilderCache cache;

    public synchronized void invalidateProjectCache( @Observes final InvalidateDMOProjectCacheEvent event ) {
        PortablePreconditions.checkNotNull( "event",
                                            event );
        final Path resourcePath = event.getResourcePath();
        final KieProject project = projectService.resolveProject( resourcePath );

        //If resource was not within a Project there's nothing to invalidate
        if ( project != null ) {
            invalidateCache( project );
        }
    }

    //Check the ProjectOracle for the Project has been created, otherwise create one!
    public synchronized ProjectDataModelOracle assertProjectDataModelOracle( final KieProject project ) {
        ProjectDataModelOracle projectOracle = getEntry( project );
        if ( projectOracle == null ) {
            projectOracle = makeProjectOracle( project );
            setEntry( project,
                      projectOracle );
        }
        return projectOracle;
    }

    private ProjectDataModelOracle makeProjectOracle( final KieProject project ) {
        //Get a Builder for the project
        final Builder builder = cache.assertBuilder( project );

        //Create the ProjectOracle...
        final KieModuleMetaData kieModuleMetaData = KieModuleMetaData.Factory.newKieModuleMetaData( builder.getKieModuleIgnoringErrors() );
        final ProjectDataModelOracleBuilder pdBuilder = ProjectDataModelOracleBuilder.newProjectOracleBuilder();

        //Get a "white list" of package names that are available for authoring
        final Set<String> packageNamesWhiteList = loadPackageNameWhiteList( project,
                                                                            kieModuleMetaData.getPackages() );

        // Add all packages that are available for authoring
        pdBuilder.addPackages( packageNamesWhiteList );

        //Add all classes from the KieModule metaData
        final Map<String, FactBuilder> discoveredFieldFactBuilders = new HashMap<String, FactBuilder>();
        for ( final String packageName : kieModuleMetaData.getPackages() ) {
            if ( packageNamesWhiteList.contains( packageName ) ) {
                for ( final String className : kieModuleMetaData.getClasses( packageName ) ) {
                    try {
                        final Class clazz = kieModuleMetaData.getClass( packageName,
                                                                        className );
                        final TypeMetaInfo typeMetaInfo = kieModuleMetaData.getTypeMetaInfo( clazz );
                        final TypeSource typeSource = builder.getClassSource( kieModuleMetaData,
                                                                              clazz );
                        pdBuilder.addClass( clazz,
                                            discoveredFieldFactBuilders,
                                            typeMetaInfo.isEvent(),
                                            typeSource );

                    } catch ( Throwable e ) {
                        log.error( e.getMessage() );
                    }
                }
            }
        }

        //Add external imports. The availability of these classes is checked in Builder and failed fast. Here we load them into the DMO
        final org.uberfire.java.nio.file.Path nioExternalImportsPath = Paths.convert( project.getImportsPath() );
        if ( Files.exists( nioExternalImportsPath ) ) {
            final Path externalImportsPath = Paths.convert( nioExternalImportsPath );
            final ProjectImports projectImports = importsService.load( externalImportsPath );
            final Imports imports = projectImports.getImports();
            for ( final Import item : imports.getImports() ) {
                try {
                    Class clazz = this.getClass().getClassLoader().loadClass( item.getType() );
                    pdBuilder.addClass( clazz,
                                        discoveredFieldFactBuilders,
                                        false,
                                        TypeSource.JAVA_DEPENDENCY );
                } catch ( ClassNotFoundException cnfe ) {
                    //This would have been raised to the user by Builder's validation but record the error here too
                    log.error( cnfe.getMessage() );
                } catch ( IOException ioe ) {
                    log.error( ioe.getMessage() );
                }
            }
        }

        return pdBuilder.build();
    }

    private Set<String> loadPackageNameWhiteList( final KieProject project,
                                                  final Collection<String> packageNames ) {
        final Set<String> packageNamesWhiteList = new HashSet<String>();
        if ( packageNames == null ) {
            return packageNamesWhiteList;
        }
        packageNamesWhiteList.addAll( packageNames );
        final org.uberfire.java.nio.file.Path packageNamesWhiteListPath = Paths.convert( project.getPackageNamesWhiteList() );

        if ( Files.exists( packageNamesWhiteListPath ) ) {
            final String content = ioService.readAllString( packageNamesWhiteListPath );
            if ( !( content == null || content.trim().isEmpty() ) ) {

                //If a White List is defined build set of acceptable Package Names from it
                packageNamesWhiteList.clear();
                final String[] patterns = content.split( System.getProperty( "line.separator" ) );

                //Convert to Paths as we're delegating to an Ant-style pattern matcher.
                //Convert once outside of the nested loops for performance reasons.
                for ( int i = 0; i < patterns.length; i++ ) {
                    patterns[ i ] = patterns[ i ].replaceAll( "\\.",
                                                              AntPathMatcher.DEFAULT_PATH_SEPARATOR );
                }
                final HashMap<String, String> packageNamePaths = new HashMap<String, String>();
                for ( String packageName : packageNames ) {
                    packageNamePaths.put( packageName,
                                          packageName.replaceAll( "\\.",
                                                                  AntPathMatcher.DEFAULT_PATH_SEPARATOR ) );
                }

                //Add Package Names matching the White List to the available packages
                for ( String pattern : patterns ) {
                    for ( Map.Entry<String, String> pnp : packageNamePaths.entrySet() ) {
                        if ( ANT_PATH_MATCHER.match( pattern,
                                                     pnp.getValue() ) ) {
                            packageNamesWhiteList.add( pnp.getKey() );
                        }
                    }
                }
            }
        }

        return packageNamesWhiteList;
    }

}

