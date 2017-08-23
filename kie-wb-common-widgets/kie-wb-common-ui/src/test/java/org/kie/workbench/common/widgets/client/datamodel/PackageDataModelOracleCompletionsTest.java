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

import javax.enterprise.inject.Instance;

import org.appformer.project.datamodel.oracle.DataType;
import org.appformer.project.datamodel.oracle.FieldAccessorsAndMutators;
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
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.callbacks.Callback;

import static org.junit.Assert.*;
import static org.kie.workbench.common.widgets.client.datamodel.PackageDataModelOracleTestUtils.*;
import static org.mockito.Mockito.*;

/**
 * Tests for the ProjectDataModelOracle completions
 */
public class PackageDataModelOracleCompletionsTest {

    @Mock
    private Instance<DynamicValidator> validatorInstance;

    @Test
    public void testFactsAndFields() {
        final ProjectDataModelOracle projectLoader = ProjectDataModelOracleBuilder.newProjectOracleBuilder()
                .addFact( "Person" )
                .addField( new ModelField( "age",
                                           Integer.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           ModelField.FIELD_ORIGIN.DECLARED,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_NUMERIC_INTEGER ) )
                .addField( new ModelField( "sex",
                                           String.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           ModelField.FIELD_ORIGIN.DECLARED,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_STRING ) )
                .end()
                .build();

        final PackageDataModelOracle packageLoader = PackageDataModelOracleBuilder.newPackageOracleBuilder().setProjectOracle( projectLoader ).build();

        //Emulate server-to-client conversions
        final Caller<IncrementalDataModelService> service = new MockIncrementalDataModelServiceCaller( packageLoader );
        final AsyncPackageDataModelOracle oracle = new AsyncPackageDataModelOracleImpl( service,
                                                                                        validatorInstance );

        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setPackageName( packageLoader.getPackageName() );
        dataModel.setModelFields( packageLoader.getProjectModelFields() );
        PackageDataModelOracleTestUtils.populateDataModelOracle( mock( Path.class ),
                                                                 new MockHasImports(),
                                                                 oracle,
                                                                 dataModel );

        assertEquals( DataType.TYPE_NUMERIC_INTEGER,
                      oracle.getFieldType( "Person",
                                           "age" ) );
        assertEquals( DataType.TYPE_STRING,
                      oracle.getFieldType( "Person",
                                           "sex" ) );
    }

    @Test
    public void testFactCompletions() {
        final ProjectDataModelOracle projectLoader = ProjectDataModelOracleBuilder.newProjectOracleBuilder()
                .addFact( "Person" )
                .addField( new ModelField( "age",
                                           Integer.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           ModelField.FIELD_ORIGIN.DECLARED,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_NUMERIC_INTEGER ) )
                .addField( new ModelField( "rank",
                                           Integer.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           ModelField.FIELD_ORIGIN.DECLARED,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_COMPARABLE ) )
                .addField( new ModelField( "name",
                                           String.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           ModelField.FIELD_ORIGIN.DECLARED,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_STRING ) )
                .end()
                .addFact( "Vehicle" )
                .addField( new ModelField( "make",
                                           String.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           ModelField.FIELD_ORIGIN.DECLARED,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_STRING ) )
                .addField( new ModelField( "type",
                                           String.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           ModelField.FIELD_ORIGIN.DECLARED,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_STRING ) )
                .end()
                .build();

        final PackageDataModelOracle packageLoader = PackageDataModelOracleBuilder.newPackageOracleBuilder().setProjectOracle( projectLoader ).build();

        //Emulate server-to-client conversions
        final Caller<IncrementalDataModelService> service = new MockIncrementalDataModelServiceCaller( packageLoader );
        final AsyncPackageDataModelOracle oracle = new AsyncPackageDataModelOracleImpl( service,
                                                                                        validatorInstance );

        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setPackageName( packageLoader.getPackageName() );
        dataModel.setModelFields( packageLoader.getProjectModelFields() );
        PackageDataModelOracleTestUtils.populateDataModelOracle( mock( Path.class ),
                                                                 new MockHasImports(),
                                                                 oracle,
                                                                 dataModel );

        String[] types = oracle.getFactTypes();
        assertEquals( 2,
                      types.length );
        assertContains( "Person",
                        types );
        assertContains( "Vehicle",
                        types );
    }

