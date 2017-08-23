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

import java.util.Date;
import java.util.List;
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
import static org.mockito.Mockito.*;

/**
 * Tests for the ProjectDataModelOracle CEP completions
 */
public class PackageDataModelOracleCEPCompletionsTest {

    @Mock
    private Instance<DynamicValidator> validatorInstance;

    @Test
    public void testCEPCompletions() {
        final ProjectDataModelOracle projectLoader = ProjectDataModelOracleBuilder.newProjectOracleBuilder()
                .addFact( "NotAnEvent" )
                .addField( new ModelField( "dateField",
                                           Date.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           ModelField.FIELD_ORIGIN.DECLARED,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_DATE ) )
                .end()
                .addFact( "AnEvent",
                          true )
                .addField( new ModelField( "this",
                                           Object.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           ModelField.FIELD_ORIGIN.SELF,
                                           FieldAccessorsAndMutators.BOTH,
                                           "AnEvent" ) )
                .addField( new ModelField( "dateField",
                                           Date.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           ModelField.FIELD_ORIGIN.DECLARED,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_DATE ) )
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
        dataModel.setEventTypes( packageLoader.getProjectEventTypes() );
        PackageDataModelOracleTestUtils.populateDataModelOracle( mock( Path.class ),
                                                                 new MockHasImports(),
                                                                 oracle,
                                                                 dataModel );

        //Check completions
        String[] types = oracle.getFactTypes();
        assertEquals( 2,
                      types.length );
        PackageDataModelOracleTestUtils.assertContains( "NotAnEvent",
                                                        types );
        PackageDataModelOracleTestUtils.assertContains( "AnEvent",
                                                        types );

        oracle.getOperatorCompletions( "NotAnEvent",
                                       "dateField",
                                       new Callback<String[]>() {
                                           @Override
                                           public void callback( final String[] notAnEventDateFieldOperators ) {
                                               assertEquals( 13,
                                                             notAnEventDateFieldOperators.length );
                                               assertEquals( notAnEventDateFieldOperators[ 0 ],
                                                             "==" );
                                               assertEquals( notAnEventDateFieldOperators[ 1 ],
                                                             "!=" );
                                               assertEquals( notAnEventDateFieldOperators[ 2 ],
                                                             "<" );
                                               assertEquals( notAnEventDateFieldOperators[ 3 ],
                                                             ">" );
                                               assertEquals( notAnEventDateFieldOperators[ 4 ],
                                                             "<=" );
                                               assertEquals( notAnEventDateFieldOperators[ 5 ],
                                                             ">=" );
                                               assertEquals( notAnEventDateFieldOperators[ 6 ],
                                                             "== null" );
                                               assertEquals( notAnEventDateFieldOperators[ 7 ],
                                                             "!= null" );
                                               assertEquals( notAnEventDateFieldOperators[ 8 ],
                                                             "in" );
                                               assertEquals( notAnEventDateFieldOperators[ 9 ],
                                                             "not in" );
                                               assertEquals( notAnEventDateFieldOperators[ 10 ],
                                                             "after" );
                                               assertEquals( notAnEventDateFieldOperators[ 11 ],
                                                             "before" );
                                               assertEquals( notAnEventDateFieldOperators[ 12 ],
                                                             "coincides" );
                                           }
                                       } );

        oracle.getOperatorCompletions( "AnEvent",
                                       "this",
                                       new Callback<String[]>() {
                                           @Override
                                           public void callback( final String[] anEventThisOperators ) {
                                               assertEquals( 17,
                                                             anEventThisOperators.length );
                                               assertEquals( anEventThisOperators[ 0 ],
                                                             "==" );
                                               assertEquals( anEventThisOperators[ 1 ],
                                                             "!=" );
                                               assertEquals( anEventThisOperators[ 2 ],
                                                             "== null" );
                                               assertEquals( anEventThisOperators[ 3 ],
                                                             "!= null" );
                                               assertEquals( anEventThisOperators[ 4 ],
                                                             "after" );
                                               assertEquals( anEventThisOperators[ 5 ],
                                                             "before" );
                                               assertEquals( anEventThisOperators[ 6 ],
                                                             "coincides" );
                                               assertEquals( anEventThisOperators[ 7 ],
                                                             "during" );
                                               assertEquals( anEventThisOperators[ 8 ],
                                                             "finishes" );
                                               assertEquals( anEventThisOperators[ 9 ],
                                                             "finishedby" );
                                               assertEquals( anEventThisOperators[ 10 ],
                                                             "includes" );
                                               assertEquals( anEventThisOperators[ 11 ],
                                                             "meets" );
                                               assertEquals( anEventThisOperators[ 12 ],
                                                             "metby" );
                                               assertEquals( anEventThisOperators[ 13 ],
                                                             "overlaps" );
                                               assertEquals( anEventThisOperators[ 14 ],
                                                             "overlappedby" );
                                               assertEquals( anEventThisOperators[ 15 ],
                                                             "starts" );
                                               assertEquals( anEventThisOperators[ 16 ],
                                                             "startedby" );
                                           }
                                       } );
    }

