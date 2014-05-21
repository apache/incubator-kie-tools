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
package org.drools.workbench.screens.dtablexls.backend.server.indexing;

import java.io.IOException;
import java.io.InputStream;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.drools.compiler.compiler.DecisionTableFactory;
import org.drools.compiler.compiler.DrlParser;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.workbench.models.datamodel.oracle.ProjectDataModelOracle;
import org.drools.workbench.screens.dtablexls.type.DecisionTableXLSResourceTypeDefinition;
import org.kie.workbench.common.services.datamodel.backend.server.service.DataModelService;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.DefaultIndexBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.PackageDescrIndexVisitor;
import org.kie.workbench.common.services.refactoring.backend.server.util.KObjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.StandardOpenOption;
import org.uberfire.metadata.engine.Indexer;
import org.uberfire.metadata.model.KObject;
import org.uberfire.metadata.model.KObjectKey;

@ApplicationScoped
public class DecisionTableXLSFileIndexer implements Indexer {

    private static final Logger logger = LoggerFactory.getLogger( DecisionTableXLSFileIndexer.class );

    @Inject
    @Named("ioStrategy")
    protected IOService ioService;

    @Inject
    private DataModelService dataModelService;

    @Inject
    protected DecisionTableXLSResourceTypeDefinition type;

    @Override
    public boolean supportsPath( final Path path ) {
        return type.accept( Paths.convert( path ) );
    }

    @Override
    public KObject toKObject( final Path path ) {
        KObject index = null;
        InputStream inputStream = null;

        try {
            inputStream = ioService.newInputStream( path,
                                                    StandardOpenOption.READ );
            final String drl = DecisionTableFactory.loadFromInputStream( inputStream,
                                                                         null );
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
        } finally {
            if ( inputStream != null ) {
                try {
                    inputStream.close();
                } catch ( IOException e ) {
                }
            }
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

}
