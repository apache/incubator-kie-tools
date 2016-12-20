/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.datasource.management.backend.core.wildfly;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.datasource.management.model.DataSourceDef;
import org.kie.workbench.common.screens.datasource.management.model.DriverDef;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith( MockitoJUnitRunner.class )
public class DeploymentIdGeneratorTest {

    @Mock
    private DriverDef driverDef;

    @Mock
    private DataSourceDef dataSourceDef;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testExtractUuid() {
        try {
            assertEquals( "abc", DeploymentIdGenerator.extractUuid( "kie#abc#" ) );
        } catch ( Exception e ) {
            fail( e.getMessage() );
        }
    }

    @Test
    public void testExtractInvalidUuid( ) {
        //all this are invalid deployment ids, so the extraction should fail for all of them.
        String invalidValues[] = { "abc", "kie#", "kie##" };
        for ( int i = 0; i < invalidValues.length; i++ ) {
            testExtractUuidInvalidUuid( invalidValues[ i ] );
        }
    }

    @Test
    public void testGenerateDeploymentId() {
        when ( driverDef.getUuid() ).thenReturn( "driver1" );
        when( dataSourceDef.getUuid() ).thenReturn( "dataSource1" );

        assertEquals( "kie#driver1#", DeploymentIdGenerator.generateDeploymentId( driverDef ) );
        assertEquals( "kie#dataSource1#", DeploymentIdGenerator.generateDeploymentId( dataSourceDef ) );
        assertEquals( "kie#someValue#", DeploymentIdGenerator.generateDeploymentId( "someValue" ) );
    }

    private void testExtractUuidInvalidUuid( String invalidUuid ) {
        try {
            expectedException.expectMessage( "Unknown deployment identifier." + invalidUuid );
            DeploymentIdGenerator.extractUuid( invalidUuid );
        } catch ( Exception e ) {
            fail( e.getMessage() );
        }
    }
}