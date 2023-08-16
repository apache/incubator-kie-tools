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
import org.kie.workbench.common.forms.model.TypeInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.notification.NotificationTypeListValue;
import org.kie.workbench.common.stunner.bpmn.forms.model.NotificationsEditorFieldDefinition;
import org.kie.workbench.common.stunner.bpmn.forms.model.NotificationsEditorFieldType;
import org.mockito.Mock;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;

public class NotificationsEditorFieldProviderTest {

    private NotificationsEditorFieldProvider notificationsEditorFieldProviderUnderTest;

    @Mock
    private TypeInfo typeInfo;

    @Before
    public void setUp() {
        notificationsEditorFieldProviderUnderTest = new NotificationsEditorFieldProvider();
    }

    @Test
    public void testGetPriority() {
        // Setup
        final int expectedResult = 20001;

        // Run the test
        final int result = notificationsEditorFieldProviderUnderTest.getPriority();

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testGetFieldType() {
        // Setup
        final Class<NotificationsEditorFieldType> expectedResult = org.kie.workbench.common.stunner.bpmn.forms.model.NotificationsEditorFieldType.class;

        // Run the test
        final Class<NotificationsEditorFieldType> result = notificationsEditorFieldProviderUnderTest.getFieldType();

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testGetFieldTypeName() {
        // Setup
        final String expectedResult = "NotificationsEditor";

        // Run the test
        final String result = notificationsEditorFieldProviderUnderTest.getFieldTypeName();

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testGetDefaultField() {
        // Run the test
        final NotificationsEditorFieldDefinition result = notificationsEditorFieldProviderUnderTest.getDefaultField();

        // Verify the results
        assertEquals(NotificationsEditorFieldDefinition.FIELD_TYPE, result.getFieldType());
    }

    @Test
    public void testCreateFieldByType() {
        assertTrue(notificationsEditorFieldProviderUnderTest.createFieldByType(null) instanceof NotificationsEditorFieldDefinition);
    }

    @Test
    public void testDoRegisterFields() {
        notificationsEditorFieldProviderUnderTest = spy(notificationsEditorFieldProviderUnderTest);
        notificationsEditorFieldProviderUnderTest.doRegisterFields();
        assertEquals(1, notificationsEditorFieldProviderUnderTest.getSupportedTypes().length);
        assertEquals(NotificationTypeListValue.class.getName(), notificationsEditorFieldProviderUnderTest.getSupportedTypes()[0]);
    }
}
