/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.model;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.definition.property.reassignment.ReassignmentValue;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

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
        expectedResult.setDuration("0h");
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