    @Test
    public void testFactFieldCompletions() {
        final ProjectDataModelOracle projectLoader = ProjectDataModelOracleBuilder.newProjectOracleBuilder()
                .addFact( "Person" )
                .addField( new ModelField( "age",
                                           Integer.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           ModelField.FIELD_ORIGIN.DECLARED,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_NUMERIC_INTEGER ) )
                .addField( new ModelField( "rank",
                                           Integer.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           ModelField.FIELD_ORIGIN.DECLARED,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_COMPARABLE ) )
                .addField( new ModelField( "name",
                                           String.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           ModelField.FIELD_ORIGIN.DECLARED,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_STRING ) )
                .end()
                .build();

        final PackageDataModelOracle packageLoader = PackageDataModelOracleBuilder.newPackageOracleBuilder().setProjectOracle( projectLoader ).build();

        //Emulate server-to-client conversions
        final Caller<IncrementalDataModelService> service = new MockIncrementalDataModelServiceCaller( packageLoader );
        final AsyncPackageDataModelOracle oracle = new AsyncPackageDataModelOracleImpl( service,
                                                                                        validatorInstance );

        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setPackageName( packageLoader.getPackageName() );
        dataModel.setModelFields( packageLoader.getProjectModelFields() );
        PackageDataModelOracleTestUtils.populateDataModelOracle( mock( Path.class ),
                                                                 new MockHasImports(),
                                                                 oracle,
                                                                 dataModel );

        Callback<ModelField[]> callback = spy( new Callback<ModelField[]>() {
            @Override
            public void callback( final ModelField[] fields ) {
                assertEquals( 4,
                              fields.length );
                assertEquals( "age",
                              fields[ 0 ].getName() );
                assertEquals( "name",
                              fields[ 1 ].getName() );
                assertEquals( "rank",
                              fields[ 2 ].getName() );
                assertEquals( "this",
                              fields[ 3 ].getName() );
            }
        } );

        oracle.getFieldCompletions( "Person",
                                    callback );

        verify( callback ).callback( any( ModelField[].class ) );
    }

    @Test
    public void testFactFieldOperatorCompletions() {
        final ProjectDataModelOracle projectLoader = ProjectDataModelOracleBuilder.newProjectOracleBuilder()
                .addFact( "Person" )
                .addField( new ModelField( "age",
                                           Integer.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           ModelField.FIELD_ORIGIN.DECLARED,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_NUMERIC_INTEGER ) )
                .addField( new ModelField( "rank",
                                           Integer.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           ModelField.FIELD_ORIGIN.DECLARED,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_COMPARABLE ) )
                .addField( new ModelField( "name",
                                           String.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           ModelField.FIELD_ORIGIN.DECLARED,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_STRING ) )
                .end()
                .build();

        final PackageDataModelOracle packageLoader = PackageDataModelOracleBuilder.newPackageOracleBuilder().setProjectOracle( projectLoader ).build();

        //Emulate server-to-client conversions
        final Caller<IncrementalDataModelService> service = new MockIncrementalDataModelServiceCaller( packageLoader );
        final AsyncPackageDataModelOracle oracle = new AsyncPackageDataModelOracleImpl( service,
                                                                                        validatorInstance );

        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setPackageName( packageLoader.getPackageName() );
        dataModel.setModelFields( packageLoader.getProjectModelFields() );
        PackageDataModelOracleTestUtils.populateDataModelOracle( mock( Path.class ),
                                                                 new MockHasImports(),
                                                                 oracle,
                                                                 dataModel );

        Callback<String[]> personCallback = spy( new Callback<String[]>() {
            @Override
            public void callback( final String[] personThisOperators ) {
                assertEquals( 4,
                              personThisOperators.length );
                assertEquals( personThisOperators[ 0 ],
                              "==" );
                assertEquals( personThisOperators[ 1 ],
                              "!=" );
                assertEquals( personThisOperators[ 2 ],
                              "== null" );
                assertEquals( personThisOperators[ 3 ],
                              "!= null" );
            }
        } );
        oracle.getOperatorCompletions( "Person",
                                       "this",
                                       personCallback );

        verify( personCallback ).callback( any( String[].class ) );

        Callback<String[]> personAgeCallback = spy( new Callback<String[]>() {
            @Override
            public void callback( final String[] personAgeOperators ) {
                assertEquals( 10,
                              personAgeOperators.length );
                assertEquals( personAgeOperators[ 0 ],
                              "==" );
                assertEquals( personAgeOperators[ 1 ],
                              "!=" );
                assertEquals( personAgeOperators[ 2 ],
                              "<" );
                assertEquals( personAgeOperators[ 3 ],
                              ">" );
                assertEquals( personAgeOperators[ 4 ],
                              "<=" );
                assertEquals( personAgeOperators[ 5 ],
                              ">=" );
                assertEquals( personAgeOperators[ 6 ],
                              "== null" );
                assertEquals( personAgeOperators[ 7 ],
                              "!= null" );
                assertEquals( personAgeOperators[ 8 ],
                              "in" );
                assertEquals( personAgeOperators[ 9 ],
                              "not in" );
            }
        } );
        oracle.getOperatorCompletions( "Person",
                                       "age",
                                       personAgeCallback );

        verify( personAgeCallback ).callback( any( String[].class ) );

        Callback<String[]> personRankCallback = spy( new Callback<String[]>() {
            @Override
            public void callback( final String[] personRankOperators ) {
                assertEquals( 8,
                              personRankOperators.length );
                assertEquals( personRankOperators[ 0 ],
                              "==" );
                assertEquals( personRankOperators[ 1 ],
                              "!=" );
                assertEquals( personRankOperators[ 2 ],
                              "<" );
                assertEquals( personRankOperators[ 3 ],
                              ">" );
                assertEquals( personRankOperators[ 4 ],
                              "<=" );
                assertEquals( personRankOperators[ 5 ],
                              ">=" );
                assertEquals( personRankOperators[ 6 ],
                              "== null" );
                assertEquals( personRankOperators[ 7 ],
                              "!= null" );
            }
        } );
        oracle.getOperatorCompletions( "Person",
                                       "rank",
                                       personRankCallback );

        verify( personRankCallback ).callback( any( String[].class ) );

        Callback<String[]> personNameCallback = spy( new Callback<String[]>() {
            @Override
            public void callback( final String[] personNameOperators ) {
                assertEquals( 12,
                              personNameOperators.length );
                assertEquals( "==",
                              personNameOperators[ 0 ] );
                assertEquals( "!=",
                              personNameOperators[ 1 ] );
                assertEquals( "<",
                              personNameOperators[ 2 ] );
                assertEquals( ">",
                              personNameOperators[ 3 ] );
                assertEquals( "<=",
                              personNameOperators[ 4 ] );
                assertEquals( ">=",
                              personNameOperators[ 5 ] );
                assertEquals( "matches",
                              personNameOperators[ 6 ] );
                assertEquals( "soundslike",
                              personNameOperators[ 7 ] );
                assertEquals( "== null",
                              personNameOperators[ 8 ] );
                assertEquals( "!= null",
                              personNameOperators[ 9 ] );
                assertEquals( "in",
                              personNameOperators[ 10 ] );
                assertEquals( "not in",
                              personNameOperators[ 11 ] );
            }
        } );
        oracle.getOperatorCompletions( "Person",
                                       "name",
                                       personNameCallback );

        verify( personNameCallback ).callback( any( String[].class ) );
    }

