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
package org.drools.workbench.screens.dsltext.backend.server.indexing;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.drools.compiler.compiler.DrlParser;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.compiler.lang.dsl.DSLMappingEntry;
import org.drools.compiler.lang.dsl.DSLTokenizedMappingFile;
import org.drools.workbench.models.datamodel.oracle.ProjectDataModelOracle;
import org.drools.workbench.screens.dsltext.type.DSLResourceTypeDefinition;
import org.guvnor.common.services.project.service.ProjectService;
import org.kie.workbench.common.services.datamodel.backend.server.service.DataModelService;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.DefaultIndexBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.PackageDescrIndexVisitor;
import org.kie.workbench.common.services.refactoring.backend.server.util.KObjectUtil;
import org.kie.workbench.common.services.refactoring.model.index.IndexElementsGenerator;
import org.kie.workbench.common.services.refactoring.model.index.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;
import org.uberfire.metadata.engine.Indexer;
import org.uberfire.metadata.model.KObject;
import org.uberfire.metadata.model.KObjectKey;

@ApplicationScoped
public class DslFileIndexer implements Indexer {

    private static final Logger logger = LoggerFactory.getLogger( DslFileIndexer.class );

    @Inject
    @Named("ioStrategy")
    protected IOService ioService;

    @Inject
    private DataModelService dataModelService;

    @Inject
    private ProjectService projectService;

    @Inject
    protected DSLResourceTypeDefinition dslType;

    @Override
    public boolean supportsPath( final Path path ) {
        return dslType.accept( Paths.convert( path ) );
    }

    @Override
    public KObject toKObject( final Path path ) {
        KObject index = null;

        try {
            final List<String> lhs = new ArrayList<String>();
            final List<String> rhs = new ArrayList<String>();
            final String dsl = ioService.readAllString( path );

            //Construct a dummy DRL file to parse index elements
            final DSLTokenizedMappingFile dslLoader = new DSLTokenizedMappingFile();
            if ( dslLoader.parseAndLoad( new StringReader( dsl ) ) ) {
                for ( DSLMappingEntry e : dslLoader.getMapping().getEntries() ) {
                    switch ( e.getSection() ) {
                        case CONDITION:
                            lhs.add( e.getValuePattern() );
                            break;
                        case CONSEQUENCE:
                            rhs.add( e.getValuePattern() );
                            break;
                    }
                }

                final String drl = makeDrl( path,
                                            lhs,
                                            rhs );
                final DrlParser drlParser = new DrlParser();
                final PackageDescr packageDescr = drlParser.parse( true,
                                                                   drl );
                if ( packageDescr == null ) {
                    logger.error( "Unable to parse DRL for '" + path.toUri().toString() + "'." );
                    return index;
                }

                //Don't include rules created to parse DSL
                final DefaultIndexBuilder builder = new DefaultIndexBuilder() {
                    @Override
                    public DefaultIndexBuilder addGenerator( final IndexElementsGenerator generator ) {
                        if ( generator instanceof Rule ) {
                            return this;
                        }
                        return super.addGenerator( generator );
                    }
                };

                final ProjectDataModelOracle dmo = getProjectDataModelOracle( path );
                final PackageDescrIndexVisitor visitor = new PackageDescrIndexVisitor( dmo,
                                                                                       builder,
                                                                                       packageDescr );
                visitor.visit();

                return KObjectUtil.toKObject( path,
                                              builder.build() );
            }
        } catch ( Exception e ) {
            logger.error( e.getMessage() );
        }
        return index;
    }

    @Override
    public KObjectKey toKObjectKey( final Path path ) {
        return KObjectUtil.toKObjectKey( path );
    }

    //Delegate resolution of package name to method to assist testing
    protected String getPackageName( final Path path ) {
        return projectService.resolvePackage( Paths.convert( path ) ).getPackageName();
    }

    //Delegate resolution of DMO to method to assist testing
    protected ProjectDataModelOracle getProjectDataModelOracle( final Path path ) {
        return dataModelService.getProjectDataModel( Paths.convert( path ) );
    }

    private String makeDrl( final Path path,
                            final List<String> lhs,
                            final List<String> rhs ) {
        final StringBuilder sb = new StringBuilder();
        final String packageName = getPackageName( path );

        sb.append( "package " ).append( packageName ).append( "\n" );
        sb.append( "rule \"mock\"\n" );
        sb.append( "when\n" );
        for ( String e : lhs ) {
            sb.append( e ).append( "\n" );
        }
        sb.append( "then\n" );
        for ( String e : rhs ) {
            sb.append( e ).append( "\n" );
        }
        sb.append( "end\n" );
        return sb.toString();
    }

}
