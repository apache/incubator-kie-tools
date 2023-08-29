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
import org.kie.workbench.common.stunner.bpmn.forms.model.DataObjectTypeFieldDefinition;
import org.kie.workbench.common.stunner.bpmn.forms.model.DataObjectTypeFieldType;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;

public class DataObjectTypeEditorFieldProviderTest {

    private DataObjectTypeEditorFieldProvider dataObjectTypeEditorFieldProvider;

    @Before
    public void setUp() {
        dataObjectTypeEditorFieldProvider = new DataObjectTypeEditorFieldProvider();
    }

    @Test
    public void testGetPriority() {
        // Setup
        final int expectedResult = 20004;

        // Run the test
        final int result = dataObjectTypeEditorFieldProvider.getPriority();

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testGetFieldType() {
        // Setup
        final Class<DataObjectTypeFieldType> expectedResult = DataObjectTypeFieldType.class;

        // Run the test
        final Class<DataObjectTypeFieldType> result = dataObjectTypeEditorFieldProvider.getFieldType();

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testGetFieldTypeName() {
        // Setup
        final String expectedResult = "DataObjectEditor";

        // Run the test
        final String result = dataObjectTypeEditorFieldProvider.getFieldTypeName();

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testGetDefaultField() {
        // Run the test
        final DataObjectTypeFieldDefinition result = dataObjectTypeEditorFieldProvider.getDefaultField();

        // Verify the results
        assertEquals(DataObjectTypeFieldDefinition.FIELD_TYPE, result.getFieldType());
    }

    @Test
    public void testCreateFieldByType() {
        assertTrue(dataObjectTypeEditorFieldProvider.createFieldByType(null) instanceof DataObjectTypeFieldDefinition);
    }

    @Test
    public void testDoRegisterFields() {
        dataObjectTypeEditorFieldProvider = spy(dataObjectTypeEditorFieldProvider);
        dataObjectTypeEditorFieldProvider.doRegisterFields();
        assertEquals(1, dataObjectTypeEditorFieldProvider.getSupportedTypes().length);
        assertEquals(String.class, dataObjectTypeEditorFieldProvider.getSupportedTypes().getClass().getComponentType());
    }
}