    @Test
    @SuppressWarnings("serial")
    public void testCEPParameterCompletions() {
        List<Integer> c = CEPOracle.getCEPOperatorParameterSets( "after" );
        assertEquals( 3,
                      c.size() );
        assertEquals( 0,
                      c.get( 0 ).intValue() );
        assertEquals( 1,
                      c.get( 1 ).intValue() );
        assertEquals( 2,
                      c.get( 2 ).intValue() );

        c = CEPOracle.getCEPOperatorParameterSets( "before" );
        assertEquals( 3,
                      c.size() );
        assertEquals( 0,
                      c.get( 0 ).intValue() );
        assertEquals( 1,
                      c.get( 1 ).intValue() );
        assertEquals( 2,
                      c.get( 2 ).intValue() );

        c = CEPOracle.getCEPOperatorParameterSets( "coincides" );
        assertEquals( 3,
                      c.size() );
        assertEquals( 0,
                      c.get( 0 ).intValue() );
        assertEquals( 1,
                      c.get( 1 ).intValue() );
        assertEquals( 2,
                      c.get( 2 ).intValue() );

        c = CEPOracle.getCEPOperatorParameterSets( "during" );
        assertEquals( 4,
                      c.size() );
        assertEquals( 0,
                      c.get( 0 ).intValue() );
        assertEquals( 1,
                      c.get( 1 ).intValue() );
        assertEquals( 2,
                      c.get( 2 ).intValue() );
        assertEquals( 4,
                      c.get( 3 ).intValue() );

        c = CEPOracle.getCEPOperatorParameterSets( "finishes" );
        assertEquals( 2,
                      c.size() );
        assertEquals( 0,
                      c.get( 0 ).intValue() );
        assertEquals( 1,
                      c.get( 1 ).intValue() );

        c = CEPOracle.getCEPOperatorParameterSets( "finishedby" );
        assertEquals( 2,
                      c.size() );
        assertEquals( 0,
                      c.get( 0 ).intValue() );
        assertEquals( 1,
                      c.get( 1 ).intValue() );

        c = CEPOracle.getCEPOperatorParameterSets( "includes" );
        assertEquals( 4,
                      c.size() );
        assertEquals( 0,
                      c.get( 0 ).intValue() );
        assertEquals( 1,
                      c.get( 1 ).intValue() );
        assertEquals( 2,
                      c.get( 2 ).intValue() );
        assertEquals( 4,
                      c.get( 3 ).intValue() );

        c = CEPOracle.getCEPOperatorParameterSets( "meets" );
        assertEquals( 2,
                      c.size() );
        assertEquals( 0,
                      c.get( 0 ).intValue() );
        assertEquals( 1,
                      c.get( 1 ).intValue() );

        c = CEPOracle.getCEPOperatorParameterSets( "metby" );
        assertEquals( 2,
                      c.size() );
        assertEquals( 0,
                      c.get( 0 ).intValue() );
        assertEquals( 1,
                      c.get( 1 ).intValue() );

        c = CEPOracle.getCEPOperatorParameterSets( "overlaps" );
        assertEquals( 3,
                      c.size() );
        assertEquals( 0,
                      c.get( 0 ).intValue() );
        assertEquals( 1,
                      c.get( 1 ).intValue() );
        assertEquals( 2,
                      c.get( 2 ).intValue() );

        c = CEPOracle.getCEPOperatorParameterSets( "overlappedby" );
        assertEquals( 3,
                      c.size() );
        assertEquals( 0,
                      c.get( 0 ).intValue() );
        assertEquals( 1,
                      c.get( 1 ).intValue() );
        assertEquals( 2,
                      c.get( 2 ).intValue() );

        c = CEPOracle.getCEPOperatorParameterSets( "starts" );
        assertEquals( 2,
                      c.size() );
        assertEquals( 0,
                      c.get( 0 ).intValue() );
        assertEquals( 1,
                      c.get( 1 ).intValue() );

        c = CEPOracle.getCEPOperatorParameterSets( "startedby" );
        assertEquals( 2,
                      c.size() );
        assertEquals( 0,
                      c.get( 0 ).intValue() );
        assertEquals( 1,
                      c.get( 1 ).intValue() );

    }

    @Test
    public void testCEPOperatorValidation() {
        assertFalse( CEPOracle.isCEPOperator( "==" ) );
        assertFalse( CEPOracle.isCEPOperator( "!=" ) );
        assertFalse( CEPOracle.isCEPOperator( "<" ) );
        assertFalse( CEPOracle.isCEPOperator( ">" ) );
        assertFalse( CEPOracle.isCEPOperator( "<=" ) );
        assertFalse( CEPOracle.isCEPOperator( ">=" ) );
        assertFalse( CEPOracle.isCEPOperator( "== null" ) );
        assertFalse( CEPOracle.isCEPOperator( "!= null" ) );
        assertTrue( CEPOracle.isCEPOperator( "after" ) );
        assertTrue( CEPOracle.isCEPOperator( "before" ) );
        assertTrue( CEPOracle.isCEPOperator( "coincides" ) );
        assertTrue( CEPOracle.isCEPOperator( "during" ) );
        assertTrue( CEPOracle.isCEPOperator( "finishes" ) );
        assertTrue( CEPOracle.isCEPOperator( "finishedby" ) );
        assertTrue( CEPOracle.isCEPOperator( "includes" ) );
        assertTrue( CEPOracle.isCEPOperator( "meets" ) );
        assertTrue( CEPOracle.isCEPOperator( "metby" ) );
        assertTrue( CEPOracle.isCEPOperator( "overlaps" ) );
        assertTrue( CEPOracle.isCEPOperator( "overlappedby" ) );
        assertTrue( CEPOracle.isCEPOperator( "starts" ) );
        assertTrue( CEPOracle.isCEPOperator( "startedby" ) );
    }

    @Test
    public void testCEPWindowOperators() {
        List<String> operators = CEPOracle.getCEPWindowOperators();
        assertEquals( 2,
                      operators.size() );
        assertEquals( "over window:time",
                      operators.get( 0 ) );
        assertEquals( "over window:length",
                      operators.get( 1 ) );
    }

}
