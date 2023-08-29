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

package org.kie.workbench.common.dmn.api.resource;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.dmn.api.DMNDefinitionSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DMNDefinitionSetResourceTypeTest {

    private DMNDefinitionSetResourceType resourceType;

    @Before
    public void setup() {
        this.resourceType = new DMNDefinitionSetResourceType(new Decision());
    }

    @Test
    public void testCategory() {
        assertTrue(resourceType.getCategory() instanceof Decision);
    }

    @Test
    public void testShortName() {
        assertEquals(DMNDefinitionSetResourceType.NAME, resourceType.getShortName());
    }

    @Test
    public void testDescription() {
        assertEquals(DMNDefinitionSetResourceType.DESCRIPTION, resourceType.getDescription());
    }

    @Test
    public void testSuffix() {
        assertEquals(DMNDefinitionSetResourceType.DMN_EXTENSION, resourceType.getSuffix());
    }

    @Test
    public void testPriority() {
        assertEquals(0, resourceType.getPriority());
    }

    @Test
    public void testDefinitionSetType() {
        assertEquals(DMNDefinitionSet.class, resourceType.getDefinitionSetType());
    }
}
