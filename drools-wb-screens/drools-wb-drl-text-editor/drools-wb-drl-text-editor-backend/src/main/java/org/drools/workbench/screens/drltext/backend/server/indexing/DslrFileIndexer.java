/*
 * Copyright 2014 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.screens.drltext.backend.server.indexing;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.drools.compiler.compiler.DrlParser;
import org.drools.compiler.lang.Expander;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.compiler.lang.dsl.DSLMappingFile;
import org.drools.compiler.lang.dsl.DSLTokenizedMappingFile;
import org.drools.compiler.lang.dsl.DefaultExpander;
import org.drools.workbench.models.datamodel.oracle.ProjectDataModelOracle;
import org.drools.workbench.screens.drltext.type.DSLRResourceTypeDefinition;
import org.guvnor.common.services.backend.file.FileDiscoveryService;
import org.guvnor.common.services.project.service.ProjectService;
import org.kie.workbench.common.services.backend.file.DSLFileFilter;
import org.kie.workbench.common.services.datamodel.backend.server.service.DataModelService;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.DefaultIndexBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.PackageDescrIndexVisitor;
import org.kie.workbench.common.services.refactoring.backend.server.util.KObjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;
import org.uberfire.metadata.engine.Indexer;
import org.uberfire.metadata.model.KObject;
import org.uberfire.metadata.model.KObjectKey;

@ApplicationScoped
public class DslrFileIndexer implements Indexer {

    private static final Logger logger = LoggerFactory.getLogger( DslrFileIndexer.class );

    private static final DSLFileFilter FILTER_DSLS = new DSLFileFilter();

    @Inject
    @Named("ioStrategy")
    protected IOService ioService;

    @Inject
    private DataModelService dataModelService;

    @Inject
    private FileDiscoveryService fileDiscoveryService;

    @Inject
    private ProjectService projectService;

    @Inject
    private DSLRResourceTypeDefinition dslrType;

    @Override
    public boolean supportsPath( final Path path ) {
        return dslrType.accept( Paths.convert( path ) );
    }

    @Override
    public KObject toKObject( final Path path ) {

        KObject index = null;
        try {
            final String dslr = ioService.readAllString( path );
            final Expander expander = getDSLExpander( path );
            final String drl = expander.expand( dslr );
            final DrlParser drlParser = new DrlParser();
            final PackageDescr packageDescr = drlParser.parse( true,
                                                               drl );
            if ( packageDescr == null ) {
                logger.error( "Unable to parse DRL for '" + path.toUri().toString() + "'." );
                return index;
            }

            final ProjectDataModelOracle dmo = getProjectDataModelOracle( path );
            final DefaultIndexBuilder builder = new DefaultIndexBuilder();
            final PackageDescrIndexVisitor visitor = new PackageDescrIndexVisitor( dmo,
                                                                                   builder,
                                                                                   packageDescr );
            visitor.visit();

            index = KObjectUtil.toKObject( path,
                                           builder.build() );

        } catch ( Exception e ) {
            logger.error( "Unable to index '" + path.toUri().toString() + "'.",
                          e.getMessage() );
        }

        return index;
    }

    @Override
    public KObjectKey toKObjectKey( final Path path ) {
        return KObjectUtil.toKObjectKey( path );
    }

    //Delegate resolution of DMO to method to assist testing
    protected ProjectDataModelOracle getProjectDataModelOracle( final Path path ) {
        return dataModelService.getProjectDataModel( Paths.convert( path ) );
    }

    /**
     * Returns an expander for DSLs (only if there is a DSL configured for this package).
     */
    private Expander getDSLExpander( final Path path ) {
        final Expander expander = new DefaultExpander();
        final List<DSLMappingFile> dsls = getDSLMappingFiles( path );
        for ( DSLMappingFile dsl : dsls ) {
            expander.addDSLMapping( dsl.getMapping() );
        }
        return expander;
    }

    private List<DSLMappingFile> getDSLMappingFiles( final Path path ) {
        final List<DSLMappingFile> dsls = new ArrayList<DSLMappingFile>();
        final org.uberfire.backend.vfs.Path vfsPath = Paths.convert( path );
        final org.uberfire.backend.vfs.Path packagePath = projectService.resolvePackage( vfsPath ).getPackageMainResourcesPath();
        final org.uberfire.java.nio.file.Path nioPackagePath = Paths.convert( packagePath );
        final Collection<Path> dslPaths = fileDiscoveryService.discoverFiles( nioPackagePath,
                                                                              FILTER_DSLS );
        for ( final org.uberfire.java.nio.file.Path dslPath : dslPaths ) {
            final String dslDefinition = ioService.readAllString( dslPath );
            final DSLTokenizedMappingFile dslFile = new DSLTokenizedMappingFile();
            try {
                if ( dslFile.parseAndLoad( new StringReader( dslDefinition ) ) ) {
                    dsls.add( dslFile );
                } else {
                    logger.error( "Unable to parse DSL definition: " + dslDefinition );
                }
            } catch ( IOException ioe ) {
                logger.error( ioe.getMessage() );
            }
        }
        return dsls;
    }

}