    @Test
    public void testFactFieldConnectiveOperatorCompletions() {
        final ProjectDataModelOracle projectLoader = ProjectDataModelOracleBuilder.newProjectOracleBuilder()
                .addFact( "Person" )
                .addField( new ModelField( "age",
                                           Integer.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           ModelField.FIELD_ORIGIN.DECLARED,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_NUMERIC_INTEGER ) )
                .addField( new ModelField( "rank",
                                           Integer.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           ModelField.FIELD_ORIGIN.DECLARED,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_COMPARABLE ) )
                .addField( new ModelField( "name",
                                           String.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           ModelField.FIELD_ORIGIN.DECLARED,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_STRING ) )
                .end()
                .build();

        final PackageDataModelOracle packageLoader = PackageDataModelOracleBuilder.newPackageOracleBuilder().setProjectOracle( projectLoader ).build();

        //Emulate server-to-client conversions
        final Caller<IncrementalDataModelService> service = new MockIncrementalDataModelServiceCaller( packageLoader );
        final AsyncPackageDataModelOracle oracle = new AsyncPackageDataModelOracleImpl( service,
                                                                                        validatorInstance );

        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setPackageName( packageLoader.getPackageName() );
        dataModel.setModelFields( packageLoader.getProjectModelFields() );
        PackageDataModelOracleTestUtils.populateDataModelOracle( mock( Path.class ),
                                                                 new MockHasImports(),
                                                                 oracle,
                                                                 dataModel );

        Callback<String[]> personThisCallback = spy( new Callback<String[]>() {
            @Override
            public void callback( final String[] personThisConnectiveOperators ) {
                assertEquals( 3,
                              personThisConnectiveOperators.length );
                assertEquals( personThisConnectiveOperators[ 0 ],
                              "|| ==" );
                assertEquals( personThisConnectiveOperators[ 1 ],
                              "|| !=" );
                assertEquals( personThisConnectiveOperators[ 2 ],
                              "&& !=" );
            }
        } );
        oracle.getConnectiveOperatorCompletions( "Person",
                                                 "this",
                                                 personThisCallback );

        verify( personThisCallback ).callback( any( String[].class ) );

        Callback<String[]> personAgeCallback = spy( new Callback<String[]>() {
            @Override
            public void callback( final String[] personAgeConnectiveOperators ) {
                assertEquals( 11,
                              personAgeConnectiveOperators.length );
                assertEquals( personAgeConnectiveOperators[ 0 ],
                              "|| ==" );
                assertEquals( personAgeConnectiveOperators[ 1 ],
                              "|| !=" );
                assertEquals( personAgeConnectiveOperators[ 2 ],
                              "&& !=" );
                assertEquals( personAgeConnectiveOperators[ 3 ],
                              "&& >" );
                assertEquals( personAgeConnectiveOperators[ 4 ],
                              "&& <" );
                assertEquals( personAgeConnectiveOperators[ 5 ],
                              "|| >" );
                assertEquals( personAgeConnectiveOperators[ 6 ],
                              "|| <" );
                assertEquals( personAgeConnectiveOperators[ 7 ],
                              "&& >=" );
                assertEquals( personAgeConnectiveOperators[ 8 ],
                              "&& <=" );
                assertEquals( personAgeConnectiveOperators[ 9 ],
                              "|| <=" );
                assertEquals( personAgeConnectiveOperators[ 10 ],
                              "|| >=" );
            }
        } );
        oracle.getConnectiveOperatorCompletions( "Person",
                                                 "age",
                                                 personAgeCallback );

        verify( personAgeCallback ).callback( any( String[].class ) );

        Callback<String[]> personNameCallback = spy( new Callback<String[]>() {
            @Override
            public void callback( final String[] personRankConnectiveOperators ) {
                assertEquals( 11,
                              personRankConnectiveOperators.length );
                assertEquals( personRankConnectiveOperators[ 0 ],
                              "|| ==" );
                assertEquals( personRankConnectiveOperators[ 1 ],
                              "|| !=" );
                assertEquals( personRankConnectiveOperators[ 2 ],
                              "&& !=" );
                assertEquals( personRankConnectiveOperators[ 3 ],
                              "&& >" );
                assertEquals( personRankConnectiveOperators[ 4 ],
                              "&& <" );
                assertEquals( personRankConnectiveOperators[ 5 ],
                              "|| >" );
                assertEquals( personRankConnectiveOperators[ 6 ],
                              "|| <" );
                assertEquals( personRankConnectiveOperators[ 7 ],
                              "&& >=" );
                assertEquals( personRankConnectiveOperators[ 8 ],
                              "&& <=" );
                assertEquals( personRankConnectiveOperators[ 9 ],
                              "|| <=" );
                assertEquals( personRankConnectiveOperators[ 10 ],
                              "|| >=" );
            }
        } );
        oracle.getConnectiveOperatorCompletions( "Person",
                                                 "rank",
                                                 personNameCallback );

        verify( personAgeCallback ).callback( any( String[].class ) );

        Callback<String[]> personNameCallback2 = spy( new Callback<String[]>() {
            @Override
            public void callback( final String[] personNameConnectiveOperators ) {
                assertEquals( 13,
                              personNameConnectiveOperators.length );
                assertEquals( personNameConnectiveOperators[ 0 ],
                              "|| ==" );
                assertEquals( personNameConnectiveOperators[ 1 ],
                              "|| !=" );
                assertEquals( personNameConnectiveOperators[ 2 ],
                              "&& !=" );
                assertEquals( personNameConnectiveOperators[ 3 ],
                              "&& >" );
                assertEquals( personNameConnectiveOperators[ 4 ],
                              "&& <" );
                assertEquals( personNameConnectiveOperators[ 5 ],
                              "|| >" );
                assertEquals( personNameConnectiveOperators[ 6 ],
                              "|| <" );
                assertEquals( personNameConnectiveOperators[ 7 ],
                              "&& >=" );
                assertEquals( personNameConnectiveOperators[ 8 ],
                              "&& <=" );
                assertEquals( personNameConnectiveOperators[ 9 ],
                              "|| <=" );
                assertEquals( personNameConnectiveOperators[ 10 ],
                              "|| >=" );
                assertEquals( personNameConnectiveOperators[ 11 ],
                              "&& matches" );
                assertEquals( personNameConnectiveOperators[ 12 ],
                              "|| matches" );
            }
        } );
        oracle.getConnectiveOperatorCompletions( "Person",
                                                 "name",
                                                 personNameCallback2 );

        verify( personNameCallback2 ).callback( any( String[].class ) );
    }

}
