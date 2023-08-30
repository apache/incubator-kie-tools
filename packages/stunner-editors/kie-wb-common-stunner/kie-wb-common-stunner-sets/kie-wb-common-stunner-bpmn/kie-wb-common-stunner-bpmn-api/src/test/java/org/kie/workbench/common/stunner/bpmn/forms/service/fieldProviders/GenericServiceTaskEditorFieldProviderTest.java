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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.definition.property.service.GenericServiceTaskValue;
import org.kie.workbench.common.stunner.bpmn.forms.model.GenericServiceTaskEditorFieldDefinition;
import org.kie.workbench.common.stunner.bpmn.forms.model.GenericServiceTaskEditorFieldType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;

public class GenericServiceTaskEditorFieldProviderTest {

    private GenericServiceTaskEditorFieldProvider genericServiceTaskEditorFieldProviderUnderTest;

    @Before
    public void setUp() {
        genericServiceTaskEditorFieldProviderUnderTest = new GenericServiceTaskEditorFieldProvider();
    }

    @Test
    public void getPriority() {
        Assert.assertEquals(5044337, new GenericServiceTaskEditorFieldProvider().getPriority());
    }

    @Test
    public void testGetFieldType() {
        // Setup
        final Class<GenericServiceTaskEditorFieldType> expectedResult = org.kie.workbench.common.stunner.bpmn.forms.model.GenericServiceTaskEditorFieldType.class;

        // Run the test
        final Class<GenericServiceTaskEditorFieldType> result = genericServiceTaskEditorFieldProviderUnderTest.getFieldType();

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testGetFieldTypeName() {
        // Setup
        final String expectedResult = "GenericServiceTaskEditor";

        // Run the test
        final String result = genericServiceTaskEditorFieldProviderUnderTest.getFieldTypeName();

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testGetDefaultField() {
        // Run the test
        final GenericServiceTaskEditorFieldDefinition result = genericServiceTaskEditorFieldProviderUnderTest.getDefaultField();

        // Verify the results
        assertEquals(GenericServiceTaskEditorFieldDefinition.FIELD_TYPE, result.getFieldType());
    }

    @Test
    public void testCreateFieldByType() {
        assertTrue(genericServiceTaskEditorFieldProviderUnderTest.createFieldByType(null) instanceof GenericServiceTaskEditorFieldDefinition);
    }

    @Test
    public void testDoRegisterFields() {
        genericServiceTaskEditorFieldProviderUnderTest = spy(genericServiceTaskEditorFieldProviderUnderTest);
        genericServiceTaskEditorFieldProviderUnderTest.doRegisterFields();
        assertEquals(1, genericServiceTaskEditorFieldProviderUnderTest.getSupportedTypes().length);
        assertEquals(GenericServiceTaskValue.class.getName(), genericServiceTaskEditorFieldProviderUnderTest.getSupportedTypes()[0]);
    }
}