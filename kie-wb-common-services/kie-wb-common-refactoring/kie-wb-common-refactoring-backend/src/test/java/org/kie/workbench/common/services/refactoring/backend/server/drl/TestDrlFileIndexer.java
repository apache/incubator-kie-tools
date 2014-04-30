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
package org.kie.workbench.common.services.refactoring.backend.server.drl;

import java.util.HashMap;
import javax.enterprise.context.ApplicationScoped;

import org.drools.compiler.compiler.DrlParser;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.workbench.models.commons.backend.oracle.ProjectDataModelOracleImpl;
import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.datamodel.oracle.FieldAccessorsAndMutators;
import org.drools.workbench.models.datamodel.oracle.ModelField;
import org.drools.workbench.models.datamodel.oracle.ProjectDataModelOracle;
import org.kie.workbench.common.services.refactoring.backend.server.TestIndexer;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.DefaultIndexBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.PackageDescrIndexVisitor;
import org.kie.workbench.common.services.refactoring.backend.server.util.KObjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;
import org.uberfire.metadata.model.KObject;
import org.uberfire.metadata.model.KObjectKey;

@ApplicationScoped
public class TestDrlFileIndexer implements TestIndexer<TestDrlFileTypeDefinition> {

    private static final Logger logger = LoggerFactory.getLogger( TestDrlFileIndexer.class );

    private IOService ioService;

    private TestDrlFileTypeDefinition type;

    @Override
    public void setIOService( final IOService ioService ) {
        this.ioService = ioService;
    }

    @Override
    public void setResourceTypeDefinition( final TestDrlFileTypeDefinition type ) {
        this.type = type;
    }

    @Override
    public boolean supportsPath( final Path path ) {
        return type.accept( Paths.convert( path ) );
    }

    @Override
    public KObject toKObject( final Path path ) {
        KObject index = null;

        try {
            final String drl = ioService.readAllString( path );
            final DrlParser drlParser = new DrlParser();
            final PackageDescr packageDescr = drlParser.parse( true,
                                                               drl );
            if ( packageDescr == null ) {
                logger.error( "Unable to parse DRL for '" + path.toUri().toString() + "'." );
                return index;
            }

            final ProjectDataModelOracle dmo = getProjectDataModelOracle( path );

            final DefaultIndexBuilder builder = new DefaultIndexBuilder( path );
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

    private ProjectDataModelOracle getProjectDataModelOracle( final Path path ) {
        final ProjectDataModelOracle dmo = new ProjectDataModelOracleImpl();
        dmo.addProjectModelFields( new HashMap<String, ModelField[]>() {{
            put( "org.kie.workbench.common.services.refactoring.backend.server.drl.classes.Applicant",
                 new ModelField[]{ new ModelField( "age",
                                                   "java.lang.Integer",
                                                   ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                                   ModelField.FIELD_ORIGIN.DECLARED,
                                                   FieldAccessorsAndMutators.ACCESSOR,
                                                   DataType.TYPE_NUMERIC_INTEGER ) } );
            put( "org.kie.workbench.common.services.refactoring.backend.server.drl.classes.Mortgage",
                 new ModelField[]{ new ModelField( "amount",
                                                   "java.lang.Integer",
                                                   ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                                   ModelField.FIELD_ORIGIN.DECLARED,
                                                   FieldAccessorsAndMutators.ACCESSOR,
                                                   DataType.TYPE_NUMERIC_INTEGER ) } );
            put( "org.kie.workbench.common.services.refactoring.backend.server.drl.classes.Mortgage",
                 new ModelField[]{ new ModelField( "applicant",
                                                   "org.kie.workbench.common.services.refactoring.backend.server.drl.classes.Applicant",
                                                   ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                                   ModelField.FIELD_ORIGIN.DECLARED,
                                                   FieldAccessorsAndMutators.ACCESSOR,
                                                   "org.kie.workbench.common.services.refactoring.backend.server.drl.classes.Applicant" ) } );
            put( "org.kie.workbench.common.services.refactoring.backend.server.drl.classes.Bank",
                 new ModelField[]{ new ModelField( "mortgage",
                                                   "org.kie.workbench.common.services.refactoring.backend.server.drl.classes.Mortgage",
                                                   ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                                   ModelField.FIELD_ORIGIN.DECLARED,
                                                   FieldAccessorsAndMutators.ACCESSOR,
                                                   "org.kie.workbench.common.services.refactoring.backend.server.drl.classes.Mortgage" ) } );
        }} );
        return dmo;
    }

}
