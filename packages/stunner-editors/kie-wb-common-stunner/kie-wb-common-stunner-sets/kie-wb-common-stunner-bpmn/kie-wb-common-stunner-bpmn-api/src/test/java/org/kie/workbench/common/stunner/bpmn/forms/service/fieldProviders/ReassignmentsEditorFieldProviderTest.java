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


package org.kie.workbench.common.stunner.bpmn.forms.service.fieldProviders;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.definition.property.reassignment.ReassignmentTypeListValue;
import org.kie.workbench.common.stunner.bpmn.forms.model.ReassignmentsEditorFieldDefinition;
import org.kie.workbench.common.stunner.bpmn.forms.model.ReassignmentsEditorFieldType;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;

public class ReassignmentsEditorFieldProviderTest {

    private ReassignmentsEditorFieldProvider reassignmentsEditorFieldProviderUnderTest;

    @Before
    public void setUp() {
        reassignmentsEditorFieldProviderUnderTest = new ReassignmentsEditorFieldProvider();
    }

    @Test
    public void testGetPriority() {
        // Setup
        final int expectedResult = 20002;

        // Run the test
        final int result = reassignmentsEditorFieldProviderUnderTest.getPriority();

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testGetFieldType() {
        // Setup
        final Class<ReassignmentsEditorFieldType> expectedResult = ReassignmentsEditorFieldType.class;

        // Run the test
        final Class<ReassignmentsEditorFieldType> result = reassignmentsEditorFieldProviderUnderTest.getFieldType();

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testGetFieldTypeName() {
        // Setup
        final String expectedResult = "ReassignmentsEditor";

        // Run the test
        final String result = reassignmentsEditorFieldProviderUnderTest.getFieldTypeName();

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testGetDefaultField() {
        // Run the test
        final ReassignmentsEditorFieldDefinition result = reassignmentsEditorFieldProviderUnderTest.getDefaultField();

        // Verify the results
        assertEquals(ReassignmentsEditorFieldDefinition.FIELD_TYPE, result.getFieldType());
    }

    @Test
    public void testCreateFieldByType() {
        assertTrue(reassignmentsEditorFieldProviderUnderTest.createFieldByType(null) instanceof ReassignmentsEditorFieldDefinition);
    }

    @Test
    public void testDoRegisterFields() {
        reassignmentsEditorFieldProviderUnderTest = spy(reassignmentsEditorFieldProviderUnderTest);
        reassignmentsEditorFieldProviderUnderTest.doRegisterFields();
        assertEquals(1, reassignmentsEditorFieldProviderUnderTest.getSupportedTypes().length);
        assertEquals(ReassignmentTypeListValue.class.getName(), reassignmentsEditorFieldProviderUnderTest.getSupportedTypes()[0]);
    }
}
