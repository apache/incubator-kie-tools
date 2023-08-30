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
import org.kie.workbench.common.stunner.bpmn.forms.model.ProcessTypeEditorFieldDefinition;
import org.kie.workbench.common.stunner.bpmn.forms.model.ProcessTypeEditorFieldType;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;

public class ProcessTypeProviderFieldProviderTest {

    private ProcessTypeProviderFieldProvider processTypeProviderFieldProvider;

    @Before
    public void setUp() {
        processTypeProviderFieldProvider = new ProcessTypeProviderFieldProvider();
    }

    @Test
    public void testGetPriority() {
        // Setup
        final int expectedResult = 20005;

        // Run the test
        final int result = processTypeProviderFieldProvider.getPriority();

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testGetFieldType() {
        // Setup
        final Class<ProcessTypeEditorFieldType> expectedResult = ProcessTypeEditorFieldType.class;

        // Run the test
        final Class<ProcessTypeEditorFieldType> result = processTypeProviderFieldProvider.getFieldType();

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testGetFieldTypeName() {
        // Setup
        final String expectedResult = "ProcessTypeEditor";

        // Run the test
        final String result = processTypeProviderFieldProvider.getFieldTypeName();

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testGetDefaultField() {
        // Run the test
        final ProcessTypeEditorFieldDefinition result = processTypeProviderFieldProvider.getDefaultField();

        // Verify the results
        assertEquals(ProcessTypeEditorFieldDefinition.FIELD_TYPE, result.getFieldType());
    }

    @Test
    public void testCreateFieldByType() {
        assertTrue(processTypeProviderFieldProvider.createFieldByType(null) instanceof ProcessTypeEditorFieldDefinition);
    }

    @Test
    public void testDoRegisterFields() {
        processTypeProviderFieldProvider = spy(processTypeProviderFieldProvider);
        processTypeProviderFieldProvider.doRegisterFields();
        assertEquals(1, processTypeProviderFieldProvider.getSupportedTypes().length);
        assertEquals(String.class.getName(), processTypeProviderFieldProvider.getSupportedTypes()[0]);
    }
}
