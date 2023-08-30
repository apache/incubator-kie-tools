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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.definition.property.reassignment.ReassignmentValue;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(MockitoJUnitRunner.class)
public class ReassignmentRowTest {

    @Mock
    private ReassignmentValue mockReassignment;

    private ReassignmentRow reassignmentRowUnderTest;

    @Before
    public void setUp() {
        initMocks(this);
        when(mockReassignment.getType()).thenReturn(ReassignmentType.NotCompletedReassign.getType());

        reassignmentRowUnderTest = new ReassignmentRow(mockReassignment);
    }

    @Test
    public void testClone() {
        assertEquals(reassignmentRowUnderTest, reassignmentRowUnderTest.clone());
    }

    @Test
    public void testToReassignmentValue() {
        // Setup
        final ReassignmentValue expectedResult = new ReassignmentValue();
        expectedResult.setDuration("0" + Duration.HOUR.getAlias());
        expectedResult.setType("NotCompletedReassign");
        // Run the test
        final ReassignmentValue result = new ReassignmentRow().toReassignmentValue();

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testEquals() {
        // Setup
        final Object obj = null;
        // Verify the results
        assertFalse(reassignmentRowUnderTest.equals(obj));
    }
}
