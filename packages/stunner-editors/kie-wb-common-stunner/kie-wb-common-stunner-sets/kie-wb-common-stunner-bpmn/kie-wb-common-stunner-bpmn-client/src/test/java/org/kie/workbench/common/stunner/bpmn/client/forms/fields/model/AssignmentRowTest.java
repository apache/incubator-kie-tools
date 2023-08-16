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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class AssignmentRowTest {

    @Test
    public void testIsComplete() {
        AssignmentRow row = new AssignmentRow(null, Variable.VariableType.INPUT, "String", null, null, "Hello World");
        assertFalse(row.isComplete());
        row.setName("");
        assertFalse(row.isComplete());

        row.setName("someName");
        assertTrue(row.isComplete());

        row.setDataType(null);
        assertFalse(row.isComplete());

        row.setDataType("");
        assertFalse(row.isComplete());

        row.setCustomDataType("String");
        assertTrue(row.isComplete());

        row.setExpression(null);
        assertFalse(row.isComplete());

        row.setProcessVar("processVar");
        assertTrue(row.isComplete());
    }
}
