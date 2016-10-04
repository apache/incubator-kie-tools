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

package org.kie.workbench.common.screens.datasource.management.util;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.screens.datasource.management.model.DriverDef;

import static org.junit.Assert.*;

public class DriverDefValidatorTest {

    private DriverDefValidator validator = new DriverDefValidator();

    private static final String VALUE = "VALUE";

    private static final String EMPTY_VALUE = "    ";

    private DriverDef driverDef;

    @Before
    public void setup() {
        driverDef = new DriverDef();
        driverDef.setUuid( VALUE );
        driverDef.setName( VALUE );
        driverDef.setDriverClass( VALUE );
        driverDef.setGroupId( VALUE );
        driverDef.setArtifactId( VALUE );
        driverDef.setVersion( VALUE );
    }

    @Test
    public void testValidate() {
        // validate a complete definition.
        assertTrue( validator.validate( driverDef ) );
    }

    @Test
    public void testValidateUuid( ) {
        // validates that the uuid is complete.
        driverDef.setUuid( null );
        assertFalse( validator.validate( driverDef ) );

        driverDef.setUuid( EMPTY_VALUE );
        assertFalse( validator.validate( driverDef ) );
    }

    @Test
    public void testValidateName() {
        // validates that the name is  complete.
        driverDef.setName( null );
        assertFalse( validator.validate( driverDef ) );

        driverDef.setName( EMPTY_VALUE );
        assertFalse( validator.validate( driverDef ) );
    }

    @Test
    public void testValidateDriverClass() {
        // validates that the driverClass is complete.
        driverDef.setDriverClass( null );
        assertFalse( validator.validate( driverDef ) );

        driverDef.setDriverClass( EMPTY_VALUE );
        assertFalse( validator.validate( driverDef ) );
    }

    @Test
    public void testValidateGroupId() {
        // validates that the groupId is complete.
        driverDef.setGroupId( null );
        assertFalse( validator.validate( driverDef ) );

        driverDef.setGroupId( EMPTY_VALUE );
        assertFalse( validator.validate( driverDef ) );
    }

    @Test
    public void testValidateArtifactId() {
        // validates that the artifactId is complete.
        driverDef.setArtifactId( null );
        assertFalse( validator.validate( driverDef ) );

        driverDef.setArtifactId( EMPTY_VALUE );
        assertFalse( validator.validate( driverDef ) );
    }

    @Test
    public void testValidateVersion() {
        // validates that the version is complete.
        driverDef.setVersion( null );
        assertFalse( validator.validate( driverDef ) );

        driverDef.setVersion( EMPTY_VALUE );
        assertFalse( validator.validate( driverDef ) );
    }
}