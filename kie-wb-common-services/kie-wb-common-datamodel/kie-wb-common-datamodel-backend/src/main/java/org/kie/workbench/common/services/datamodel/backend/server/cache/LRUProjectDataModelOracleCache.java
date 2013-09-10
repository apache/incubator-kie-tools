package org.kie.workbench.common.services.datamodel.backend.server.cache;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;

import org.drools.core.rule.TypeMetaInfo;
import org.drools.workbench.models.commons.shared.imports.Import;
import org.drools.workbench.models.commons.shared.imports.Imports;
import org.drools.workbench.models.datamodel.model.TypeSource;
import org.drools.workbench.models.datamodel.oracle.ProjectDataModelOracle;
import org.guvnor.common.services.backend.cache.LRUCache;
import org.guvnor.common.services.builder.Builder;
import org.guvnor.common.services.builder.LRUBuilderCache;
import org.guvnor.common.services.project.builder.events.InvalidateDMOProjectCacheEvent;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.model.ProjectImports;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.common.services.project.service.ProjectService;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.file.Files;
import org.kie.commons.validation.PortablePreconditions;
import org.kie.scanner.KieModuleMetaData;
import org.kie.workbench.common.services.datamodel.backend.server.builder.projects.ProjectDataModelOracleBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

/**
 * A simple LRU cache for Project DataModelOracles
 */
@ApplicationScoped
@Named("ProjectDataModelOracleCache")
public class LRUProjectDataModelOracleCache extends LRUCache<Project, ProjectDataModelOracle> {

    private static final Logger log = LoggerFactory.getLogger( LRUProjectDataModelOracleCache.class );

    @Inject
    private Paths paths;

    @Inject
    private POMService pomService;

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private ProjectService projectService;

    @Inject
    private LRUBuilderCache cache;

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

    //Check the ProjectOracle for the Project has been created, otherwise create one!
    public synchronized ProjectDataModelOracle assertProjectDataModelOracle( final Project project ) {
        ProjectDataModelOracle projectOracle = getEntry( project );
        if ( projectOracle == null ) {
            projectOracle = makeProjectOracle( project );
            setEntry( project,
                      projectOracle );
        }
        return projectOracle;
    }

    private ProjectDataModelOracle makeProjectOracle( final Project project ) {
        //Get a Builder for the project
        final Builder builder = cache.assertBuilder( project );

        //Create the ProjectOracle...
        final KieModuleMetaData kieModuleMetaData = KieModuleMetaData.Factory.newKieModuleMetaData( builder.getKieModuleIgnoringErrors() );
        final ProjectDataModelOracleBuilder pdBuilder = ProjectDataModelOracleBuilder.newProjectOracleBuilder();

        // Add all packages
        // XXX: For Testing
        ArrayList<String> temp = new ArrayList<String>();
        temp.add("A.B.C");
        temp.add("B.C.D");
        temp.add("C.D.E");
        pdBuilder.addPackages(temp);
//        pdBuilder.addPackages(kieModuleMetaData.getPackages());

        //Add all classes from the KieModule metaData
        for ( final String packageName : kieModuleMetaData.getPackages() ) {
            for ( final String className : kieModuleMetaData.getClasses( packageName ) ) {
                final Class clazz = kieModuleMetaData.getClass( packageName,
                                                       className );
                final TypeMetaInfo typeMetaInfo = kieModuleMetaData.getTypeMetaInfo( clazz );
                final TypeSource typeSource = builder.getClassSource( kieModuleMetaData,
                                                                      clazz );
                try {
                    pdBuilder.addClass( clazz,
                                        typeMetaInfo.isEvent(),
                                        typeSource );
                } catch ( IOException ioe ) {
                    log.error( ioe.getMessage() );
                }
            }
        }

        //Add external imports. The availability of these classes is checked in Builder and failed fast. Here we load them into the DMO
        final org.kie.commons.java.nio.file.Path nioExternalImportsPath = paths.convert( project.getImportsPath() );
        if ( Files.exists( nioExternalImportsPath ) ) {
            final Path externalImportsPath = paths.convert( nioExternalImportsPath );
            final ProjectImports projectImports = projectService.load( externalImportsPath );
            final Imports imports = projectImports.getImports();
            for ( final Import item : imports.getImports() ) {
                try {
                    Class clazz = this.getClass().getClassLoader().loadClass( item.getType() );
                    pdBuilder.addClass( clazz );
                } catch ( ClassNotFoundException cnfe ) {
                    //This should not happen as Builder would have failed to load them and failed fast.
                    log.error( cnfe.getMessage() );
                } catch ( IOException ioe ) {
                    log.error( ioe.getMessage() );
                }
            }
        }

        addAllRuleNames( builder, pdBuilder );

        return pdBuilder.build();
    }

    private void addAllRuleNames( Builder builder,
                                  ProjectDataModelOracleBuilder pdBuilder ) {

        final KieModuleMetaData kieModuleMetaData = KieModuleMetaData.Factory.newKieModuleMetaData(builder.getKieModuleIgnoringErrors());

        final List<String> ruleNames = new ArrayList<String>();
        for (String packageName : kieModuleMetaData.getPackages()) {
            ruleNames.addAll(kieModuleMetaData.getRuleNamesInPackage(packageName));
        }

        pdBuilder.addRuleNames( ruleNames );

    }
}

