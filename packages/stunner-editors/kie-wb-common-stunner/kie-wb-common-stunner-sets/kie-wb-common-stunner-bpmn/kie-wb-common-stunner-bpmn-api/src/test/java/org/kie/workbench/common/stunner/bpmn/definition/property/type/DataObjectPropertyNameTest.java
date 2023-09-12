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


package org.kie.workbench.common.stunner.bpmn.definition.property.type;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class DataObjectPropertyNameTest {

    private DataObjectPropertyName dataObjectPropertyName = new DataObjectPropertyName();

    @Test
    public void getName() {
        assertEquals(DataObjectPropertyName.NAME, dataObjectPropertyName.getName());
    }

    @Test
    public void testHashCode() {
        assertEquals(new DataObjectPropertyName().hashCode(), dataObjectPropertyName.hashCode());
    }

    @Test
    public void testEquals() {
        assertEquals(new DataObjectPropertyName(), dataObjectPropertyName);
        assertNotEquals(new DataObjectPropertyName(), new Object());

        final DataObjectPropertyName dataObjectPropertyName = new DataObjectPropertyName();
        assertEquals(dataObjectPropertyName, dataObjectPropertyName);
    }

    @Test
    public void testToString() {
        assertEquals(new DataObjectPropertyName().toString(), dataObjectPropertyName.toString());
    }

}
