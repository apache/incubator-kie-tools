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


package org.kie.workbench.common.stunner.core.definition.adapter;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DefinitionIdTest {

    @Test
    public void testNotDynamicDefinitionId() {
        String input = "id1";
        DefinitionId id = DefinitionId.build(input);
        assertEquals(input, id.value());
        assertFalse(id.isDynamic());
        assertEquals(input, id.type());
    }

    @Test
    public void testDynamicDefinitionId() {
        String input = DefinitionId.generateId("type", "id1");
        DefinitionId id = DefinitionId.build("type",
                                             "id1");
        assertEquals(input, id.value());
        assertTrue(id.isDynamic());
        assertEquals("type", id.type());
    }
}
