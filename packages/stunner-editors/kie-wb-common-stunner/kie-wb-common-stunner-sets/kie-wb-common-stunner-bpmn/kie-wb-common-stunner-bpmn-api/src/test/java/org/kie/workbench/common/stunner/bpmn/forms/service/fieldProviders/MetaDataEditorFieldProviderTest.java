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
import org.kie.workbench.common.stunner.bpmn.forms.model.MetaDataEditorFieldDefinition;
import org.kie.workbench.common.stunner.bpmn.forms.model.MetaDataEditorFieldType;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;

public class MetaDataEditorFieldProviderTest {

    private MetaDataEditorFieldProvider metaDataEditorFieldProviderUnderTest;

    @Before
    public void setUp() {
        metaDataEditorFieldProviderUnderTest = new MetaDataEditorFieldProvider();
    }

    @Test
    public void testGetPriority() {
        assertEquals(60000, metaDataEditorFieldProviderUnderTest.getPriority());
    }

    @Test
    public void testGetFieldType() {
        assertEquals(MetaDataEditorFieldType.class, metaDataEditorFieldProviderUnderTest.getFieldType());
    }

    @Test
    public void testGetFieldTypeName() {
        assertEquals("MetaDataEditor", metaDataEditorFieldProviderUnderTest.getFieldTypeName());
    }

    @Test
    public void testGetDefaultField() {
        assertEquals(MetaDataEditorFieldDefinition.FIELD_TYPE, metaDataEditorFieldProviderUnderTest.getDefaultField().getFieldType());
    }

    @Test
    public void testCreateFieldByType() {
        assertTrue(metaDataEditorFieldProviderUnderTest.createFieldByType(null) instanceof MetaDataEditorFieldDefinition);
    }

    @Test
    public void testDoRegisterFields() {
        metaDataEditorFieldProviderUnderTest = spy(metaDataEditorFieldProviderUnderTest);
        metaDataEditorFieldProviderUnderTest.doRegisterFields();
        assertEquals(1, metaDataEditorFieldProviderUnderTest.getSupportedTypes().length);
        assertEquals(String.class.getName(), metaDataEditorFieldProviderUnderTest.getSupportedTypes()[0]);
    }
}
