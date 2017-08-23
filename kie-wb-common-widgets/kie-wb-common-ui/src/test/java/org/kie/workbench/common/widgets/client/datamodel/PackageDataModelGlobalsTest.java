/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.widgets.client.datamodel;

import java.util.Arrays;
import javax.enterprise.inject.Instance;

import org.appformer.project.datamodel.imports.HasImports;
import org.appformer.project.datamodel.imports.Import;
import org.appformer.project.datamodel.oracle.ModelField;
import org.appformer.project.datamodel.oracle.ProjectDataModelOracle;
import org.drools.workbench.models.datamodel.oracle.PackageDataModelOracle;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.validation.client.dynamic.DynamicValidator;
import org.junit.Test;
import org.kie.workbench.common.services.datamodel.backend.server.builder.packages.PackageDataModelOracleBuilder;
import org.kie.workbench.common.services.datamodel.backend.server.builder.projects.ProjectDataModelOracleBuilder;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleBaselinePayload;
import org.kie.workbench.common.services.datamodel.service.IncrementalDataModelService;
import org.kie.workbench.common.widgets.client.datamodel.testclasses.Product;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.callbacks.Callback;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Tests for Globals
 */
public class PackageDataModelGlobalsTest {

    @Mock
    private Instance<DynamicValidator> validatorInstance;

    @Test
    public void testGlobal() throws Exception {
        final ProjectDataModelOracle projectLoader = ProjectDataModelOracleBuilder.newProjectOracleBuilder()
                .addClass( Product.class )
                .build();

        final PackageDataModelOracle packageLoader = PackageDataModelOracleBuilder.newPackageOracleBuilder( "org.kie.workbench.common.widgets.client.datamodel.testclasses" )
                .setProjectOracle( projectLoader )
                .addGlobals( "global org.kie.workbench.common.widgets.client.datamodel.testclasses.Product g;" )
                .build();

        //Emulate server-to-client conversions
        final Caller<IncrementalDataModelService> service = new MockIncrementalDataModelServiceCaller( packageLoader );
        final AsyncPackageDataModelOracle oracle = new AsyncPackageDataModelOracleImpl( service,
                                                                                        validatorInstance );

        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setPackageName( packageLoader.getPackageName() );
        dataModel.setModelFields( packageLoader.getProjectModelFields() );
        dataModel.setGlobalTypes( packageLoader.getPackageGlobals() );
        dataModel.setCollectionTypes( packageLoader.getProjectCollectionTypes() );
        PackageDataModelOracleTestUtils.populateDataModelOracle( mock( Path.class ),
                                                                 new MockHasImports(),
                                                                 oracle,
                                                                 dataModel );

        assertNotNull( oracle );

        assertEquals( 2,
                      oracle.getFactTypes().length );
        assertEquals( "Product",
                      oracle.getFactTypes()[ 0 ] );
        assertEquals( "Product.Colour",
                      oracle.getFactTypes()[ 1 ] );

        assertEquals( 1,
                      oracle.getGlobalVariables().length );
        assertEquals( "g",
                      oracle.getGlobalVariables()[ 0 ] );
        assertEquals( "Product",
                      oracle.getGlobalVariable( "g" ) );

        oracle.getFieldCompletions( "Product",
                                    new Callback<ModelField[]>() {
                                        @Override
                                        public void callback( final ModelField[] fields ) {
                                            assertNotNull( fields );
                                            final String[] fieldNames = new String[ fields.length ];
                                            for ( int i = 0; i < fields.length; i++ ) {
                                                fieldNames[ i ] = fields[ i ].getName();
                                            }
                                            assertTrue( Arrays.asList( fieldNames ).contains( "this" ) );
                                            assertTrue( Arrays.asList( fieldNames ).contains( "colour" ) );
                                        }
                                    } );
        assertEquals( 0,
                      oracle.getGlobalCollections().length );
    }

    @Test
    public void testGlobalCollections() throws Exception {
        final ProjectDataModelOracle projectLoader = ProjectDataModelOracleBuilder.newProjectOracleBuilder()
                .addClass( java.util.List.class )
                .build();

        final PackageDataModelOracle packageLoader = PackageDataModelOracleBuilder.newPackageOracleBuilder( "org.kie.workbench.common.services.datamodel.backend.server.testclasses" )
                .setProjectOracle( projectLoader )
                .addGlobals( "global java.util.List list;" )
                .build();

        final HasImports imports = new MockHasImports();
        imports.getImports().addImport( new Import( "java.util.List" ) );

        //Emulate server-to-client conversions
        final Caller<IncrementalDataModelService> service = new MockIncrementalDataModelServiceCaller( packageLoader );
        final AsyncPackageDataModelOracle oracle = new AsyncPackageDataModelOracleImpl( service,
                                                                                        validatorInstance );

        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setPackageName( packageLoader.getPackageName() );
        dataModel.setModelFields( packageLoader.getProjectModelFields() );
        dataModel.setGlobalTypes( packageLoader.getPackageGlobals() );
        dataModel.setCollectionTypes( packageLoader.getProjectCollectionTypes() );
        PackageDataModelOracleTestUtils.populateDataModelOracle( mock( Path.class ),
                                                                 imports,
                                                                 oracle,
                                                                 dataModel );

        assertNotNull( oracle );

        assertEquals( 1,
                      oracle.getFactTypes().length );
        assertEquals( "List",
                      oracle.getFactTypes()[ 0 ] );

        assertEquals( 1,
                      oracle.getGlobalVariables().length );
        assertEquals( "list",
                      oracle.getGlobalVariables()[ 0 ] );
        assertEquals( "List",
                      oracle.getGlobalVariable( "list" ) );

        assertEquals( 1,
                      oracle.getGlobalCollections().length );
        assertEquals( "list",
                      oracle.getGlobalCollections()[ 0 ] );
    }

}
