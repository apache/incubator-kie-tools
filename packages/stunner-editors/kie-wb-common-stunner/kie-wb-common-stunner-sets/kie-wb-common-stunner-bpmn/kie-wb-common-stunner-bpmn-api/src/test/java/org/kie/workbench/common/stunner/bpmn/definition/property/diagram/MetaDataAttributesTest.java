/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.bpmn.definition.property.diagram;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.definition.property.type.MetaDataType;
import org.kie.workbench.common.stunner.core.definition.property.PropertyType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

public class MetaDataAttributesTest {

    private final String METADATA = "securityRolesß<![CDATA[employees,managers]]>Ø securityRoles2ß<![CDATA[admin,managers]]>";

    private MetaDataAttributes tested;

    @Before
    public void setUp() throws Exception {
        tested = new MetaDataAttributes(METADATA);
    }

    @Test
    public void testGetType() {
        PropertyType type = new MetaDataType();
        assertEquals(type, tested.getType());
    }

    @Test
    public void testGetValue() {
        assertEquals(METADATA, tested.getValue());
    }

    @Test
    public void testSetValue() {
        String metaData = "securityRolesß<![CDATA[employees,managers]]>";
        tested.setValue(metaData);
        assertEquals(metaData, tested.getValue());
    }

    @Test
    public void testEquals() {
        MetaDataAttributes metaDataAttributes = new MetaDataAttributes(METADATA);
        GlobalVariables globalVariables = new GlobalVariables();
        assertEquals(metaDataAttributes, tested);

        metaDataAttributes.setValue("securityRolesß<![CDATA[employees,managers]]>");
        assertNotEquals(metaDataAttributes, tested);

        assertFalse(tested.equals(globalVariables));
    }
}
